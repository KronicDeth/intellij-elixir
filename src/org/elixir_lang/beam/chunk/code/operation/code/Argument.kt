package org.elixir_lang.beam.chunk.code.operation.code

import org.elixir_lang.beam.Cache
import org.elixir_lang.beam.chunk.Code
import org.elixir_lang.beam.chunk.Code.Options
import org.elixir_lang.beam.chunk.Code.Options.Inline
import org.elixir_lang.beam.term.*
import org.elixir_lang.beam.term.Float
import org.elixir_lang.beam.term.List
import org.elixir_lang.code.Identifier.inspectAsFunction
import org.elixir_lang.utils.ElixirModulesUtil

data class Argument(val name: String, val supportedOptions: Code.Options = Code.Options()) {
    fun assembly(term: Term, cache: Cache, configuredOptions: Code.Options): String {
        val nameAssembly = nameAssembly(configuredOptions)
        val valueAssembly = valueAssembly(term, cache, configuredOptions)

        return "$nameAssembly$valueAssembly"
    }

    private fun nameAssembly(configuredOptions: Code.Options) =
            if (supportedOptions.showArgumentNames && configuredOptions.showArgumentNames) {
                "$name: "
            } else {
                ""
            }

    private fun valueAssembly(term: Term, cache: Cache, configuredOptions: Code.Options): String {
        return when (term) {
            is AllocationList -> {
                val allocationList = term.allocationList

                "allocation_list(size: ${allocationList.size}, elements: [${allocationList.joinToString(", ")}])"
            }
            is Atom -> {
                val index = term.index

                when {
                    supportedOptions.inline.atoms && configuredOptions.inline.atoms -> {
                        if (index == 0) {
                            "nil"
                        } else {
                            cache.atoms?.get(index)?.string?.let { string ->
                                ElixirModulesUtil.erlangModuleNameToElixir(string)
                            } ?: "invalid_atom_index($index)"
                        }
                    }
                    supportedOptions.inline.integers && configuredOptions.inline.integers -> index.toString()
                    else -> "atom($index)"
                }
            }
            is Character ->
                "character(codePoint: ${term.codePoint})"
            is ExtendedLiteral ->
                TODO("ExtendedLiteral should never be instantiated")
            is Float ->
                TODO("Float only exists outside Literals on OTP <= 19")
            is FloatingPointRegister ->
                "fp(${term.index})"
            is Integer -> {
                val long = term.long

                if (supportedOptions.inline.integers && configuredOptions.inline.integers) {
                    long.toString()
                } else {
                    "integer($long)"
                }
            }
            is Label -> {
                val index = term.index

                if (supportedOptions.inline.labels && configuredOptions.inline.labels) {
                    index.toString()
                } else {
                    "label($index)"
                }
            }
            is List ->
                "list(${term.elements.joinToString(", ") { valueAssembly(it, cache, configuredOptions) }})"
            is Literal -> {
                val index = term.index

                when {
                    supportedOptions.inline.functions && configuredOptions.inline.functions -> {
                        cache.functions?.get(index)?.let { function ->
                            function.name?.let { name ->
                                ":${inspectAsFunction(name)}"
                            } ?: "invalid_function_atom_index(${function.atomIndex})"
                        } ?: "invalid_function_index($index)"
                    }
                    supportedOptions.inline.imports && configuredOptions.inline.imports -> {
                        cache.imports?.get(index)?.let { import ->
                            val moduleName = import.moduleName
                            val functionName = import.functionName

                            when {
                                moduleName != null && functionName != null ->
                                    "&${ElixirModulesUtil.erlangModuleNameToElixir(moduleName)}.$functionName/${import.arity}"
                                moduleName != null && functionName == null ->
                                    "invalid_import_function_name_atom_index(${import.functionAtomIndex}, moduleName: $moduleName)"
                                moduleName == null && functionName != null ->
                                    "invalid_import_module_name_atom_index(${import.moduleAtomIndex}, functionName: $functionName)"
                                else ->
                                    "invalid_import_module_name_and_function_name_atom_indexes(${import.moduleAtomIndex}, ${import.functionAtomIndex})"
                            }
                        } ?: "invalid_import_index($index)"
                    }
                    supportedOptions.inline.literals && configuredOptions.inline.literals -> {
                        cache.literals?.get(index)?.let { literal ->
                            inspect(literal)
                        } ?: "invalid_literal_index($index)"
                    }
                    supportedOptions.inline.integers && configuredOptions.inline.integers -> index.toString()
                    else -> "literal($index)"
                }
            }
            is XRegister ->
                "x(${term.index})"
            is YRegister ->
                "y(${term.index})"
        }
    }
}

val ARITY = Argument("arity", Options(Inline(integers = true, literals = false)))
val DESTINATION_REGISTER = Argument("destination_register")
val DEALLOCATE_WORDS_OF_STACK = Argument("deallocate_words_of_stack", Options(Inline(integers = true)))
val FAIL_LABEL = Argument("fail_label", Options(Inline(labels = true)))
val FLAGS = Argument("flags")
val IMPORT_INDEX = Argument("import_index", Options(Inline(imports = true, integers = true, literals = false)))
val LABEL_ARGUMENT = Argument("label", Options(Inline(labels = true)))
val LIVE_X_REGISTER_COUNT = Argument("live_x_register_count", Options(Inline(integers = true, literals = false)))
val SIZE = Argument("size")
val SOURCE = Argument("source", Options(Inline(atoms = true, integers = false, literals = true)))
val TUPLE = Argument("tuple")
val UNIT = Argument("unit", Options(Inline(atoms = false, integers = true)))
val WORDS_OF_HEAP = Argument("words_of_heap", Options(Inline(integers = true, literals = false)))
val WORDS_OF_STACK = Argument("words_of_stack", Options(Inline(integers = true, literals = false)))

fun arguments(count: Int): Array<Argument> = (1..count).map { Argument("argument$it") }.toTypedArray()

val COMPARISON = arrayOf(FAIL_LABEL, Argument("left"), Argument("right"))
val FIVE = arguments(5)
val FOUR = arguments(4)
val ONE = arrayOf(Argument("argument"))
val SEVEN = arguments(7)
val SIX = arguments(6)
val TWO = arguments(2)
val THREE = arguments(3)
val UNARY = arrayOf(FAIL_LABEL, *ONE)
