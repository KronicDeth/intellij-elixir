package org.elixir_lang.inspection

import com.intellij.ProjectTopics
import com.intellij.facet.Facet
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetManagerAdapter
import com.intellij.facet.FacetManagerListener
import com.intellij.ide.actions.ShowSettingsUtilImpl
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.roots.ModuleRootAdapter
import com.intellij.openapi.roots.ModuleRootEvent
import com.intellij.openapi.roots.ModuleRootListener
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
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
class SetupSDKNotificationProvider(private val project: Project,
                                   notifications: EditorNotifications):
        EditorNotifications.Provider<EditorNotificationPanel>() {
    init {
        val connection = project.messageBus.connect(project)

        connection.subscribe<ModuleRootListener>(ProjectTopics.PROJECT_ROOTS, object : ModuleRootAdapter() {
            override fun rootsChanged(event: ModuleRootEvent?) {
                notifications.updateAllNotifications()
            }
        })
        connection.subscribe<FacetManagerListener>(FacetManager.FACETS_TOPIC, object : FacetManagerAdapter() {
            override fun facetConfigurationChanged(facet: Facet<*>) {
                if (facet is org.elixir_lang.Facet) {
                    notifications.updateAllNotifications()
                }
            }

        })
    }

    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(virtualFile: VirtualFile, fileEditor: FileEditor): EditorNotificationPanel? {
        var notificationPanel: EditorNotificationPanel? = null

        if (virtualFile.fileType is ElixirFileType) {
            val psiFile = PsiManager.getInstance(project).findFile(virtualFile)

            if (psiFile != null &&
                    psiFile.language === ElixirLanguage.INSTANCE &&
                    Type.mostSpecificSdk(psiFile) == null) {
                notificationPanel = createPanel(project, psiFile)
            }
        }

        return notificationPanel
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
            ProjectSettingsService.getInstance(project).chooseAndSetSdk()
        }

        fun showSmallIDEFacetSettings(project: Project) {
            ShowSettingsUtilImpl.showSettingsDialog(project, "language", "Elixir")
        }

        private fun createSmallIDEFacetPanel(project: Project): EditorNotificationPanel {
            return EditorNotificationPanel().apply {
                setText("Elixir Facet SDK is not defined")
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
                setText("Elixir Module SDK is not defined")
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
                setText(ProjectBundle.message("project.sdk.not.defined"))
                createActionLabel(ProjectBundle.message("project.sdk.setup")) {
                    ProjectSettingsService.getInstance(project).chooseAndSetSdk()
                }
            }

        }
    }
}
