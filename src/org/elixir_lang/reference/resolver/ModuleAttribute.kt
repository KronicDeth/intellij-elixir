package org.elixir_lang.reference.resolver

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ThreeState
import org.elixir_lang.beam.psi.impl.CallDefinitionStubImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.module_attribute.MultiResolve
import org.elixir_lang.psi.scope.module_attribute.implemetation.For
import org.elixir_lang.psi.scope.module_attribute.implemetation.Protocol
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.psi.stub.index.QuoteModuleAttributeName
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.sdk.elixir.Type.Companion.mostSpecificSdk

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
                // no need to recheck `keepProcessing` since `nameInAnyQuote` is always invalid results
                resolveResultOrderedSet.addAll(nameInElixirModuleErl(element, moduleAttribute.value, incompleteCode))
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

    private val ELIXIR_MODULE_ERL_NAMES = arrayOf("after_compile", "before_compile", "behaviour", "compile", "derive", "dialyzer", "external_resource",  "on_definition")
    private val ELIXIR_MODULE_ERL_MODULE_ATTRIBUTES = ELIXIR_MODULE_ERL_NAMES.map { "@${it}" }

    private fun nameInElixirModuleErl(element: PsiElement, name: String, incompleteCode: Boolean): ResolveResultOrderedSet {
        val resolveResultOrderedSet = ResolveResultOrderedSet()
        val matchedModuleAttributes = ELIXIR_MODULE_ERL_MODULE_ATTRIBUTES.filter { it.startsWith(name) }

        if (matchedModuleAttributes.isNotEmpty()) {
            val project = element.project
            val scope = GlobalSearchScope.allScope(project)

            StubIndex
                    .getInstance()
                    .processElements(ModularName.KEY, ":elixir_module", project, scope, NamedElement::class.java) { namedElement ->
                        if (namedElement is ModuleImpl<*>) {
                            namedElement
                                    .stub
                                    ?.childrenStubs
                                    ?.filterIsInstance<CallDefinitionStubImpl<*>>()
                                    ?.filter { it.name == "build" && it.callDefinitionClauseHeadArity() == 3 }
                                    ?.forEach { callDefinitionStubImpl ->
                                        val navigationElement = callDefinitionStubImpl.psi.navigationElement
                                        val text = navigationElement.text

                                        matchedModuleAttributes.forEach { matchedModuleAttribute ->
                                            val matchedName = matchedModuleAttribute.substring(1, matchedModuleAttribute.length)
                                            val atomString = ":${matchedName}"
                                            val index = text.indexOf(atomString)

                                            if (index != -1) {
                                                navigationElement
                                                        .findElementAt(index)
                                                        ?.parent?.let { it as? ElixirAtom }
                                                        ?.let { deriveAtom ->
                                                            resolveResultOrderedSet.add(deriveAtom, deriveAtom.text, true, emptySet())
                                                        }
                                            }
                                        }
                                    }
                        }

                        true
                    }
        }

        return resolveResultOrderedSet
    }
}
