package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.ThreeState
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAtIdentifier
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ancestorSequence
import org.elixir_lang.psi.impl.prevSiblingSequence
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.module_attribute.implemetation.For
import org.elixir_lang.psi.scope.module_attribute.implemetation.Protocol
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.reference.ModuleAttribute.Companion.isNonReferencing

object ModuleAttribute : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.ModuleAttribute> {
    override fun resolve(moduleAttribute: org.elixir_lang.reference.ModuleAttribute, incompleteCode: Boolean): Array<ResolveResult> {
        val resolveResultOrderedSet = ResolveResultOrderedSet()
        val element = moduleAttribute.element

        val isNonReferencing = when (element) {
            is AtNonNumericOperation -> isNonReferencing(element)
            is ElixirAtIdentifier -> isNonReferencing(element)
            else -> false
        }

        if (!isNonReferencing) {
            val validProtocolResult = validResult(moduleAttribute, "@protocol")

            if (validProtocolResult != ThreeState.UNSURE) {
                validProtocolResult
                        .toBoolean()
                        .let { validResult -> Protocol.resolveResultOrderedSet(validResult, element) }
                        .let { resolveResultOrderedSet.addAll(it) }
            }

            if (resolveResultOrderedSet.keepProcessing(incompleteCode)) {
                val validForResult = validResult(moduleAttribute, "@for")

                if (validForResult != ThreeState.UNSURE) {
                    validForResult
                            .toBoolean()
                            .let { validResult -> For.resolveResultOrderedSet(validResult, element) }
                            .let { resolveResultOrderedSet.addAll(it) }
                }

                if (resolveResultOrderedSet.keepProcessing(incompleteCode)) {
                    resolveResultOrderedSet.addAll(multiResolveUpFromElement(moduleAttribute, element))
                }
            }
        }

        return resolveResultOrderedSet.toTypedArray()
    }

    private fun multiResolveSibling(
            moduleAttribute: ModuleAttribute,
            lastSibling: PsiElement?
    ): ResolveResultOrderedSet {
        val resolveResultOrderedSet = ResolveResultOrderedSet()

        if (lastSibling != null) {
            val value = moduleAttribute.value

            lastSibling
                    .prevSiblingSequence()
                    .forEach {
                        resolveResultOrderedSet.addIfResolved(it, value)
                    }
        }

        return resolveResultOrderedSet
    }

    private fun multiResolveUpFromElement(
            moduleAttribute: ModuleAttribute,
            element: PsiElement
    ): ResolveResultOrderedSet {
        val resolveResultOrderedSet = ResolveResultOrderedSet()

        resolveResultOrderedSet.addIfResolved(element, moduleAttribute.value)

        element
                .ancestorSequence()
                .forEach { resolveResultOrderedSet.addAll(multiResolveSibling(moduleAttribute, it)) }

        return resolveResultOrderedSet
    }

    private fun validResult(moduleAttribute: org.elixir_lang.reference.ModuleAttribute,
                            moduleAttributeName: String): ThreeState =
            moduleAttribute.value.let { value ->
                when {
                    value == moduleAttributeName -> ThreeState.YES
                    moduleAttributeName.startsWith(value) -> ThreeState.NO
                    else -> ThreeState.UNSURE
                }
            }
}

private fun ResolveResultOrderedSet.addIfResolved(
        element: PsiElement,
        resolvingName: String
) {
    when (element) {
        is AtUnqualifiedNoParenthesesCall<*> -> addIfResolved(element, resolvingName)
    }
}

private fun ResolveResultOrderedSet.addIfResolved(
        element: AtUnqualifiedNoParenthesesCall<*>,
        resolvingName: String
) {
    val moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(element)

    if (moduleAttributeName.startsWith(resolvingName)) {
        val validResult = moduleAttributeName == resolvingName

        this.add(element, moduleAttributeName, validResult)
    }
}


