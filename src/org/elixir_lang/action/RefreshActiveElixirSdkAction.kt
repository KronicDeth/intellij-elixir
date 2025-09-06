package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData

class RefreshActiveElixirSdkAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        try {
            refreshElixirSdkPaths(project)
        } catch (ex: Exception) {
            Notifier.sdkRefreshError(project, ex.message ?: "Unknown error")
        }
    }

    private fun refreshElixirSdkPaths(project: Project) {
        // Find active SDKs in the project
        val activeElixirSdks = getActiveElixirSdks(project)
        val activeErlangSdks = getActiveErlangSdks(project)

        val totalElixirSdks = activeElixirSdks.size
        val totalErlangSdks = activeErlangSdks.size
        val totalActiveSdks = totalElixirSdks + totalErlangSdks

        if (totalActiveSdks == 0) {
            Notifier.sdkRefreshWarning(project, "No active Elixir or Erlang SDKs found in this project.")
            return
        }

        var refreshedElixirCount = 0
        var refreshedErlangCount = 0

        runWithModalProgressBlocking(
            ModalTaskOwner.project(project), "Refreshing Active SDK Paths"
        ) {
            val elixirSdkType = ElixirSdkType.instance

            // Refresh active Elixir SDKs
            for (elixirSdk in activeElixirSdks) {
                try {
                    refreshSingleElixirSdk(elixirSdk, elixirSdkType)
                    refreshedElixirCount++
                } catch (ex: Exception) {
                    // Continue with other SDKs if one fails
                    continue
                }
            }

            // Refresh active Erlang SDKs
            for (erlangSdk in activeErlangSdks) {
                try {
                    refreshSingleErlangSdk(erlangSdk, erlangSdk.sdkType as ErlangSdkType)
                    refreshedErlangCount++
                } catch (ex: Exception) {
                    // Continue with other SDKs if one fails
                    continue
                }
            }
        }

        // Show success notification with counts
        Notifier.sdkRefreshSuccess(project, refreshedElixirCount, totalElixirSdks, refreshedErlangCount, totalErlangSdks)
    }

    private fun refreshSingleElixirSdk(sdk: Sdk, sdkType: ElixirSdkType) {
        // setupSdkPaths handles its own write action and SDK modificator management
        // This clears existing paths and reconfigures them using the existing logic
        sdkType.setupSdkPaths(sdk)
    }

    private fun refreshSingleErlangSdk(sdk: Sdk, sdkType: ErlangSdkType) {
        // setupSdkPaths handles its own write action and SDK modificator management
        // This clears existing paths and reconfigures them using the existing logic
        sdkType.setupSdkPaths(sdk)
    }

    override fun isDumbAware(): Boolean = true

    // Utility functions to detect active Elixir SDKs
    private fun getActiveElixirSdks(project: Project): Set<Sdk> {
        val activeSdks = mutableSetOf<Sdk>()
        val elixirSdkType = ElixirSdkType.instance

        // Check project-level SDK
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk?.sdkType === elixirSdkType) {
            activeSdks.add(projectSdk)
        }

        // Check module-level SDKs
        ModuleManager.getInstance(project).modules.forEach { module ->
            val moduleSdk = ModuleRootManager.getInstance(module).sdk
            if (moduleSdk?.sdkType === elixirSdkType) {
                activeSdks.add(moduleSdk)
            }
        }

        return activeSdks
    }

    private fun getActiveErlangSdks(project: Project): Set<Sdk> {
        val activeSdks = mutableSetOf<Sdk>()

        // Check project-level SDK
        val projectSdk = ProjectRootManager.getInstance(project).projectSdk
        if (projectSdk?.sdkType is ErlangSdkType) {
            activeSdks.add(projectSdk)
        }

        // Check module-level SDKs
        ModuleManager.getInstance(project).modules.forEach { module ->
            val moduleSdk = ModuleRootManager.getInstance(module).sdk
            if (moduleSdk?.sdkType is ErlangSdkType) {
                activeSdks.add(moduleSdk)
            }
        }

        // Also include Erlang SDKs referenced by active Elixir SDKs
        val activeElixirSdks = getActiveElixirSdks(project)
        activeElixirSdks.forEach { elixirSdk ->
            val additionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
            val erlangSdk = additionalData?.getErlangSdk()
            if (erlangSdk != null) {
                activeSdks.add(erlangSdk)
            }
        }

        return activeSdks
    }
}
