package org.elixir_lang.console

import com.intellij.execution.filters.FileHyperlinkInfo
import com.intellij.execution.filters.Filter
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.vfs.LocalFileSystem
import org.elixir_lang.PlatformTestCase
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

/**
 * Characterises the console filter that has linked compiler errors and formatted stack frames since
 * 2015 with no test of its own. It is pinned here because [SourceFileResolver] was lifted out of it
 * to be shared with [LiteralStackTraceFilter], and the path cascade it moved - as written, then
 * project-relative, then by filename suffix - is the part a reader is most likely to simplify by
 * accident.
 */
class FileReferenceFilterTest : PlatformTestCase() {
    fun testLinksAFormattedStackFrame() {
        val file = myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")
        val line = "    (gald) lib/gald/turn.ex:38: Gald.Turn.handle_cast/2"

        val item = applyFilter(line).single()

        val descriptor = requireNotNull((item.hyperlinkInfo as FileHyperlinkInfo).descriptor)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals("`:38` is 1-based; the descriptor is 0-based", 37, descriptor.line)
    }

    /** The path is found by filename and matched as a suffix, so a partial path still resolves. */
    fun testResolvesAPathThatIsOnlyASuffixOfTheFile() {
        val file = myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val item = applyFilter("gald/turn.ex:1: whatever").single()

        assertEquals(file.virtualFile, (item.hyperlinkInfo as FileHyperlinkInfo).descriptor!!.file)
    }

    /**
     * A formatted frame followed by an inspected term on the same line. The expression's path class
     * accepts spaces, so what follows the frame decides how much of the line it swallows.
     */
    fun testLinksAFormattedFrameFollowedByAKeywordList() {
        val file = myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val items = applyFilter(
            "    (gald) lib/gald/turn.ex:38: raised from [file: 'lib/gald/phase.ex', line: 54]"
        )

        assertEquals("Expected the formatted frame to link, got: $items", 1, items.size)
        assertEquals(file.virtualFile, (items.single().hyperlinkInfo as FileHyperlinkInfo).descriptor!!.file)
    }

    /**
     * A compile error on Windows prints the path with backslashes, while the VFS holds forward ones.
     * Neither `startsWith` nor `endsWith` knows a separator from any other character, so without
     * normalising both sides the frame resolves to nothing - on the platform where every path in
     * console output looks like this.
     */
    fun testLinksAPathWrittenWithBackslashes() {
        val file = myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val items = applyFilter("    (gald) lib\\gald\\turn.ex:38: Gald.Turn.handle_cast/2")

        assertEquals("Expected the backslash path to link, got: $items", 1, items.size)
        assertEquals(file.virtualFile, (items.single().hyperlinkInfo as FileHyperlinkInfo).descriptor!!.file)
    }

    fun testIgnoresAPathOutsideTheProject() {
        assertEmpty(applyFilter("    (stdlib) gen_server.erl:615: :gen_server.try_dispatch/4"))
    }

