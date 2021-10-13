package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ThreeState
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.module_attribute.MultiResolve
import org.elixir_lang.psi.scope.module_attribute.implemetation.For
import org.elixir_lang.psi.scope.module_attribute.implemetation.Protocol
import org.elixir_lang.psi.stub.index.QuoteModuleAttributeName
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

            if (resolveResultOrderedSet.keepProcessing(incompleteCode)) {
                resolveResultOrderedSet.addAll(nameInAnyQuote(element, moduleAttribute.value, incompleteCode))
            }
        }

        return resolveResultOrderedSet.toList().toTypedArray()
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

    private fun nameInAnyQuote(element: PsiElement,
                               name: String,
                               incompleteCode: Boolean): ResolveResultOrderedSet {
        val project = element.project
        val keys = mutableListOf<String>()
        val stubIndex = StubIndex.getInstance()

        stubIndex.processAllKeys(QuoteModuleAttributeName.KEY, project) { key ->
            if ((incompleteCode && key.startsWith(name)) || key == name) {
                keys.add(key)
            }

            true
        }

        val scope = GlobalSearchScope.allScope(project)
        val resolveResultOrderedSet = ResolveResultOrderedSet()
        // results are never valid because this doesn't prove the parent `quote` is included at the `element` site.
        val validResult = false

        for (key in  keys) {
            stubIndex
                    .processElements(QuoteModuleAttributeName.KEY, key, project, scope, NamedElement::class.java) { namedElement ->
                        val namedElementName = namedElement.name

                        if (namedElementName != null && namedElementName.startsWith(name)) {
                            resolveResultOrderedSet.add(namedElement, namedElementName, validResult, emptySet())
                        }

                        true
                    }
        }

        return resolveResultOrderedSet
    }
}
