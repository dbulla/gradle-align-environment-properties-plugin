package com.nurflugel.gradle.environmentproperties.ui

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.getSecretsKeyWords
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.isPluginEnabledInProject
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.setPluginEnabledInProject
import com.nurflugel.gradle.environmentproperties.config.ProjectSettings.Companion.setSecretsKeyWords
import java.awt.event.ActionEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class ProjectSettingsPage(private val propertiesComponent: PropertiesComponent) : SearchableConfigurable, Configurable.NoScroll {
    // these need to be vars so the Intellij GUI binder can handle them
    private var enablePluginInProjectCheckBox: JCheckBox? = null
    private var keywordsTextArea: JTextArea? = null
    private var containingPanel: JPanel? = null

    override fun getId(): String {
        return "Align Environment Properties Plugin"
    }

    //    @Nls(capitalization = Title)
    override fun getDisplayName(): String? {
        return null
    }

    override fun createComponent(): JComponent? {
        initFromSettings()
        return containingPanel
    }

    private fun configureCheckbox(actionEvent: ActionEvent, checkbox: JCheckBox?) {
        val checkBox = actionEvent.source as JCheckBox
        val selected = checkBox.model.isSelected
        checkbox !!.isSelected = selected
    }

    private fun initFromSettings() {
        println("initFromSettings")
        enablePluginInProjectCheckBox !!.isSelected = isPluginEnabledInProject(propertiesComponent)
        keywordsTextArea !!.text = getSecretsKeyWords(propertiesComponent)
    }

    override fun isModified(): Boolean {
        println("isModified")
        val enabledChanged = enablePluginInProjectCheckBox !!.isSelected != isPluginEnabledInProject(propertiesComponent)
        val secretsChanged = keywordsTextArea !!.text != getSecretsKeyWords(propertiesComponent)
        return enabledChanged || secretsChanged
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        println("Applying settings")
        setPluginEnabledInProject(propertiesComponent, enablePluginInProjectCheckBox !!.isSelected)
        setSecretsKeyWords(propertiesComponent, keywordsTextArea !!.text)
    }

    override fun reset() {
        initFromSettings()
    }

}
