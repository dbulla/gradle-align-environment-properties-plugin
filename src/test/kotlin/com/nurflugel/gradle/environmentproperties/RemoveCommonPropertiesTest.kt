package com.nurflugel.gradle.environmentproperties

import com.nurflugel.gradle.environmentproperties.RoundTripSpec.Companion.validateNewFile
import io.kotlintest.specs.StringSpec
import java.io.File

/** Test that all common properties (the same for every environment) are moved into application.properties and no longer exist in the env properties */
class RemoveCommonPropertiesTest : StringSpec(
    {

        val action = AlignPropertiesAction()
        val parent = File("src/test/resources/examples/sample2")
        val files = listOf(File(parent, "application.properties"), File(parent, "dev.properties"), File(parent, "qa.properties"))
        action.processPropertyFiles(files)

        "test base".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "application_new.properties", listOf(
                    "key 1 base = value from base",
                    "key 2  = value from base",
                    "key 3  = value from base",
                    "key 3.5 differs across SOME envs = value from base"
                )
            )
        }


        "test dev doesn't have common properties".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "dev_new.properties", listOf(
                    "key 3.5 differs across SOME envs = value from dev"
                )
            )
        }

        "test qa".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "qa_new.properties", listOf(
                )
            )
        }

    }) {
    companion object {
        // setting this to false lets you test a single method if you then set it's enabled flag to true.
        // Irritating that the framework does not value from dev"ns't do this better...

        // const val ALL_TESTS_ENABLED = false
        const val ALL_TESTS_ENABLED = true
    }
}


