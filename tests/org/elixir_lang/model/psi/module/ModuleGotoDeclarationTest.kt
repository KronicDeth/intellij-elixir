package org.elixir_lang.model.psi.module

import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.openapi.actionSystem.IdeActions
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.call.Call

class ModuleGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnModuleAliasChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration.ex")
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.GTD,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor,
                myFixture.file,
                myFixture.caretOffset
            )
        )
    }

    fun testGoToDeclarationNavigatesToDefmoduleName() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.performEditorAction(IdeActions.ACTION_GOTO_DECLARATION)

        val target = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Go To Declaration should navigate somewhere", target)
        assertEquals("Target", target!!.text)
        val enclosingModule = generateSequence(target) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { Module.`is`(it) }
        assertNotNull("Expected caret to land in a defmodule declaration", enclosingModule)
    }

}
