package com.nurflugel.gradle.environmentproperties

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.nurflugel.gradle.environmentproperties.FileUtil.Companion.processProperties
import com.nurflugel.gradle.environmentproperties.FileUtil.Companion.readPropertyFiles
import com.nurflugel.gradle.environmentproperties.FileUtil.Companion.removeOldFiles
import com.nurflugel.gradle.environmentproperties.FileUtil.Companion.writePropertiesFiles
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException

class AlignPropertiesAction : AnAction() {

    @Suppress("MissingRecentApi")
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val data: Array<VirtualFile>? = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)
        val inputFiles = data!!.asSequence()
            .map { it.canonicalFile }
            .map { it!!.path }
            .map { File(it) }
            .toList()

        processPropertyFiles(inputFiles)
    }

    fun processPropertyFiles(inputFiles: List<File>) {
        val parentDir = inputFiles.first().parentFile
        val propsForEnvs = readPropertyFiles(inputFiles)
        removeOldFiles(parentDir, propsForEnvs)
        val finalPropsForEnvs = processProperties(propsForEnvs)
        writePropertiesFiles(finalPropsForEnvs, parentDir)
    }
}

@Suppress("UnnecessaryVariable")
class FileUtil {
    companion object {
        private const val BASE_PROPERTIES_FILE_NAME = "application.properties"

        internal fun readPropertyFiles(inputFiles: List<File>): MutableMap<String, MutableMap<String, String>> {
            val allProps: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

            for (inputFile in inputFiles) {
                val propsForEnv = readPropsFromFile(inputFile)
                allProps[inputFile.name] = propsForEnv
            }
            allProps.putIfAbsent(BASE_PROPERTIES_FILE_NAME, mutableMapOf())
            return allProps
        }

        /**
         * Do smsms
         * @param propsForEnvs a map of properties maps, one for each environment plus the base
         */
        internal fun processProperties(
            propsForEnvs: MutableMap<String, MutableMap<String, String>>
        ): MutableMap<String, MutableMap<String, String>> { // read in all properties files in src dir

            // first get the application.properties out of the map, then remove it from the map
            val baseProperties = propsForEnvs.getValue(BASE_PROPERTIES_FILE_NAME)
            propsForEnvs.remove(BASE_PROPERTIES_FILE_NAME)


            // Now, get all the properties that are common to all environments..
            val commonProperties = getCommonProperties(propsForEnvs)

            // Make a collection to hold the new base properties files - just copy it 
            //  anything already in application.properties stays there - any redundant stuff is removed later
            val newApplicationProperties = HashMap(baseProperties)

            // now add any common properties we found in the other files
            newApplicationProperties.putAll(commonProperties)

            val commonKeys = commonProperties.keys
            val finalPropsForEnvs: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

            filterEnvs(propsForEnvs, commonKeys, finalPropsForEnvs)

            // remove redundant key/values in application.properties which are overridden in every environment,
            removeRedundantProperties(
                newApplicationProperties,
                finalPropsForEnvs
            )

            // put the new application.properties back into the map
            finalPropsForEnvs[BASE_PROPERTIES_FILE_NAME] = newApplicationProperties

            return finalPropsForEnvs
        }

        /**
         *  remove redundant key/values in application.properties which are overridden in every environment,
         */
        private fun removeRedundantProperties(
            applicationProperties: MutableMap<String, String>,
            propsForEnvs: MutableMap<String, MutableMap<String, String>>
        ) {
            val commonKeys = getCommonKeys(propsForEnvs)
            // go through the common keys, see if the values differ for all envs. If so, remove from application.properties
            commonKeys.forEach { key ->
                val keyValues = propsForEnvs.keys
                    .map { propsForEnvs.getValue(it) }
                    .map { it.getValue(key) }
                //  if the set is the same size as the list, then all values are different
                if (keyValues.size == keyValues.toSet().size) {
                    val containsKey = applicationProperties.containsKey(key)
                    applicationProperties.remove(key) //remove the redundant key 
                }
            }
        }


        // go through all of the properties files - if all of them have the same key with the same values, add it to the list.
        // Because we've remove application.properties from the list, we don't have to worry about that file being here now
        fun getCommonProperties(propsForEnvs: Map<String, Map<String, String>>): Map<String, String> {
            val commonProps: MutableMap<String, String> = mutableMapOf()

            // grab the keys of the first entry (could be any)
            val firstEnvMap: Map<String, String> = propsForEnvs.entries.first().value

            val envs = propsForEnvs.keys
            val possibleCommonKeys = firstEnvMap.keys

            possibleCommonKeys.forEach {
                checkForCommonness(
                    firstEnvMap,
                    it,
                    envs,
                    propsForEnvs,
                    commonProps
                )
            }
            return commonProps
        }

        // go through all of the properties files - if all of them have the same key, and make a list
        fun getCommonKeys(propsForEnvs: Map<String, Map<String, String>>): Set<String> {
            val commonProps: MutableMap<String, String> = mutableMapOf()

            // grab the keys of the first entry (could be any)
            val firstEnvMap: Map<String, String> = propsForEnvs.entries.first().value
            val possibleCommonKeys = firstEnvMap.keys
            val envs = propsForEnvs.keys
            val commonKeys: MutableSet<String> = mutableSetOf()

            possibleCommonKeys.forEach { key ->
                // if keys exists in all envs, true
                val existsInAll = envs.all { propsForEnvs.getValue(it).containsKey(key) }
                if (existsInAll) commonKeys.add(key)
            }

            return commonKeys
        }

        private fun checkForCommonness(
            firstEnvMap: Map<String, String>,
            possibleCommonKey: String,
            envs: Set<String>,
            propsForEnvs: Map<String, Map<String, String>>,
            commonProps: MutableMap<String, String>
        ) {
            val possibleCommonValue = firstEnvMap[possibleCommonKey]
            val isCommon = isKeyCommonAcrossAllEnvironments(
                envs,
                propsForEnvs,
                possibleCommonKey,
                possibleCommonValue
            )

            //go through each env and see if it contains it
            if (isCommon && possibleCommonValue != null) {
                if (isSecret(possibleCommonKey)) {
                    println("Leaving key $possibleCommonKey in env props, as it's a secret")
                } else {
                    commonProps[possibleCommonKey] = possibleCommonValue
                }
            }
        }

        fun isKeyCommonAcrossAllEnvironments(
            envs: Set<String>,
            propsForEnvs: Map<String, Map<String, String>>,
            possibleCommonKey: String,
            possibleCommonValue: String?
        ): Boolean {

            for (env in envs) {
                val propsForEnv = propsForEnvs.getValue(env)
                if (!propsForEnv.contains(possibleCommonKey)) return false
                val value = propsForEnv.getValue(possibleCommonKey)
                val isCommon = value == possibleCommonValue
                if (!isCommon) return false
            }
            return true
        }

        /**   filter so anything with "password" or "secret" is skipped */
        fun isSecret(key: String): Boolean {
            val lowerCaseKey = key.toLowerCase()
            return when {
                lowerCaseKey.contains("access") && lowerCaseKey.contains("key") -> true
                lowerCaseKey.contains("secret") -> true
                lowerCaseKey.contains("password") -> true
                lowerCaseKey.contains("headervalue") -> true
                // todo add configurable extras, like "token" or "header"
                else -> false
            }
        }

        /** Write the properties files */
        fun writePropertiesFiles(
            propsForEnvs: Map<String, Map<String, String>>,
            parentDir: File
        ) {
            propsForEnvs.forEach { (env: String, propsForEnv: Map<String, String>) ->
                try {
                    val newFileName = createNewFileName(env)
                    val file = File(parentDir, newFileName)
                    val props = alignPropertyColumns(propsForEnv)
                    FileUtils.writeLines(file, props)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }

        private fun createNewFileName(env: String): String {
            val name = env.substringBefore(".")
            val extension = env.substringAfter(".")
            val newFileName = "${name}_new.$extension"

            return newFileName
        }

        fun removeOldFiles(
            parentDir: File,
            propsForEnvs: Map<String, Map<String, String>>
        ) {
            propsForEnvs
                .map { it.key }
                .map { createNewFileName(it) }
                .map { File(parentDir, it) }
                .forEach { it.delete() }
        }

        /** Line up the "=" and values so they're pretty */
        private fun alignPropertyColumns(propsForEnv: Map<String, String>): List<String> {
            val maxLength = propsForEnv.keys
                .map { it.length }
                .max() ?: 0
            val lines = propsForEnv.entries
                .map { "${it.key}${StringUtils.leftPad(" ", maxLength - it.key.length + 1)} = ${it.value}" }
                .sorted()
            return lines
        }

        /** Remove any common elements from the environment maps */
        private fun filterEnvs(
            propsForEnvs: Map<String, Map<String, String>>,
            commonKeys: Set<String>,
            finalPropsForEnvs: MutableMap<String, MutableMap<String, String>>
        ) {

            propsForEnvs.entries
                .forEach { entry ->
                    val env = entry.key
                    val props = entry.value
                    val filteredProps: MutableMap<String, String> = mutableMapOf()
                    props.entries
                        .filter { !commonKeys.contains(it.key) }
                        .forEach { filteredProps[it.key] = it.value }
                    finalPropsForEnvs[env] = filteredProps
                }
        }

        /**
         * Read in the appropriate file and break it into key/values
         */
        private fun readPropsFromFile(propertyFile: File): MutableMap<String, String> {

            return FileUtils.readLines(propertyFile)
                .asSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .filter { !it.startsWith("#") }
                .filter { !it.startsWith("//") }
                .map { it.split(Regex("="), 2) }
                .map { it[0].trim() to it[1].trim() }
                .toList()
                .toMap()
                .toMutableMap()
        }
    }
}



