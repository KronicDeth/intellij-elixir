package org.elixir_lang.psi.impl.call.qualification

import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.jetbrains.annotations.Contract


/**
 * Finds modular (`defmodule`, `defimpl`, or `defprotocol`) for the qualifier of
 * `qualified`.
 *
 * @param this@qualifiedToModular a qualified expression
 * @return `null` if the modular cannot be resolved, such as when the qualifying Alias is invalid or is an
 * unparsed module like `Kernel` or `Enum` OR if the qualified isn't an Alias.
 */
@Contract(pure = true)
fun Qualified.qualifiedToModulars(): List<Call> =
        qualifier().maybeModularNameToModulars(maxScope = containingFile, useCall = null, incompleteCode = false)
