package org.elixir_lang.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.platform.ide.progress.ModalTaskOwner
import com.intellij.platform.ide.progress.runWithModalProgressBlocking

/**
 * Runs [block] directly if off-EDT (or if [skipModalIf] returns `true`), otherwise wraps it in
 * [runWithModalProgressBlocking] so the EDT is not blocked.
 *
 * Use [skipModalIf] when the modal progress dialog must not be shown - for example, when a write
 * action is already held on the EDT (`ApplicationManager.getApplication().isWriteAccessAllowed`),
 * because starting modal progress from inside a write action would deadlock.
 *
 * Use this to guard any blocking subprocess or I/O call that may be triggered from the EDT.
 */
fun <T> runWithEdtGuard(
    title: String,
    skipModalIf: () -> Boolean = { false },
    block: () -> T,
): T {
    val app = ApplicationManager.getApplication()
    return if (app.isDispatchThread && !skipModalIf()) {
        runWithModalProgressBlocking(ModalTaskOwner.guess(), title) { block() }
    } else {
        block()
    }
}
