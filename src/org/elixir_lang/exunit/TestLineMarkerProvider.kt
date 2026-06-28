package org.elixir_lang.exunit

import com.intellij.execution.TestStateStorage
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
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
class ExUnitLineMarkerProvider : RunLineMarkerContributor() {

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

        val number = lineNumber(element)
        val url = "file://${fileUrlAsExUnitSeesIt(element)}:${number}"
        val state = TestStateStorage.getInstance(element.project)?.getState(url)
        return withExecutorActions(getTestStateIcon(state, isClass))
    }

    private fun fileUrlAsExUnitSeesIt(element: PsiElement): String = if (OS.CURRENT == OS.Windows) {
        // In Elixir, the drive letter is lowercase for some reason.
        val fixedDriveLetter = element.containingFile.virtualFile.path.replaceFirstChar { it.lowercase() }
        // On WSL, ExUnit reports the POSIX path, but IDEA references the file by the UNC path. We convert it to posix if
        // it is UNC, otherwise return unchanged.
        wslCompat.maybeParseWindowsUncPath(fixedDriveLetter)
    } else {
        element.containingFile.virtualFile.path
    }
}

fun isUnderTestSources(clazz: PsiElement): Boolean {
    val psiFile = clazz.containingFile
    val vFile = psiFile.virtualFile ?: return false
    val isTest = psiFile is ElixirFile && psiFile.virtualFile.path.endsWith(SUFFIX)
    return ProjectRootManager.getInstance(clazz.project).fileIndex.isInTestSourceContent(vFile) and isTest
}
