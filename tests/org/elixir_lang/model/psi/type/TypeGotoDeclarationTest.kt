package org.elixir_lang.model.psi.type

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.model.psi.PsiSymbolReferenceService
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertGotoDeclarationLandsIn
import org.elixir_lang.code_insight.enclosingCallAtCaret
import org.elixir_lang.structure_view.element.Type as TypeElement

@Suppress("UnstableApiUsage")
class TypeGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/type"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnTypeInSpecChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToTypeName() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.assertGotoDeclarationLandsIn(
            "user_id",
            "a @type/@typep/@opaque declaration"
        ) { TypeElement.`is`(it) }
    }

    fun testSpecTypeReferenceResolvesToTypeSymbol() {
        myFixture.configureByFiles("goto_declaration.ex")
        val callElement = myFixture.enclosingCallAtCaret()
        assertNotNull("Call element at caret should exist", callElement)

        val references = PsiSymbolReferenceService.getService().getReferences(callElement!!)
        assertTrue("Type usage site should have at least one symbol reference", references.isNotEmpty())

        val resolved = references.flatMap { it.resolveReference() }
        assertTrue("Reference should resolve to at least one TypeSymbol", resolved.any { it is TypeSymbol })
    }
}
