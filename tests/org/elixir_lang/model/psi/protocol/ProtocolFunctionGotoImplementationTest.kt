package org.elixir_lang.model.psi.protocol

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.codeInsight.navigation.ImplementationSearcher
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.psi.search.searches.DefinitionsScopedSearch
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.Implementation
import org.elixir_lang.psi.call.Call

/**
 * Behavior-level tests for "Go To Implementation" (`Ctrl+Alt+B`) on a protocol function `def`.
 *
 * The action first resolves a *source* element through [TargetElementUtil.findTargetElement] using
 * the definition-search flags, then hands that source to [DefinitionsScopedSearch]. If the protocol
 * function name is suppressed from the named-element path (as the Symbol-model migration briefly did
 * via `TargetElementEvaluator.isAcceptableNamedParent`), the source resolves to `null` and the IDE
 * reports "No implementations found". These tests lock in that the source resolves and the `defimpl`
 * implementations are found.
 */
class ProtocolFunctionGotoImplementationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/protocol"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    /** Ctrl+Alt+B must resolve a non-null source element at the protocol function `def` name. */
    fun testGotoImplementationResolvesSource() {
        myFixture.configureByFiles("goto_literal_protocol.ex")

        val source = TargetElementUtil.getInstance()
            .findTargetElement(myFixture.editor, ImplementationSearcher.getFlags(), myFixture.caretOffset)

        assertNotNull(
            "Go To Implementation must resolve a source element at the protocol `def` name; " +
                "a null source makes the IDE report \"No implementations found\"",
            source
        )
    }

    /** Ctrl+Alt+B on the protocol function `def` lists its `defimpl` implementations. */
    fun testGotoImplementationFindsDefimplImplementations() {
        myFixture.configureByFiles("goto_literal_protocol.ex")

        val source = TargetElementUtil.getInstance()
            .findTargetElement(myFixture.editor, ImplementationSearcher.getFlags(), myFixture.caretOffset)
        assertNotNull("Go To Implementation must resolve a source element", source)

        val implementations = DefinitionsScopedSearch.search(source!!).findAll()
        assertFalse(
            "Go To Implementation should find at least one `defimpl` implementation",
            implementations.isEmpty()
        )

        val implementingDef = implementations.filterIsInstance<Call>().firstOrNull { call ->
            CallDefinitionClause.`is`(call) &&
                CallDefinitionClause.enclosingModularMacroCall(call)?.let { Implementation.`is`(it) } == true
        }
        assertNotNull(
            "At least one implementation should be a `def`/`defmacro` clause inside a `defimpl`",
            implementingDef
        )
    }
}
