package org.elixir_lang.sdk

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.roots.OrderRootType

/**
 * Renames SDK library root nodes in External Libraries from the generic directory name ("ebin" or "lib")
 * to the parent application name (e.g., "iex", "elixir", "logger").
 *
 * Without this, every Elixir/Erlang SDK entry in External Libraries is labeled "ebin library root",
 * making it impossible to tell which OTP application each node belongs to.
 *
 * Works in tandem with [ElixirSdkLibraryTreeStructureProvider] which replaces "ebin" class root nodes
 * with "lib" source root nodes for Elixir applications (where .ex source files are available).
 */
internal class ElixirSdkLibraryNodeDecorator : ProjectViewNodeDecorator {
    override fun decorate(node: ProjectViewNode<*>, data: PresentationData) {
        if (node !is PsiDirectoryNode) return
        val psiDir = node.value ?: return
        val vFile = psiDir.virtualFile

        // Only consider the canonical leaf names used as SDK roots
        if (vFile.name != "ebin" && vFile.name != "lib") return

        // Check if this directory is directly registered as a root in any SDK (not merely under one).
        // This avoids renaming project-internal "ebin" or "lib" dirs that happen to share the name.
        val isSdkRoot = ProjectJdkTable.getInstance().allJdks.any { sdk ->
            sdk.rootProvider.getFiles(OrderRootType.CLASSES).any { it == vFile } ||
                sdk.rootProvider.getFiles(OrderRootType.SOURCES).any { it == vFile }
        }
        if (!isSdkRoot) return

        // Rename to the OTP application directory name, e.g. lib/iex/ebin → "iex"
        val appName = vFile.parent?.name ?: return
        data.presentableText = appName
    }
}
