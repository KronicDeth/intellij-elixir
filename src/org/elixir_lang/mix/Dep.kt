package org.elixir_lang.mix

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.stripAccessExpressions
import java.nio.file.Paths

/**
 * `Mix.Dep`
 */
data class Dep(val application: String, val path: String, val type: Type = Type.LIBRARY) {
    fun virtualFile(project: Project): VirtualFile? =
        ProjectRootManager
            .getInstance(project)
            .contentRootsFromAllModules
            .mapNotNull { it.findFileByRelativePath(path) }
            .firstOrNull()
            ?: VfsUtil.findFile(Paths.get(path), true)

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

        private fun name(atom: ElixirAtom): String? =
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
