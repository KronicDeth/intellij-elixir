package org.elixir_lang.code_insight

import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.Protocol
import org.elixir_lang.psi.call.Call

class GotoSuperTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/code_insight/goto_super"

    @RequiresReadLock
    fun testDefimplFunctionNavigatesToProtocolFunction() {
        myFixture.configureByFile("defimpl_to_defprotocol.ex")

        GotoSuper().invoke(project, myFixture.editor, myFixture.file)

        val leaf = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Goto Super should move caret to a protocol function", leaf)

        val callDefinitionClause = generateSequence(leaf!!) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
        assertNotNull("Caret should be inside a call-definition clause", callDefinitionClause)

        val enclosingModular = CallDefinitionClause.enclosingModularMacroCall(callDefinitionClause!!)
        assertNotNull("Expected enclosing modular macro call", enclosingModular)
        assertTrue("Expected enclosing modular to be defprotocol", Protocol.`is`(enclosingModular!!))
        assertEquals("run", CallDefinitionClause.nameIdentifier(callDefinitionClause)?.text)
    }
}
