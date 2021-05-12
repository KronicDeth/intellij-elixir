package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.*
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.psi.scope.hasMapFieldOptionalityName
import org.elixir_lang.reference.Module
import org.elixir_lang.reference.Resolver

class TargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun isAcceptableNamedParent(parent: PsiElement): Boolean = when (parent) {
        // Don't allow the identifier of a module attribute or assign usage be a named parent.
        is UnqualifiedNoArgumentsCall<*> -> when (parent.parent) {
            is AtNonNumericOperation -> false
            else -> super.isAcceptableNamedParent(parent)
        }
        is Call -> {
            if (parent.hasMapFieldOptionalityName() && parent.ancestorTypeSpec() != null) {
                false
            } else {
                super.isAcceptableNamedParent(parent)
            }
        }
        else -> super.isAcceptableNamedParent(parent)
    }

    override fun getTargetCandidates(reference: PsiReference): MutableCollection<PsiElement>? =
        when (reference) {
            // Module references need to resolve to decompiled element in case a call is only defined in the decompiled
            // code and not the source, but users prefer source for the actual Alias Go To Declaration.
            is Module -> {
                reference
                        .multiResolve(false)
                        .toList()
                        .let { Resolver.preferSource(it) }
                        .mapNotNull(ResolveResult::getElement)
                        .toMutableList()
            }
            else -> super.getTargetCandidates(reference)
        }
}
