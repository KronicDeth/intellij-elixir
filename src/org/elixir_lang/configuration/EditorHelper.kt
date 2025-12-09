package org.elixir_lang.configuration

import com.intellij.application.options.ModulesComboBox
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleTypeManager
import com.intellij.openapi.project.Project
import java.util.function.Consumer

object EditorHelper {
    @JvmStatic
    fun reset(
        project: Project,
        module: Module?,
        modulesComboBox: ModulesComboBox,
        runInModuleSetter: Consumer<Boolean>,
        parametersPanelResetter: Runnable
    ) {
        // 1. Safe Module Filling (WebStorm safe)
        // We look up the type dynamically. If null (WebStorm), we pass null.
        val elixirModuleType = ModuleTypeManager.getInstance().findByID("ELIXIR_MODULE")
        modulesComboBox.fillModules(project, elixirModuleType)

        // 2. centralized Module Selection Logic
        if (module != null) {
            runInModuleSetter.accept(true)
            modulesComboBox.selectedModule = module
        } else {
            runInModuleSetter.accept(false)
        }

        // 3. Reset the specific parameters panel
        parametersPanelResetter.run()
    }
}
