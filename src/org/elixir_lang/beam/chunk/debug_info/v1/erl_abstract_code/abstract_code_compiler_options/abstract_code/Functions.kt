package org.elixir_lang.beam.chunk.debug_info.v1.erl_abstract_code.abstract_code_compiler_options.abstract_code

import org.elixir_lang.NameArity
import org.elixir_lang.toArity

class Functions(val functions: List<Function>) {
    val byNameArity by lazy {
        functions
                .mapNotNull { function ->
                    function.name?.let { name ->
                        function.arity?.let { arity ->
                            NameArity(name.atomValue(), arity.toArity()) to function
                        }
                    }
                }
                .toMap()
    }
}

