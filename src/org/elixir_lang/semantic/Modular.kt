package org.elixir_lang.semantic

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewShortNameLocation
import org.elixir_lang.CanonicallyNamed
import org.elixir_lang.Presentable
import org.elixir_lang.psi.call.Call
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.call.definition.clause.Using
import org.elixir_lang.semantic.modular.MaybeEnclosed
import org.elixir_lang.semantic.type.Definition

interface Modular : CanonicallyNamed, MaybeEnclosed, Named, Presentable, Semantic {
    val usings: kotlin.collections.List<Using>
    val moduleDocs: kotlin.collections.List<org.elixir_lang.semantic.documentation.Module>
    val types: kotlin.collections.List<Definition>

    /**
     * Only those definitions accessible from other modules.  Excludes imported and private calls.
     */
    val exportedCallDefinitions: Definitions

    /**
     * Both [exportedCallDefinitions] and those that are only in scope due to `import` or are private.
     */
    val callDefinitions: Definitions
    val isDecompiled: Boolean
    val decompiled: Set<Modular>
    val nested: Set<Modular>
        get() = CachedValuesManager.getCachedValue(psiElement, NESTED) {
            CachedValueProvider.Result.create(computeNested(this), psiElement)
        }
    val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
    val locationString: String

    override fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewLongNameLocation.INSTANCE, UsageViewShortNameLocation.INSTANCE ->
                canonicalName
                    ?: canonicalNameSet.sorted().joinToString(prefix = "{", separator = ", ", postfix = "}")
            else -> null
        }

    companion object {
        fun from(enclosingModular: Modular?, psiElement: PsiElement): Modular? =
            when (psiElement) {
                is Modular -> psiElement
                is Call -> org.elixir_lang.semantic.modular.Call.from(enclosingModular, psiElement)
                else -> null
            }

        private val NESTED: Key<CachedValue<Set<Modular>>> = Key("elixir.modular.nested")

        private fun computeNested(modular: Modular): Set<Modular> {
            val project = modular.psiElement.project
            val nestedModularNames =
                modular.canonicalNameSet
                    .flatMap { canonicalName ->
                        Alias.nestedModularNames(project, canonicalName)
                    }
                    .toSet()

            return Alias.modulars(project, nestedModularNames)
        }
    }
}
