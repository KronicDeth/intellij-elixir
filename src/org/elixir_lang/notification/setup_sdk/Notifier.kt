package org.elixir_lang.notification.setup_sdk

import com.intellij.execution.ExecutionException
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project

object Notifier {
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
            .notify(project);
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
                "Elixir SDK Paths Refreshed",
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
                "Elixir SDK Refresh Failed",
                "Failed to refresh SDK paths: $errorMessage",
                NotificationType.ERROR
            )
            .notify(project)
    }
}
