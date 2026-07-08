package org.elixir_lang.model.psi.module

import com.intellij.ide.impl.HeadlessDataManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationChosenAtCaret
import org.elixir_lang.code_insight.assertGotoDeclarationLandsIn
import org.elixir_lang.psi.Module

class ModuleGotoDeclarationTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
    }

    fun testCtrlClickOnModuleAliasChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationNavigatesToDefmoduleName() {
        myFixture.configureByFiles("goto_declaration.ex")
        myFixture.assertGotoDeclarationLandsIn("Target", "a defmodule declaration") { Module.`is`(it) }
    }

    fun testGoToDeclarationFromQualifiedAliasQualifierNavigatesToDefmoduleName() {
        myFixture.configureByFiles("goto_declaration_qualified.ex")
        myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
    }

}
