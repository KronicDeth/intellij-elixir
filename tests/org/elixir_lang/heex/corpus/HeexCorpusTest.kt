package org.elixir_lang.heex.corpus

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase

/**
 * Hand-written `.heex` fixtures under `testData/org/elixir_lang/heex/corpus/`, each asserted to
 * produce zero `PsiErrorElement` across every PSI root. Hand-written rather than vendored from
 * `phoenix_live_view` so each fixture targets this plugin's own edge cases without third-party content.
 */
class HeexCorpusTest : PlatformTestCase() {
    private fun assertNoParseErrors(fileName: String) {
        val text = java.io.File(testDataPath, fileName).readText()
        val file = myFixture.configureByText(fileName, text)

        for (root in file.viewProvider.allFiles) {
            val errors = PsiTreeUtil.findChildrenOfType(root, PsiErrorElement::class.java)
            assertTrue(
                "expected no PsiErrorElement in the ${root.language.id} root of $fileName, found: " +
                    errors.joinToString { "\"${it.errorDescription}\" at ${it.textRange}" },
                errors.isEmpty()
            )
        }
    }

    fun testScriptAndStyleComponents() {
        assertNoParseErrors("scriptAndStyleComponents.html.heex")
    }

    fun testBraceNesting() {
        assertNoParseErrors("braceNesting.html.heex")
    }

    fun testSlotsAndComments() {
        assertNoParseErrors("slotsAndComments.html.heex")
    }

    fun testEexAcrossTags() {
        assertNoParseErrors("eexAcrossTags.html.heex")
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/corpus"
}
