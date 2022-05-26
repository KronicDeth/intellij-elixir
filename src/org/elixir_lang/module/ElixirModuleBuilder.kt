package org.elixir_lang.module

import com.intellij.compiler.CompilerWorkspaceConfiguration
import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.SdkTypeId
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Pair
import org.elixir_lang.Icons
import org.elixir_lang.sdk.elixir.Type.Companion.instance
import javax.swing.Icon

/**
 * Created by zyuyou on 2015/5/26.
 *
 */
class ElixirModuleBuilder : JavaModuleBuilder(), ModuleBuilderListener {
    @kotlin.jvm.Throws(ConfigurationException::class)
    override fun setupRootModel(rootModel: ModifiableRootModel) {
        addListener(this)
        super.setupRootModel(rootModel)
        val contentEntry = rootModel.contentEntries.single()
        val projectDirectory = contentEntry.file!!
        contentEntry.addSourceFolder(projectDirectory.findChild("test")!!, true)
        contentEntry.addExcludeFolder("${projectDirectory.url}/deps")
    }

    override fun getModuleType(): ModuleType<*> = ElixirModuleType.getInstance()
    override fun isSuitableSdkType(sdkType: SdkTypeId): Boolean = sdkType === instance
    override fun getNodeIcon(): Icon = Icons.LANGUAGE

    override fun moduleCreated(module: Module) {
        CompilerWorkspaceConfiguration.getInstance(module.project).CLEAR_OUTPUT_DIRECTORY = false
    }

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep {
        return object : SdkSettingsStep(settingsStep, this, Condition { sdkTypeId -> isSuitableSdkType(sdkTypeId) }) {
            override fun updateDataModel() {
                super.updateDataModel()
                val path = contentEntryPath
                if (path != null) {
                    sourcePaths = listOf(
                        Pair.create(
                            "$path/lib", ""
                        )
                    )
                }
            }
        }
    }
}
