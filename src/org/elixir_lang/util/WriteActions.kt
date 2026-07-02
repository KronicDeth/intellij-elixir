package org.elixir_lang.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction

/**
 * Write-action helper to avoid blocking the EDT on itself.
 *
 * Use for small, bounded write actions that must run on the EDT without introducing runBlocking + edtWriteAction
 * deadlocks. For long or cancellable work, keep the heavy portion off the EDT and only wrap the minimal write step.
 */
object WriteActions {
    // The ThreadingConcurrency inspection flags WriteAction.run as @RequiresEdt, but this utility
    // explicitly branches on isDispatchThread / isWriteAccessAllowed before calling it, and falls
    // back to invokeAndWait when off-EDT. The inspection cannot see inside the if-branches.
    @Suppress("ThreadingConcurrency")
    fun runWriteAction(action: () -> Unit) {
        val application = ApplicationManager.getApplication()

        if (application.isWriteAccessAllowed) {
            action()
        } else if (application.isDispatchThread) {
            WriteAction.run<RuntimeException> { action() }
        } else {
            application.invokeAndWait {
                WriteAction.run<RuntimeException> { action() }
            }
        }
    }

    fun runWriteActionLater(action: () -> Unit) {
        val application = ApplicationManager.getApplication()

        if (application.isWriteAccessAllowed) {
            action()
        } else {
            val runnable = Runnable {
                WriteAction.run<RuntimeException> { action() }
            }

            if (application.isDispatchThread) {
                runnable.run()
            } else {
                application.invokeLater(runnable)
            }
        }
    }
}
