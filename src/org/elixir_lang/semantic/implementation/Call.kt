package org.elixir_lang.semantic.implementation

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Arity
import org.elixir_lang.CanonicallyNamed.Companion.CANONICAL_NAME_SET
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Implementation
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Protocol
import org.elixir_lang.semantic.call.Definitions

class Call(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call) :
    org.elixir_lang.semantic.modular.Call(enclosingModular, call), Implementation {
    override val moduleDocs: List<org.elixir_lang.semantic.documentation.Module>
        get() = TODO("Not yet implemented")
    override val callDefinitions: Definitions
        get() = TODO("Not yet implemented")
    override val decompiled: Set<Modular>
        get() = TODO("Not yet implemented")
    override val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
        get() = TODO("Not yet implemented")
    override val locationString: String
        get() = TODO("Not yet implemented")
    override val name: String?
        get() = canonicalName
    override val canonicalNameSet: Set<String>
        get() = CachedValuesManager.getCachedValue(psiElement, CANONICAL_NAME_SET) {
            CachedValueProvider.Result.create(computeCanonicalNameSet(this), psiElement)
        }
    override val presentation: ItemPresentation?
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    override val protocolName: String
        get() = CachedValuesManager.getCachedValue(psiElement, PROTOCOL_NAME) {
            CachedValueProvider.Result.create(computeProtocolName(protocolNameElement), psiElement)
        }
    val protocolNameElement: QualifiableAlias?
        get() = CachedValuesManager.getCachedValue(psiElement, PROTOCOL_NAME_ELEMENT) {
            CachedValueProvider.Result.create(computeProtocolNameElement(call), psiElement)
        }
    override val forNames: List<org.elixir_lang.semantic.implementation.call.ForName>
        get() = TODO("Not yet implemented")
    val forNamesElement: PsiElement?
        get() = CachedValuesManager.getCachedValue(psiElement, FOR_NAME_ELEMENT) {
            CachedValueProvider.Result.create(computeForNameElement(call), psiElement)
        }
    override val protocols: List<Protocol>
        get() = CachedValuesManager.getCachedValue(psiElement, PROTOCOLS) {
            CachedValueProvider.Result.create(computeProtocols(this), psiElement)
        }

    companion object {
        const val MODULE = Module.KERNEL
        const val FUNCTION = Function.DEFIMPL
        private val ARITY_RANGE = 2..3

        private fun computeCanonicalNameSet(call: Call): Set<String> {
            val enclosingPrefix = call.enclosingModular?.canonicalName?.let { "$it." } ?: ""
            val prefix = "$enclosingPrefix${call.protocolName}"

            return call
                .forNames
                .map { forName ->
                    "$prefix.$forName"
                }
                .toSet()
        }

        private val PROTOCOL_NAME: Key<CachedValue<String>> =
            Key("elixir.semantic.implementation.protocol_name")

        private fun computeProtocolName(protocolNameElement: QualifiableAlias?): String =
            protocolNameElement?.fullyQualifiedName()?.replace("Elixir.", "") ?: "?"

        private val PROTOCOL_NAME_ELEMENT: Key<CachedValue<QualifiableAlias?>> =
            Key("elixir.semantic.implementation.protocol_name_element")

        private fun computeProtocolNameElement(call: org.elixir_lang.psi.call.Call): QualifiableAlias? =
            call.finalArguments()?.firstOrNull()?.let { firstFinalArgument ->
                when (firstFinalArgument) {
                    is ElixirAccessExpression -> firstFinalArgument.stripAccessExpression() as? QualifiableAlias
                    is QualifiableAlias -> firstFinalArgument
                    else -> null
                }
            }

        private val FOR_NAME_ELEMENT: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.implementation.protocol_name_element")

        private fun computeForNameElement(call: org.elixir_lang.psi.call.Call): PsiElement? =
            call
                .finalArguments()
                ?.lastOrNull()?.let { it as? QuotableKeywordList }
                ?.keywordValue(Function.FOR)

        private val PROTOCOLS: Key<CachedValue<List<Protocol>>> = Key("elixir.implementation.protocols")

        private fun computeProtocols(call: Call): List<Protocol> {
            TODO()
        }

        fun from(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call, arity: Arity): Implementation? =
            if (arity in ARITY_RANGE) {
                Call(enclosingModular, call)
            } else {
                null
            }
    }
}
