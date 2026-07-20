package org.elixir_lang.model.psi.function

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReferenceService
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.model.psi.protocol.ProtocolFunction
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * Regression for [FunctionCallReference] resolution:
 *  - an unqualified call `foo(1)` must resolve ONLY to the matching arity `foo/1`, never also `foo/2`;
 *  - a qualified protocol call `Protocol.function(args)` must resolve to the owning [ProtocolFunction]
 *    (FunctionSymbol.fromClause returns empty for clauses inside a `defprotocol`).
 */
@Suppress("UnstableApiUsage")
class FunctionCallSiteArityTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/function"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    private fun resolvedSymbolsAtCaret(): List<Symbol> {
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)
        val callElement = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .first { !CallDefinitionClause.`is`(it) }
        return PsiSymbolReferenceService.getService().getReferences(callElement)
            .flatMap { it.resolveReference() }
    }

    fun testUnqualifiedCallResolvesOnlyToMatchingArity() {
        myFixture.configureByFiles("call_site_arity.ex")
        val functionSymbols = resolvedSymbolsAtCaret().filterIsInstance<FunctionSymbol>()

        assertTrue("foo(1) should resolve to at least one function symbol", functionSymbols.isNotEmpty())
        assertTrue(
            "foo(1) must resolve only to foo/1, never foo/2",
            functionSymbols.all { it.arity == 1 }
        )
    }

    fun testQualifiedProtocolCallResolvesToProtocolFunction() {
        myFixture.configureByFiles("protocol_qualified_call.ex")
        val resolved = resolvedSymbolsAtCaret()
        assertTrue(
            "Convertible.convert([]) should resolve to the protocol function clause",
            resolved.any { it is ProtocolFunction && it.arity == 1 }
        )
    }
}
