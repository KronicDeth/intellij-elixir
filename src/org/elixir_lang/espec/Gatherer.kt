package org.elixir_lang.espec

import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.search.PsiElementProcessor
import org.elixir_lang.psi.ElixirFile
import java.io.File

class Gatherer(val workingDirectory: String?) : PsiElementProcessor<PsiFileSystemItem> {
    val programParameters
        get() = relativePaths.joinToString(" ")

    private val relativePaths = mutableListOf<String>()

    override fun execute(element: PsiFileSystemItem): Boolean {
        when (element) {
            is PsiDirectory -> element.processChildren(this)
            is ElixirFile -> {
                val path = element.virtualFile.path

                if (path.endsWith("_spec.exs")) {
                    relativePaths.add(relativePath(path))
                }
            }
        }

        return true
    }

    private fun relativePath(path: String): String =
            if (workingDirectory != null) {
                path.removePrefix(workingDirectory + File.separator)
            } else {
                path
            }
}
