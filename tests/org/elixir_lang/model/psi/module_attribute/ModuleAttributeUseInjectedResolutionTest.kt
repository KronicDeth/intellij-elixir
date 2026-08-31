package org.elixir_lang.model.psi.module_attribute

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationLandsIn
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall

/**
 * A module attribute declared inside a `__using__` macro's `quote` block belongs to every module
 * that `use`s it, so a read of it navigates to that declaration. The declaration is enclosed by the
 * `quote` call rather than by a module, which is the case a symbol has to be built for anyway.
 */
class ModuleAttributeUseInjectedResolutionTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module_attribute"

    fun testGotoDeclarationOnUseInjectedAttributeLandsOnDeclaration() {
        myFixture.configureByFiles("goto_declaration_use_injected.ex")
        // The read and the declaration share the name `from_macro`, so the assertion is on the
        // enclosing element: only the declaration is an AtUnqualifiedNoParenthesesCall, which
        // distinguishes reaching it from navigating to the read itself.
        myFixture.assertGotoDeclarationLandsIn("from_macro", "the @from_macro declaration") {
            it is AtUnqualifiedNoParenthesesCall<*>
        }
    }
}
