package com.efbsm5.easyway.build

import java.net.InetAddress
import java.net.URI

object ReleaseUrlPolicy {
    fun isSafe(value: String): Boolean {
        if (!isValidHttpBaseUrl(value)) return false
        val uri = runCatching { URI(value) }.getOrNull() ?: return false
        val host = normalizeHost(uri.host)
        if (!isAllowedHostName(host)) return false

        val numericAddress = parseNumericAddress(host)
        if (numericAddress != null) {
            if (host.contains(':') && numericAddress.address.size == 4) return false
            return isGloballyRoutable(numericAddress)
        }
        return !host.contains(':') && !looksLikeIpv4Literal(host)
    }

    fun isValidHttpBaseUrl(value: String): Boolean = runCatching { URI(value) }
        .getOrNull()
        ?.let { uri ->
            uri.scheme?.lowercase() in setOf("http", "https") &&
                !uri.host.isNullOrBlank() &&
                uri.rawUserInfo == null &&
                uri.rawQuery == null &&
                uri.rawFragment == null &&
                (uri.port == -1 || uri.port in 1..65_535) &&
                value.endsWith('/')
        } == true

    private fun normalizeHost(host: String): String = host
        .trim()
        .removePrefix("[")
        .removeSuffix("]")
        .trimEnd('.')
        .lowercase()

    private fun isAllowedHostName(host: String): Boolean {
        if (host.isEmpty()) return false
        val reservedSuffixes = listOf(
            "localhost",
            "local",
            "internal",
            "invalid",
            "test",
            "example",
            "onion",
            "home.arpa",
        )
        if (reservedSuffixes.any { suffix -> host == suffix || host.endsWith(".$suffix") }) {
            return false
        }
        val exampleDomains = listOf("example.com", "example.net", "example.org")
        if (exampleDomains.any { domain -> host == domain || host.endsWith(".$domain") }) {
            return false
        }
        return host.contains('.') || host.contains(':') || looksLikeIpv4Literal(host)
    }

    private fun parseIpv4Component(component: String): Long? {
        if (component.isEmpty()) return null
        val (digits, radix) = when {
            component.startsWith("0x", ignoreCase = true) -> component.drop(2) to 16
            component.length > 1 && component.startsWith('0') -> component.drop(1) to 8
            else -> component to 10
        }
        if (digits.isEmpty()) return 0
        return digits.toLongOrNull(radix)?.takeIf { it >= 0 }
    }

    private fun looksLikeIpv4Literal(host: String): Boolean = host.matches(
        Regex("(?i)(?:0x[0-9a-f]+|[0-9]+)(?:\\.(?:0x[0-9a-f]+|[0-9]+)){0,3}")
    )

    private fun parseIpv4Literal(host: String): ByteArray? {
        val components = host.split('.')
        if (components.size !in 1..4) return null
        val numbers = components.map { parseIpv4Component(it) ?: return null }
        val value = when (numbers.size) {
            1 -> numbers[0].takeIf { it <= 0xffff_ffffL }
            2 -> if (numbers[0] <= 0xff && numbers[1] <= 0xff_ffff) {
                (numbers[0] shl 24) or numbers[1]
            } else null
            3 -> if (numbers[0] <= 0xff && numbers[1] <= 0xff && numbers[2] <= 0xffff) {
                (numbers[0] shl 24) or (numbers[1] shl 16) or numbers[2]
            } else null
            4 -> if (numbers.all { it <= 0xff }) {
                (numbers[0] shl 24) or
                    (numbers[1] shl 16) or
                    (numbers[2] shl 8) or
                    numbers[3]
            } else null
            else -> null
        } ?: return null
        return byteArrayOf(
            (value ushr 24).toByte(),
            (value ushr 16).toByte(),
            (value ushr 8).toByte(),
            value.toByte(),
        )
    }

    private fun parseNumericAddress(host: String): InetAddress? {
        parseIpv4Literal(host)?.let { return InetAddress.getByAddress(it) }
        if (!host.contains(':') || host.contains('%')) return null
        if (!host.matches(Regex("[0-9a-fA-F:.]+"))) return null
        return runCatching { InetAddress.getByName(host) }.getOrNull()
    }

    private fun isGloballyRoutable(address: InetAddress): Boolean {
        if (address.isAnyLocalAddress ||
            address.isLoopbackAddress ||
            address.isLinkLocalAddress ||
            address.isSiteLocalAddress ||
            address.isMulticastAddress
        ) return false

        val bytes = address.address
        return when (bytes.size) {
            4 -> isGloballyRoutableIpv4(bytes)
            16 -> isGloballyRoutableIpv6(bytes)
            else -> false
        }
    }

    private fun isGloballyRoutableIpv4(bytes: ByteArray): Boolean {
        val first = bytes.u(0)
        val second = bytes.u(1)
        val third = bytes.u(2)
        return when {
            first == 0 || first == 10 || first == 127 -> false
            first == 100 && second in 64..127 -> false
            first == 169 && second == 254 -> false
            first == 172 && second in 16..31 -> false
            first == 192 && second == 0 && third == 0 -> false
            first == 192 && second == 0 && third == 2 -> false
            first == 192 && second == 88 && third == 99 -> false
            first == 192 && second == 168 -> false
            first == 198 && second in 18..19 -> false
            first == 198 && second == 51 && third == 100 -> false
            first == 203 && second == 0 && third == 113 -> false
            first >= 224 -> false
            else -> true
        }
    }

