package org.elixir_lang.action

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.ui.Messages
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.ProcessOutput
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

class DeleteAllSdksAction : AnAction() {
    companion object {
        private val LOG = Logger.getInstance(DeleteAllSdksAction::class.java)
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        try {
            removeAllElixirAndErlangSdks(project)
        } catch (ex: Exception) {
            Notifier.sdkRefreshError(project, "Error removing SDKs: ${ex.message ?: "Unknown error"}")
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project
        e.presentation.isVisible = project != null
    }

    private fun removeAllElixirAndErlangSdks(project: Project) {
        val projectJdkTable = ProjectJdkTable.getInstance()
        val allSdks = projectJdkTable.allJdks

        // Find all Elixir and Erlang SDKs
        val elixirSdks = allSdks.filter { it.sdkType is ElixirSdkType }
        val erlangSdks = allSdks.filter { it.sdkType is ErlangSdkType }
        // Remove Erlang SDKs first since Elixir SDKs depend on them
        val allTargetSdks = erlangSdks + elixirSdks

        if (allTargetSdks.isEmpty()) {
            Notifier.sdkRefreshWarning(project, "No Elixir or Erlang SDKs found in the IDE.")
            return
        }

        // Show confirmation dialog
        val sdkNames = allTargetSdks.joinToString("\n") { "• ${it.name}" }
        val result = Messages.showYesNoDialog(
            project,
            "Are you sure you want to delete all Elixir and Erlang SDKs?\n\n" +
                    "This will remove the following SDKs from the IDE:\n$sdkNames\n\n" +
                    "This action cannot be undone.",
            "Delete All Elixir/Erlang SDKs",
            Messages.getQuestionIcon()
        )

        if (result != Messages.YES) {
            return
        }

        // Remove SDKs and their associated libraries using modal progress with proper threading
        val sdksRemoved = runWithModalProgressBlocking(project, "Removing All Elixir/Erlang SDKs") {
            var removedCount = 0
            
            edtWriteAction {
                LOG.info("Starting removal of ${allTargetSdks.size} SDK(s) and associated libraries")
                
                for (sdk in allTargetSdks) {
                    try {
                        LOG.debug("Removing SDK and library: ${sdk.name}")
                        
                        // First remove associated library (like the SDK listeners do)
                        removeAssociatedLibrary(sdk)
                        
                        // Then remove the SDK itself
                        projectJdkTable.removeJdk(sdk)
                        removedCount++
                        
                        LOG.debug("Successfully removed SDK and library: ${sdk.name}")
                    } catch (ex: Exception) {
                        LOG.error("Failed to remove SDK: ${sdk.name}", ex)
                        // Continue with other SDKs if one fails
                        continue
                    }
                }
                LOG.info("Completed removal of $removedCount SDK(s) and associated libraries")
            }
            
            removedCount
        }

        // Show success notification
        if (sdksRemoved > 0) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "Elixir SDKs Removed Successfully",
                    "Successfully removed $sdksRemoved SDK${if (sdksRemoved == 1) "" else "s"} (${elixirSdks.size} Elixir, ${erlangSdks.size} Erlang) from the IDE.",
                    NotificationType.INFORMATION
                )
                .notify(project)
        } else {
            Notifier.sdkRefreshWarning(
                project,
                "No Elixir SDKs were removed. They may have been removed already or there was an error."
            )
        }
    }

    override fun isDumbAware(): Boolean = true

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
    
    /**
     * Removes the library associated with an SDK, following the same pattern as the SDK listeners
     * in sdks/Configurable.kt
     */
    private fun removeAssociatedLibrary(sdk: Sdk) {
        try {
            val libraryTable = LibraryTablesRegistrar.getInstance().libraryTable
            val library = libraryTable.getLibraryByName(sdk.name)
            
            if (library != null) {
                LOG.debug("Removing associated library: ${sdk.name}")
                libraryTable.removeLibrary(library)
            } else {
                LOG.debug("No associated library found for SDK: ${sdk.name}")
            }
        } catch (ex: Exception) {
            LOG.warn("Failed to remove associated library for SDK: ${sdk.name}", ex)
            // Don't fail the whole operation if library removal fails
        }
    }
}
