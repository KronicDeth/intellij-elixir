package org.elixir_lang.heex.corpus

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.heexSigilModuleText
import org.elixir_lang.heex.reference.injectedHeexRoots

/**
 * Hand-written `.heex` fixtures under `testData/org/elixir_lang/heex/corpus/`, each asserted to
 * produce zero `PsiErrorElement` across every PSI root, as a `.heex` file and wrapped in a `~H`
 * sigil. Hand-written rather than vendored from `phoenix_live_view` so each fixture targets this
 * plugin's own edge cases without third-party content.
 */
class HeexCorpusTest : HeexHostTestCase() {
    private fun assertNoParseErrors(fileName: String) {
        val text = java.io.File(testDataPath, fileName).readText()
        val file = myFixture.configureByText(fileName, text)
        assertNoParseErrorsInRoots(file.viewProvider.allFiles, fileName)
    }

    private fun assertNoParseErrorsInSigil(fileName: String) {
        val text = java.io.File(testDataPath, fileName).readText()
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(entranceModuleText = "defmodule Test do\nend", heexBody = text)
        )
        assertNoParseErrorsInRoots(myFixture.injectedHeexRoots().single().viewProvider.allFiles, "$fileName (~H)")
    }

    private fun assertNoParseErrorsInRoots(roots: List<PsiFile>, label: String) {
        for (root in roots) {
            val errors = PsiTreeUtil.findChildrenOfType(root, PsiErrorElement::class.java)
            assertTrue(
                "expected no PsiErrorElement in the ${root.language.id} root of $label, found: " +
                    errors.joinToString { "\"${it.errorDescription}\" at ${it.textRange}" },
                errors.isEmpty()
            )
        }
    }

    fun testScriptAndStyleComponents() {
        assertNoParseErrors("scriptAndStyleComponents.html.heex")
    }

    fun testScriptAndStyleComponentsInsideHSigil() {
        assertNoParseErrorsInSigil("scriptAndStyleComponents.html.heex")
    }

    fun testBraceNesting() {
        assertNoParseErrors("braceNesting.html.heex")
    }

    fun testBraceNestingInsideHSigil() {
        assertNoParseErrorsInSigil("braceNesting.html.heex")
    }

    fun testSlotsAndComments() {
        assertNoParseErrors("slotsAndComments.html.heex")
    }

    fun testSlotsAndCommentsInsideHSigil() {
        assertNoParseErrorsInSigil("slotsAndComments.html.heex")
    }

    fun testEexAcrossTags() {
        assertNoParseErrors("eexAcrossTags.html.heex")
    }

    fun testEexAcrossTagsInsideHSigil() {
        assertNoParseErrorsInSigil("eexAcrossTags.html.heex")
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/corpus"
}
