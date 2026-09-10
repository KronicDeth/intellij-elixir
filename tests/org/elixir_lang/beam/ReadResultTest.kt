package org.elixir_lang.beam

import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.IndexNotReadyException
import org.elixir_lang.PlatformTestCase
import org.junit.Assert
import java.util.concurrent.CancellationException

/** What [contain] turns into an unreadable chunk, what it lets through, and what gets reported. */
class ReadResultTest : PlatformTestCase() {
    fun testAValueIsPresent() = Assert.assertEquals(ReadResult.Present("atoms"), contain("AtU8") { "atoms" })

    fun testNothingIsAbsent() = Assert.assertEquals(ReadResult.Absent, contain<String>("AtU8") { null })

    fun testAnExceptionIsUnreadable() = assertUnreadable(IllegalArgumentException("corrupt"))

    fun testAStackOverflowIsUnreadable() = assertUnreadable(StackOverflowError())

    fun testATodoIsUnreadable() = assertUnreadable(NotImplementedError())

    fun testAFailedAssertIsUnreadable() = assertUnreadable(AssertionError("assert"))

    /** `TestLoggerAssertionError` is a subclass, and an error logged mid-read must still fail its test. */
    fun testAnAssertionErrorSubclassIsRethrown() = assertRethrown(object : AssertionError("logged") {})

    fun testControlFlowIsRethrown() = assertRethrown(ProcessCanceledException())

    /** [ProcessCanceledException] is both, and the check short-circuits on the first. */
    fun testABareCancellationIsRethrown() = assertRethrown(CancellationException())

    fun testRunningOutOfMemoryIsRethrown() = assertRethrown(OutOfMemoryError())

    /** Quick Docs reads inside a contained block, and dumb mode is not a corrupt BEAM. */
    fun testIndexesNotBeingReadyIsRethrown() = assertRethrown(IndexNotReadyException.create())

    fun testAnUnreadableChunkIsReportedWithItsPath() {
        val (value, errors) = captureLoggedErrors {
            contain<String>("Code") { throw IllegalArgumentException("corrupt") }.orReported("Elixir.Corrupt.beam")
        }

        Assert.assertNull(value)
        Assert.assertEquals("got $errors", 1, errors.size)
        Assert.assertTrue("got $errors", errors.single().message.contains("Elixir.Corrupt.beam: Code: corrupt"))
    }

    fun testAnAbsentChunkIsNotReported() {
        val (value, errors) = captureLoggedErrors { ReadResult.Absent.orReported("Elixir.Corrupt.beam") }

        Assert.assertNull(value)
        Assert.assertEquals(emptyList<Any>(), errors)
    }

    private fun assertUnreadable(failure: Throwable) {
        val read = contain<String>("Code") { throw failure }

        Assert.assertTrue("got $read", read is ReadResult.Unreadable && read.what == "Code" && read.cause === failure)
    }

    private fun assertRethrown(failure: Throwable) {
        val escaped = try {
            contain<String>("Code") { throw failure }
            null
        } catch (caught: Throwable) {
            caught
        }

        Assert.assertSame(failure, escaped)
    }
}
