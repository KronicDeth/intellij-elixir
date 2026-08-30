package org.elixir_lang.structure_view.element

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.moduleAttributeName
import org.elixir_lang.structure_view.Model

/**
 * A `@spec` head the parser resolves to no function name must not crash the structure view.
 *
 * `@spec foo.() :: term` and `@spec bar not in baz :: term` are the shapes an editor sees mid-edit;
 * both parse to a [org.elixir_lang.psi.call.Call] whose `functionNameElement()` is a hard-coded
 * `null`, which `typeNameArity` used to force-unwrap.
 */
class CallDefinitionSpecificationTest : PlatformTestCase() {
    private fun specAttributes(): List<AtUnqualifiedNoParenthesesCall<*>> =
        PsiTreeUtil
            .findChildrenOfType(myFixture.file, AtUnqualifiedNoParenthesesCall::class.java)
            .filter { moduleAttributeName(it) == "@spec" }

    private fun walk(element: StructureViewTreeElement) {
        for (child in element.children) {
            if (child is StructureViewTreeElement) {
                walk(child)
            }
        }
    }

    private fun walkStructureView() = walk(Model(myFixture.file as ElixirFile, null).root)

    fun testHeadWithoutFunctionNameHasNoNameArity() {
        myFixture.configureByText(
            "spec_head_without_function_name.ex",
            "defmodule A do\n" +
                "  @spec foo.() :: term\n" +
                "  @spec bar not in baz :: term\n" +
                "  def foo, do: :ok\n" +
                "end\n"
        )

        val nameArities = specAttributes().map { CallDefinitionSpecification.moduleAttributeNameArity(it) }

        assertEquals(2, nameArities.size)
        assertEquals(listOf(null, null), nameArities)
    }

    fun testHeadWithFunctionNameStillHasNameArity() {
        myFixture.configureByText(
            "spec_head_with_function_name.ex",
            "defmodule A do\n" +
                "  @spec ok(term) :: term\n" +
                "  def ok(x), do: x\n" +
                "end\n"
        )

        val nameArity = specAttributes().single().let { CallDefinitionSpecification.moduleAttributeNameArity(it) }

        assertEquals(org.elixir_lang.NameArity("ok", 1), nameArity)
    }

    fun testStructureViewDoesNotThrowOnHeadWithoutFunctionName() {
        myFixture.configureByText(
            "structure_view_spec_head.ex",
            "defmodule A do\n" +
                "  @spec foo.() :: term\n" +
                "  @spec bar not in baz :: term\n" +
                "  @spec baz.() :: term when term: any\n" +
                "  def foo, do: :ok\n" +
                "end\n"
        )

        walkStructureView()
    }
}
