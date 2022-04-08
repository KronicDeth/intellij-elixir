package org.elixir_lang.semantic.module

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.beam.psi.ModuleDefinition
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.call.definition.clause.Using
import org.elixir_lang.semantic.documentation.Module
import org.elixir_lang.semantic.semantic

open class Binary(val moduleDefinition: ModuleDefinition) : org.elixir_lang.semantic.Module {
    override val psiElement: PsiElement
        get() = moduleDefinition
    override val types: List<org.elixir_lang.semantic.type.Definition>
        get() = CachedValuesManager.getCachedValue(psiElement, TYPES) {
            val computed =
                moduleDefinition
                    .typeDefinitions
                    .map(PsiElement::semantic)
                    .filterIsInstance<org.elixir_lang.semantic.type.Definition>()

            CachedValueProvider.Result.create(computed, listOf(moduleDefinition))
        }
    override val exportedCallDefinitions: Definitions
        get() = CachedValuesManager.getCachedValue(psiElement, EXPORTED_CALL_DEFINITIONS) {
            val definitions =
                moduleDefinition
                    .callDefinitions
                    .map(PsiElement::semantic)
                    .filterIsInstance<Definition>()
                    .sortedBy(Definition::nameArityInterval)

            CachedValueProvider.Result.create(definitions, listOf(moduleDefinition))
        }
    override val usings: List<Using> = emptyList()
    override val isDecompiled: Boolean = true
    override val decompiled: Set<Modular>
        get() = TODO("Not yet implemented")

    override val moduleDocs: List<Module>
        get() = TODO("Not yet implemented")
    override val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
        get() = TODO("Not yet implemented")
    override val locationString: String
        get() = TODO("Not yet implemented")
    override val enclosingModular: Modular? = null
    override val name: String?
        get() = moduleDefinition.name
    override val canonicalName: String
        get() = moduleDefinition.name
    override val canonicalNameSet: Set<String>
        get() = setOf(canonicalName)
    override val presentation: ItemPresentation?
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        TODO("Not yet implemented")

    companion object {
        private val TYPES: Key<CachedValue<List<org.elixir_lang.semantic.type.Definition>>> =
            Key("elixir.modular.types")
        private val EXPORTED_CALL_DEFINITIONS: Key<CachedValue<List<Definition>>> =
            Key("elixir.modular.exported_call_definitions")
    }

    override val callDefinitions: Definitions
        get() = TODO("Not yet implemented")
}
