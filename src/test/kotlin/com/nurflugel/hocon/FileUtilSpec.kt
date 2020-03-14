package com.nurflugel.hocon

//import com.nurflugel.hocon.PropertiesToConfSpec.Companion.convertToConf
import com.jetbrains.rd.util.first
import com.nurflugel.gradle.FileUtil.Companion.getCommonProperties
import com.nurflugel.gradle.FileUtil.Companion.isKeyCommonAcrossAllEnvironments
import com.nurflugel.gradle.FileUtil.Companion.isSecret
import io.kotlintest.matchers.maps.shouldContainKey
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class FileUtilSpec : StringSpec(
    {

        val key1 = "key 1 base"
        val baseValue = "value from base"

        val commonKey = "key 4 common across envs"
        val commonValue = "some value"
        val devValue = "value from dev"
        val qaValue = "value from qa"
        val differentValuesKey = "key 3 differs across envs"

        val base = mapOf(
            key1 to baseValue,
            "key 2 overridden base" to baseValue,
            differentValuesKey to baseValue,
            commonKey to commonValue
        )
        val qa = mapOf(
            "key 2 overridden base" to qaValue,
            commonKey to commonValue,
            differentValuesKey to qaValue,
            "key qa" to qaValue
        )
        val dev = mapOf(
            commonKey to commonValue,
            differentValuesKey to devValue,
            "key dev" to devValue
        )

        val propsForEnvs = mapOf(
            "application.properties" to base,
            "dev.properties" to dev,
            "qa.properties" to qa
        )

        "test getCommonProperties".config(enabled = ALL_TESTS_ENABLED) {

            printEnvProps("Application", base)
            printEnvProps("dev", dev)
            printEnvProps("qa", qa)

            val commonProperties: Map<String, String> = getCommonProperties(propsForEnvs)

            printEnvProps("Common", commonProperties)

            commonProperties shouldContainKey commonKey
            commonProperties[commonKey] shouldBe commonValue

            // assure no other entries
            commonProperties.size shouldBe 1
        }

        "test filterEnvs".config(enabled = ALL_TESTS_ENABLED) {
            val commonProperties: Map<String, String> = getCommonProperties(propsForEnvs)

            printEnvProps("Application", base)
            printEnvProps("dev", dev)
            printEnvProps("qa", qa)

            printEnvProps("Common", commonProperties)
            commonProperties.size shouldBe 1
            
//            commonProperties.containsKey(key1) shouldBe true
//            commonProperties[key1] shouldBe baseValue


            commonProperties.containsKey(commonKey) shouldBe true
            commonProperties[commonKey] shouldBe commonValue
        }


        "test isSecret".config(enabled = ALL_TESTS_ENABLED) {
            isSecret("dibble") shouldBe false
            isSecret("password") shouldBe true
            isSecret("access") shouldBe false
            isSecret("accesskey") shouldBe true
            isSecret("headervalue") shouldBe true
        }


        "test isKeyCommonAcrossAllEnvs".config(enabled = ALL_TESTS_ENABLED) {
            val envs: Set<String> = setOf("dev.properties", "qa.properties")

            var isCommon = isKeyCommonAcrossAllEnvironments(envs, propsForEnvs, commonKey, commonValue)
            isCommon shouldBe true
            val first = propsForEnvs.first()
            val possibleCommonValue = first.value.getValue(differentValuesKey)
            isCommon = isKeyCommonAcrossAllEnvironments(envs, propsForEnvs, differentValuesKey, possibleCommonValue)
            isCommon shouldBe false
        }


    }) {
    companion object {
        // setting this to false lets you test a single method if you then set it's enabled flag to true.
        // Irritating that the framework does not value from dev"ns't do this better...

        // const val ALL_TESTS_ENABLED = false
        const val ALL_TESTS_ENABLED = true
    }
}

private fun printEnvProps(env: String, envProps: Map<String, String>) {
    println("$env Properties: ")
    envProps.entries.forEach { println("    $it") }
}

