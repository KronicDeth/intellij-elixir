package org.elixir_lang.model.psi.type

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.scope.type.MultiResolve
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.structure_view.element.Type as TypeElement

@Suppress("UnstableApiUsage")
class TypeReference(
    private val call: Call,
    private val rangeInElement: TextRange
) : PsiSymbolReference {
    override fun getElement(): PsiElement = call

    override fun getRangeInElement(): TextRange = rangeInElement

    @RequiresReadLock
    override fun resolveReference(): Collection<Symbol> =
        resolveSymbols(call)

    companion object {
        @RequiresReadLock
        fun resolveSymbols(call: Call): List<TypeSymbol> {
            val resolved = if (call is Qualified) {
                resolveQualified(call)
            } else {
                resolveUnqualified(call)
            }

            return resolved
                .filter(ResolveResult::isValidResult)
                .mapNotNull { it.element as? Call }
                .filter { TypeElement.`is`(it) }
                .flatMap { TypeSymbol.fromTypeAttribute(it) }
                .distinct()
        }

        @RequiresReadLock
        private fun resolveQualified(qualified: Qualified): List<ResolveResult> =
            qualified.qualifiedToModulars().flatMap { modular ->
                qualified.functionName()?.let { name ->
                    MultiResolve.resolveResults(name, qualified.resolvedFinalArity(), false, modular)
                } ?: emptyList()
            }

        @RequiresReadLock
        private fun resolveUnqualified(call: Call): List<ResolveResult> =
            call.functionName()?.let { name ->
                val arity = call.resolvedFinalArity()
                val inScope = MultiResolve.resolveResults(name, arity, false, call)
                if (inScope.any(ResolveResult::isValidResult) ||
                    TypeBuiltins.BUILTIN_ARITY_BY_NAME[name]?.contains(arity) != true
                ) {
                    inScope
                } else {
                    inScope + resolveBuiltin(call, name, arity)
                }
            } ?: emptyList()

        @RequiresReadLock
        private fun resolveBuiltin(entrance: PsiElement, name: String, arity: Int): List<ResolveResult> {
            val project = entrance.project
            if (DumbService.isDumb(project)) return emptyList()

            val resolveResults = mutableListOf<ResolveResult>()
            StubIndex.getInstance().processElements(
                ModularName.KEY,
                ":erlang",
                project,
                GlobalSearchScope.allScope(project),
                NamedElement::class.java
            ) { namedElement ->
                resolveResults.addAll(MultiResolve.resolveResults(name, arity, false, namedElement))
                true
            }
            return resolveResults
        }
    }
}
