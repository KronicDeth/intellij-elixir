package org.elixir_lang.mix

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.stripAccessExpressions

/**
 * `Mix.Dep`
 */
data class Dep(val application: String, val path: String, val type: Type = Type.LIBRARY) {
    /**
     * Resolves the virtual file for this dependency relative to [moduleRoot].
     *
     * [path] is typically "deps/<name>" (relative) or an absolute path from a `path:` option.
     * Relative paths (including ones with `../` traversal like `"../../../exc1"`) are first
     * resolved via [VirtualFile.findFileByRelativePath]; if that fails (e.g. because parent
     * directories above the content root are not yet in the VFS), the path is resolved against
     * [moduleRoot]'s filesystem path using [java.nio.file.Path] before attempting a
     * [LocalFileSystem.refreshAndFindFileByPath] lookup.
     *
     * @param moduleRoot The content root of the module that declared this dependency.
     * @return The VirtualFile for the dep directory, or null if not found.
     */
    fun virtualFile(moduleRoot: VirtualFile): VirtualFile? {
        // First try VFS traversal - VirtualFile.findFileByRelativePath handles ../ by walking up
        // via getParent(), so this works whenever the ancestor directories are in the VFS.
        moduleRoot.findFileByRelativePath(path)?.let { return it }

        // Compute an absolute path for the fallback refresh.
        // For absolute paths (Unix "/" or Windows "C:\") use as-is.
        // For relative paths, resolve against the module root's *filesystem* path so that
        // directories above the content root (not tracked by the VFS) can still be found.
        // Passing a bare relative path like "../../../exc1" to refreshAndFindFileByPath
        // is meaningless because the method has no working-directory context.
        val absolutePath: String = if (java.io.File(path).isAbsolute) {
            path
        } else {
            try {
                java.nio.file.Path.of(moduleRoot.path).resolve(path).normalize().toString()
            } catch (_: Throwable) {
                return null
            }
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(absolutePath)
    }

    enum class Type {
        LIBRARY,
        MODULE
    }

    companion object {
        fun from(depsListElement: ElixirTuple): Dep? {
            val stripped = depsListElement.children.stripAccessExpressions()

            return if (stripped.isNotEmpty()) {
                name(stripped[0])?.let { name ->
                    val initial = Dep(application = name, path = "deps/$name")

                    if (stripped.size > 1) {
                        stripped.last().let { it as? ElixirKeywords }?.keywordPairList?.let { keywordPairList ->
                            keywordPairList.fold(initial) { acc, keywordPair ->
                                val key = keywordPair.keywordKey.text

                                when (key) {
                                    "app", "branch", "commit", "compile", "env", "git", "github", "hex", "manager",
                                    "only", "optional", "organization", "override", "ref", "repo", "runtime",
                                    GUARDIAN_RUNTIME_TYPO, "sparse", "submodules", "system_env", "tag", "targets",
                                    EDELIVER_DISTILLERY_WARN_MISSING, "sha", "depth", "subdir", "warn_if_outdated" -> acc

                                    "in_umbrella" -> acc.copy(path = "apps/$name", type = Type.MODULE)
                                    "path" -> putPath(acc, keywordPair.keywordValue)
                                    else -> {
                                        Logger.error(
                                            logger,
                                            "Don't know if Mix.Dep option `$key` is important for determining location of dependency",
                                            depsListElement
                                        )
                                        acc
                                    }
                                }
                            }
                        } ?: initial
                    } else {
                        initial
                    }
                }
            } else {
                null
            }
        }

        private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Dep::class.java) }

        private fun name(nameElement: PsiElement): String? =
            when (nameElement) {
                is ElixirAtom -> name(nameElement)
                else -> null
            }

        private fun name(atom: ElixirAtom): String =
            atom.line?.let { name(it) }
                ?: atom.node.lastChildNode.text

        private fun name(line: ElixirLine): String? {
            Logger.error(logger, "Don't know how to convert ${line.text} to dep name", line.parent)

            return null
        }

        private fun putPath(dep: Dep, keywordValue: Quotable): Dep {
            val strippedKeywordValue = keywordValue.stripAccessExpression()

            return when (strippedKeywordValue) {
                is ElixirLine -> putPath(dep, strippedKeywordValue)
                is Call -> putPath(dep, strippedKeywordValue)
                else -> {
                    Logger.error(logger, "Don't know how to convert ${keywordValue.text} to path", keywordValue.parent)

                    dep
                }
            }
        }

        private fun putPath(dep: Dep, stringLine: ElixirLine): Dep = dep.copy(path = stringLine.body!!.text)

        // NOTE: path: <call> patterns (e.g. path: some_helper()) are not resolved because multiResolve is expensive
        // and the resolution chain always returns the dep unchanged. If full resolution is needed in the future,
        // re-implement with a non-blocking, suspending approach.
        @Suppress("UNUSED_PARAMETER")
        private fun putPath(dep: Dep, call: Call): Dep = dep
    }
}

// https://github.com/ueberauth/guardian/issues/594
@Suppress("SpellCheckingInspection")
const val GUARDIAN_RUNTIME_TYPO: String = "runtume"
const val EDELIVER_DISTILLERY_WARN_MISSING: String = "warn_missing"
