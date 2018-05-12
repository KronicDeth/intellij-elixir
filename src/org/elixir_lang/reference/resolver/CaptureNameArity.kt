package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModular
import org.elixir_lang.psi.scope.call_definition_clause.MultiResolve
import org.elixir_lang.reference.CaptureNameArity

object CaptureNameArity : ResolveCache.PolyVariantResolver<CaptureNameArity> {
    override fun resolve(reference: CaptureNameArity, incompleteCode: Boolean): Array<out ResolveResult> {
        val nameElement  = reference.nameElement

        return nameElement.functionName()?.let { name ->
            val arity = reference.arity

            if (nameElement is Qualified) {
                nameElement.qualifiedToModular()?.let { modular ->
                    Modular.callDefinitionClauseCallFoldWhile(
                            modular,
                            name,
                            arity,
                            mutableListOf<ResolveResult>()
                    ) { callDefinitionClauseCall, _, _, acc ->
                        acc.add(PsiElementResolveResult(callDefinitionClauseCall, true))

                        AccumulatorContinue(acc, true)
                    }.accumulator.toTypedArray()
                }
            } else {
                MultiResolve.resolveResults(name, arity, incompleteCode, reference.element)
            }
        } ?: emptyArray()
    }
}
