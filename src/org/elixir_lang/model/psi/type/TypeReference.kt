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
import org.elixir_lang.beam.psi.impl.TypeDefinitionImpl
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
    override fun resolveReference(): Collection<Symbol> {
        val typeSymbols = resolveSymbols(call)

        return if (typeSymbols.isNotEmpty()) typeSymbols else resolveTypeVariableSymbols(call)
    }

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

        /**
         * Whether [call] resolves to any type definition. Unlike [resolveSymbols], this also counts decompiled BEAM
         * types (`TypeDefinitionImpl`, e.g. `:queue.queue/0` from the Erlang stdlib), which [resolveSymbols] drops
         * because it can only build [TypeSymbol]s from source `@type` [Call]s.
         */
        @RequiresReadLock
        fun resolvesToType(call: Call): Boolean {
            val resolved = if (call is Qualified) resolveQualified(call) else resolveUnqualified(call)

            return resolved
                .filter(ResolveResult::isValidResult)
                .any { result ->
                    when (val element = result.element) {
                        is Call -> TypeElement.`is`(element)
                        is TypeDefinitionImpl<*> -> true
                        else -> false
                    }
                }
        }

        /**
         * Resolves an unqualified type name to the type variable(s) that declare it - a head parameter of an
         * enclosing `@type`/`@typep`/`@opaque` (e.g. `a` in `@type box(a) :: {:box, a}`) or a `@spec ... when`
         * binding. Qualified names can never be type variables, so they resolve to nothing here. Only consulted
         * when [resolveSymbols] finds no `@type` definition, so real types always win.
         */
        @RequiresReadLock
        fun resolveTypeVariableSymbols(call: Call): List<TypeVariableSymbol> {
            if (call is Qualified) return emptyList()
            val name = call.functionName() ?: return emptyList()

            return MultiResolve.resolveResults(name, call.resolvedFinalArity(), false, call)
                .filter(ResolveResult::isValidResult)
                .mapNotNull { it.element }
                .mapNotNull { TypeVariableSymbol.fromDeclaration(it) }
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
