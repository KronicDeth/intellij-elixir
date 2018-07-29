package org.elixir_lang

import com.intellij.openapi.util.Condition
import org.jetbrains.annotations.Contract

object Module {
    private const val SEPARATOR = "."

    class IsNestedUnder (moduleName: String) : Condition<String> {
        private val splitModuleName: kotlin.collections.List<String> = split(moduleName)

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
    fun split(name: String): kotlin.collections.List<String> = name.split(SEPARATOR)
}
