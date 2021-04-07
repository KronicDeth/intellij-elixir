package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModulars
import org.elixir_lang.psi.scope.call_definition_clause.MultiResolve
import org.elixir_lang.reference.CaptureNameArity

object CaptureNameArity : ResolveCache.PolyVariantResolver<CaptureNameArity> {
    override fun resolve(reference: CaptureNameArity, incompleteCode: Boolean): Array<out ResolveResult> {
        val nameElement  = reference.nameElement

        return nameElement.functionName()?.let { name ->
            val arity = reference.arity

            if (nameElement is Qualified) {
                nameElement.qualifiedToModulars().flatMap { modular ->
                    Modular.callDefinitionClauseCallFoldWhile(
                            modular,
                            name,
                            mutableListOf<ResolveResult>()
                    ) { callDefinitionClauseCall, _, arityRange, acc ->
                        acc.add(PsiElementResolveResult(callDefinitionClauseCall, arityRange.contains(arity)))

                        AccumulatorContinue(acc, true)
                    }.accumulator
                }.toTypedArray()
            } else {
                MultiResolve.resolveResults(name, arity, incompleteCode, reference.element)
            }
        } ?: emptyArray()
    }
}
