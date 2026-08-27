package org.elixir_lang.console

import com.intellij.execution.filters.FileHyperlinkInfo
import com.intellij.execution.filters.Filter
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.markup.HighlighterLayer
import org.elixir_lang.PlatformTestCase
import java.util.concurrent.Callable

/**
 * A stack trace inspected as a term - the shape a `GenServer` crash puts inside its message - prints
 * each frame's location as `[file: 'lib/gald/phase.ex', line: 75]`. [FileReferenceFilter] cannot
 * link that: it wants the digits immediately after the colon, and a keyword list has a space there.
 *
 * These pin the split between the two filters as much as the linking itself. Widening
 * [FileReferenceFilter]'s expression to cover keyword lists would make both match the same text and
 * produce two overlapping links, so `testIgnoresFormattedFrames` fails if someone tries it.
 */
class LiteralStackTraceFilterTest : PlatformTestCase() {
    fun testLinksACharlistPath() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = "{Gald.Phase, :initialize_screen, 3, [file: 'lib/gald/phase.ex', line: 75]}"

        val items = applyFilter(line)

        assertEquals("Expected exactly one link, got: ${items.describe(line)}", 1, items.size)
        val descriptor = items.single().descriptor()
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals("`line: 75` is 1-based; the descriptor is 0-based", 74, descriptor.line)
    }

    /**
     * The reworked terminal registers its own `TerminalGenericFileFilter` ahead of every plugin's
     * filters and builds the composite with `setForceUseAllFilters(true)`, so it marks up the same
     * bare path this filter does. Its link is invisible - Ctrl to follow - and opens the file at line
     * 1, because a bare path carries no line number. An exact-range tie at the same layer goes to
     * whichever was added first, which is the terminal's, so ours has to outrank it.
     *
     * [FileReferenceFilter] needs no such thing: its match covers `path:line`, a wider range than
     * the bare path, so it never ties.
     */
    fun testOutranksTheTerminalsOwnBarePathLink() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        val item = applyFilter("{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}").single()

        assertTrue(
            "A link over the same text as the terminal's own must outrank it, was ${item.highlighterLayer}",
            item.highlighterLayer > HighlighterLayer.HYPERLINK
        )
    }

    /** Elixir 1.15 and later inspect a charlist as a `~c` sigil, so the same frame reads differently. */
    fun testLinksASigilCharlistPath() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = """{Gald.Phase, :initialize_screen, 3, [file: ~c"lib/gald/phase.ex", line: 75]}"""

        val items = applyFilter(line)

        assertEquals("Expected exactly one link, got: ${items.describe(line)}", 1, items.size)
        assertEquals(74, items.single().descriptor().line)
    }

    /**
     * The reported `MatchError` carried five locations on one line. Linking only the first - which is
     * all [Filter.Result] does when a filter stops at the first match - would leave the frame the
     * reporter actually wanted unlinked.
     */
    fun testLinksEveryEntryOnTheLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = "** (MatchError) no match of right hand side value: {:error, {{:undef, [" +
                "{Gald.Phase, :initialize_screen, 3, [file: 'lib/gald/phase.ex', line: 75]}, " +
                "{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}]}}}"

        val items = applyFilter(line)

        assertEquals("Expected a link per entry, got: ${items.describe(line)}", 2, items.size)
        assertEquals(listOf(74, 53), items.map { it.descriptor().line })
    }

    /** The highlight covers the path alone - extending it to the line number would swallow `', line: `. */
    fun testHighlightsOnlyTheQuotedPath() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = "{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}"

        val item = applyFilter(line).single()

        assertEquals("lib/gald/phase.ex", line.substring(item.highlightStartOffset, item.highlightEndOffset))
    }

    /**
     * [FileReferenceFilter] already links these, and both filters run on every console line. A match
     * here would double-link them.
     */
    fun testIgnoresFormattedFrames() {
        myFixture.addFileToProject("lib/gald/turn.ex", "defmodule Gald.Turn do\nend\n")

        assertEmpty(applyFilter("    (gald) lib/gald/turn.ex:38: Gald.Turn.handle_cast/2"))
    }

    /**
     * A trace mixes Elixir with the templates and Erlang beside it, so the accepted extensions
     * cover all of them. Whether a path then resolves is a separate matter - these files exist.
     */
    fun testLinksEveryAcceptedExtension() {
        for (extension in listOf("ex", "exs", "eex", "heex", "leex", "erl")) {
            val name = "lib/gald/phase.$extension"
            val file = myFixture.addFileToProject(name, "")

            val items = applyFilter("{Gald.Phase, :init, 1, [file: '$name', line: 54]}")

            assertEquals("Expected $name to link, got: $items", 1, items.size)
            assertEquals(file.virtualFile, items.single().descriptor().file)
        }
    }

    /** An OTP frame names a file that is not in the project, so there is nothing to open. */
    fun testIgnoresAnUnresolvablePath() {
        assertEmpty(applyFilter("{:gen_server, :init_it, 6, [file: 'gen_server.erl', line: 328]}"))
    }

    /**
     * Offsets are relative to the whole console document, not to the line, so a filter that returned
     * in-line offsets would highlight the wrong text on every line but the first.
     *
     * States the contract rather than proving it - `ConsoleFiltersTest.testLinksAnEntryOnALaterLine`
     * is what shows a real console agrees. Kept because it localises a failure to the arithmetic.
     */
    fun testOffsetsAreRelativeToTheDocument() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = "{Gald.Phase, :init, 1, [file: 'lib/gald/phase.ex', line: 54]}"
        val precedingLines = 100

        val item = applyFilter(line, entireLength = precedingLines + line.length).single()

        assertEquals(precedingLines + line.indexOf("lib/gald"), item.highlightStartOffset)
    }

    /**
     * Pins the defensive flag only. Every console this plugin attaches to builds its composite with
     * `setForceUseAllFilters(true)`, so nothing today depends on it - what the user actually gets is
     * `ConsoleFiltersTest.testLinksBothFormsOnOneLine`.
     */
    fun testLetsTheOtherFilterSeeTheSameLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val line = "raised at lib/gald/phase.ex:75 from [file: 'lib/gald/phase.ex', line: 54]"

        val result = ReadAction.nonBlocking(
            Callable { LiteralStackTraceFilter(project).applyFilter(line, line.length) }
        ).executeSynchronously()

        assertNotNull("The keyword-list location should still match", result)
        assertEquals(Filter.NextAction.CONTINUE_FILTERING, result!!.nextAction)
    }

    /**
     * `inspect` breaks a keyword list one key per line once the entry is too wide, which is what
     * `IO.inspect(__STACKTRACE__)` produces at the default width. In a console whose offsets run
     * through the document, the path can be carried to the line holding the number and linked
     * there, on the line it was printed on.
     */
    fun testLinksAWrappedPathToItsOwnLine() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        assertNull(filter.feed(PATH_LINE, DOCUMENT_START))
        val item = filter.feed(NUMBER_LINE, DOCUMENT_START + PATH_LINE.length)!!.resultItems.single()

        val descriptor = item.descriptor()
        assertEquals(file.virtualFile, descriptor.file)
        assertEquals("`line: 67` is 1-based; the descriptor is 0-based", 66, descriptor.line)
        assertEquals(
            "The link belongs to the path, back on the line before the number",
            DOCUMENT_START + PATH_LINE.indexOf("lib/gald"),
            item.highlightStartOffset
        )
    }

    /**
     * The same wrap, as Elixir 1.14 and earlier print it. Every other wrapped case here uses the
     * `~c` sigil, so without this the closing quote is only ever matched against a double quote and
     * the backreference that ties it to the opening one is never tested on the older form.
     */
    fun testLinksAWrappedCharlistPathToItsOwnLine() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        assertNull(filter.feed(CHARLIST_PATH_LINE, DOCUMENT_START))
        val item = filter.feed(NUMBER_LINE, DOCUMENT_START + CHARLIST_PATH_LINE.length)!!
            .resultItems
            .single()

        assertEquals(file.virtualFile, item.descriptor().file)
        assertEquals(66, item.descriptor().line)
        assertEquals(
            DOCUMENT_START + CHARLIST_PATH_LINE.indexOf("lib/gald"),
            item.highlightStartOffset
        )
    }

    /**
     * `AsyncFilterRunner` hands the same line over more than once - observed in the sandbox, twice
     * before the number's line arrived. Re-reading a carrying line must carry the same path again
     * rather than cancel it or link it twice.
     */
    fun testACarryingLineReadTwiceStillLinksOnce() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        filter.feed(PATH_LINE, DOCUMENT_START)
        filter.feed(PATH_LINE, DOCUMENT_START)
        val items = filter.feed(NUMBER_LINE, DOCUMENT_START + PATH_LINE.length)?.resultItems.orEmpty()

        assertEquals(1, items.size)
        assertEquals(66, items.single().descriptor().line)
    }

    /**
     * The terminal console passes `applyFilter(line, line.length)`, so offsets mean nothing outside
     * the line and nothing may be carried. The path opens the file at the top instead - the number
     * is on the next line in plain sight, and a carried offset would land on the wrong text.
     */
    fun testLinksAWrappedPathToTheTopOfTheFileWithoutDocumentOffsets() {
        val file = myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        val item = filter.feed(PATH_LINE, NO_DOCUMENT)!!.resultItems.single()

        val descriptor = item.descriptor()

        assertEquals(file.virtualFile, descriptor.file)
        // The top of the file, however the descriptor chose to say so: FileHyperlinkInfoBase resolves
        // the line to an offset when the document has one, and an offset-built descriptor reports
        // line -1, while a line-built one reports offset -1.
        assertTrue(
            "Nothing may be carried, so the number is out of reach: $descriptor",
            descriptor.offset == 0 || descriptor.line == 0
        )
        assertNull("Nothing was carried, so nothing continues it", filter.feed(NUMBER_LINE, NO_DOCUMENT))
    }

    /** The number's own line has no path on it, so on its own there is nothing to link. */
    fun testIgnoresTheWrappedNumbersOwnLine() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")

        assertEmpty(applyFilter(NUMBER_LINE))
    }

    /**
     * The other document-backed pipeline, `Filter.applyToLineRange`, drives the Reworked terminal,
     * which this plugin does register filters with. It includes the terminator too, so consecutive
     * lines abut there as well - except on the last line of a batch, where it appends a synthetic
     * `"
"` and the next line starts one past the end. Both count as adjacent; getting this wrong
     * loses every wrapped link in that console while every fixture still passes.
     */
    fun testTreatsALineTerminatorGapAsAdjacent() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)
        val unterminated = PATH_LINE.trimEnd('\n')

        filter.feed(unterminated, DOCUMENT_START)
        val result = filter.feed(NUMBER_LINE, DOCUMENT_START + unterminated.length + 1)

        assertNotNull("A one-character terminator gap must still link", result)
        assertEquals(66, result!!.resultItems.single().descriptor().line)
    }

    /**
     * `applyToLineRange` iterates `startLineInclusive downTo endLineInclusive` when the range is
     * reversed, so the number's line can arrive before the path's. Carrying backwards would put the
     * link on text that is not the path.
     */
    fun testDoesNotCarryAPathBackwards() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        filter.feed(PATH_LINE, DOCUMENT_START)

        assertNull(filter.feed(NUMBER_LINE, DOCUMENT_START - NUMBER_LINE.length))
    }

    /**
     * A carried path is keyed on where the next line must start, so a line that did not follow it
     * cannot spend it - which is what stops a carried offset landing on unrelated text.
     */
    fun testDoesNotCarryAPathAcrossAGap() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)

        filter.feed(PATH_LINE, DOCUMENT_START)

        assertNull(filter.feed(NUMBER_LINE, DOCUMENT_START + PATH_LINE.length + 900))
    }

    /**
     * A path with its number on the same line must not also be carried, or the entry would get a
     * second link from whatever line came next.
     */
    fun testDoesNotCarryAWholeEntry() {
        myFixture.addFileToProject("lib/gald/phase.ex", "defmodule Gald.Phase do\nend\n")
        val filter = LiteralStackTraceFilter(project)
        val entry = """   [file: ~c"lib/gald/phase.ex", line: 157]},"""

        val items = filter.feed(entry, DOCUMENT_START)!!.resultItems

        assertEquals(1, items.size)
        assertEquals(156, items.single().descriptor().line)
        assertNull(filter.feed(NUMBER_LINE, DOCUMENT_START + entry.length))
    }

    /**
     * Hands one line over the way a console does, from where it starts in the document - which is
     * how the platform expresses it too, as the end of the line before. A console with no document
     * starts every line at 0.
     */
    private fun LiteralStackTraceFilter.feed(line: String, lineStartOffset: Int) =
        ReadAction.nonBlocking(
            Callable { applyFilter(line, lineStartOffset + line.length) }
        ).executeSynchronously()

    private fun applyFilter(line: String, entireLength: Int = line.length): List<Filter.ResultItem> =
        ReadAction.nonBlocking(
            Callable { LiteralStackTraceFilter(project).applyFilter(line, entireLength) }
        ).executeSynchronously()?.resultItems.orEmpty()

    private fun Filter.ResultItem.descriptor() =
        requireNotNull((hyperlinkInfo as FileHyperlinkInfo).descriptor) {
            "Hyperlink resolved to no file"
        }

    private fun List<Filter.ResultItem>.describe(line: String) =
        joinToString(", ", "[", "]") { line.substring(it.highlightStartOffset, it.highlightEndOffset) }

    companion object {
        /** As `inspect` wraps them, terminator included the way the console hands a line over. */
        private const val PATH_LINE = "     file: ~c\"lib/gald/phase.ex\",\n"

        /** The same line as Elixir 1.14 and earlier inspect it. */
        private const val CHARLIST_PATH_LINE = "     file: 'lib/gald/phase.ex',\n"

        private const val NUMBER_LINE = "     line: 67,\n"

        /**
         * Any non-zero start: what marks a console whose offsets run through its document. The
         * value is the one the sandbox showed for a real wrapped entry, kept for recognisability.
         */
        private const val DOCUMENT_START = 1257

        /** Where every line starts in a console that has no document, such as the terminal. */
        private const val NO_DOCUMENT = 0
    }
}
