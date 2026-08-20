package org.elixir_lang.model.psi.module

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.resolver.Module as ModuleResolver

/**
 * A module reference hosted on the outermost [QualifiableAlias] of a usage site, resolving [alias]
 * (the chain node this reference's range covers - the whole chain or one of its prefixes).
 */
@Suppress("UnstableApiUsage")
class ModuleReference(
    private val host: QualifiableAlias,
    private val alias: QualifiableAlias,
    private val rangeInElement: TextRange
) : PsiSymbolReference {
    override fun getElement(): PsiElement = host

    override fun getRangeInElement(): TextRange = rangeInElement

    @RequiresReadLock
    override fun resolveReference(): Collection<Symbol> {
        val resolved = ModuleResolver.resolve(
            alias,
            alias.fullyQualifiedName().removeElixirPrefix(),
            false
        )

        return resolved
            .filter(ResolveResult::isValidResult)
            .mapNotNull { result ->
                when (val element = result.element) {
                    is Call -> element
                    // A module that exists only as compiled `.beam` resolves to the coarse stub element;
                    // its navigationElement is the `defmodule` Call in the decompiled mirror, which the
                    // source pipeline below accepts (same pattern as TypeReference/FunctionCallReference).
                    is org.elixir_lang.beam.psi.impl.ModuleImpl<*> -> element.navigationElement as? Call
                    else -> null
                }
            }
            .filter { Module.`is`(it) }
            .mapNotNull(ModuleSymbol::fromModular)
            .distinct()
    }

    private fun String.removeElixirPrefix(): String =
        if (startsWith("Elixir.")) removePrefix("Elixir.") else this
}
