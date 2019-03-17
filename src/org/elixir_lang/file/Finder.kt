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

class Finder(val suffix: String) : PsiElementProcessor<PsiFileSystemItem> {
    var found = false
      private set

    override fun execute(psiFileSystemItem: PsiFileSystemItem): Boolean =
            when (psiFileSystemItem) {
                is PsiDirectory -> psiFileSystemItem.processChildren(this)
                is ElixirFile -> {
                    found = psiFileSystemItem.virtualFile.path.endsWith(suffix)

                    false
                }
                else -> true
            }
}
