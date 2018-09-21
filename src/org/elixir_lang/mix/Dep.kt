package org.elixir_lang.mix

import com.intellij.psi.PsiElement
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.impl.stripAccessExpressions

/**
 * `Mix.Dep`
 */
data class Dep(val application: String, val path: String, val type: Type = Type.LIBRARY) {
    enum class Type {
        LIBRARY,
        MODULE
    }

    companion object {
        fun from(depsListElement: PsiElement): Dep? =
            when (depsListElement) {
                is ElixirTuple -> from(depsListElement)
                else -> null
            }

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
                                    "app", "compile", "git", "github", "only", "optional" -> acc
                                    "in_umbrella" -> acc.copy(path =  "apps/$name", type = Type.MODULE)
                                    else -> {
                                        Logger.error(logger, "Don't know if Mix.Dep option `$key` is important for determining location of dependency", depsListElement)
                                        acc
                                    }
                                }
                            }
                        } ?:
                        initial
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
    }
}