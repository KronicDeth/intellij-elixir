package org.elixir_lang.reference.callable

import com.intellij.psi.PsiErrorElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call

/**
 * A variable used inside an EEx tag has no declaration to find, so its use scope is empty.
 * `Callable.variableUseScope` reaches that answer by recognising the `ElixirEexTag` it walks up onto;
 * without that case the tag fell through to the catch-all and the plugin reported
 * `Don't know how to find variable use scope` at the user instead.
 *
 * #1831, #1849, #1851, #1772 and #3004 all report exactly that, every one naming
 * `org.elixir_lang.psi.impl.ElixirEexTagImpl` as the element class.
 */
class Issue1831Test : PlatformTestCase() {
    /**
     * A bare `<%= subject %>`, whose parent is the tag itself, so the walk lands on it immediately.
     * Run in both extensions because #1849 and #1851 differ in that and nothing else.
     */
    fun testBareTagVariableHasEmptyUseScope() {
        for (extension in listOf("eex", "leex")) {
            assertEmptyUseScopeAndNoError(
                "index.html.$extension",
                condBlockAround("""<%= subj<caret>ect %>""")
            )
        }
    }

    /** The qualifier of a call, so the walk climbs the dot call before it reaches the tag. */
    fun testQualifierOfCallInTagHasEmptyUseScope() {
        assertEmptyUseScopeAndNoError(
            "tokens.html.eex",
            condBlockAround("""<%= tok<caret>en.transaction %>""")
        )
    }

    /** An argument to a call, so the walk passes through that call before it reaches the tag. */
    fun testVariableArgumentInTagHasEmptyUseScope() {
        assertEmptyUseScopeAndNoError(
            "filters.html.eex",
            condBlockAround("""<%= text_input @f, fie<caret>ld %>""")
        )
    }

    /**
     * Wraps [tag] in a `cond` block, which is load-bearing rather than decoration.
     *
     * `UseScopeImpl.get` only consults `variableUseScope` once `Callable.isVariable` is true, and
     * `isVariable` walks the same ancestors: from a tag at file scope it reaches the `PsiFile` and
     * answers false, so the use scope comes back as the module-with-dependents fallback and the EEx
     * case is never reached. A stab clause is what makes it answer true, which is why a test written
     * on a bare top-level tag would pass even with the EEx case removed.
     */
    private fun condBlockAround(tag: String): String =
        """<%= cond do %><% true -> %><p>$tag</p><% end %>"""

    /**
     * Asserts that the call at the caret has an empty use scope and that reaching that answer logged
     * nothing.
     *
     * Both halves matter: the catch-all returns `LocalSearchScope.EMPTY` too and merely complains on
     * the way, so a test asserting only the scope would pass against the defect.
     */
    private fun assertEmptyUseScopeAndNoError(fileName: String, text: String) {
        myFixture.configureByText(fileName, text)

        val elixirRoot = myFixture.file.viewProvider.allFiles.filterIsInstance<ElixirFile>().first()
        assertEmpty(
            "$fileName did not parse cleanly, so the use scope is not what is under test",
            PsiTreeUtil.findChildrenOfType(elixirRoot, PsiErrorElement::class.java)
        )

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
        val call = generateSequence(elementAtCaret) { it.parent }.filterIsInstance<Call>().firstOrNull()
        assertNotNull("caret is not inside a Call in $fileName", call)

        val (useScope, loggedErrors) = captureLoggedErrors { call!!.useScope }

        assertEmpty(
            "finding the use scope of a variable in an EEx tag reported an error at the user: " +
                "${loggedErrors.map { it.title }}. ElixirEexTag must be recognised by " +
                "Callable.variableUseScope rather than reaching its catch-all.",
            loggedErrors.filter { it.category.contains("elixir_lang.reference.Callable") }
        )
        assertSame(
            "a variable in an EEx tag should have no use scope, since it has no declaration to find",
            LocalSearchScope.EMPTY,
            useScope as SearchScope
        )
    }
}
