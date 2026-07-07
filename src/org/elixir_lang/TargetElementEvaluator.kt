package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.ResolveResult
import com.intellij.util.ThreeState
import org.elixir_lang.psi.AtOperation
import org.elixir_lang.psi.Protocol
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
    override fun isAcceptableNamedParent(parent: PsiElement): Boolean = when {
        Callback.isHead(parent) -> {
            // `@callback`/`@macrocallback` names are owned by the Symbol model (the `Callback` symbol).
            // Don't expose them as a legacy named element, which the platform would otherwise offer as a
            // redundant Find Usages / Go To target beside the symbol's `name/arity` target.
            false
        }

        parent is UnqualifiedNoArgumentsCall<*> -> when (val grandParent = parent.parent) {
            is AtOperation -> false
            // Don't allow `name` in `&name/arity` to be named parent. The capture needs to be the parent.
            is Multiplication -> when (grandParent.parent) {
                is NonNumeric -> false
                else -> super.isAcceptableNamedParent(parent)
            }

            else -> super.isAcceptableNamedParent(parent)
        }
        // Don't allow `Mod.name` in `&Mod.name/arity` to be named parent.  The capture needs to be the parent.
        parent is QualifiedNoArgumentsCall<*> -> when (val grandParent = parent.parent) {
            is Multiplication -> when (grandParent.parent) {
                is NonNumeric -> false
                else -> super.isAcceptableNamedParent(parent)
            }

            else -> super.isAcceptableNamedParent(parent)
        }

        parent is Call -> {
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
            when {
                referenceOrReferencedElement != null && Callback.isHead(referenceOrReferencedElement) -> {
                    // A `@callback`/`@macrocallback` name resolves (via a reference) to itself; the Symbol model
                    // owns it, so don't accept it as a legacy *referenced* Find Usages / Go To target either
                    // (`isAcceptableNamedParent` above already closes the named-element path). Together these leave
                    // the `Callback` symbol as the sole target - no redundant chooser entry.
                    ThreeState.NO
                }

                referenceOrReferencedElement != null && Protocol.isHead(referenceOrReferencedElement) -> {
                    // A protocol function name resolves to itself; the Symbol model owns it, so don't accept it as a
                    // legacy *referenced* Find Usages / Go To target.
                    ThreeState.NO
                }

                else -> super.isAcceptableReferencedElement(element, referenceOrReferencedElement)
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
                    val resolved = reference.resolve()
                    when {
                        resolved != null && Callback.isHead(resolved) -> {
                            // `@callback`/`@macrocallback` names are owned by the Symbol model. The Find Usages
                            // action's `targetVariants` gathers a legacy `PsiTargetVariant` straight from
                            // `getTargetCandidates(reference)` (bypassing `isAcceptableReferencedElement`), so the
                            // callback's own `Type` self-reference (`perform()` -> the `@callback` head) would
                            // otherwise add a redundant target beside the symbol's `name/arity` target - an
                            // ambiguity popup. `TargetElementUtil.getTargetCandidates` only skips its default
                            // `multiResolve` fallback when the evaluator returns a NON-null collection, so return an
                            // empty (non-null) list for a callback-head self-reference; defer everything else.
                            mutableListOf()
                        }

                        resolved != null && Protocol.isHead(resolved) -> {
                            // Protocol function names are owned by the Symbol model; return empty list to prevent
                            // redundant target for self-references.
                            mutableListOf()
                        }

                        else -> super.getTargetCandidates(reference)
                    }
                }
            }
}
