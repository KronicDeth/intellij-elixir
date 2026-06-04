package org.elixir_lang.sdk

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ProjectViewNodeDecorator
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode

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
        val vFile = node.value?.virtualFile ?: return

        // Cheap name pre-filter before touching the cache
        if (vFile.name != "ebin" && vFile.name != "lib") return

        // O(1) set lookup - avoids scanning all SDKs on every render
        if (vFile !in ElixirSdkRootsCache.classAndSourceRoots()) return

        // Rename to the OTP application directory name, e.g. lib/iex/ebin → "iex"
        data.presentableText = vFile.parent?.name ?: return
    }
}

