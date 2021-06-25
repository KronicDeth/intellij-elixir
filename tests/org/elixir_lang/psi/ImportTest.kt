package org.elixir_lang.psi

import com.intellij.psi.ResolveState
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase
import org.elixir_lang.NameArityInterval
import org.elixir_lang.NameArityRange
import org.elixir_lang.psi.call.Call

import java.util.ArrayList

import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval

class ImportTest : LightPlatformCodeInsightFixtureTestCase() {
    /*
     * Tests
     */

    fun testCallDefinitionClauseCallWhileImportModule() {
        myFixture.configureByFiles("import_module.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<Call>()
        val resolveState = ResolveState.initial().putInitialVisitedElement(call)

        Import.callDefinitionClauseCallWhile(call, resolveState) { call1, _ ->
            importedCallList.add(call1)
            true
        }

        assertEquals(3, importedCallList.size)
    }

    fun testCallDefinitionClauseCallWhileImportModuleExceptNameArity() {
        myFixture.configureByFiles("import_module_except_name_arity.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<Call>()
        val resolveState = ResolveState.initial().putInitialVisitedElement(call)

        Import.callDefinitionClauseCallWhile(call, resolveState) { call1, _ ->
            importedCallList.add(call1)
            true
        }

        assertEquals(2, importedCallList.size)

        val nameArityIntervalList = importedCallList.map { importedCall ->
            nameArityInterval(importedCall, ResolveState.initial())
        }

        assertContainsElements(
                listOf(
                        NameArityInterval("imported", ArityInterval(1, 1)),
                        NameArityInterval("imported", ArityInterval(0, 0))
                ),
                nameArityIntervalList
        )
    }

    fun testCallDefinitionClauseCallWhileImportModuleOnlyNameArity() {
        myFixture.configureByFiles("import_module_only_name_arity.ex", "imported.ex")
        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)

        assertNotNull(elementAtCaret)

        val maybeCall = elementAtCaret!!.parent.parent

        assertInstanceOf(maybeCall, Call::class.java)

        val call = maybeCall as Call
        assertTrue(Import.`is`(call))

        val importedCallList = ArrayList<Call>()
        val resolveState = ResolveState.initial().putInitialVisitedElement(call)

        Import.callDefinitionClauseCallWhile(call, resolveState) { call1, _ ->
            importedCallList.add(call1)
            true
        }

        assertEquals(1, importedCallList.size)

        val importedCall = importedCallList[0]

        assertEquals(
                NameArityInterval("imported", ArityInterval(0, 0)),
                nameArityInterval(importedCall, ResolveState.initial())
        )
    }

    /*
     * Protected Instance Methods
     */

    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/psi/import"
    }
}
