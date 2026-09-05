package org.elixir_lang.psi.impl.qualifiable_alias

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.QualifiableAlias

/**
 * Building an alias's fully-qualified name walks up through the containers that can qualify it and
 * stops at the first that cannot. A container the walk does not name ends qualification the same
 * way; it is not an error. Likewise a call qualifier that resolves to no module contributes a
 * placeholder segment rather than a report.
 */
class UnhandledContainerTest : PlatformTestCase() {
    fun testAliasInsideBitStringIsItsOwnName() {
        assertFullyQualifiedName("<<Fo<caret>o>>\n", "Foo")
    }

    fun testCallQualifierResolvingToNoModuleIsAPlaceholder() {
        assertFullyQualifiedName("__MODULE__.Fo<caret>o\n", "?.Foo")
    }

    private fun assertFullyQualifiedName(text: String, expected: String) {
        myFixture.configureByText("alias.ex", text)
        val alias = PsiTreeUtil.getParentOfType(myFixture.file.findElementAt(myFixture.caretOffset), ElixirAlias::class.java)
        assertNotNull("caret is not on an alias", alias)

        val (name, errors) = captureLoggedErrors { (alias as QualifiableAlias).fullyQualifiedName() }

        assertEmpty("an unqualifiable container is not an error", errors)
        assertEquals(expected, name)
    }
}
