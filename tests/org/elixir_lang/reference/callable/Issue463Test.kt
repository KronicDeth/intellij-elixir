package org.elixir_lang.reference.callable

import org.elixir_lang.semantic.call.definition.Clause.`is`
import com.intellij.psi.PsiPolyVariantReference
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

class Issue463Test : PlatformTestCase() {
    fun testDirectModuleQualifier() {
        myFixture.configureByFiles("direct_module_qualifier.ex", "referenced.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val grandParent = elementAtCaret!!.parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)

        val reference = grandParent.reference
        assertNotNull(reference)

        val resolved = reference!!.resolve()
        assertNotNull(resolved)
        assertInstanceOf(resolved, Call::class.java)
        assertEquals(
                """def changeset(params) do
    %__MODULE__{}
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end""",
                resolved!!.text
        )
        assertInstanceOf(resolved, Call::class.java)
        assertTrue(`is`((resolved as Call?)!!))
    }

    fun testAliasedModuleQualifier() {
        myFixture.configureByFiles("aliased_module_qualifier.ex", "referenced.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val grandParent = elementAtCaret!!.parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)

        val reference = grandParent.reference
        assertNotNull(reference)

        val resolved = reference!!.resolve()
        assertNotNull(resolved)
        assertInstanceOf(resolved, Call::class.java)
        assertEquals(
                """def changeset(params) do
    %__MODULE__{}
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end""",
                resolved!!.text
        )
        assertInstanceOf(resolved, Call::class.java)
        assertTrue(`is`((resolved as Call?)!!))
    }

    fun testDoubleAliasesModuleQualifier() {
        myFixture.configureByFiles("double_aliased_module_qualifier.ex", "referenced.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val grandParent = elementAtCaret!!.parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)

        val reference = grandParent.reference
        assertNotNull(reference)

        val resolved = reference!!.resolve()
        assertNotNull(resolved)
        assertEquals(resolved!!.text, """def changeset(params) do
    %__MODULE__{}
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end""")
    }

    fun testMapAccessQualifier() {
        myFixture.configureByFiles("map_access_qualifier.ex", "referenced.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val grandParent = elementAtCaret!!.parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)

        val reference = grandParent.reference
        assertNotNull(reference)

        val resolved = reference!!.resolve()
        assertNull(resolved)
    }

    fun testUnresolvedAliasQualifier() {
        myFixture.configureByFiles("unresolved_alias_qualifier.ex", "referenced.ex")
        val elementAtCaret = myFixture
                .file
                .findElementAt(myFixture.caretOffset)
        assertNotNull(elementAtCaret)

        val grandParent = elementAtCaret!!.parent.parent
        assertNotNull(grandParent)
        assertInstanceOf(grandParent, Call::class.java)

        val reference = grandParent.reference
        assertNotNull(reference)
        assertInstanceOf(reference, PsiPolyVariantReference::class.java)
        val psiPolyVariantReference = reference as PsiPolyVariantReference

        val resolveResults = psiPolyVariantReference.multiResolve(false)
        assertEquals(1, resolveResults.size)

        val firstResolveResult = resolveResults[0]
        assertFalse(firstResolveResult.isValidResult)
        val firstElement = firstResolveResult.element
        assertNotNull(firstElement)
        assertEquals(firstElement!!.text, """def changeset(params) do
    %__MODULE__{}
    |> cast(params, ~w(name))
    |> validate_required(:name)
  end""")
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/callable/issue_463"
}
