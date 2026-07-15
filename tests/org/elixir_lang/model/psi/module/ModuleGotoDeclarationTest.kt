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

    fun testCtrlClickOnQualifiedAliasQualifierChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_qualified.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromQualifiedAliasQualifierNavigatesToDefmoduleName() {
        myFixture.configureByFiles("goto_declaration_qualified.ex")
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp", ModuleSymbol.moduleNameText(declaration))
    }

    fun testCtrlClickOnQualifiedAliasLastSegmentChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_qualified_last_segment.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromQualifiedAliasLastSegmentNavigatesToFullModule() {
        myFixture.configureByFiles("goto_declaration_qualified_last_segment.ex")
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp.Module", ModuleSymbol.moduleNameText(declaration))
    }

    fun testCtrlClickOnQualifiedAliasMiddleSegmentChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_qualified_middle_segment.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromQualifiedAliasMiddleSegmentNavigatesToPrefixModule() {
        myFixture.configureByFiles("goto_declaration_qualified_middle_segment.ex")
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp.Module", ModuleSymbol.moduleNameText(declaration))
    }

    fun testCtrlClickOnBareAliasedNameChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_bare_aliased.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromBareAliasedNameNavigatesDirectlyToDefmodule() {
        myFixture.configureByFiles("goto_declaration_bare_aliased.ex")
        // assertGotoDeclarationLandsIn requires a SINGLE target: this is the regression assertion for
        // the legacy chooser that offered the `alias` line alongside the defmodule.
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp.Module", ModuleSymbol.moduleNameText(declaration))
    }

    fun testCtrlClickOnBareQualifiedNameChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_bare_qualified.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromBareQualifiedNameNavigatesToDefmodule() {
        myFixture.configureByFiles("goto_declaration_bare_qualified.ex")
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp.Module", ModuleSymbol.moduleNameText(declaration))
    }

    fun testCtrlClickOnMultiAliasBraceMemberChoosesGotoDeclaration() {
        myFixture.configureByFiles("goto_declaration_multi_alias.ex")
        myFixture.assertGotoDeclarationChosenAtCaret()
    }

    fun testGoToDeclarationFromMultiAliasBraceMemberNavigatesToDefmodule() {
        myFixture.configureByFiles("goto_declaration_multi_alias.ex")
        val declaration = myFixture.assertGotoDeclarationLandsIn("MyApp", "a defmodule declaration") { Module.`is`(it) }
        assertEquals("MyApp.B", ModuleSymbol.moduleNameText(declaration))
    }

}
