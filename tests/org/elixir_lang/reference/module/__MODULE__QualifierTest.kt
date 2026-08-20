package org.elixir_lang.reference.module

import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.QualifiedAlias

/**
 * Tests for qualified aliases whose qualifier is a `__MODULE__` call, e.g.
 * `__MODULE__.Endpoint.config_change(changed, removed)` inside `defmodule MyApp`.
 *
 * The `Module` reference for `__MODULE__.Endpoint` lives on the whole [QualifiedAlias] node, and
 * resolution starts from [QualifiedAlias.fullyQualifiedName].  `getName()` for a [QualifiedAlias]
 * is the raw source text, which for an Alias-only chain happens to equal the resolvable module
 * name - but for a call qualifier the text (`__MODULE__.Endpoint`) matches nothing in the module
 * name index.  The qualifier must be resolved to the enclosing module so the fully-qualified name
 * becomes `MyApp.Endpoint`.
 */
@Suppress("ClassName") // named for the `__MODULE__` special form, matching `org.elixir_lang.psi.__MODULE__`
class __MODULE__QualifierTest : PlatformTestCase() {

    private fun qualifiedAliasWithText(text: String): QualifiedAlias =
        PsiTreeUtil.findChildrenOfType(myFixture.file, QualifiedAlias::class.java)
            .single { it.text == text }

    fun testFullyQualifiedNameExpandsModuleQualifier() {
        myFixture.configureByText(
            "my_app.ex",
            """
            defmodule MyApp do
              def config_change(changed, removed) do
                __MODULE__.Endpoint.config_change(changed, removed)
              end
            end
            """.trimIndent()
        )

        assertEquals(
            "MyApp.Endpoint",
            qualifiedAliasWithText("__MODULE__.Endpoint").fullyQualifiedName()
        )
    }

    /**
     * The qualifier expansion must recurse: in `__MODULE__.Foo.Bar` the outer qualified alias's
     * qualifier is itself a qualified alias (`__MODULE__.Foo`) whose own qualifier is the call.
     */
    fun testFullyQualifiedNameExpandsNestedModuleQualifier() {
        myFixture.configureByText(
            "my_app.ex",
            """
            defmodule MyApp do
              def child_spec(arg) do
                __MODULE__.Foo.Bar.child_spec(arg)
              end
            end
            """.trimIndent()
        )

        assertEquals(
            "MyApp.Foo.Bar",
            qualifiedAliasWithText("__MODULE__.Foo.Bar").fullyQualifiedName()
        )
    }

    fun testReferenceResolvesToModuleDefinition() {
        myFixture.addFileToProject(
            "endpoint.ex",
            """
            defmodule MyApp.Endpoint do
            end
            """.trimIndent()
        )
        myFixture.configureByText(
            "my_app.ex",
            """
            defmodule MyApp do
              def config_change(changed, removed) do
                __MODULE__.Endpoint.config_change(changed, removed)
              end
            end
            """.trimIndent()
        )

        val reference = qualifiedAliasWithText("__MODULE__.Endpoint").reference as PsiPolyVariantReference?
        assertNotNull("__MODULE__.Endpoint must have a Module reference", reference)

        val resolvedTexts = reference!!
            .multiResolve(false)
            .mapNotNull { it.element?.text }
        assertTrue(
            "__MODULE__.Endpoint inside defmodule MyApp must resolve to " +
                "`defmodule MyApp.Endpoint`. Resolved: $resolvedTexts",
            resolvedTexts.any { it.startsWith("defmodule MyApp.Endpoint") }
        )
    }
}
