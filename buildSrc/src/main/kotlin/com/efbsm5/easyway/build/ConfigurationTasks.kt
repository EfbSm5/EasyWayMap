package com.efbsm5.easyway.build

import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

data class EasyWayConfigurationTasks(
    val verifyReleaseUrlPolicy: TaskProvider<VerifyReleaseUrlPolicyTask>,
    val validateReleaseConfig: TaskProvider<ValidateReleaseConfigTask>,
)

fun Project.registerEasyWayConfigurationTasks(
    configuration: EasyWayExternalConfiguration,
): EasyWayConfigurationTasks {
    val verifyPolicy = tasks.register(
        "verifyReleaseUrlPolicy",
        VerifyReleaseUrlPolicyTask::class.java,
    ) {
        group = "verification"
        description = "Verifies release URL policy against public safe and unsafe fixtures."
        unsafeUrls.set(ReleaseUrlPolicyFixtures.unsafeUrls)
        safeUrls.set(ReleaseUrlPolicyFixtures.safeUrls)
    }
    val validateConfiguration = tasks.register(
        "validateReleaseConfig",
        ValidateReleaseConfigTask::class.java,
    ) {
        group = "verification"
        description = "Validates external configuration required by release variants."
        dependsOn(verifyPolicy)
        apiKeyConfigured.set(configuration.amapApiKey.map(String::isNotBlank))
        releaseBaseUrlSafe.set(configuration.releaseBaseUrl.map(ReleaseUrlPolicy::isSafe))
    }

    tasks.configureEach {
        val verificationTaskNames = setOf(
            validateConfiguration.name,
            verifyPolicy.name,
        )
        if (name.contains("Release", ignoreCase = true) && name !in verificationTaskNames) {
            dependsOn(validateConfiguration)
        }
    }
    tasks.named("check").configure {
        dependsOn(verifyPolicy)
    }

    return EasyWayConfigurationTasks(
        verifyReleaseUrlPolicy = verifyPolicy,
        validateReleaseConfig = validateConfiguration,
    )
}
