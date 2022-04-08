package org.elixir_lang.semantic.use

import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.*
import org.elixir_lang.semantic.module_attribute.definition.Literal

class Call(val call: Call) : Use {
    override val psiElement: PsiElement
        get() = call
    override val modulars: Set<Modular>
        get() = CachedValuesManager.getCachedValue(call, MODULARS) {
            CachedValueProvider.Result.create(computeModulars(call), call)
        }
    override val moduleAttributes: List<Literal>
        get() = TODO("Not yet implemented")
    override val types: List<org.elixir_lang.semantic.type.Definition>
        get() = TODO("Not yet implemented")
    override val exportedCallDefinitions: List<org.elixir_lang.semantic.call.Definition>
        get() = CachedValuesManager.getCachedValue(call, EXPORTED_CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeExportedCallDefinitions(this), call)
        }
    override val callDefinitions: List<org.elixir_lang.semantic.call.Definition>
        get() = CachedValuesManager.getCachedValue(call, CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeCallDefinitions(this), call)
        }
    override val variables: List<Variable>
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val MODULARS: Key<CachedValue<Set<Modular>>> = Key("elixir.use.modulars")

        private fun computeModulars(call: Call): Set<Modular> =
            call
                .finalArguments()
                ?.firstOrNull()
                ?.stripAccessExpression()
                ?.semantic
                ?.let { it as? Alias }
                ?.modulars
                .orEmpty()

        private val EXPORTED_CALL_DEFINITIONS:
                Key<CachedValue<List<org.elixir_lang.semantic.call.Definition>>> =
            Key("elixir.use.call_definition_clause.exported")

        private fun computeExportedCallDefinitions(use: Use): List<org.elixir_lang.semantic.call.Definition> {
            TODO()
        }

        private val CALL_DEFINITIONS:
                Key<CachedValue<List<org.elixir_lang.semantic.call.Definition>>> =
            Key("elixir.use.call_definition_clause")

        private fun computeCallDefinitions(use: Use): List<org.elixir_lang.semantic.call.Definition> {
            TODO()
        }
    }
}
