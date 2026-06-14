package org.elixir_lang.file

import com.intellij.psi.PsiManager
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import org.elixir_lang.PlatformTestCase

class ContainsFileWithSuffixTest : PlatformTestCase() {

    /**
     * Reproduces the real-world bug: a `support/` subdirectory containing `factory.ex` (non-matching)
     * is visited before `controllers/` which contains a `_test.exs` file.
     * The buggy Finder stops on the first non-matching ElixirFile and never reaches the match.
     */
    @RequiresBackgroundThread
    fun testContainsFileWithSuffix_matchInSiblingSubdirectory() {
        myFixture.copyDirectoryToProject("with_nonmatching_in_subdirectory", "")
        val baseDir = myFixture.findFileInTempDir("")!!
        val psiDir = PsiManager.getInstance(project).findDirectory(baseDir)!!

        // Ensure both subdirectories exist in the fixture
        assertNotNull("support/ should exist", psiDir.findSubdirectory("support"))
        assertNotNull("controllers/ should exist", psiDir.findSubdirectory("controllers"))

        assertTrue(
            "containsFileWithSuffix should find _test.exs in controllers/ even when support/ " +
                    "contains non-matching .ex files and is visited first",
            containsFileWithSuffix(psiDir, "_test.exs")
        )
    }

    /**
     * Negative case: directory tree with no `_test.exs` files anywhere should return false.
     */
    @RequiresBackgroundThread
    fun testContainsFileWithSuffix_noMatchAnywhere() {
        myFixture.copyDirectoryToProject("without_matching", "")
        val baseDir = myFixture.findFileInTempDir("")!!
        val psiDir = PsiManager.getInstance(project).findDirectory(baseDir)!!

        assertFalse(
            "containsFileWithSuffix should return false when no _test.exs files exist anywhere",
            containsFileWithSuffix(psiDir, "_test.exs")
        )
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/file/finder"
}
