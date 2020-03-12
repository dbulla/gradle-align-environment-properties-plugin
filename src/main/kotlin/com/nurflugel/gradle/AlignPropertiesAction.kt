package com.nurflugel.gradle


import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.vfs.VirtualFile
import com.nurflugel.gradle.FileUtil.Companion.processProperties
import com.nurflugel.gradle.FileUtil.Companion.readPropertyFiles
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.io.IOException


class AlignPropertiesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val data: Array<VirtualFile>? = dataContext.getData(PlatformDataKeys.VIRTUAL_FILE_ARRAY)!!
        val inputFiles = data!!.asSequence()
            .map { it.canonicalFile }
            .map { it!!.path }
            .map { File(it) }
            .toList()

        val propsForEnvs = readPropertyFiles(inputFiles)
        val parentDir = inputFiles.first().parentFile
        processProperties(propsForEnvs, parentDir)
    }
}

class FileUtil {
    companion object {
        private const val BASE_PROPERTIES_FILE_NAME = "application.properties"

       internal fun readPropertyFiles(inputFiles: List<File>): MutableMap<String, Map<String, String>> {
            val allProps: MutableMap<String, Map<String, String>> = mutableMapOf()

            for (inputFile in inputFiles) {
                val propsForEnv = readPropsFromFile(inputFile)
                allProps[inputFile.name] = propsForEnv
            }
            allProps.putIfAbsent(BASE_PROPERTIES_FILE_NAME, mutableMapOf())
            return allProps
        }

        internal fun processProperties(
            propsForEnvs: MutableMap<String, Map<String, String>>,
            parentDir: File
        ) { // read in all properties files in src dir

            // first get the application.properties out of the map, then remove it from the map
            val baseProperties = propsForEnvs.getValue(BASE_PROPERTIES_FILE_NAME)
            propsForEnvs.remove(BASE_PROPERTIES_FILE_NAME)

            val commonProperties = getCommonProperties(propsForEnvs)
            val newApplicationProperties: MutableMap<String, String> = mutableMapOf()

            //  anything already in application.properties stays there
            newApplicationProperties.putAll(baseProperties.toMutableMap())

            // now add any common properties we found in the other files
            newApplicationProperties.putAll(commonProperties)

            val commonKeys = commonProperties.keys
            val finalPropsForEnvs: MutableMap<String, MutableMap<String, String>> = mutableMapOf()

            // put the new application.properties back into the map
            finalPropsForEnvs[BASE_PROPERTIES_FILE_NAME] = newApplicationProperties

            filterEnvs(propsForEnvs, newApplicationProperties, commonKeys, finalPropsForEnvs)

            writePropertiesFiles(finalPropsForEnvs,parentDir)
        }


        // go through all of the properties files - if all of them have the same key with the same values, add it to the list.
        // Because we've remove application.properties from the list, we don't have to worry about that file being here now
        private fun getCommonProperties(propsForEnvs: Map<String, Map<String, String>>): Map<String, String> {
            val commonProps: MutableMap<String, String> = mutableMapOf()

            // grab the keys of the first entry
            val firstEnvMap: Map<String, String> = propsForEnvs.entries.first().value

            val envs = propsForEnvs.keys
            val possibleCommonKeys = firstEnvMap.keys

            for (possibleCommonKey in possibleCommonKeys) {

                val possibleCommonValue = firstEnvMap[possibleCommonKey]
                var isCommon = true
                for (env in envs) {
                    val propsForEnv: Map<String, String> = propsForEnvs.getValue(env)
                    if (propsForEnv.containsKey(possibleCommonKey)) {
                        val valueForEnv = propsForEnv[possibleCommonKey]
                        if (possibleCommonValue != valueForEnv) {
                            isCommon = false
                            break
                        }
                    }
                }
                //go through each env and see if it contains it
                if (isCommon && possibleCommonValue != null) {
                    if (isSecret(possibleCommonKey)) {
                        println("Leaving key $possibleCommonKey in env props, as it's a secret")
                    } else {
                        commonProps[possibleCommonKey] = possibleCommonValue
                    }
                }
            }
            return commonProps
        }

        /**   filter so anything with "password" or "secret" is skipped */
        private fun isSecret(key: String): Boolean {
            return when {
                key.toLowerCase().contains("access") && key.toLowerCase().contains("key") -> true
                key.toLowerCase().contains("secret") -> true
                key.toLowerCase().contains("password") -> true
                // todo add configurable extras, like "token" or "header"
                else -> false
            }
        }

        /** Write the properties files */
        private fun writePropertiesFiles(
            propsForEnvs: Map<String, Map<String, String>>,
            parentDir: File
        ) {
            removeOldFiles(parentDir, propsForEnvs)
            propsForEnvs.forEach { (env: String, propsForEnv: Map<String, String>) ->
                try {
                    val name = env.substringBefore(".")
                    val extension = env.substringAfter(".")
                    val file = File(parentDir, "${name}_new.$extension")
                    val props = alignPropertyColumns(propsForEnv)
                    FileUtils.writeLines(file, props)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }

        private fun removeOldFiles(
            parentDir: File,
            propsForEnvs: Map<String, Map<String, String>>
        ) {
//            getPropertyFiles(parentDir, propsForEnvs).forEach { it.delete() }
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
            applicationProperties: Map<String, String>,
            commonKeys: Set<String>,
            finalPropsForEnvs: MutableMap<String, MutableMap<String, String>>
        ) {
            finalPropsForEnvs[BASE_PROPERTIES_FILE_NAME] = applicationProperties.toMutableMap()

            propsForEnvs.entries
                .filter { it.key != BASE_PROPERTIES_FILE_NAME }
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
        private fun readPropsFromFile(pathname: File): Map<String, String> {
            return FileUtils.readLines(pathname)
                .asSequence()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .filter { !it.startsWith("#") }
                .filter { !it.startsWith("//") }
                .map { it.split(Regex("="), 2) }
                .map { it[0].trim() to it[1].trim() }
                .toMap()
        }
    }
}



