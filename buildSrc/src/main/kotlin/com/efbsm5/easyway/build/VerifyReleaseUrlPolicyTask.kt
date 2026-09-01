package com.efbsm5.easyway.build

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class VerifyReleaseUrlPolicyTask : DefaultTask() {
    @get:Input
    abstract val unsafeUrls: ListProperty<String>

    @get:Input
    abstract val safeUrls: ListProperty<String>

    @TaskAction
    fun verifyPolicy() {
        check(unsafeUrls.get().none(ReleaseUrlPolicy::isSafe)) {
            "Release URL policy accepted an unsafe fixture."
        }
        check(safeUrls.get().all(ReleaseUrlPolicy::isSafe)) {
            "Release URL policy rejected a public safe fixture."
        }
    }
}
