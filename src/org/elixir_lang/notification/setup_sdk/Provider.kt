package org.elixir_lang.notification.setup_sdk

import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleUtilCore
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
        // Quick check without additional read - file type check is thread-safe.
        if (file.fileType !is ElixirFileType) return null

        // collectNotificationData is always called under a platform-held read lock
        // (@RequiresReadLock, see EditorNotificationProvider), so PSI/model access here is safe.
        val psiFile = PsiManager.getInstance(project).findFile(file) ?: return null
        if (psiFile.language !== ElixirLanguage) return null

        val module = ModuleUtilCore.findModuleForPsiElement(psiFile)

        val sdk = module?.let { Type.mostSpecificSdk(it) } ?: Type.mostSpecificSdk(project)
        if (sdk != null) return null

        // No SDK configured. Mise-based suggestion is handled by the status bar widget's
        // notification scan (ElixirEditorBasedSdkWidget), which runs off the read lock on
        // Dispatchers.IO and surfaces a balloon notification with a one-click configure action.
        // This panel remains as a persistent per-file indicator for manual setup, but is a candidate
        // for removal in the future if the Experimental Widget becomes stable.
        return when {
            module != null && ModuleType.get(module).id == "ELIXIR_MODULE" ->
                Function { createModulePanel(project, module) }
            module != null && ProcessOutput.isSmallIde ->
                Function { createSmallIDEFacetPanel(project) }
            module != null ->
                Function { createProjectPanel(project) }
            else -> null
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
        text = "Elixir SDK is not defined"
        createActionLabel(ProjectBundle.message("project.sdk.setup")) {
            showProjectSettings(project)
        }
    }
