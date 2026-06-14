package org.elixir_lang.psi.impl.call.qualification

import com.intellij.psi.PsiElement
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.jetbrains.annotations.Contract


/**
 * Finds modular (`defmodule`, `defimpl`, or `defprotocol`) for the qualifier of
 * `qualified`.
 *
 * @receiver this@qualifiedToModulars a qualified expression
 * @return `null` if the modular cannot be resolved, such as when the qualifying Alias is invalid or is an
 * unparsed module like `Kernel` or `Enum` OR if the qualified isn't an Alias.
 */
@RequiresReadLock
@Contract(pure = true)
fun Qualified.qualifiedToModulars(): Set<PsiElement> =
    qualifier().maybeModularNameToModulars(maxScope = containingFile, useCall = null, incompleteCode = false)
