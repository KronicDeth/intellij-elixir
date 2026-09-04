package org.elixir_lang.psi.impl.declarations

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.scope.type.MultiResolve

/**
 * A `when` guard on a `@spec` declares type variables through keyword restrictions. A guard that is
 * something else, here a string, declares none, and meeting it while resolving a type is not an
 * error.
 */
class UnhandledTypeRestrictionTest : PlatformTestCase() {
    fun testUnrestrictedGuardDeclaresNothingWithoutAnError() {
        myFixture.configureByText(
            "guard.ex",
            """
            defmodule Guard do
              @spec f(a) :: <caret>a when "x"
              def f(a), do: a
            end
            """.trimIndent()
        )
        val usage = PsiTreeUtil.getParentOfType(
            myFixture.file.findElementAt(myFixture.caretOffset),
            UnqualifiedNoArgumentsCall::class.java
        )
        assertNotNull("caret is not on a type usage", usage)

        val (_, errors) = captureLoggedErrors { MultiResolve.resolveResults("a", 0, false, usage!!) }

        assertEmpty("a guard that restricts nothing is not an error", errors)
    }
}
