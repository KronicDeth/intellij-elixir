package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL

enum class Definition(val type: Type) {
    IMPLEMENTATION(Type.MODULAR),
    MODULE(Type.MODULAR),
    PROTOCOL(Type.MODULAR),
    PUBLIC_FUNCTION(Type.CALLABLE),
    PRIVATE_FUNCTION(Type.CALLABLE),
    PUBLIC_GUARD(Type.CALLABLE),
    PRIVATE_GUARD(Type.CALLABLE),
    PUBLIC_MACRO(Type.CALLABLE),
    PRIVATE_MACRO(Type.CALLABLE);

    enum class Type {
        CALLABLE,
        MODULAR
    }
}

/**
 * What kind of definition is defined by the [call]
 */
fun definition(call: Call): Definition? =
    call.resolvedModuleName()?.let { resolvedModuleName ->
        call.functionName()?.let { functionName ->
            if (resolvedModuleName == KERNEL) {
                val resolvedFinalArity = call.resolvedFinalArity()
                val hasDoBlockOrKeyword= call.hasDoBlockOrKeyword()

                definition(functionName, resolvedFinalArity, hasDoBlockOrKeyword)
            } else {
                null
            }
        }
    }

fun definition(resolvedModuleName: String?, functionName: String?, resolvedFinalArity: Int, hasDoBlockOrKeyword: Boolean) =
        if (resolvedModuleName == KERNEL) {
            definition(functionName, resolvedFinalArity, hasDoBlockOrKeyword)
        } else {
            null
        }

fun definition(functionName: String?, resolvedFinalArity: Int, hasDoBlockOrKeyword: Boolean) =
        if (resolvedFinalArity == 3 && hasDoBlockOrKeyword) {
            when (functionName) {
                DEFIMPL -> Definition.IMPLEMENTATION
                else -> null
            }
        } else if (resolvedFinalArity == 2 && hasDoBlockOrKeyword) {
            when (functionName) {
                DEF -> Definition.PUBLIC_FUNCTION
                DEFP -> Definition.PRIVATE_FUNCTION
                DEFMACRO -> Definition.PUBLIC_MACRO
                DEFMACROP -> Definition.PRIVATE_MACRO
                DEFMODULE -> Definition.MODULE
                DEFIMPL -> Definition.IMPLEMENTATION
                DEFPROTOCOL -> Definition.PROTOCOL
                else -> null
            }
        } else if (resolvedFinalArity == 1) {
            when (functionName) {
                DEF -> Definition.PUBLIC_FUNCTION
                DEFP -> Definition.PRIVATE_FUNCTION
                DEFGUARD -> Definition.PUBLIC_GUARD
                DEFGUARDP -> Definition.PRIVATE_GUARD
                DEFMACRO -> Definition.PUBLIC_MACRO
                DEFMACROP -> Definition.PRIVATE_MACRO
                else -> null
            }
        } else {
            null
        }
