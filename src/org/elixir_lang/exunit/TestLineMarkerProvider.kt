package org.elixir_lang.exunit

import com.intellij.execution.TestStateStorage
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.util.system.OS
import org.elixir_lang.exunit.configuration.SUFFIX
import org.elixir_lang.exunit.configuration.lineNumber
import org.elixir_lang.mix.TestFinder
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.sdk.wsl.wslCompat

/**
 * Provides the gutter "run" icons for tests.
 */
internal class ExUnitLineMarkerProvider : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {

        // Speed things up by only looking at files inside the test directory.
        if (!isUnderTestSources(element)) {
            return null
        }

        if (!TestFinder.isTestElement(element) || element !is LeafPsiElement) {
            return null
        }

        val parent = element.parent?.parent

        if (parent !is Call) {
            return null
        }

        // Assignment
        if (parent.parent is Match) {
            return null
        }

        val functionElement = parent.firstChild

        if (functionElement != element.parent) {
            return null
        }

        val isClass = when (parent.functionName()) {
            "test" -> false
            "describe" -> true
            "defmodule" -> true
            "doctest" -> false
            else -> null
        } ?: return null

        val state = if (isClass) {
            // describe/defmodule: aggregate states of all contained tests, since
            // ExUnit never emits describe-level TC events with a locationHint, so
            // TestStateStorage has no direct entry for the describe/module URL.
            aggregateChildTestStates(parent, element.project)
        } else {
            val url = "file://${fileUrlAsExUnitSeesIt(element)}:${lineNumber(element)}"
            TestStateStorage.getInstance(element.project)?.getState(url)
        }
        return withExecutorActions(getTestStateIcon(state, isClass))
    }

    /**
     * For describe/defmodule gutter icons: find all child test/doctest calls, look up each
     * test's state in TestStateStorage, and return the worst state.
     *
     * Priority: FAILED (6) or ERROR (8) > PASSED/COMPLETE (1) > null (no data).
     */
    private fun aggregateChildTestStates(scopeCall: Call, project: Project): TestStateStorage.Record? {
        val storage = TestStateStorage.getInstance(project) ?: return null
        var badState: TestStateStorage.Record? = null
        var goodState: TestStateStorage.Record? = null

        PsiTreeUtil.findChildrenOfType(scopeCall, Call::class.java)
            .filter { call -> call.functionName() in listOf("test", "doctest") }
            .forEach { testCall ->
                val testUrl = "file://${fileUrlAsExUnitSeesIt(testCall)}:${lineNumber(testCall)}"
                val state = storage.getState(testUrl) ?: return@forEach
                when (state.magnitude) {
                    6, 8 -> if (badState == null) badState = state  // FAILED_INDEX or ERROR_INDEX
                    1 -> if (goodState == null) goodState = state   // PASSED_INDEX / COMPLETE_INDEX
                }
            }

        return badState ?: goodState
    }

    private fun fileUrlAsExUnitSeesIt(element: PsiElement): String = if (OS.CURRENT == OS.Windows) {
        // In Elixir, the drive letter is lowercase for some reason.
        val fixedDriveLetter = element.containingFile.virtualFile.path.replaceFirstChar { it.lowercase() }
        // If it's UNC, convert it to linux, otherwise return unchanged.
        wslCompat.maybeParseWindowsUncPath(fixedDriveLetter)
    } else {
        element.containingFile.virtualFile.path
    }
}

@RequiresReadLock
fun isUnderTestSources(clazz: PsiElement): Boolean {
    val psiFile = clazz.containingFile
    val vFile = psiFile.virtualFile ?: return false
    val isTest = psiFile is ElixirFile && psiFile.virtualFile.path.endsWith(SUFFIX)
    return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile) and isTest
}
