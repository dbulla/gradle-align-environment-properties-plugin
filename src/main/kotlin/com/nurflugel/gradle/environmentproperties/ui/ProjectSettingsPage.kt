package com.nurflugel.gradle.environmentproperties.ui

//import org.jetbrains.annotations.Nls.Capitalization.Title
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import java.awt.event.ActionEvent
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextArea

class ProjectSettingsPage(private val propertiesComponent: PropertiesComponent) : SearchableConfigurable, Configurable.NoScroll {
    // these need to be vars so the Intellij GUI binder can handle them
    private var addExtraSecretKeywordsCheckbox: JCheckBox? = null
    private var enablePluginInProjectCheckBox: JCheckBox? = null
    private var extraKeywordsTextArea: JTextArea? = null
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
        addExtraSecretKeywordsCheckbox!!.addActionListener { event ->
            configureCheckbox(event, addExtraSecretKeywordsCheckbox)
            extraKeywordsTextArea!!.isVisible = addExtraSecretKeywordsCheckbox!!.isSelected
        }
        return containingPanel
    }

    private fun configureCheckbox(actionEvent: ActionEvent, checkbox: JCheckBox?) {
        val checkBox = actionEvent.source as JCheckBox
        val selected = checkBox.model.isSelected
        checkbox!!.isSelected = selected
    }

    private fun initFromSettings() {
        println("initFromSettings")
//        enablePluginInProjectCheckBox!!.isSelected = isPluginEnabledInProject(propertiesComponent)
//        addExtraSecretKeywordsCheckbox!!.isSelected = isFlattenKeys(propertiesComponent)
//        putTopLevelListsAtBottomCheckbox!!.isSelected = putTopLevelListsAtBottom(propertiesComponent)
    }

    override fun isModified(): Boolean {
        println("isModified")
//        val enabledChanged = enablePluginInProjectCheckBox!!.isSelected != isPluginEnabledInProject(propertiesComponent)
//        val flattenKeysChanged = addExtraSecretKeywordsCheckbox!!.isSelected != isFlattenKeys(propertiesComponent)
//        val listsAtBottomChanged =
//            putTopLevelListsAtBottomCheckbox!!.isSelected != isTopLevelListsAtBottom(propertiesComponent)
//        return enabledChanged || flattenKeysChanged || listsAtBottomChanged
return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        println("Applying settings")
//        setPluginEnabledInProject(propertiesComponent, enablePluginInProjectCheckBox!!.isSelected)
//        setFlattenKeys(propertiesComponent, addExtraSecretKeywordsCheckbox!!.isSelected)
//        setTopLevelListsAtBottom(propertiesComponent, addExtraSecretKeywordsCheckbox!!.isSelected)
    }

    override fun reset() {
        initFromSettings()
    }

}
