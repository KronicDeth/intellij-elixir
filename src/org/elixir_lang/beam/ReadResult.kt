package org.elixir_lang.beam

import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.IndexNotReadyException
import java.util.concurrent.CancellationException

private val LOGGER = Logger.getInstance(ReadResult::class.java)

/** What reading one part of a BEAM file produced. */
sealed interface ReadResult<out T : Any> {
    data class Present<out T : Any>(val value: T) : ReadResult<T>

    data object Absent : ReadResult<Nothing>

    class Unreadable(val what: String, val cause: Throwable) : ReadResult<Nothing> {
        val reason: String
            get() = "$what: ${cause.message ?: cause.javaClass.name}"

        override fun toString(): String = "Unreadable($reason)"
    }

    val valueOrNull: T?
        get() = (this as? Present)?.value
}

/** The value, or null - reporting it when the part is there but could not be read. */
internal fun <T : Any> ReadResult<T>.orReported(path: String): T? {
    if (this is ReadResult.Unreadable) {
        LOGGER.reportUnexpectedBeamData("$path: $reason", cause)
    }

    return valueOrNull
}

/**
 * Runs [read], turning a failure that corrupt input can cause into [ReadResult.Unreadable].
 *
 * `OutOfMemoryError` is not one of them - nothing contains it reliably - so a length the bytes cannot hold has
 * to be refused before it is allocated. `AssertionError` counts only as exactly that class: the test framework's
 * `TestLoggerAssertionError` extends it, and an error logged mid-read must still fail the test.
 */
internal inline fun <T : Any> contain(what: String, read: () -> T?): ReadResult<T> =
    try {
        read()?.let { ReadResult.Present(it) } ?: ReadResult.Absent
    } catch (failure: Throwable) {
        if (!isCausedByCorruption(failure)) throw failure
        ReadResult.Unreadable(what, failure)
    }

internal fun isCausedByCorruption(failure: Throwable): Boolean =
    when (failure) {
        is ControlFlowException, is CancellationException, is IndexNotReadyException -> false
        is Exception, is StackOverflowError, is NotImplementedError -> true
        else -> failure.javaClass == AssertionError::class.java
    }
