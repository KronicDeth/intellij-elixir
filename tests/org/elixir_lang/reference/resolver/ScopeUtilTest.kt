package org.elixir_lang.reference.resolver

import com.intellij.codeInsight.daemon.SyntheticPsiFileSupport
import com.intellij.openapi.application.runReadActionBlocking
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.search.GlobalSearchScope
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase

class ScopeUtilTest : PlatformTestCase() {

    private val source =
        """
        defmodule Foo do
        end
        """.trimIndent()

    /** A real file resolves to its module's narrow scope, not the global fallback. */
    fun testNormalFileResolvesNarrowModuleScope() {
        val psiFile = myFixture.configureByText("foo.ex", source)
        val realVirtualFile = psiFile.virtualFile

        val scope = runReadActionBlocking { narrowedScope(elementIn(psiFile), project) }

        assertNotSame(GlobalSearchScope.allScope(project), scope)
        assertTrue("Module scope should contain the module's own file", scope.contains(realVirtualFile))
    }

    /**
     * Reproduces the commit / VCS diff view as observed in the debugger: the PSI is backed by a
     * [com.intellij.testFramework.LightVirtualFile] surfaced through `originalFile.virtualFile`
     * (event system enabled). That light file belongs to no module, so [narrowedScope] must map it
     * back to the real file via the marked original URL instead of falling back to
     * [GlobalSearchScope.allScope].
     */
    fun testOutsiderDiffFileWithBackingVirtualFileRecoversModuleScope() {
        assertRecoversModuleScope(eventSystemEnabled = true)
    }

    /** Same recovery when the backing light file is only reachable via `viewProvider.virtualFile`. */
    fun testOutsiderDiffFileWithoutBackingVirtualFileRecoversModuleScope() {
        assertRecoversModuleScope(eventSystemEnabled = false)
    }

    private fun assertRecoversModuleScope(eventSystemEnabled: Boolean) {
        val realVirtualFile = myFixture.configureByText("foo.ex", source).virtualFile

        val outsiderFile = PsiFileFactory.getInstance(project)
            .createFileFromText("foo.ex", ElixirLanguage, source, eventSystemEnabled, false)
        val backingFile = outsiderFile.viewProvider.virtualFile
        assertFalse("Precondition: backing file is not a real local file", backingFile.isInLocalFileSystem)
        SyntheticPsiFileSupport.markFileWithUrl(backingFile, realVirtualFile.url)

        val scope = runReadActionBlocking { narrowedScope(elementIn(outsiderFile), project) }

        assertNotSame(
            "Should narrow to the recovered module instead of allScope",
            GlobalSearchScope.allScope(project),
            scope
        )
        assertTrue("Recovered module scope should contain the original file", scope.contains(realVirtualFile))
    }

    /** A synthetic file with no recoverable on-disk original keeps the global fallback. */
    fun testUnmarkedSyntheticFileFallsBackToAllScope() {
        val syntheticFile = PsiFileFactory.getInstance(project)
            .createFileFromText("foo.ex", ElixirLanguage, source, false, false)

        val scope = runReadActionBlocking { narrowedScope(elementIn(syntheticFile), project) }

        assertSame(GlobalSearchScope.allScope(project), scope)
    }

    private fun elementIn(file: PsiFile): PsiElement =
        file.findElementAt(source.indexOf("Foo")) ?: file
}
