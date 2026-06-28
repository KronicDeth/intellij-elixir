package org.elixir_lang.exunit

import com.intellij.execution.TestStateStorage
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.system.OS
import org.elixir_lang.exunit.configuration.lineNumber
import org.elixir_lang.mix.Test
import org.elixir_lang.mix.TestFinder
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.sdk.wsl.wslCompat

/**
 * Provides the gutter "run" icons for tests.
 */
internal class ExUnitLineMarkerProvider : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {

        // The platform calls getInfo for every element (leaf and composite) and recommends returning null as fast
        // as possible, marking leaf elements only. Do the cheap, local AST checks before any file-system, index,
        // or stub-resolution work.
        if (element !is LeafPsiElement) {
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

        // Only genuine test/describe/defmodule/doctest call-name leaves reach here; now pay for the file-level and
        // stub-resolution checks. Resolve the containing file once (an AST walk) and reuse it below.
        val containingFile = element.containingFile ?: return null

        if (!Test.isUnderTestSources(containingFile)) {
            return null
        }

        if (!TestFinder.isTestElement(element)) {
            return null
        }

        val number = lineNumber(element, containingFile)
        val url = "file://${fileUrlAsExUnitSeesIt(containingFile)}:${number}"
        val state = TestStateStorage.getInstance(element.project)?.getState(url)
        return withExecutorActions(getTestStateIcon(state, isClass))
    }

    private fun fileUrlAsExUnitSeesIt(containingFile: PsiFile): String = if (OS.CURRENT == OS.Windows) {
        // In Elixir, the drive letter is lowercase for some reason.
        val fixedDriveLetter = containingFile.virtualFile.path.replaceFirstChar { it.lowercase() }
        // On WSL, ExUnit reports the POSIX path, but IDEA references the file by the UNC path. We convert it to posix if
        // it is UNC, otherwise return unchanged.
        wslCompat.maybeParseWindowsUncPath(fixedDriveLetter)
    } else {
        containingFile.virtualFile.path
    }
}
