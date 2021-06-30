package org.elixir_lang.notification.setup_sdk

import com.intellij.execution.ExecutionException
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.module.Module

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
                        NotificationType.ERROR,
                        Listener(project, module)
                )
                .notify(project);
    }
}
