package org.elixir_lang.mix.project

import com.intellij.ide.IdeBundle
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtil
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.components.StorageScheme
import com.intellij.openapi.project.Project
import com.intellij.openapi.application.writeAction
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.projectImport.ProjectImportBuilder
import com.intellij.projectImport.ProjectOpenProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.elixir_lang.mix.project._import.Builder
import java.nio.file.Files

/**
 * Created by zyuyou on 15/7/1.
 */
class OpenProcessor : ProjectOpenProcessor() {
    private val supportedExtensions = arrayOf("mix.exs")

    override val name: String
        get() = "Mix"

    private val builder: Builder
        get() = ProjectImportBuilder.EXTENSIONS_POINT_NAME.findExtensionOrFail(Builder::class.java)

    override fun canOpenProject(file: VirtualFile): Boolean {
        return if (file.isDirectory) {
            file.children?.any { it.name in supportedExtensions } ?: false
        } else {
            file.name in supportedExtensions
        }
    }

    override suspend fun openProjectAsync(
        virtualFile: VirtualFile,
        projectToClose: Project?,
        forceOpenInNewFrame: Boolean
    ): Project? {
        val wizardContext = WizardContext(null, null)

        var resolvedFile = virtualFile
        if (virtualFile.isDirectory) {
            resolvedFile = virtualFile.children?.firstOrNull { it.name in supportedExtensions } ?: return null
        }

        val projectRoot = resolvedFile.parent
        wizardContext.projectName = projectRoot.name
        wizardContext.setProjectFileDirectory(projectRoot.toNioPath(), false)

        // Import Mix project configuration
        builder.setProjectRoot(projectRoot)

        val projectPath = wizardContext.projectDirectory
        val options = OpenProjectTask {
            this.projectToClose = projectToClose
            this.forceOpenInNewFrame = forceOpenInNewFrame
            this.projectName = wizardContext.projectName
            this.isNewProject = true
            this.beforeOpen = { project ->
                // Configure the project - Builder now uses coroutine-aware write actions
                builder.commit(project, null, ModulesProvider.EMPTY_MODULES_PROVIDER, null)
                true
            }
        }

        // Open the project without modal progress (to avoid EDT issues)
        val project = withContext(Dispatchers.IO) {
            ProjectManagerEx.getInstanceEx().openProjectAsync(projectPath, options)
        }

        if (project != null) {
            ProjectUtil.updateLastProjectLocation(projectPath)
        }

        return project
    }
}
