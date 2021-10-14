package org.elixir_lang.psi

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.MODULE

enum class Definition(val type: Type) {
    IMPLEMENTATION(Type.MODULAR),
    MODULE(Type.MODULAR),
    MODULE_ATTRIBUTE(Type.CALLABLE),
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
        definition(call.resolvedModuleName(), call.functionName(), call.resolvedFinalArity(), call.hasDoBlockOrKeyword())

fun definition(resolvedModuleName: String?, functionName: String?, resolvedFinalArity: Int, hasDoBlockOrKeyword: Boolean) =
        when (resolvedModuleName) {
            KERNEL -> if (resolvedFinalArity == 3 && hasDoBlockOrKeyword) {
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
            MODULE -> if (functionName == "register_attribute" && resolvedFinalArity == 3 && !hasDoBlockOrKeyword) {
                Definition.MODULE_ATTRIBUTE
            } else {
                null
            }
            else -> null
        }
