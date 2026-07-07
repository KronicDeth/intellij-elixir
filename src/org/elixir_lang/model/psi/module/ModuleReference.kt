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

@Suppress("UnstableApiUsage")
class ModuleReference(
    private val call: Call,
    private val alias: QualifiableAlias,
    private val rangeInElement: TextRange
) : PsiSymbolReference {
    override fun getElement(): PsiElement = call

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
            .mapNotNull { it.element as? Call }
            .filter { Module.`is`(it) }
            .mapNotNull(ModuleSymbol::fromModular)
            .distinct()
    }

    private fun String.removeElixirPrefix(): String =
        if (startsWith("Elixir.")) removePrefix("Elixir.") else this
}
