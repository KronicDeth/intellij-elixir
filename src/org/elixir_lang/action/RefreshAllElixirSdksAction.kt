package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.readAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.elixir.ElixirSdkValidation
import org.elixir_lang.status_bar_widget.ElixirSdkRefreshListener
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

private val LOG = logger<RefreshAllElixirSdksAction>()

class RefreshAllElixirSdksAction : AnAction() {
    
    
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        try {
            refreshAllElixirSdkPaths(project)
        } catch (ex: Exception) {
            LOG.warn("Failed to refresh SDK paths (RefreshAllElixirSdksAction)", ex)
            Notifier.sdkRefreshError(project, ex.message ?: "Unknown error")
        }
    }

    private fun refreshAllElixirSdkPaths(project: com.intellij.openapi.project.Project) {
        // Find all configured SDKs in the IDE
        val allElixirSdks = getAllElixirSdks()
        val allErlangSdks = getAllErlangSdks()

        val totalElixirSdks = allElixirSdks.size
        val totalErlangSdks = allErlangSdks.size
        val totalSdks = totalElixirSdks + totalErlangSdks

        if (totalSdks == 0) {
            Notifier.sdkRefreshWarning(project, "No Elixir or Erlang SDKs are configured in the IDE.")
            return
        }

        var refreshedElixirCount = 0
        var refreshedErlangCount = 0
        // Collect OTP mismatches found during refresh (sdk name → mismatch description)
        val otpMismatches = mutableListOf<String>()

        runWithModalProgressBlocking(
            ModalTaskOwner.project(project), "Refreshing All SDK Paths"
        ) {
            val elixirSdkType = ElixirSdkType.instance

            // Refresh all Erlang SDKs first so that the Elixir refresh picks up the freshest paths.
            for (erlangSdk in allErlangSdks) {
                try {
                    refreshSingleErlangSdk(erlangSdk, erlangSdk.sdkType as ErlangSdkType)
                    refreshedErlangCount++
                } catch (_: Exception) {
                    // Continue with other SDKs if one fails
                    continue
                }
            }

            // Refresh all Elixir SDKs
            for (elixirSdk in allElixirSdks) {
                try {
                    refreshSingleElixirSdk(elixirSdk, elixirSdkType)
                    refreshedElixirCount++
                } catch (_: Exception) {
                    // Continue with other SDKs if one fails
                    continue
                }
            }

            // Check OTP mismatches for each Elixir SDK after refresh (informational).
            // ElixirSdkValidation.detectOtpMismatch handles the suppress flag internally.
            // getErlangSdk() → findErlangSdkByHomePath asserts read access, so wrap in readAction.
            for (elixirSdk in allElixirSdks) {
                val mismatch = readAction { ElixirSdkValidation.detectOtpMismatch(elixirSdk) } ?: continue
                otpMismatches.add(
                    "'${elixirSdk.name}' compiled for OTP ${mismatch.first} but paired with OTP ${mismatch.second}"
                )
            }
        }

        // Notify listeners that SDKs have been refreshed (updates status widget)
        ApplicationManager.getApplication().messageBus
            .syncPublisher(ElixirSdkRefreshListener.TOPIC)
            .sdksRefreshed()

        // Show success notification with counts
        Notifier.sdkRefreshSuccess(project, refreshedElixirCount, totalElixirSdks, refreshedErlangCount, totalErlangSdks)

        // Show separate OTP mismatch notification if any were found
        if (otpMismatches.isNotEmpty()) {
            val mismatchMessage = buildString {
                append("OTP version mismatches detected. Configure in Project Structure to fix:\n")
                otpMismatches.forEach { append("  • $it\n") }
            }
            Notifier.sdkRefreshWarning(project, mismatchMessage)
        }
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

    // Utility functions to get all configured Elixir SDKs
    private fun getAllElixirSdks(): List<Sdk> {
        val elixirSdkType = ElixirSdkType.instance
        return ProjectJdkTable.getInstance().allJdks.filter { sdk ->
            sdk.sdkType === elixirSdkType
        }
    }

    private fun getAllErlangSdks(): List<Sdk> {
        return ProjectJdkTable.getInstance().allJdks.filter { sdk ->
            sdk.sdkType is ErlangSdkType
        }
    }
}
