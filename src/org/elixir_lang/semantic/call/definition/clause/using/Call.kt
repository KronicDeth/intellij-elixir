package org.elixir_lang.semantic.call.definition.clause.using

import com.intellij.openapi.util.Key
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.semantic.Apply
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Quote
import org.elixir_lang.semantic.branching.conditional.Case
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.clause.Call
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Using
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.semantic

class Call(enclosingModular: Modular, call: org.elixir_lang.psi.call.Call) :
    Call(
        enclosingModular,
        call,
        Visibility.PUBLIC,
        Time.COMPILE
    ), Using {
    override val definition: Definition
        get() = TODO("Not yet implemented")

    override val nameArityInterval: NameArityInterval = NAME_ARITY_INTERVAL

    override val exportedCallDefinitions: List<Definition>
        get() = CachedValuesManager.getCachedValue(psiElement, EXPORTED_CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeExportedCallDefinitions(call), psiElement)
        }

    companion object {
        val NAME_ARITY_INTERVAL: NameArityInterval = NameArityInterval("__using__", ArityInterval(1, 1))

        private val EXPORTED_CALL_DEFINITIONS:
                Key<CachedValue<List<Definition>>> =
            Key("elixir.using.call_definition_clause.exported")

        private fun computeExportedCallDefinitions(call: org.elixir_lang.psi.call.Call): List<Definition> =
            call
                .stabBodyChildExpressions(forward = false)
                ?.mapNotNull { psiElement ->
                    when (val semantic = psiElement.semantic) {
                        is Quote -> semantic.exportedCallDefinitions
                        is Apply -> semantic.using.exportedCallDefinitions
                        is Case -> semantic.exportedCallDefinitions
                        is org.elixir_lang.semantic.call.usage.Call -> semantic.using.exportedCallDefinitions
                        else -> null
                    }
                }
                // Because `forward = false`, `firstOrNull` gets the last Call in the `do` block
                ?.firstOrNull()
                ?: emptyList()
    }
}

