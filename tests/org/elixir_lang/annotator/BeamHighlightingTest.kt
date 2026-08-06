package org.elixir_lang.annotator

import com.intellij.openapi.editor.colors.EditorColorsManager
import org.elixir_lang.beam.BeamLibraryTestCase
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirSyntaxHighlighter

/**
 * Semantic (annotator) highlighting over decompiled `.beam` files - see [Highlighter] for why the severity
 * matters.
 *
 * The three tests are a set and must stay together: [testSourceElixirFileGetsSemanticHighlighting] shows the
 * harness can see annotator output at all, and
 * [testDecompiledTextGetsSemanticHighlightingAsPlainElixirSource] shows the decompiler's output is
 * highlightable. If either goes red, [testDecompiledBeamFunctionNameGetsSemanticHighlighting] proves nothing.
 */
class BeamHighlightingTest : BeamLibraryTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    fun testSourceElixirFileGetsSemanticHighlighting() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")

        // Forced attributes specifically, not just any info: injected fragments and inspections would satisfy
        // a bare isNotEmpty() without any annotator having run.
        assertTrue(
            "The harness must produce annotator HighlightInfos for ordinary Elixir source",
            myFixture.doHighlighting().any { it.forcedTextAttributes != null }
        )
    }

    /** Highlights the *identical* decompiled text as ordinary source, leaving compiled-file handling as the
     * only difference from [testDecompiledBeamFunctionNameGetsSemanticHighlighting]. */
    fun testDecompiledTextGetsSemanticHighlightingAsPlainElixirSource() {
        openBeam("queue.beam")
        val decompiledText = myFixture.editor.document.text

        myFixture.configureByText(ElixirFileType.INSTANCE, decompiledText)

        assertConsFunctionNameIsHighlighted("the same text as a plain .ex source file")
    }

    fun testDecompiledBeamFunctionNameGetsSemanticHighlighting() {
        openBeam("queue.beam")

        assertConsFunctionNameIsHighlighted("the decompiled queue.beam")
    }

    /**
     * Shared so the decompiled and plain-source cases differ *only* in what was configured. Offsets come from
     * the live document because the fixture `.beam` decompiles to a different length than an SDK one.
     *
     * Compares resolved attributes rather than the [ElixirSyntaxHighlighter.FUNCTION_DECLARATION] key because
     * the key is not observable on an annotator's `HighlightInfo` (see [Highlighter]). Known weakness: another
     * annotator emitting attributes that resolve identically over an overlapping range would satisfy this.
     */
    private fun assertConsFunctionNameIsHighlighted(what: String) {
        val clause = "def cons(x, q)"
        val clauseIndex = myFixture.editor.document.text.indexOf(clause)
        assertTrue("Clause '$clause' not found in $what", clauseIndex >= 0)

        val nameStart = clauseIndex + "def ".length
        val nameEnd = nameStart + "cons".length

        val expected = EditorColorsManager
            .getInstance()
            .globalScheme
            .getAttributes(ElixirSyntaxHighlighter.FUNCTION_DECLARATION)

        val covering = myFixture
            .doHighlighting()
            .filter { it.startOffset <= nameStart && it.endOffset >= nameEnd }

        assertTrue(
            "The `cons` function name in $what should carry " +
                "${ElixirSyntaxHighlighter.FUNCTION_DECLARATION.externalName} attributes; " +
                "${covering.size} HighlightInfo(s) cover $nameStart..$nameEnd: " +
                covering.joinToString(" ; ") { "${it.startOffset}..${it.endOffset} ${it.forcedTextAttributes}" },
            covering.any { it.forcedTextAttributes == expected }
        )
    }
}
