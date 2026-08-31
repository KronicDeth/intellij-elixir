package org.elixir_lang.psi.impl.qualifiable_alias

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirEexTag
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.outerMostQualifiableAlias

/**
 * https://github.com/KronicDeth/intellij-elixir/issues/3278
 *
 * `QualifiableAliasImpl.prependQualifiers` walks from an alias toward the file root and matches each
 * ancestor against a `when`. An EEx tag was absent from it, so an alias written inside `<%= ... %>`
 * fell to the catch-all, which logs "Don't know how to prepend qualifier" and returns a name
 * prefixed `"?."` - a name that resolves to nothing. Every report of it named
 * `org.elixir_lang.psi.impl.ElixirEexTagImpl` as the unrecognised ancestor.
 *
 * Two things catch a revert here. [PlatformTestCase]'s test logger turns the catch-all's own
 * `Logger.error` into a failure carrying the reporters' `ElixirEexTagImpl` body verbatim; and the
 * name assertion catches the same regression if that logging is ever removed or downgraded, since
 * `"?.Foo"` resolves to nothing whether or not anything was logged.
 */
class EexTagFullyQualifiedNameTest : PlatformTestCase() {
    fun testBareAliasInAnEexTagIsFullyQualified() {
        assertFullyQualifiedName("template.eex", "<%= Foo %>\n", "Foo")
    }

    fun testQualifiedAliasInAnEexTagIsFullyQualified() {
        assertFullyQualifiedName("template.eex", "<%= Foo.Bar %>\n", "Foo.Bar")
    }

    fun testAliasInAnHtmlEexTagIsFullyQualified() {
        assertFullyQualifiedName("page.html.eex", "<div>\n  <%= Foo.Bar %>\n</div>\n", "Foo.Bar")
    }

    private fun assertFullyQualifiedName(fileName: String, text: String, expected: String) {
        myFixture.configureByText(fileName, text)

        val elixirRoot = checkNotNull(myFixture.file.viewProvider.getPsi(ElixirLanguage)) {
            "Expected an Elixir root in $fileName"
        }
        val alias = checkNotNull(PsiTreeUtil.findChildOfType(elixirRoot, QualifiableAlias::class.java)) {
            "Expected an alias in the Elixir root of $fileName: ${elixirRoot.text}"
        }.outerMostQualifiableAlias()

        // Without this the assertion below could pass because the tag never became an ancestor at
        // all, which is a different tree and would not exercise the branch under test.
        assertNotNull(
            "Expected the alias to sit inside an EEx tag, or this does not exercise prependQualifiers' EEx branch",
            PsiTreeUtil.getParentOfType(alias, ElixirEexTag::class.java)
        )

        assertEquals(
            "Alias inside an EEx tag should qualify to its own name, not the catch-all's \"?.\" prefix",
            expected,
            alias.fullyQualifiedName()
        )
    }
}
