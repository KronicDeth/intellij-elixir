package org.elixir_lang.file

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.search.PsiElementProcessor
import org.elixir_lang.psi.ElixirFile

fun containsFileWithSuffix(directory: PsiDirectory, suffix: String): Boolean {
    val testFinder = Finder(suffix)

    directory.processChildren(testFinder)

    return testFinder.found
}

private class Finder(val suffix: String) : PsiElementProcessor<PsiFileSystemItem> {
    var found = false
      private set

    override fun execute(psiFileSystemItem: PsiFileSystemItem): Boolean =
            when (psiFileSystemItem) {
                is PsiDirectory -> {
                    // Recurse into subdirectory. The return value of processChildren conflates
                    // "found a match" with "hit a non-match that stopped iteration" in the old
                    // buggy code, so we intentionally ignore it and use `found` as the sole
                    // source of truth for whether to continue.
                    psiFileSystemItem.processChildren(this)
                    !found
                }
                is ElixirFile -> {
                    if (psiFileSystemItem.virtualFile.path.endsWith(suffix)) {
                        found = true
                        false // match found, stop early
                    } else {
                        true // no match, keep looking
                    }
                }
                else -> true
            }
}
