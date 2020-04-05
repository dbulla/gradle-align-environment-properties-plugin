package com.nurflugel.gradle.environmentproperties.ui

import com.intellij.ide.util.AppPropertiesComponentImpl
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.getSecretsKeyWords
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.isPluginEnabledInProject
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.setPluginEnabledInProject
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.setSecretsKeyWords
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class ProjectSettingsPage(private val propertiesComponent: PropertiesComponent) : SearchableConfigurable, Configurable.NoScroll {
    // these need to be vars so the Intellij GUI binder can handle them
    private var enablePluginInProjectCheckBox: JCheckBox? = null
    private var keywordsTextArea: JTextArea? = null
    private var containingPanel: JPanel? = null
    private val appPropertiesComponent = AppPropertiesComponentImpl.getInstance()// consistent for all projects

    override fun getId(): String {
        return "Align Environment Properties Plugin"
    }

    override fun getDisplayName(): String? {
        return null
    }

    override fun createComponent(): JComponent? {
        initFromSettings()
        return containingPanel
    }

    private fun initFromSettings() {
        enablePluginInProjectCheckBox!!.isSelected = isPluginEnabledInProject(appPropertiesComponent)
        keywordsTextArea!!.text = getSecretsKeyWords(appPropertiesComponent)
    }

    override fun isModified(): Boolean {
        val enabledChanged =
            enablePluginInProjectCheckBox!!.isSelected != isPluginEnabledInProject(appPropertiesComponent)
        val secretsChanged = keywordsTextArea!!.text != getSecretsKeyWords(appPropertiesComponent)
        return enabledChanged || secretsChanged
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        setPluginEnabledInProject(appPropertiesComponent, enablePluginInProjectCheckBox!!.isSelected)
        setSecretsKeyWords(appPropertiesComponent, keywordsTextArea!!.text)
    }

    override fun reset() {
        initFromSettings()
    }

}
