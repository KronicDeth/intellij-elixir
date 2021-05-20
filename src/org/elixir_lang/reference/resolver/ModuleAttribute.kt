package org.elixir_lang.reference.resolver

import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.util.ThreeState
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.module_attribute.MultiResolve
import org.elixir_lang.psi.scope.module_attribute.implemetation.For
import org.elixir_lang.psi.scope.module_attribute.implemetation.Protocol
import org.elixir_lang.reference.ModuleAttribute

object ModuleAttribute : ResolveCache.PolyVariantResolver<ModuleAttribute> {
    override fun resolve(moduleAttribute: ModuleAttribute, incompleteCode: Boolean): Array<ResolveResult> {
        val resolveResultOrderedSet = ResolveResultOrderedSet()
        val element = moduleAttribute.element

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
                resolveResultOrderedSet.addAll(MultiResolve.resolveResultOrderedSet(moduleAttribute.value, moduleAttribute.element))
            }
        }

        return resolveResultOrderedSet.toTypedArray()
    }

    private fun validResult(moduleAttribute: ModuleAttribute,
                            moduleAttributeName: String): ThreeState =
            moduleAttribute.value.let { value ->
                when {
                    value == moduleAttributeName -> ThreeState.YES
                    moduleAttributeName.startsWith(value) -> ThreeState.NO
                    else -> ThreeState.UNSURE
                }
            }
}
