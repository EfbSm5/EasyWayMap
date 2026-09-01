package com.efbsm5.easyway.build

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters

const val SAFE_DEBUG_BASE_URL = "https://example.invalid/"

data class EasyWayExternalConfiguration(
    val amapApiKey: Provider<String>,
    val debugBaseUrl: Provider<String>,
    val releaseBaseUrl: Provider<String>,
)

abstract class LocalPropertyValueSource :
    ValueSource<String, LocalPropertyValueSource.Parameters> {

    interface Parameters : ValueSourceParameters {
        val file: RegularFileProperty
        val key: Property<String>
    }

    override fun obtain(): String? {
        val localFile = parameters.file.asFile.get()
        if (!localFile.isFile) return null
        val properties = java.util.Properties().apply {
            localFile.inputStream().use { load(it) }
        }
        return properties.getProperty(parameters.key.get())
    }
}

fun Project.easyWayExternalConfiguration(): EasyWayExternalConfiguration {
    fun nonBlank(provider: Provider<String>): Provider<String> = provider
        .map(String::trim)
        .filter(String::isNotEmpty)

    fun resolve(gradlePropertyName: String, environmentVariableName: String): Provider<String> {
        val localValue = providers.of(LocalPropertyValueSource::class.java) {
            parameters.file.set(rootProject.layout.projectDirectory.file("local.properties"))
            parameters.key.set(gradlePropertyName)
        }
        return nonBlank(providers.gradleProperty(gradlePropertyName))
            .orElse(nonBlank(providers.environmentVariable(environmentVariableName)))
            .orElse(nonBlank(localValue))
    }

    val amapApiKey = resolve(
        gradlePropertyName = "easyway.amapApiKey",
        environmentVariableName = "EASYWAY_AMAP_API_KEY",
    ).orElse(providers.provider { "" })
    val debugBaseUrl = resolve(
        gradlePropertyName = "easyway.debugBaseUrl",
        environmentVariableName = "EASYWAY_DEBUG_BASE_URL",
    ).map { value -> if (value.endsWith('/')) value else "$value/" }
        .orElse(providers.provider { SAFE_DEBUG_BASE_URL })
    val releaseBaseUrl = resolve(
        gradlePropertyName = "easyway.releaseBaseUrl",
        environmentVariableName = "EASYWAY_RELEASE_BASE_URL",
    ).orElse(providers.provider { SAFE_DEBUG_BASE_URL })

    return EasyWayExternalConfiguration(
        amapApiKey = amapApiKey,
        debugBaseUrl = debugBaseUrl,
        releaseBaseUrl = releaseBaseUrl,
    )
}
