package org.elixir_lang

import com.intellij.openapi.util.Condition
import org.jetbrains.annotations.Contract
import kotlin.collections.List

object Module {
    private const val SEPARATOR = "."

    class IsNestedUnder (moduleName: String) : Condition<String> {
        private val splitModuleName: List<String> = split(moduleName)

        override fun value(maybeStartsWithModuleName: String): Boolean {
            val splitMaybeStartsWithModuleName = split(maybeStartsWithModuleName)
            var isNestedUnder = true

            if (splitMaybeStartsWithModuleName.size > splitModuleName.size) {
                for (i in splitModuleName.indices) {
                    if (splitMaybeStartsWithModuleName[i] != splitModuleName[i]) {
                        isNestedUnder = false

                        break
                    }
                }
            } else {
                isNestedUnder = false
            }

            return isNestedUnder
        }
    }

    /**
     * Emulates Module.concat/1
     */
    @Contract(pure = true)
    @JvmStatic
    fun concat(aliases: Collection<String>): String = aliases.joinToString(SEPARATOR)

    /**
     * Emulates Module.split/1
     */
    @Contract(pure = true)
    @JvmStatic
    fun split(name: String): List<String> = name.split(SEPARATOR)

    fun prefix(name: String): List<String> = split(name).dropLast(1)

    fun isRelative(ancestors: List<String>, descendant: String): Boolean = relative(ancestors, descendant).isNotEmpty()

    fun relative(ancestors: List<String>, descendant: String): List<String> {
        val descendants = split(descendant)
        return relative(ancestors, descendants)
    }

    fun relative(ancestors: List<String>, descendants: List<String>): List<String> {
        return if (ancestors.size < descendants.size &&
                ancestors.zip(descendants).all { (ancestor, descendent) -> ancestor == descendent }) {
            descendants.subList(ancestors.size, descendants.size)
        } else {
            emptyList()
        }
    }
}
