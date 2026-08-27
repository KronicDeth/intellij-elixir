package org.elixir_lang.console

import com.intellij.execution.filters.FileHyperlinkInfo
import com.intellij.execution.filters.Filter
import com.intellij.openapi.application.ReadAction
import org.elixir_lang.PlatformTestCase
import java.util.concurrent.Callable

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

    private fun applyFilter(line: String): List<Filter.ResultItem> =
        ReadAction.nonBlocking(
            Callable {
                FileReferenceFilter(project, FileReferenceFilter.COMPILATION_ERROR_PATH)
                    .applyFilter(line, line.length)
            }
        ).executeSynchronously()?.resultItems.orEmpty()
}