    private fun isGloballyRoutableIpv6(bytes: ByteArray): Boolean {
        if (bytes.u(0) and 0xe0 != 0x20) return false
        if (bytes.take(12).all { it.toInt() == 0 }) return false
        if (bytes.prefix(0x00, 0x64, 0xff, 0x9b) && bytes.sliceArray(4..11).all { it.toInt() == 0 }) {
            return false
        }
        if (bytes.prefix(0x00, 0x64, 0xff, 0x9b, 0x00, 0x01)) return false
        if (bytes.prefix(0x01, 0x00) && bytes.sliceArray(2..7).all { it.toInt() == 0 }) return false
        if (bytes.prefix(0x20, 0x01, 0x00, 0x00)) return false
        if (bytes.prefix(0x20, 0x01, 0x00, 0x02, 0x00, 0x00)) return false
        if (bytes.prefix(0x20, 0x01, 0x00) &&
            bytes.u(3) and 0xf0 in setOf(0x10, 0x20)
        ) return false
        if (bytes.prefix(0x20, 0x01, 0x0d, 0xb8)) return false
        if (bytes.prefix(0x20, 0x02)) return false
        if (bytes.u(0) == 0x3f && bytes.u(1) and 0xf0 == 0xf0) return false
        if (bytes.u(0) and 0xfe == 0xfc) return false
        if (bytes.u(0) == 0xff) return false
        return true
    }

    private fun ByteArray.u(index: Int): Int = this[index].toInt() and 0xff

    private fun ByteArray.prefix(vararg values: Int): Boolean =
        values.indices.all { index -> u(index) == values[index] }
}

object ReleaseUrlPolicyFixtures {
    val unsafeUrls = listOf(
        "",
        "relative/path/",
        "ftp://api.openstreetmap.org/",
        "https://api.openstreetmap.org",
        "https://user@api.openstreetmap.org/",
        "https://api.openstreetmap.org/?query=1",
        "https://api.openstreetmap.org/#fragment",
        "https://api.openstreetmap.org:0/",
        "https://api.openstreetmap.org:65536/",
        "https://api.openstreetmap.org:99999/",
        "http://LOCALHOST./",
        "http://api.localhost/",
        "http://router/",
        "http://service.local/",
        "http://service.internal/",
        "http://placeholder.invalid/",
        "http://service.test/",
        "http://service.example/",
        "http://service.onion/",
        "http://home.arpa/",
        "https://api.example.com/",
        "https://api.example.net/",
        "https://api.example.org/",
        "http://0.0.0.0/",
        "http://10.0.0.1/",
        "http://100.64.0.1/",
        "http://127.0.0.1/",
        "http://2130706433/",
        "http://127.1/",
        "http://0177.0.0.1/",
        "http://0x7f000001/",
        "http://169.254.0.1/",
        "http://172.16.0.1/",
        "http://192.0.0.1/",
        "http://192.0.2.1/",
        "http://192.88.99.1/",
        "http://192.168.0.1/",
        "http://198.18.0.1/",
        "http://198.51.100.1/",
        "http://203.0.113.1/",
        "http://224.0.0.1/",
        "http://240.0.0.1/",
        "http://[::]/",
        "http://[::1]/",
        "http://[::ffff:127.0.0.1]/",
        "http://[::ffff:10.0.0.1]/",
        "http://[::ffff:8.8.8.8]/",
        "http://[::ffff:0a00:0001]/",
        "http://[::ffff:0808:0808]/",
        "http://[::ffff:0:8.8.8.8]/",
        "http://[::ffff:0:10.0.0.1]/",
        "http://[::ffff:0:0808:0808]/",
        "http://[::ffff:0:0a00:0001]/",
        "http://[64:ff9b::1]/",
        "http://[64:ff9b:1::1]/",
        "http://[100::1]/",
        "http://[2001::1]/",
        "http://[2001:2::1]/",
        "http://[2001:10::1]/",
        "http://[2001:20::1]/",
        "http://[2001:db8::1]/",
        "http://[2002::1]/",
        "http://[3fff::1]/",
        "http://[fc00::1]/",
        "http://[fe80::1]/",
        "http://[fec0::1]/",
        "http://[ff00::1]/",
        "http://[fe80::1%25en0]/",
        "http://[1fff::1]/",
        "http://[4000::1]/",
        "http://[8000::1]/",
        "http://[c000::1]/",
    )

    val safeUrls = listOf(
        "https://api.openstreetmap.org/",
        "http://api.openstreetmap.org/",
        "http://api.openstreetmap.org:80/",
        "https://api.openstreetmap.org:443/",
        "https://api.openstreetmap.org:65535/",
        "https://8.8.8.8/",
        "https://[2606:4700:4700::1111]/",
    )
}
