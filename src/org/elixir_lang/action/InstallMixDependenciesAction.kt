package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.mix.runner.MixTaskRunner
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

class InstallMixDependenciesAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val sdk = getProjectElixirSdk(project)
        if (sdk == null) {
            Notifier.mixDepsNoSdk(project)
            return
        }

        val projectRoot = getProjectRoot(project)
        if (projectRoot == null) {
            Notifier.mixDepsError(project, "Could not determine project root directory")
            return
        }

        val workingDirectory = projectRoot.path

        try {
            val result = MixTaskRunner.installDependencies(project, workingDirectory, sdk)

            result.fold(
                onSuccess = { Notifier.mixDepsInstallSuccess(project) },
                onFailure = { error ->
                    Notifier.mixDepsInstallError(project, error.message ?: "Unknown error")
                }
            )
        } catch (e: Exception) {
            Notifier.mixDepsInstallError(project, e.message ?: "Unknown error")
        }
    }

    override fun isDumbAware(): Boolean = true

    private fun getProjectElixirSdk(project: Project): Sdk? {
        val elixirSdkType = ElixirSdkType.instance

        // Check project-level SDK
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk?.sdkType === elixirSdkType) {
            return projectSdk
        }

        // Check module-level SDKs
        ModuleManager.getInstance(project).modules.forEach { module ->
            val moduleSdk = ModuleRootManager.getInstance(module).sdk
            if (moduleSdk?.sdkType === elixirSdkType) {
                return moduleSdk
            }
        }

        return null
    }

    private fun getProjectRoot(project: Project): VirtualFile? {
        val contentRoots = ProjectRootManager.getInstance(project).contentRootsFromAllModules

        // Find first root that has mix.exs
        return contentRoots.firstOrNull { root ->
            root.findChild("mix.exs") != null
        } ?: contentRoots.firstOrNull()
    }
}
