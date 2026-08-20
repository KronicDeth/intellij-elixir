package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.NameArityInterval
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.enclosingMacroCall

class ImportTest : PlatformTestCase() {
    /*
     * Tests
     */

    fun testTreeWalkUpImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<PsiElement>()
        val resolveState =
            ResolveState.initial().put(ENTRANCE, call.enclosingMacroCall()).putInitialVisitedElement(call)

        Import.treeWalkUp(call, resolveState) { call1, _ ->
            importedCallList.add(call1)
            true
        }

        assertEquals(3, importedCallList.size)
    }

    fun testTreeWalkUpImportModuleExceptNameArity() {
        myFixture.configureByFiles("import_module_except_name_arity.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<PsiElement>()
        val resolveState =
            ResolveState.initial().put(ENTRANCE, call.enclosingMacroCall()).putInitialVisitedElement(call)

        Import.treeWalkUp(call, resolveState) { call1, _ ->
            importedCallList.add(call1)
            true
        }

        assertEquals(2, importedCallList.size)

        val nameArityIntervalList = importedCallList.map { importedCall ->
            when (importedCall) {
                is Call -> nameArityInterval(importedCall, ResolveState.initial())
                else -> TODO()
            }
        }

        assertContainsElements(
            listOf(
                NameArityInterval("imported", ArityInterval(1, 1)),
                NameArityInterval("imported", ArityInterval(0, 0))
            ),
            nameArityIntervalList
        )
    }

    fun testTreeWalkUpImportModuleOnlyNameArity() {
        myFixture.configureByFiles("import_module_only_name_arity.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<PsiElement>()
        val resolveState =
            ResolveState.initial().put(ENTRANCE, call.enclosingMacroCall()).putInitialVisitedElement(call)

        Import.treeWalkUp(call, resolveState) { element, _ ->
            importedCallList.add(element)
            true
        }

        assertEquals(1, importedCallList.size)

        val importedCall = importedCallList[0]

        assertEquals(
            NameArityInterval("imported", ArityInterval(0, 0)),
            nameArityInterval(importedCall as Call, ResolveState.initial())
        )
    }

    /*
     * Protected Instance Methods
     */

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/psi/import"
    }
}
