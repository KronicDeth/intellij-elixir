package org.elixir_lang.console

import com.intellij.execution.filters.FileHyperlinkInfo
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.GlobalSearchScope
import org.elixir_lang.PlatformTestCase

/**
 * Drives a real [ConsoleViewImpl] through [ElixirConsoleUtil.attachFilters] and asserts the
 * hyperlinks the console ends up holding.
 *
 * The per-filter tests call `applyFilter` directly and have to state what the console would pass.
 * Twice that guess was wrong in a way every one of them still passed: `inspect` wraps a wide entry
 * across lines, and offsets mean different things in different consoles. Both were only caught by
 * running the plugin. This closes that gap - it goes through the same registration a run
 * configuration uses, the real `AsyncFilterRunner`, and the real offsets - so a wrong assumption
 * about the console fails here rather than in a sandbox two days later.
 */
class ConsoleFiltersTest : PlatformTestCase() {
    fun testLinksAWholeEntry() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole("{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}\n")

        val (text, descriptor) = console.onlyHyperlinkOnLine(0)
        assertEquals("lib/gald/phase.ex", text)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals(53, descriptor.line)
    }

    /**
     * The shape `IO.inspect(__STACKTRACE__)` produces at the default width: the path and its number
     * on different lines. The link belongs to the path, on the line before the number - which only
     * works because this console's offsets run through its document.
     */
    fun testLinksAWrappedEntryToItsOwnLine() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole(
            """
            |[
            |  {Gald.Phase, :initialize_screen, 3,
            |   [
            |     file: ~c"lib/gald/phase.ex",
            |     line: 75,
            |     error_info: %{module: Exception}
            |   ]}
            |]
            """.trimMargin() + "\n"
        )

        // Line 3 is the path's, not line 4 where the number is: the link belongs to the path.
        val (text, descriptor) = console.onlyHyperlinkOnLine(3)
        assertEquals("lib/gald/phase.ex", text)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals("`line: 75` is on the next line but still navigates", 74, descriptor.line)
    }

    /** Elixir 1.15 and later inspect the path as a `~c` sigil, which is what a current IDE sees. */
    fun testLinksASigilCharlistPath() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole("""{Gald.Phase, :init, 1, [file: ~c"lib/gald/phase.ex", line: 54]}""" + "\n")

        val (text, descriptor) = console.onlyHyperlinkOnLine(0)
        assertEquals("lib/gald/phase.ex", text)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals(53, descriptor.line)
    }

    /**
     * The reported `MatchError` put five locations on one line. Each has to become its own link, at
     * its own offsets - which is where a filter that gets document offsets wrong shows it.
     */
    fun testLinksEveryEntryOnALogLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole(
            "** (MatchError) no match of right hand side value: {:error, {{:undef, [" +
                    "{Gald.Phase, :initialize_screen, 3, [file: 'lib/gald/phase.ex', line: 75]}, " +
                    "{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}]}}}\n"
        )

        val links = console.hyperlinksOnLine(0)
        assertEquals("Expected a link per entry, got: $links", 2, links.size)
        assertEquals(listOf("lib/gald/phase.ex", "lib/gald/phase.ex"), links.map { it.first })
        assertEquals(listOf(74, 53), links.map { it.second.line })
    }

    /**
     * A second line of output means every offset after the first line is wrong if the filter treats
     * `entireLength` as a line length. The unit tests can only assume that; this shows it.
     */
    fun testLinksAnEntryOnALaterLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole(
            "some preceding output that pushes the entry well past the first line\n" +
                    "{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}\n"
        )

        val (text, descriptor) = console.onlyHyperlinkOnLine(1)
        assertEquals("lib/gald/phase.ex", text)
        assertEquals(53, descriptor.line)
    }

    /** The wrapped entry as Elixir 1.14 and earlier print it, through a real console. */
    fun testLinksAWrappedCharlistEntryToItsOwnLine() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val console = printToConsole(
            """
            |  {Gald.Phase, :initialize_screen, 3,
            |   [
            |     file: 'lib/gald/phase.ex',
            |     line: 75
            |   ]}
            """.trimMargin() + "\n"
        )

        val (text, descriptor) = console.onlyHyperlinkOnLine(2)
        assertEquals("lib/gald/phase.ex", text)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals(74, descriptor.line)
    }

    /** [FileReferenceFilter] is attached by the same call and still links what it always did. */
    fun testLinksAFormattedFrameFromTheSameRegistration() {
        val file = myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val console = printToConsole("    (gald) lib/gald/turn.ex:38: Gald.Turn.handle_cast/2\n")

        val (_, descriptor) = console.onlyHyperlinkOnLine(0)
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals(37, descriptor.line)
    }

    /**
     * A line holding both forms has to come out with both links - neither filter may claim the line
     * to itself.
     *
     * The `(gald)` prefix is load-bearing: [FileReferenceFilter]'s path class accepts letters and
     * spaces, so with no character it must stop at, it swallows the preceding words into the path
     * and then resolves nothing.
     */
    fun testLinksBothFormsOnOneLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        val console = printToConsole(
            "    (gald) lib/gald/turn.ex:38: raised from [file: 'lib/gald/phase.ex', line: 54]\n"
        )

        val links = console.hyperlinksOnLine(0)
        assertEquals("Expected a link from each filter, got: $links", 2, links.size)
        assertContainsElements(links.map { it.first.trim() }, "lib/gald/turn.ex:38", "lib/gald/phase.ex")
        assertContainsElements(links.map { it.second.line }, 37, 53)
    }

    /** Neither filter may link a path the project does not have. */
    fun testLinksNothingOutsideTheProject() {
        val console = printToConsole(
            "    (stdlib) gen_server.erl:615: :gen_server.try_dispatch/4\n" +
                    "{:gen_server, :init_it, 6, [file: 'gen_server.erl', line: 328]}\n"
        )

        assertEmpty(console.hyperlinksOnLine(0))
        assertEmpty(console.hyperlinksOnLine(1))
    }

    private fun printToConsole(text: String): ConsoleViewImpl {
        val console = ConsoleViewImpl(project, GlobalSearchScope.allScope(project), false, false)
        Disposer.register(testRootDisposable, console)
        console.component // forces initConsoleEditor()
        console.attachToProcess(NopProcessHandler().also { it.startNotify() })
        ElixirConsoleUtil.attachFilters(project, console)

        console.print(text, ConsoleViewContentType.NORMAL_OUTPUT)
        console.flushDeferredText()
        console.waitAllRequests()
        console.getHyperlinks()!!.waitForPendingFilters(FILTER_TIMEOUT_MS)

        return console
    }

    /** Every hyperlink on [line] as (highlighted text, where it navigates). */
    private fun ConsoleViewImpl.hyperlinksOnLine(line: Int): List<Pair<String, OpenFileDescriptor>> {
        val support = getHyperlinks()!!
        val document = editor!!.document

        return support.findAllHyperlinksOnLine(line).map { highlighter ->
            val text = document.getText(TextRange(highlighter.startOffset, highlighter.endOffset))
            val info = support.getHyperlinkAt(highlighter.startOffset)
            val descriptor = requireNotNull((info as? FileHyperlinkInfo)?.descriptor) {
                "Hyperlink over '$text' resolved to no file"
            }

            text to descriptor
        }
    }

    private fun ConsoleViewImpl.onlyHyperlinkOnLine(line: Int) =
        hyperlinksOnLine(line).also {
            assertEquals("Expected exactly one hyperlink, got: $it", 1, it.size)
        }.single()

    companion object {
        /** Filtering is asynchronous; generous because a slow CI box failing here would say nothing. */
        private const val FILTER_TIMEOUT_MS = 30_000L
    }
}
