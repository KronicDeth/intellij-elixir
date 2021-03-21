package org.elixir_lang.navigation.item_presentation

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VfsUtilCore
import org.elixir_lang.exunit.configuration.lineNumber
import org.elixir_lang.psi.ElixirKeywordKey
import javax.swing.Icon

class KeywordKey(private val keywordKey: ElixirKeywordKey) : ItemPresentation {
    override fun getPresentableText(): String = keywordKey.parent.text

    override fun getLocationString(): String? =
        keywordKey
                .containingFile
                .virtualFile
                ?.let { virtualFile ->
            ProjectFileIndex
                    .getInstance(keywordKey.project)
                    .getModuleForFile(virtualFile, false)
                    ?.let { module ->
                ModuleRootManager
                        .getInstance(module)
                        .contentRoots
                        .mapNotNull { root ->
                    VfsUtilCore.getRelativePath(virtualFile, root)
                }.singleOrNull()?.let { "$it at line ${lineNumber(keywordKey)}" }
            }
        }

    override fun getIcon(unused: Boolean): Icon? = null
}
