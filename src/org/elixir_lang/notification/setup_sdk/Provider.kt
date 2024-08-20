package org.elixir_lang.notification.setup_sdk

import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    ): Function<in FileEditor, out JComponent?> =
        Function {
            kotlinx.coroutines.runBlocking {
                createNotificationPanel(file, project)
            }
        }

    private suspend fun createNotificationPanel(
        virtualFile: VirtualFile,
        project: Project,
    ): EditorNotificationPanel? =
        withContext(Dispatchers.Default) {
            ReadAction.compute<EditorNotificationPanel?, Throwable> {
                if (virtualFile.fileType is ElixirFileType) {
                    PsiManager
                        .getInstance(project)
                        .findFile(virtualFile)
                        ?.let { psiFile ->
                            if (psiFile.language === ElixirLanguage &&
                                Type.mostSpecificSdk(psiFile) == null
                            ) {
                                createPanel(project, psiFile)
                            } else {
                                null
                            }
                        }
                } else {
                    null
                }
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

private fun createFacetPanel(project: Project): EditorNotificationPanel? =
    if (ProcessOutput.isSmallIde) {
        createSmallIDEFacetPanel(project)
    } else {
        // TODO Elixir Facet in non-Elixir module in IntelliJ
        null
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

private fun createPanel(
    project: Project,
    psiFile: PsiFile,
): EditorNotificationPanel? {
    val module = ModuleUtilCore.findModuleForPsiElement(psiFile)

    return when {
        module != null -> {
            // CANNOT use ModuleType.is(module, ElixirModuleType.getInstance()) as ElixirModuleType depends on
            // JavaModuleBuilder and so only available in IntelliJ
            if (ModuleType.get(module).id == "ELIXIR_MODULE") {
                createModulePanel(project, module)
            } else {
                createFacetPanel(project)
            }
        }
        ProcessOutput.isSmallIde -> createSmallIDEFacetPanel(project)
        else -> createProjectPanel(project)
    }
}

private fun createProjectPanel(project: Project): EditorNotificationPanel =
    EditorNotificationPanel().apply {
        text = "Project SDK is not defined"
        createActionLabel(ProjectBundle.message("project.sdk.setup")) {
            showProjectSettings(project)
        }
    }