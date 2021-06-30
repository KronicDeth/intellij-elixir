package org.elixir_lang.mix

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveResult
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.stripAccessExpressions
import org.elixir_lang.psi.operation.Match
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
                                    GUARDIAN_RUNTIME_TYPO, "sparse", "submodules", "system_env", "tag", "targets" -> acc
                                    "in_umbrella" -> acc.copy(path = "apps/$name", type = Type.MODULE)
                                    "path" -> putPath(acc, keywordPair.keywordValue)
                                    else -> {
                                        Logger.error(logger, "Don't know if Mix.Dep option `$key` is important for determining location of dependency", depsListElement)
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
                atom.charListLine?.let { name(it) }
                        ?: atom.stringLine?.let { name(it) }
                        ?: atom.node.lastChildNode.text

        private fun name(charListLine: ElixirCharListLine): String? {
            Logger.error(logger, "Don't know how to convert ${charListLine.text} to dep name", charListLine.parent)

            return null
        }

        private fun name(stringLine: ElixirStringLine): String? {
            Logger.error(logger, "Don't know how to convert ${stringLine.text} to dep name", stringLine.parent)

            return null
        }

        private fun putPath(dep: Dep, keywordValue: Quotable): Dep {
            val strippedKeywordValue = keywordValue.stripAccessExpression()

            return when (strippedKeywordValue) {
                is ElixirStringLine -> putPath(dep, strippedKeywordValue)
                is Call -> putPath(dep, strippedKeywordValue)
                else -> {
                    Logger.error(logger, "Don't know how to convert ${keywordValue.text} to path", keywordValue.parent)

                    dep
                }
            }
        }

        private fun putPath(dep: Dep, stringLine: ElixirStringLine): Dep = dep.copy(path = stringLine.body.text)

        private fun putPath(dep: Dep, call: Call): Dep =
                call
                        .reference?.let { it as? PsiPolyVariantReference }
                        ?.multiResolve(false)
                        ?.asSequence()?.filter(ResolveResult::isValidResult)?.mapNotNull(ResolveResult::getElement)
                        ?.fold(dep) { dep, resolved ->
                            putPathFromResolved(dep, resolved)
                        }
                        ?: dep

        private fun putPathFromResolved(dep: Dep, resolved: PsiElement): Dep =
                when (resolved) {
                    is Call -> putPathFromResolved(dep, resolved)
                    else -> dep
                }

        private fun putPathFromResolved(dep: Dep, resolved: Call): Dep =
                if (CallDefinitionClause.`is`(resolved)) {
                    dep
                } else {
                    putPathFromVariable(dep, resolved)
                }

        private fun putPathFromVariable(dep: Dep, variable: Call): Dep =
                when (val parent = variable.parent) {
                    is Match -> {
                        // variable = ..
                        if (parent.leftOperand() == variable) {
                            parent.rightOperand()?.let { value ->
                                putPathFromValue(dep, value)
                            } ?: dep
                        }
                        // ... = variable
                        else {
                            dep
                        }
                    }
                    else -> dep
                }

        private fun putPathFromValue(dep: Dep, value: PsiElement): Dep =
                when (value) {
                    is Call -> putPathFromValue(dep, value)
                    else -> dep
                }

        private fun putPathFromValue(dep: Dep, value: Call): Dep =
                if (value.isCalling("System", "get_env")) {
                    // Getting environment variable in IDEs is unreliable because whether the local shell or not is
                    // used is based on how the IDE was launched.
                    dep
                } else {
                    dep
                }
    }
}

// https://github.com/ueberauth/guardian/issues/594
@Suppress("SpellCheckingInspection")
const val GUARDIAN_RUNTIME_TYPO: String = "runtume"
