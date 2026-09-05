package org.elixir_lang.navigation

import com.intellij.navigation.ChooseByNameRegistry
import com.intellij.navigation.NavigationItem
import org.elixir_lang.beam.BeamLibraryTestCase
import org.elixir_lang.beam.psi.TypeDefinition

/**
 * Go To Symbol over types defined in decompiled BEAM modules.
 *
 * The two tests fail differently on purpose: the first covers the crash, the second covers a fix that
 * stops the crash but omits the `toTypedArray()` concatenation, leaving the symbol silently absent.
 */
class BeamTypeGotoSymbolTest : BeamLibraryTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    private fun itemsNamed(name: String): Array<NavigationItem> =
        ChooseByNameRegistry
            .getInstance()
            .symbolModelContributors
            .filterIsInstance<GotoSymbolContributor>()
            .single()
            .getItemsByName(name, name, myFixture.project, true)

    fun testGoToSymbolOnADecompiledTypeDoesNotThrow() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")

        itemsNamed("queue")
    }

    fun testGoToSymbolOffersTheDecompiledTypeDefinition() {
        myFixture.configureByFiles("beam_qualified_type_goto.ex")

        val items = itemsNamed("queue")

        assertTrue(
            "Go To Symbol should offer the decompiled `@type queue`, got: ${items.map { it.name }}",
            items.any { it is TypeDefinition }
        )
    }
}
