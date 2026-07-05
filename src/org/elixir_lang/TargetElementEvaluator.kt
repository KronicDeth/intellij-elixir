package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.util.ThreeState
import org.elixir_lang.psi.AtOperation
import org.elixir_lang.psi.QualifiedNoArgumentsCall
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Multiplication
import org.elixir_lang.psi.operation.capture.NonNumeric
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.psi.scope.hasMapFieldOptionalityName
import org.elixir_lang.reference.Module
import org.elixir_lang.structure_view.element.Callback

internal class TargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun isAcceptableNamedParent(parent: PsiElement): Boolean = if (Callback.isHead(parent)) {
        // `@callback`/`@macrocallback` names are owned by the Symbol model (the `Callback` symbol).
        // Don't expose them as a legacy named element, which the platform would otherwise offer as a
        // redundant Find Usages / Go To target beside the symbol's `name/arity` target.
        false
    } else when (parent) {
        // Don't allow the identifier of a module attribute or assign usage be a named parent.
        is UnqualifiedNoArgumentsCall<*> -> when (val grandParent = parent.parent) {
            is AtOperation -> false
            // Don't allow `name` in `&name/arity` to be named parent. The capture needs to be the parent.
            is Multiplication -> when (grandParent.parent) {
                is NonNumeric -> false
                else -> super.isAcceptableNamedParent(parent)
            }
            else -> super.isAcceptableNamedParent(parent)
        }
        // Don't allow `Mod.name` in `&Mod.name/arity` to be named parent.  The capture needs to be the parent.
        is QualifiedNoArgumentsCall<*>  -> when (val grandParent = parent.parent) {
            is Multiplication -> when (grandParent.parent) {
                is NonNumeric -> false
                else -> super.isAcceptableNamedParent(parent)
            }
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

    override fun isAcceptableReferencedElement(
        element: PsiElement,
        referenceOrReferencedElement: PsiElement?
    ): ThreeState =
        // A `@callback`/`@macrocallback` name resolves (via a reference) to itself; the Symbol model
        // owns it, so don't accept it as a legacy *referenced* Find Usages / Go To target either
        // (`isAcceptableNamedParent` above already closes the named-element path). Together these leave
        // the `Callback` symbol as the sole target - no redundant chooser entry.
        if (referenceOrReferencedElement != null && Callback.isHead(referenceOrReferencedElement)) {
            ThreeState.NO
        } else {
            super.isAcceptableReferencedElement(element, referenceOrReferencedElement)
        }

    override fun getTargetCandidates(reference: PsiReference): MutableCollection<PsiElement>? =
        when (reference) {
            // Module references resolve through Resolver.preferred() which already prefers source over decompiled,
            // but falls back to decompiled if no source exists (e.g. for a call only defined in .beam).
            is Module -> {
                reference
                        .multiResolve(false)
                        .mapNotNull(ResolveResult::getElement)
                        .toMutableList()
            }
            else -> {
                // `@callback`/`@macrocallback` names are owned by the Symbol model. The Find Usages
                // action's `targetVariants` gathers a legacy `PsiTargetVariant` straight from
                // `getTargetCandidates(reference)` (bypassing `isAcceptableReferencedElement`), so the
                // callback's own `Type` self-reference (`perform()` -> the `@callback` head) would
                // otherwise add a redundant target beside the symbol's `name/arity` target - an
                // ambiguity popup. `TargetElementUtil.getTargetCandidates` only skips its default
                // `multiResolve` fallback when the evaluator returns a NON-null collection, so return an
                // empty (non-null) list for a callback-head self-reference; defer everything else.
                val resolved = reference.resolve()
                if (resolved != null && Callback.isHead(resolved)) {
                    mutableListOf()
                } else {
                    super.getTargetCandidates(reference)
                }
            }
        }
}
