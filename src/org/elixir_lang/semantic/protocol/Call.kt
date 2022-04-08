package org.elixir_lang.semantic.protocol

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.Arity
import org.elixir_lang.CanonicallyNamed.Companion.getCanonicalName
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Implementation
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Protocol
import org.elixir_lang.semantic.Protocol.Companion.computeImplementations
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.modular.CanonicallyNamedMaybeEnclosed.Companion.getCanonicalNameSet

class Call(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call) :
    org.elixir_lang.semantic.modular.Call(enclosingModular, call), Protocol {
    val nameIdentifier: PsiElement?
        get() = CachedValuesManager.getCachedValue(call, NAME_IDENTIFIER) {
            CachedValueProvider.Result.create(computeNameIdentifier(call), call)
        }
    override val canonicalName: String
        get() = getCanonicalName(this, psiElement)!!
    override val canonicalNameSet: Set<String>
        get() = getCanonicalNameSet(this, psiElement)
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
    override val presentation: ItemPresentation?
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    override val implementations: List<Implementation>
        get() = CachedValuesManager.getCachedValue(psiElement, IMPLEMENTATIONS) {
            CachedValueProvider.Result.create(computeImplementations(this), psiElement)
        }
    override val name: String
        get() = TODO("Not yet implemented")

    companion object {
        const val MODULE = Module.KERNEL
        const val FUNCTION = Function.DEFPROTOCOL
        const val ARITY = 2

        private val IMPLEMENTATIONS: Key<CachedValue<List<Implementation>>> = Key("elixir.protocol.implementations")

        fun from(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call, arity: Arity): Protocol? =
            if (arity == ARITY) {
                Call(enclosingModular, call)
            } else {
                null
            }

        private val NAME_IDENTIFIER: Key<CachedValue<PsiElement?>> =
            Key("elixir.semantic.protocol.call.name_identifier")

        private fun computeNameIdentifier(call: org.elixir_lang.psi.call.Call): PsiElement? =
            call.primaryArguments()?.firstOrNull()?.stripAccessExpression()
    }
}

