package org.elixir_lang.notification.setup_sdk

import com.intellij.execution.ExecutionException
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import org.elixir_lang.sdk.elixir.SdkSettingsOpener
import java.util.Collections
import java.util.WeakHashMap

object Notifier {
    const val MIX_DEPS_OUTDATED_TITLE: String = "Mix deps outdated"
    private const val MIX_DEPS_GROUP_ID: String = "Elixir Mix Deps"
    private val mixDepsNotifications: MutableMap<Project, Notification> =
        Collections.synchronizedMap(WeakHashMap())
    private val missingErlangSdkNotifications: MutableMap<Project, Notification> =
        Collections.synchronizedMap(WeakHashMap())

    fun mixSettings(module: Module, executionException: ExecutionException) {
        val message = executionException.message
        val isEmpty = "Executable is not specified" == message
        val notCorrect = message?.startsWith("Cannot run program") ?: false

        if (isEmpty || notCorrect) {
            mixSettings(module, isEmpty)
        }
    }

    private fun mixSettings(module: Module, isEmpty: Boolean) {
        error(
            module,
            "Mix settings",
            "Mix executable path, elixir executable path, or erl executable path is " +
                    (if (isEmpty) "empty" else "not specified correctly")
        )
    }

    fun error(module: Module, title: String, content: String) {
        val project = module.project

        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                title,
                "$content<br><a href='configure'>Configure</a></br>",
                NotificationType.ERROR
            )
            .addAction(Action(project, module))
            .notify(project)
    }


    // SDK Refresh notification methods
    fun sdkRefreshSuccess(
        project: Project,
        refreshedElixirCount: Int,
        totalElixirCount: Int,
        refreshedErlangCount: Int,
        totalErlangCount: Int
    ) {
        val parts = mutableListOf<String>()

        if (totalElixirCount > 0) {
            if (refreshedElixirCount == totalElixirCount) {
                parts.add("$refreshedElixirCount Elixir SDK${if (refreshedElixirCount != 1) "s" else ""}")
            } else {
                parts.add("$refreshedElixirCount of $totalElixirCount Elixir SDK${if (totalElixirCount != 1) "s" else ""}")
            }
        }

        if (totalErlangCount > 0) {
            if (refreshedErlangCount == totalErlangCount) {
                parts.add("$refreshedErlangCount Erlang SDK${if (refreshedErlangCount != 1) "s" else ""}")
            } else {
                parts.add("$refreshedErlangCount of $totalErlangCount Erlang SDK${if (totalErlangCount != 1) "s" else ""}")
            }
        }

        val message = "Successfully refreshed " + when (parts.size) {
            1 -> parts[0]
            2 -> "${parts[0]} and ${parts[1]}"
            else -> "SDKs"
        } + "."

        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Elixir SDK paths refreshed",
                message,
                NotificationType.INFORMATION
            )
            .notify(project)
    }

    fun sdkRefreshWarning(project: Project, message: String) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Elixir SDK Refresh",
                message,
                NotificationType.WARNING
            )
            .notify(project)
    }

    fun sdkRefreshError(project: Project, errorMessage: String) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Elixir SDK refresh failed",
                "Failed to refresh SDK paths: $errorMessage",
                NotificationType.ERROR
            )
            .notify(project)
    }

    // Mix Dependencies notification methods
    fun mixDepsOutdated(project: Project) {
        if (mixDepsNotifications.containsKey(project)) {
            return
        }

        val notification = NotificationGroupManager
            .getInstance()
            .getNotificationGroup(MIX_DEPS_GROUP_ID)
            .createNotification(
                MIX_DEPS_OUTDATED_TITLE,
                "Mix deps reported missing, outdated, or uncompiled deps",
                NotificationType.WARNING
            )
            .addAction(org.elixir_lang.notification.mix_deps.InstallAction())
            .addAction(org.elixir_lang.notification.mix_deps.ShowStatusAction())
            .whenExpired { mixDepsNotifications.remove(project) }
        mixDepsNotifications[project] = notification
        notification.notify(project)
    }

    fun mixDepsCheckFailed(project: Project, message: String) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Mix deps check failed",
                message,
                NotificationType.INFORMATION
            )
            .notify(project)
    }

    fun clearMixDepsOutdated(project: Project) {
        mixDepsNotifications.remove(project)?.expire()
    }

    fun mixDepsInstallSuccess(project: Project) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Mix deps installed",
                "Successfully installed hex, rebar, fetched deps, and compiled",
                NotificationType.INFORMATION
            )
            .notify(project)
    }

    fun mixDepsInstallError(project: Project, errorMessage: String) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Mix deps installation failed",
                "Failed during local.hex, local.rebar, deps.get, or compilation. See Run Window for details: $errorMessage",
                NotificationType.ERROR
            )
            .notify(project)
    }

    fun mixDepsNoSdk(project: Project) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "No Elixir SDK found",
                "Please configure an Elixir SDK for this project before installing deps",
                NotificationType.ERROR
            )
            .notify(project)
    }

    fun mixDepsError(project: Project, errorMessage: String) {
        NotificationGroupManager
            .getInstance()
            .getNotificationGroup("Elixir")
            .createNotification(
                "Mix deps error",
                errorMessage,
                NotificationType.ERROR
            )
            .notify(project)
    }

    fun elixirSdkMissingErlangDependency(project: Project?, sdkName: String) {
        if (project != null && missingErlangSdkNotifications.containsKey(project)) {
            return
        }

        val settingsTarget = SdkSettingsOpener.getInstance().targetName()
        val notification =
            NotificationGroupManager
                .getInstance()
                .getNotificationGroup("Elixir")
                .createNotification(
                    "Elixir SDK missing Erlang SDK",
                    "Elixir SDK '<b>$sdkName</b>' is missing its Erlang SDK dependency. Configure it in $settingsTarget.",
                    NotificationType.WARNING
                )
                .addAction(NotificationAction.create("Configure SDKs") { event, _ ->
                    SdkSettingsOpener.getInstance().open(event)
                })
                .whenExpired {
                    if (project != null) {
                        missingErlangSdkNotifications.remove(project)
                    }
                }

        if (project != null) {
            missingErlangSdkNotifications[project] = notification
        }
        notification.notify(project)
    }
}
