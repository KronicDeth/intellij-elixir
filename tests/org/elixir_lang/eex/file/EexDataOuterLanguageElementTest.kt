package org.elixir_lang.eex.file

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.EexDataAstFactory
import org.elixir_lang.psi.ElixirTypes

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/1833
 *
 * [EexDataAstFactory] is registered for the Elixir language and keys on [ElixirTypes.EEX_DATA],
 * which the EEx lexer emits as well as the HEEx one, so a `.html.eex` file's Elixir root gets the
 * same outer leaves a `.heex` file's does. Without them the Elixir and HTML roots both answer at a
 * markup offset and `MultiplePsiFilesPerDocumentFileViewProvider.findElementAt` returns whichever
 * the view provider's unordered language set yields last, which is fixed within a JVM run and
 * varies between them.
 *
 * [testEexDataLeavesAreOuterLanguageElementsInAnHtmlEexFile] is the load-bearing assertion, because
 * it is the deterministic one: unregistering the factory fails it outright, while the caret below
 * still resolves to HTML in whichever runs the unordered set happens to favour.
 */
class EexDataOuterLanguageElementTest : PlatformTestCase() {
    fun testEexDataLeavesAreOuterLanguageElementsInAnHtmlEexFile() {
        configureHtmlEex()

        val elixirRoot = checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage)) {
            "Expected an Elixir root in ${myFixture.file.name}"
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

    fun testCaretInMarkupResolvesToTheHtmlRootOfAnHtmlEexFile() {
        configureHtmlEex()

        // Without this the HTML root could be winning uncontested, and the assertion below would
        // pass for the wrong reason: the point is that two roots answer at this offset.
        val elixirRoot = checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage))
        assertNotNull(
            "Expected the Elixir root to have a leaf at the caret, or the roots are not in contention",
            elixirRoot.findElementAt(myFixture.caretOffset)
        )

        val resolvedFile = PsiUtilBase.getPsiFileInEditor(myFixture.editor, project)
        assertNotNull("Expected a PSI file at the caret", resolvedFile)
        assertTrue(
            "Expected the caret to resolve to the HTML root, got language: ${resolvedFile!!.language}",
            resolvedFile.viewProvider.getPsi(HTMLLanguage.INSTANCE) === resolvedFile
        )
    }

    private fun configureHtmlEex() {
        myFixture.configureByText("page.html.eex", "<div>\n  gree<caret>ting\n</div>\n<%= @greeting %>\n")
    }
}
