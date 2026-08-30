package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.NameArityInterval
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.enclosingMacroCall

class UsingTest : PlatformTestCase() {
    /**
     * `__using__/1` reaching a cycle of mutually recursive call definition clauses terminates and still
     * yields the definitions injected by the non-cyclic clause.
     *
     * Recording a resolved clause as visited is not enough on its own: the walk has to check the record
     * before recursing, or the cycle re-enters forever.
     */
    fun testTreeWalkUpCyclicCallDefinitionClauseChain() {
        myFixture.configureByFiles("cyclic_use.ex", "cyclic_web.ex")

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Use.`is`(call))

        val usedList = ArrayList<PsiElement>()
        val resolveState =
            ResolveState.initial().put(ENTRANCE, call.enclosingMacroCall()).putInitialVisitedElement(call)

        Use.treeWalkUp(call, resolveState) { element, _ ->
            usedList.add(element)
            true
        }

        val nameArityIntervalSet = usedList
            .map { used ->
                when (used) {
                    is Call -> nameArityInterval(used, ResolveState.initial())
                    else -> TODO()
                }
            }
            .toSet()

        assertEquals(setOf(NameArityInterval("injected_by_view", ArityInterval(0, 0))), nameArityIntervalSet)
    }

    /*
     * Protected Instance Methods
     */

    override fun getTestDataPath(): String = "testData/org/elixir_lang/psi/using"
}
