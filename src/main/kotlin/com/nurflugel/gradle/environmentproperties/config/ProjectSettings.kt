package com.nurflugel.gradle.environmentproperties.config

class ProjectSettings {


    companion object {

//        fun isPluginEnabledInProject(properties: PropertiesComponent): Boolean = isEnabled(properties, PLUGIN_ENABLED_IN_PROJECT)
//        fun setPluginEnabledInProject(properties: PropertiesComponent, value: Boolean) = setEnabled(properties, PLUGIN_ENABLED_IN_PROJECT, value)

//        fun isTopLevelListsAtBottom(properties: PropertiesComponent): Boolean =
//            isEnabled(properties, TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED)
//        fun isFlattenKeys(properties: PropertiesComponent): Boolean = isEnabled(properties, FLATTEN_KEYS_ENABLED)
//        fun isFlattenKeys(project: Project): Boolean = isEnabled(project, FLATTEN_KEYS_ENABLED)
//            fun setFlattenKeys(project: Project, value: Boolean) = setEnabled(project, FLATTEN_KEYS_ENABLED, value)
//        fun setFlattenKeys(properties: PropertiesComponent, value: Boolean) = setEnabled(properties, FLATTEN_KEYS_ENABLED, value)
//
//        fun putTopLevelListsAtBottom(properties: PropertiesComponent): Boolean =
//            isEnabled(properties, TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED)
//
//        fun putTopLevelListsAtBottom(project: Project): Boolean = isEnabled(project, TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED)
//            fun setTopLevelListsAtBottom(project: Project, value: Boolean) = setEnabled(project, TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED, value)
//        fun setTopLevelListsAtBottom(properties: PropertiesComponent, value: Boolean) = setEnabled(properties, TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED, value)
//
//        private fun isEnabled(project: Project, propertyName: String): Boolean = isEnabled(PropertiesComponent.getInstance(project), propertyName)
//            private fun isEnabled(project: Project, propertyName: String, defaultValue: Boolean): Boolean = isEnabled(PropertiesComponent.getInstance(project), propertyName, defaultValue)
//        private fun setEnabled(project: Project, propertyName: String, value: Boolean) = setEnabled(PropertiesComponent.getInstance(project), propertyName, value)
//
//        private fun isEnabled(properties: PropertiesComponent, propertyName: String, defaultValue: Boolean = true): Boolean = properties.getBoolean(propertyName, defaultValue)
//        private fun setEnabled(properties: PropertiesComponent, propertyName: String, value: Boolean) = properties.setValue(propertyName, value.toString(), "")

        private const val PREFIX = "Gradle_environment_properties_files_"
        private const val PLUGIN_ENABLED_IN_PROJECT = PREFIX + "EnabledInProject"
        const val EXTRA_SECRETS_LIST = PREFIX + "EXTRA_SECRETS_LIST"
//        const val TOP_LEVEL_LISTS_AT_BOTTOM_ENABLED = PREFIX + "TOP_LEVEL_LISTS_AT_BOTTOM_Enabled"
    }
}
