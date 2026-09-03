package org.elixir_lang.reference.callable

import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/926
 *
 * An unqualified call in a Phoenix `.eex` template resolves into the template's view module. The
 * walk out of the template is `ElixirFile.viewFile()` (which finds `views/<dir>_view.ex` for a
 * template under `templates/<dir>/`) plus `PsiScopeProcessor.maxScope()`, which lets the callable
 * resolver continue past the template file into that module.
 *
 * Shipped in v11.11.0 (2021-06-22) and untested since, which is why this exists: nothing else in the
 * suite uses the `.eex` file type for resolution, so a regression here would be silent.
 */
class Issue926Test : PlatformTestCase() {
    fun testUnqualifiedCallResolvesToViewModule() {
        myFixture.configureByFiles(
                "lib/my_app_web/templates/page/index.html.eex",
                "lib/my_app_web/views/page_view.ex"
        )

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("no element at caret in the template", elementAtCaret)

        val call = generateSequence(elementAtCaret) { it.parent }.filterIsInstance<Call>().firstOrNull()
        assertNotNull("caret is not inside a Call", call)
        assertEquals("greeting", call!!.functionName())

        val reference = call.reference
        assertNotNull("the unqualified call has no reference", reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)

        val resolveResults = (reference as PsiPolyVariantReference).multiResolve(false)
        assertTrue("`greeting(\"world\")` resolved to nothing from the template", resolveResults.isNotEmpty())

        val resolved = resolveResults.first { it.isValidResult }.element
        assertNotNull(resolved)

        // The definition it lands on is the one in the view module, not anything in the template.
        assertTrue(
                "resolved to `${resolved!!.text}` rather than the view module's `greeting/1`",
                resolved.text.startsWith("def greeting(name)")
        )
        assertEquals(
                "resolved outside page_view.ex",
                "page_view.ex",
                resolved.containingFile.name
        )
    }

    /**
     * The template's own file knows which view module it belongs to. Asserted separately from the
     * reference walk above so a regression says which half broke.
     */
    fun testViewFileIsFoundForTemplate() {
        myFixture.configureByFiles(
                "lib/my_app_web/templates/page/index.html.eex",
                "lib/my_app_web/views/page_view.ex"
        )

        val templateRoot = myFixture.file.viewProvider.allFiles.filterIsInstance<ElixirFile>().firstOrNull()
        assertNotNull("no Elixir root in the .eex template's view provider", templateRoot)

        val viewFile = templateRoot!!.viewFile()
        assertNotNull("viewFile() did not find views/page_view.ex for templates/page/index.html.eex", viewFile)
        assertEquals("page_view.ex", viewFile!!.name)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_926"
}
