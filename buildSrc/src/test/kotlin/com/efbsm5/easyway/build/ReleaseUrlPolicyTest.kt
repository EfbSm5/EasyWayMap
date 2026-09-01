package com.efbsm5.easyway.build

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseUrlPolicyTest {
    @Test
    fun unsafeFixtures_areRejected() {
        ReleaseUrlPolicyFixtures.unsafeUrls.forEach { url ->
            assertFalse("Expected unsafe fixture to be rejected: $url", ReleaseUrlPolicy.isSafe(url))
        }
    }

    @Test
    fun publicSafeFixtures_areAccepted() {
        ReleaseUrlPolicyFixtures.safeUrls.forEach { url ->
            assertTrue("Expected public safe fixture to be accepted: $url", ReleaseUrlPolicy.isSafe(url))
        }
    }

    @Test
    fun mappedIpv6AndInvalidPorts_areRejected() {
        val edgeCases = listOf(
            "http://[::ffff:8.8.8.8]/",
            "http://[::ffff:0808:0808]/",
            "https://api.openstreetmap.org:0/",
            "https://api.openstreetmap.org:65536/",
        )
        edgeCases.forEach { url ->
            assertFalse("Expected edge case to be rejected: $url", ReleaseUrlPolicy.isSafe(url))
        }
    }

    @Test
    fun translatedIpv4AndIpv6OutsideGlobalUnicastRange_areRejected() {
        val edgeCases = listOf(
            "http://[::ffff:0:8.8.8.8]/",
            "http://[::ffff:0:0808:0808]/",
            "http://[1fff::1]/",
            "http://[4000::1]/",
            "http://[8000::1]/",
            "http://[c000::1]/",
        )
        edgeCases.forEach { url ->
            assertFalse("Expected IPv6 edge case to be rejected: $url", ReleaseUrlPolicy.isSafe(url))
        }
    }
}
