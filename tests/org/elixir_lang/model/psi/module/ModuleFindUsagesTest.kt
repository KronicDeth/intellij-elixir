package org.elixir_lang.model.psi.module

import com.intellij.codeInsight.navigation.actions.GotoDeclarationOrUsageHandler2
import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.psi.search.GlobalSearchScope
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import java.util.concurrent.Callable

class ModuleFindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/module"

    fun testAliasUseImportSitesAreFound() {
        val (total, references) = moduleUsageCounts("usages_alias_use_import.ex")

        assertEquals(4, total)
        assertEquals(3, references)
    }

    fun testCtrlClickOnDefmoduleChoosesShowUsages() {
        myFixture.configureByFiles("usages_alias_use_import.ex")
        assertEquals(
            GotoDeclarationOrUsageHandler2.GTDUOutcome.SU,
            GotoDeclarationOrUsageHandler2.testGTDUOutcomeInNonBlockingReadAction(
                myFixture.editor, myFixture.file, myFixture.caretOffset
            )
        )
    }

    @Suppress("UnstableApiUsage")
    private fun moduleUsageCounts(vararg files: String): Pair<Int, Int> {
        myFixture.configureByFiles(*files)
        val moduleSymbol = moduleSymbolAtCaret()
        val allOptions = AllSearchOptions(
            UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
            textSearch = false
        )

        return ApplicationManager.getApplication().executeOnPooledThread(Callable<Pair<Int, Int>> {
            ReadAction.nonBlocking(Callable<Pair<Int, Int>> {
                val usages = buildQuery(project, moduleSymbol, allOptions).findAll().filterIsInstance<PsiUsage>()
                usages.size to usages.count { usage -> isModuleReferenceUsage(usage) }
            }).executeSynchronously()
        }).get()
    }

    private fun moduleSymbolAtCaret(): ModuleSymbol {
        val element = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull("Element at caret should exist", element)
        val callChain = generateSequence(element!!) { it.parent }
            .filterIsInstance<Call>()
            .toList()
        val moduleCall = callChain.firstOrNull { Module.`is`(it) }
        assertNotNull(
            "Expected caret to be inside a defmodule declaration; calls at caret were: ${
                callChain.map { "${it.functionName()}:${it.text.take(40)}" }
            }",
            moduleCall
        )

        return ModuleSymbol.fromModular(moduleCall!!) ?: error(
            "Expected module symbol at caret for call `${moduleCall.text}`"
        )
    }

    @Suppress("UnstableApiUsage")
    private fun isModuleReferenceUsage(usage: PsiUsage): Boolean {
        if (usage.declaration) return false
        val element = usage.file.findElementAt(usage.range.startOffset) ?: return false
        val alias = generateSequence(element) { it.parent }
            .filterIsInstance<QualifiableAlias>()
            .firstOrNull()
            ?: return false
        val enclosingCall = generateSequence(alias as com.intellij.psi.PsiElement) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull()
            ?: return false

        return !Module.`is`(enclosingCall)
    }
}
