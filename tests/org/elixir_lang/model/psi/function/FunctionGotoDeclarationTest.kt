package org.elixir_lang.model.psi.function

import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.model.psi.PsiSymbolReferenceService
import com.intellij.openapi.actionSystem.IdeActions
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Behavior-level tests for reverse navigation: "Go To Declaration" on a call site navigates to
 * the `def` that defines the function.
 *
 * Mirrors [org.elixir_lang.model.psi.protocol.ProtocolImplGotoDeclarationTest] but for regular module functions rather than protocol
 * implementations.
 */
@Suppress("UnstableApiUsage")
class FunctionGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/function"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    /** Ctrl-Click on a call site - an element with a symbol reference - should choose Go To Declaration. */
    fun testCtrlClickOnCallSiteChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration.ex")
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.GTD,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor, myFixture.file, myFixture.caretOffset
            )
        )
    }

    /** The Go To Declaration action moves the caret onto the `def` name in the same module. */
    fun testGoToDeclarationNavigatesToFunctionDef() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.performEditorAction(IdeActions.ACTION_GOTO_DECLARATION)

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Go To Declaration should navigate somewhere", target)
        assertEquals("perform", target!!.text)

        val enclosingClause = generateSequence(target) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
        assertNotNull("Expected the caret to land inside a def clause", enclosingClause)
    }

    /** The reference at the call site must resolve to at least one [FunctionSymbol]. */
    fun testCallSiteReferenceResolvesToFunctionSymbol() {
        myFixture.configureByFiles("goto_declaration.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        val callElement = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .first { !CallDefinitionClause.`is`(it) }

        val references = PsiSymbolReferenceService.getService().getReferences(callElement)
        assertTrue("Call site should have at least one symbol reference", references.isNotEmpty())

        val resolved = references.flatMap { it.resolveReference() }
        assertTrue("Reference should resolve to at least one FunctionSymbol", resolved.any { it is FunctionSymbol })
    }

    fun testMultiClauseTargetsHaveDistinctPresentation() {
        myFixture.configureByFiles("choose_declaration_multi_clause.ex")
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)

        val callElement = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .first { !CallDefinitionClause.`is`(it) }

        val references = PsiSymbolReferenceService.getService().getReferences(callElement)
        val symbols = references
            .flatMap { it.resolveReference() }
            .filterIsInstance<FunctionSymbol>()

        assertTrue("Expected multiple declaration targets for a multi-clause function", symbols.size >= 2)

        val presentationTexts = symbols.map { it.presentation().presentableText }.distinct()
        assertEquals(
            "Each resolved multi-clause target should have a distinguishable chooser row",
            symbols.size,
            presentationTexts.size
        )
    }
}
