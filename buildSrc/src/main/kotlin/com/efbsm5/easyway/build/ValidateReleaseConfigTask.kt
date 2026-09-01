package com.efbsm5.easyway.build

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class ValidateReleaseConfigTask : DefaultTask() {
    @get:Input
    abstract val apiKeyConfigured: Property<Boolean>

    @get:Input
    abstract val releaseBaseUrlSafe: Property<Boolean>

    @TaskAction
    fun validateConfiguration() {
        check(apiKeyConfigured.get()) {
            "Missing release configuration: easyway.amapApiKey or EASYWAY_AMAP_API_KEY."
        }
        check(releaseBaseUrlSafe.get()) {
            "Invalid release configuration: release base URL must be globally routable HTTP(S) and end with '/'."
        }
    }
}
