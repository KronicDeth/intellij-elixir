package org.elixir_lang.heex.file

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.PsiFile
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.configureHeexSigilHost
import org.elixir_lang.heex.reference.fixtureText
import org.elixir_lang.heex.reference.heexSigilModuleText
import org.elixir_lang.psi.EexDataAstFactory
import org.elixir_lang.psi.ElixirTypes

/**
 * [EexDataAstFactory] makes `EEx Data` leaves [OuterLanguageElement]s, so a caret at a component
 * tag's offset resolves to the HTML root deterministically in a `.heex` file and a `~H` sigil.
 */
class EexDataAstFactoryTest : HeexHostTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/file/eex_data"
    /** A real `.heex` file's Elixir root has no non-outer match outside an interpolation. */
    fun testEexDataIsOuterLanguageElementInHeexFile() {
        myFixture.configureByFiles("page_live.html.heex", "page_live.ex")
        assertEexDataIsOuterLanguageElement(myFixture.file)
    }

    /** Same, for the Elixir root of an injected `~H` fragment. */
    fun testEexDataIsOuterLanguageElementInsideHSigil() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("page_live.ex"),
                heexBody = myFixture.fixtureText("page_live.html.heex")
            )
        )
        assertEexDataIsOuterLanguageElement(myFixture.file)
    }

    /** At a component tag's offset, the caret resolves to the HTML root of a real `.heex` file. */
    fun testCaretAtTagOffsetResolvesToHtmlRootInHeexFile() {
        myFixture.configureByFiles("page_live.html.heex", "page_live.ex")
        assertCaretResolvesToHtmlRoot()
    }

    /** Same, with the tag inside a `~H` sigil instead of a separate `.heex` file. */
    fun testCaretAtTagOffsetResolvesToHtmlRootInsideHSigil() {
        myFixture.configureHeexSigilHost(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("page_live.ex"),
                heexBody = myFixture.fixtureText("page_live.html.heex")
            )
        )
        assertCaretResolvesToHtmlRoot()
    }

    private fun assertEexDataIsOuterLanguageElement(hostOrInjectedFile: PsiFile) {
        val elixirRoot = checkNotNull(hostOrInjectedFile.viewProvider.getPsi(ElixirLanguage)) {
            "Expected an Elixir root in ${hostOrInjectedFile.name}"
        }
        val eexDataLeaves = PsiTreeUtil.collectElements(elixirRoot) { it.node?.elementType == ElixirTypes.EEX_DATA }
        assertTrue("Expected at least one EEx Data leaf in: ${elixirRoot.text}", eexDataLeaves.isNotEmpty())
        for (leaf in eexDataLeaves) {
            assertTrue(
                "Expected EEx Data leaf '${leaf.text}' to be an OuterLanguageElement, was ${leaf::class.java.name}",
                leaf is OuterLanguageElement
            )
        }
    }

    private fun assertCaretResolvesToHtmlRoot() {
        val resolvedFile = PsiUtilBase.getPsiFileInEditor(myFixture.editor, project)
        assertNotNull("Expected a PSI file at the caret", resolvedFile)
        assertTrue(
            "Expected the caret to resolve to the HTML root, got language: ${resolvedFile!!.language}",
            resolvedFile.viewProvider.getPsi(HTMLLanguage.INSTANCE) === resolvedFile
        )
    }
}
