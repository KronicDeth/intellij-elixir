package org.elixir_lang.notification.setup_sdk

import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.elixir.Type
import java.util.function.Function
import javax.swing.JComponent

class Provider : EditorNotificationProvider {
    override fun collectNotificationData(
        project: Project,
        file: VirtualFile,
    ): Function<in FileEditor, out JComponent?>? {
        // Quick check without read action - file type check is thread-safe
        if (file.fileType !is ElixirFileType) {
            return null
        }

        return Function {
            createNotificationPanel(file, project)
        }
    }

    private fun createNotificationPanel(
        virtualFile: VirtualFile,
        project: Project,
    ): EditorNotificationPanel? =
        ReadAction.compute<EditorNotificationPanel?, Throwable> {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)

            if (psiFile == null ||
                psiFile.language !== ElixirLanguage ||
                Type.mostSpecificSdk(psiFile) != null
            ) {
                return@compute null
            }

            // Avoid slow ModuleUtilCore.findModuleForPsiElement call
            // Instead, check if there are any Elixir modules in the project
            val elixirModule = ModuleManager.getInstance(project).modules
                .find { ModuleType.get(it).id == "ELIXIR_MODULE" }

            when {
                elixirModule != null -> createModulePanel(project, elixirModule)
                ProcessOutput.isSmallIde -> createSmallIDEFacetPanel(project)
                else -> createProjectPanel(project)
            }
        }

}

fun showFacetSettings(project: Project) {
    if (ProcessOutput.isSmallIde) {
        showSmallIDEFacetSettings(project)
    }
    // TODO Elixir Facet in non-Elixir module in IntelliJ
}

fun showModuleSettings(
    project: Project,
    module: Module,
) {
    ProjectSettingsService.getInstance(project).openModuleSettings(module)
}

private fun showProjectSettings(project: Project) {
    SdkPopupFactory.newBuilder()
        .withProject(project)
        .withSdkType(Type.instance)
        .updateProjectSdkFromSelection()
        .buildPopup()
        .showInFocusCenter()
}

private fun showSmallIDEFacetSettings(project: Project) {
    ShowSettingsUtilImpl.showSettingsDialog(project, "language", "Elixir")
}

private fun createSmallIDEFacetPanel(project: Project): EditorNotificationPanel =
    EditorNotificationPanel().apply {
        text = "Elixir Facet SDK is not defined"
        @Suppress("DialogTitleCapitalization")
        createActionLabel("Setup Elixir Facet SDK") {
            showSmallIDEFacetSettings(project)
        }
    }

private fun createModulePanel(
    project: Project,
    module: Module,
): EditorNotificationPanel =
    EditorNotificationPanel().apply {
        text = "Elixir Module SDK is not defined"
        @Suppress("DialogTitleCapitalization")
        createActionLabel("Setup Elixir Module SDK") {
            showModuleSettings(project, module)
        }
    }

private fun createProjectPanel(project: Project): EditorNotificationPanel =
    EditorNotificationPanel().apply {
        text = "Project SDK is not defined"
        createActionLabel(ProjectBundle.message("project.sdk.setup")) {
            showProjectSettings(project)
        }
    }