    /**
     * A keyword-list location belongs to [LiteralStackTraceFilter]. This filter's expression wants
     * digits straight after the colon, and `inspect` writes `line: 75` with a space - the gap that
     * left the form unlinked for as long as it did.
     */
    fun testIgnoresAKeywordListLocation() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        assertEmpty(applyFilter("{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}"))
    }

    /** Pinned because dropping the space from the path class would be a tempting cure below. */
    fun testLinksAPathContainingSpaces() {
        val file = myFixture.addFileToProject("my app/lib/some file.ex", "defmodule Some.File do\nend\n")

        val items = applyFilter("  my app/lib/some file.ex:3: anonymous fn/2")

        assertEquals("Expected the spaced path to link, got: $items", 1, items.size)
        assertEquals(file.virtualFile, (items.single().hyperlinkInfo as FileHyperlinkInfo).descriptor!!.file)
    }

    /**
     * On Windows the drive letter's colon is not in the path class, so the match used to start after
     * it and resolve nothing. Written to disk because only a real file is found by an absolute path.
     */
    fun testLinksAnAbsolutePathIncludingAnyDriveLetter() {
        val file = File(project.basePath!!, "drive_letter.ex")
        file.parentFile.mkdirs()
        file.writeText("defmodule DriveLetter do\nend\n")
        val virtualFile = requireNotNull(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file))
        val line = "${file.absolutePath}:7: DriveLetter.boom/0"

        val items = applyFilter(line)

        assertEquals("Expected the absolute path to link, got: $items", 1, items.size)
        val item = items.single()
        assertEquals("The whole path must be highlighted, drive letter included", 0, item.highlightStartOffset)
        assertEquals(virtualFile, (item.hyperlinkInfo as FileHyperlinkInfo).descriptor!!.file)
    }

    /** Pins the offsets, which are computed from the match groups rather than from the path text. */
    fun testHighlightsExactlyThePathAndItsLine() {
        myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")
        val line = "    (gald) lib/gald/turn.ex:38: Gald.Turn.handle_cast/2"

        val item = applyFilter(line).single()

        assertEquals("lib/gald/turn.ex:38", line.substring(item.highlightStartOffset, item.highlightEndOffset))
    }

    /** The terminal's own bare-path link is registered first and wins a tie; ours knows the line. */
    fun testOutranksTheTerminalsOwnGenericFileLink() {
        myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val item = applyFilter("lib/gald/turn.ex:38: Gald.Turn.handle_cast/2").single()

        assertTrue(
            "Layer ${item.highlighterLayer} must beat the terminal's ${HighlighterLayer.HYPERLINK}",
            item.highlighterLayer > HighlighterLayer.HYPERLINK
        )
    }

    /**
     * The three shapes fail differently - retrying at every offset is quadratic, `\s*` competing
     * with the path class for a space is cubic - so a cure for one can leave the others hanging.
     */
    fun testDoesNotBacktrackOnAnIndentedLine() {
        assertRejectsWithinBudget(" ".repeat(1000) + "a".repeat(1000) + ":")
    }

    fun testDoesNotBacktrackOnAWhitespaceOnlyLine() {
        assertRejectsWithinBudget(" ".repeat(2000) + ":")
    }

    fun testDoesNotBacktrackOnALineWithoutSpaces() {
        assertRejectsWithinBudget("a".repeat(32000) + ":")
    }

    /**
     * Started before being cancelled because `runProcess` starts an indicator that is not running
     * and `EmptyProgressIndicator.start` clears the cancellation. The line clears 1024 characters,
     * which is how often the bombed sequence checks.
     */
    fun testAbortsWhenTheProgressIndicatorIsCancelled() {
        val line = " ".repeat(2000) + ":"
        val indicator = EmptyProgressIndicator().apply { start(); cancel() }
        val filter = FileReferenceFilter(project, FileReferenceFilter.COMPILATION_ERROR_PATH)

        val thrown = ApplicationManager.getApplication().executeOnPooledThread<Throwable?> {
            try {
                ProgressManager.getInstance().runProcess({ filter.applyFilter(line, line.length) }, indicator)
                null
            } catch (t: Throwable) {
                t
            }
        }.get(30, TimeUnit.SECONDS)

        assertInstanceOf(thrown, ProcessCanceledException::class.java)
    }

    private fun assertRejectsWithinBudget(line: String) {
        val start = System.nanoTime()
        val items = applyFilter(line)
        val elapsedMillis = (System.nanoTime() - start) / 1_000_000

        assertEmpty("Expected a line ending in a bare colon not to link, got: $items", items)
        assertTrue(
            "Matching ${line.length} characters took ${elapsedMillis}ms, budget is ${BUDGET_MILLIS}ms",
            elapsedMillis < BUDGET_MILLIS
        )
    }

    private fun applyFilter(line: String): List<Filter.ResultItem> =
        ReadAction.nonBlocking(
            Callable {
                FileReferenceFilter(project, FileReferenceFilter.COMPILATION_ERROR_PATH)
                    .applyFilter(line, line.length)
            }
        ).executeSynchronously()?.resultItems.orEmpty()

    companion object {
        /** Wide on purpose: a working expression takes about a millisecond, a backtracking one seconds. */
        private const val BUDGET_MILLIS = 1_000L
    }
}
