package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking

/**
 * Runs [block] directly if off-EDT, or wraps it in [runWithModalProgressBlocking] if on EDT.
 * Use this to guard any blocking subprocess or I/O call that may be triggered from the EDT.
 */
fun <T> runWithEdtGuard(title: String, block: () -> T): T {
    val app = ApplicationManager.getApplication()
    return if (app.isDispatchThread) {
        runWithModalProgressBlocking(ModalTaskOwner.guess(), title) { block() }
    } else {
        block()
    }
}
