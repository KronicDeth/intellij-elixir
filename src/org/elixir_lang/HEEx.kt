package org.elixir_lang

import com.intellij.psi.ResolveState
import org.elixir_lang.psi.call.Call

object HEEx {
    fun isFunctionFrom(call: Call, state: ResolveState): Boolean =
        call.functionName()?.let { functionName ->
            when (functionName) {
                FUNCTION_FROM_FILE_ARITY_RANGE.name ->
                    call.resolvedFinalArity() in FUNCTION_FROM_FILE_ARITY_RANGE.arityRange &&
                            resolvesToHEEx(call, state)
                FUNCTION_FROM_STRING_ARITY_RANGE.name ->
                    call.resolvedFinalArity() in FUNCTION_FROM_STRING_ARITY_RANGE.arityRange &&
                            resolvesToHEEx(call, state)
                else -> false
            }
        } ?: false

    private fun resolvesToHEEx(call: Call, state: ResolveState): Boolean =
            resolvesToModularName(call, state, "HEEx")

    // function_from_file(kind, name, file, args \\ [], options \\ [])
    val FUNCTION_FROM_FILE_ARITY_RANGE = NameArityRange("function_from_file", 3..5)
    // function_from_string(kind, name, source, args \\ [], options \\ [])
    val FUNCTION_FROM_STRING_ARITY_RANGE = NameArityRange("function_from_string", 3..5)
}
