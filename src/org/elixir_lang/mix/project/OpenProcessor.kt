package org.elixir_lang.mix.project

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectImportBuilder
import com.intellij.projectImport.ProjectOpenProcessorBase
import org.elixir_lang.mix.project._import.Builder

/**
 * Created by zyuyou on 15/7/1.
 */
class OpenProcessor : ProjectOpenProcessorBase<Builder>() {
    override fun getSupportedExtensions(): Array<String> = arrayOf("mix.exs")

    override fun doGetBuilder(): Builder = ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(Builder::class.java)

    override fun doQuickImport(exsFile: VirtualFile, wizardContext: WizardContext): Boolean {
        val projectRoot = exsFile.parent
        wizardContext.projectName = projectRoot.name
        builder.setProjectRoot(projectRoot)
        return true
    }
}
