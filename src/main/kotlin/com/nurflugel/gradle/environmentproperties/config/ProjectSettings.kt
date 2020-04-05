package com.nurflugel.gradle.environmentproperties.config

import com.intellij.ide.util.PropertiesComponent

class ProjectSettings {

    companion object {

        fun isPluginEnabledInProject(properties: PropertiesComponent): Boolean =
            isEnabled(properties, PLUGIN_ENABLED_IN_PROJECT)

        fun setPluginEnabledInProject(properties: PropertiesComponent, value: Boolean) =
            setEnabled(properties, PLUGIN_ENABLED_IN_PROJECT, value)

        /** Provide some sensible defaults */
        fun getSecretsKeyWords(properties: PropertiesComponent): String {
            val text = getText(properties, SECRETS_KEY_WORDS)
            // ensure from fresh install we have the minimum
            return when {
                text.isEmpty() -> {
                    println("Keywords were empty, using defaults")
                    val defaultValues = """
                                 secret
                                 password
                                 access && key
                                 headervalue
                             """.trimIndent()
                    setSecretsKeyWords(properties, defaultValues)
                    defaultValues
                }
                else -> text
            }
        }

        fun setSecretsKeyWords(properties: PropertiesComponent, value: String) {
            setText(properties, SECRETS_KEY_WORDS, value)
        }

        //
        private fun isEnabled(
            properties: PropertiesComponent,
            propertyName: String,
            defaultValue: Boolean = true
        ): Boolean = properties.getBoolean(propertyName, defaultValue)

        private fun setEnabled(properties: PropertiesComponent, propertyName: String, value: Boolean) =
            properties.setValue(propertyName, value.toString(), "")

        private fun getText(properties: PropertiesComponent, propertyName: String, defaultValue: String = ""): String =
            properties.getValue(propertyName, defaultValue)

        private fun setText(properties: PropertiesComponent, propertyName: String, value: String) =
            properties.setValue(propertyName, value, "")

        private const val PREFIX = "Gradle_environment_properties_files_"
        private const val PLUGIN_ENABLED_IN_PROJECT = PREFIX + "EnabledInProject"
        private const val SECRETS_KEY_WORDS = PREFIX + "EXTRA_SECRETS_LIST"
    }
}
