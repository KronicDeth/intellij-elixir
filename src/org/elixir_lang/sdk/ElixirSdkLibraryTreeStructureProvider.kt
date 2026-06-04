package org.elixir_lang.sdk

import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.OrderRootType
import com.intellij.psi.PsiManager

/**
 * Replaces Elixir/Erlang SDK "ebin" class root nodes in External Libraries with the sibling "lib" source
 * directory node when one exists.
 *
 * For OTP applications written in Elixir (e.g., `iex`, `elixir`, `logger`, `mix`, `ex_unit`), the
 * SDK path layout is:
 * ```
 * lib/<app>/ebin/  ← beam files (CLASSES root)
 * lib/<app>/lib/   ← .ex source files (SOURCES root)
 * ```
 *
 * Without this provider, External Libraries shows only beam files (from the CLASSES roots).
 * With this provider, External Libraries shows the .ex source directory instead - which is far more
 * useful for code browsing.
 *
 * For Erlang-only OTP apps (no `lib/` sibling), the original `ebin` node is kept so beam files
 * remain visible.
 *
 * Works in tandem with [ElixirSdkLibraryNodeDecorator] which renames the resulting "lib" (and
 * remaining "ebin") nodes from their generic directory name to the parent application name.
 */
internal class ElixirSdkLibraryTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings,
    ): Collection<AbstractTreeNode<*>> {
        val project = parent.project ?: return children

        var anyReplaced = false
        val result = children.map { child ->
            val replacement = tryReplaceEbinWithLib(child, project, settings)
            if (replacement != null) {
                anyReplaced = true
                replacement
            } else {
                child
            }
        }
        return if (anyReplaced) result else children
    }

    private fun tryReplaceEbinWithLib(
        node: AbstractTreeNode<*>,
        project: Project,
        settings: ViewSettings,
    ): AbstractTreeNode<*>? {
        if (node !is PsiDirectoryNode) return null
        val psiDir = node.value ?: return null
        val ebinVFile = psiDir.virtualFile
        if (ebinVFile.name != "ebin") return null

        // Only act on ebin directories that ARE a class root of a registered SDK (not just under one)
        val isSdkClassRoot = ProjectJdkTable.getInstance().allJdks.any { sdk ->
            sdk.rootProvider.getFiles(OrderRootType.CLASSES).any { it == ebinVFile }
        }
        if (!isSdkClassRoot) return null

        // Replace ebin with the sibling lib directory when it exists (Elixir apps have .ex source there)
        val appDir = ebinVFile.parent ?: return null
        val libVFile = appDir.findChild("lib")?.takeIf { it.isDirectory } ?: return null

        val psiLibDir = PsiManager.getInstance(project).findDirectory(libVFile) ?: return null
        return PsiDirectoryNode(project, psiLibDir, settings)
    }
}
