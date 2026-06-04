package org.elixir_lang.sdk

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.ExternalLibrariesNode
import com.intellij.ide.projectView.impl.nodes.NamedLibraryElementNode
import com.intellij.ide.projectView.impl.nodes.PsiDirectoryNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.JdkOrderEntry
import com.intellij.openapi.roots.LibraryOrderEntry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.elixir_lang.jps.shared.ElixirSdkTypeId
import org.elixir_lang.jps.shared.ErlangSdkTypeId
import org.elixir_lang.mix.library.CONSOLIDATED_LIBRARY_SUFFIX

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
 *
 * When a Mix dependency has roots compiled for multiple environments (e.g. `dev` and `test` both
 * present under `_build/`), the duplicate `ebin` and `consolidated` roots are grouped under virtual
 * subdirectory nodes named after the build environment so they can be distinguished.
 */
internal class ElixirSdkLibraryTreeStructureProvider : TreeStructureProvider, DumbAware {
    override fun modify(
        parent: AbstractTreeNode<*>,
        children: Collection<AbstractTreeNode<*>>,
        settings: ViewSettings,
    ): Collection<AbstractTreeNode<*>> {
        // Give nodes explicit weights so IntelliJ's tree sorter produces a stable order:
        //   Elixir/Erlang SDKs (weight -2) → consolidated (weight -1) → all other deps (weight 0, alphabetical)
        //
        // This provider is registered application-wide (the treeStructureProvider extension point has no
        // per-project filter), so scope the transformations to Elixir/Erlang content only: guard SDK nodes by
        // their SDK type and consolidated nodes by the plugin-specific library-name suffix. Otherwise SDKs in
        // unrelated (e.g. Java) projects would be re-weighted too.
        if (parent is ExternalLibrariesNode) {
            return children.map { node ->
                if (node !is NamedLibraryElementNode) return@map node
                when (val entry = node.value?.orderEntry) {
                    is JdkOrderEntry ->
                        if (entry.isElixirOrErlangSdk()) SdkLibraryNode(node, settings) else node
                    is LibraryOrderEntry ->
                        if (entry.libraryName?.endsWith(CONSOLIDATED_LIBRARY_SUFFIX) == true)
                            ConsolidatedLibraryNode(node, settings)
                        else node
                    else -> node
                }
            }
        }

        // SDK ebin dirs are direct children of NamedLibraryElementNode; skip everything else cheaply.
        if (parent !is NamedLibraryElementNode) return children
        val project = parent.project ?: return children

        // Apply ebin→lib replacement for SDK roots first, so replaced nodes get the right env detected.
        val processed = children.map { child ->
            tryReplaceEbinWithLib(child, project, settings) ?: child
        }

        // Group Mix build roots by their _build environment (e.g. "dev", "test").
        // Roots not under _build (e.g. a deps/ SOURCES root) go in the null bucket.
        val byEnv = LinkedHashMap<String?, MutableList<AbstractTreeNode<*>>>()
        for (node in processed) {
            val vFile = (node as? PsiDirectoryNode)?.value?.virtualFile
            byEnv.getOrPut(buildEnv(vFile)) { mutableListOf() }.add(node)
        }

        val envKeys = byEnv.keys.filterNotNull()
        if (envKeys.size < 2) return if (processed == children) children else processed

        // Emit non-_build roots first (e.g. deps/ source roots), then one group per env.
        val result = mutableListOf<AbstractTreeNode<*>>()
        byEnv[null]?.let { result.addAll(it) }
        for (env in envKeys) {
            result.add(BuildEnvGroupNode(project, env, byEnv[env]!!, settings))
        }
        return result
    }

    private fun tryReplaceEbinWithLib(
        node: AbstractTreeNode<*>,
        project: Project,
        settings: ViewSettings,
    ): AbstractTreeNode<*>? {
        if (node !is PsiDirectoryNode) return null
        val ebinVFile = node.value?.virtualFile ?: return null
        if (ebinVFile.name != "ebin") return null

        // O(1) set lookup - avoids scanning all SDKs per child
        if (ebinVFile !in ElixirSdkRootsCache.classRoots()) return null

        // Replace ebin with the sibling lib directory when it exists (Elixir apps have .ex source there)
        val libVFile = ebinVFile.parent?.findChild("lib")?.takeIf { it.isDirectory } ?: return null
        val psiLibDir = PsiManager.getInstance(project).findDirectory(libVFile) ?: return null
        return PsiDirectoryNode(project, psiLibDir, settings)
    }
}

/**
 * Whether this SDK order entry points at an Elixir or Erlang SDK, matching by SDK type id the same way
 * [ElixirSdkRootsCache] does. Used to avoid re-weighting SDKs of unrelated projects (e.g. Java), since this
 * provider is registered application-wide.
 */
private fun JdkOrderEntry.isElixirOrErlangSdk(): Boolean {
    val typeName = jdk?.sdkType?.name ?: return false
    return typeName == ElixirSdkTypeId.ELIXIR_SDK_TYPE_ID || typeName == ErlangSdkTypeId.ERLANG_SDK_TYPE_ID
}

/**
 * Walks up the VFS hierarchy to find the direct child of `_build/`, returning its name (e.g. `"dev"`,
 * `"test"`), or `null` if [vFile] is not under a `_build` directory.
 */
private fun buildEnv(vFile: VirtualFile?): String? {
    var current = vFile ?: return null
    while (true) {
        val parent = current.parent ?: return null
        if (parent.name == "_build") return current.name
        current = parent
    }
}

/**
 * A virtual (non-PSI) tree node representing a Mix build environment (e.g. `dev` or `test`).
 * Groups `ebin` and `consolidated` roots that would otherwise appear as indistinguishable duplicates.
 */
private class BuildEnvGroupNode(
    project: Project,
    envName: String,
    private val envChildren: List<AbstractTreeNode<*>>,
    settings: ViewSettings,
) : ProjectViewNode<String>(project, envName, settings) {
    override fun getChildren(): Collection<AbstractTreeNode<*>> = envChildren
    override fun update(presentation: PresentationData) {
        presentation.presentableText = value
        presentation.setIcon(AllIcons.Nodes.Folder)
    }
    override fun contains(file: VirtualFile): Boolean =
        envChildren.any { (it as? ProjectViewNode<*>)?.contains(file) == true }
}

/**
 * Wraps an SDK [NamedLibraryElementNode] (names starting with `<`) with weight -2 so it sorts
 * before consolidated libraries (weight -1) and all standard dep libraries (weight 0).
 */
private class SdkLibraryNode(
    wrapped: NamedLibraryElementNode,
    settings: ViewSettings,
) : NamedLibraryElementNode(wrapped.project!!, wrapped.value!!, settings) {
    override fun getWeight(): Int = -2
}

/**
 * Wraps a consolidated [NamedLibraryElementNode] with weight -1 so IntelliJ's tree sorter places
 * it after SDK nodes (weight -2) but before all standard dep nodes (weight 0).
 */
private class ConsolidatedLibraryNode(
    wrapped: NamedLibraryElementNode,
    settings: ViewSettings,
) : NamedLibraryElementNode(wrapped.project!!, wrapped.value!!, settings) {
    override fun getWeight(): Int = -1
}
