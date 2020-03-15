package com.nurflugel.gradle.environmentproperties

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File

/** General full tests */
class RoundTripSpec : StringSpec(
    {

        val action = AlignPropertiesAction()
        val parent = File("src/test/resources/examples/sample1")
        val files = listOf(
            File(parent, "application.properties"),
            File(parent, "dev.properties"),
            File(parent, "qa.properties")
        )
        action.processPropertyFiles(files)

        "test base".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "application_new.properties", listOf(
                    "key 1 base                 = value from base",
                    "key 2 overridden base      = value from base",
                    "key 3.5 differs across SOME envs = value from base",
                    "key 4 common across envs   = some value"
                )
            )
        }

        "test dev".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "dev_new.properties", listOf(
                    "key 3 differs across all envs  = value from dev",
                    "key dev                    = value from dev"
                )
            )
        }

        "test qa".config(enabled = ALL_TESTS_ENABLED) {
            validateNewFile(
                parent, "qa_new.properties", listOf(
                    "key 2 overridden base     = value from qa",
                    "key 3 differs across all envs = value from qa",
                    "key 3.5 differs across SOME envs = value from qa",
                    "key qa                    = value from qa"
                )
            )
        }

    }) {
    companion object {
        // setting this to false lets you test a single method if you then set it's enabled flag to true.
        // Irritating that the framework does not value from dev"ns't do this better...

        // const val ALL_TESTS_ENABLED = false
        const val ALL_TESTS_ENABLED = true

        fun validateNewFile(parent: File, child: String, expectedLines: List<String>) {
            val newFile = File(parent, child)

            val lines = newFile.readLines()// ensure they're in the same order
            val sortedExpected = expectedLines.sorted().map {
                adjustLineForConsistency(
                    it
                )
            }
            val sortedActual = lines.sorted().map {
                adjustLineForConsistency(
                    it
                )
            }

            sortedActual shouldBe sortedExpected
            lines.size shouldBe expectedLines.size
        }

        /** Take the line and remove whitespace for testing */
        private fun adjustLineForConsistency(line: String): String {
            return line.substringBefore("=").trim() + " = " + line.substringAfter("=").trim()
        }

    }
}


