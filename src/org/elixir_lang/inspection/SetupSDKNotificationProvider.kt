package org.elixir_lang.inspection

import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
import com.intellij.openapi.roots.ui.configuration.SdkPopupFactory
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.elixir.Type

/**
 * https://github.com/ignatov/intellij-erlang/blob/master/src/org/intellij/erlang/inspection/SetupSDKNotificationProvider.java
 */
class SetupSDKNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {
    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(virtualFile: VirtualFile, fileEditor: FileEditor, project: Project): EditorNotificationPanel? =
            if (virtualFile.fileType is ElixirFileType) {
                PsiManager
                        .getInstance(project)
                        .findFile(virtualFile)
                        ?.let { psiFile ->
                            if (psiFile.language === ElixirLanguage &&
                                    Type.mostSpecificSdk(psiFile) == null) {
                                createPanel(project, psiFile)
                            } else {
                                null
                            }
                        }
            } else {
                null
            }

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("Setup Elixir SDK")

        fun showFacetSettings(project: Project) {
            if (ProcessOutput.isSmallIde()) {
                showSmallIDEFacetSettings(project)
            }
            // TODO Elixir Facet in non-Elixir module in IntelliJ
        }

        fun showModuleSettings(project: Project, module: Module) {
            ProjectSettingsService.getInstance(project).openModuleSettings(module)
        }

        fun showProjectSettings(project: Project) {
            SdkPopupFactory
                    .newBuilder()
                    .withProject(project)
                    .withSdkType(Type.getInstance())
                    .updateProjectSdkFromSelection()
                    .buildPopup()
                    .showInFocusCenter()
        }

        fun showSmallIDEFacetSettings(project: Project) {
            ShowSettingsUtilImpl.showSettingsDialog(project, "language", "Elixir")
        }

        private fun createSmallIDEFacetPanel(project: Project): EditorNotificationPanel {
            return EditorNotificationPanel().apply {
                text = "Elixir Facet SDK is not defined"
                createActionLabel("Setup Elixir Facet SDK") {
                    showSmallIDEFacetSettings(project)
                }
            }
        }

        private fun createFacetPanel(project: Project): EditorNotificationPanel? {
            return if (ProcessOutput.isSmallIde()) {
                createSmallIDEFacetPanel(project)
            } else {
                // TODO Elixir Facet in non-Elixir module in IntelliJ
                null
            }
        }

        private fun createModulePanel(project: Project, module: Module): EditorNotificationPanel {
            return EditorNotificationPanel().apply {
                text = "Elixir Module SDK is not defined"
                createActionLabel("Setup Elixir Module SDK") {
                    showModuleSettings(project, module)
                }
            }
        }

        private fun createPanel(project: Project, psiFile: PsiFile): EditorNotificationPanel? {
            val module = ModuleUtilCore.findModuleForPsiElement(psiFile)

            return if (module != null) {
                if (module.getOptionValue(Module.ELEMENT_TYPE) == "ELIXIR_MODULE") {
                    createModulePanel(project, module)
                } else {
                    createFacetPanel(project)
                }
            } else {
                if (ProcessOutput.isSmallIde()) {
                    createSmallIDEFacetPanel(project)
                } else {
                    createProjectPanel(project)
                }
            }
        }

        private fun createProjectPanel(project: Project): EditorNotificationPanel {
            return EditorNotificationPanel().apply {
                text = ProjectBundle.message("project.sdk.not.defined")
                createActionLabel(ProjectBundle.message("project.sdk.setup")) {
                    showProjectSettings(project)
                }
            }

        }
    }
}
