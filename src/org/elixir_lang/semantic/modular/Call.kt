package org.elixir_lang.semantic.modular

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.CanonicallyNamed.Companion.getCanonicalName
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Use
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Using
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.type.Definition

abstract class Call(override val enclosingModular: Modular?, val call: Call) : Modular {
    override val canonicalName: String?
        get() = getCanonicalName(this, psiElement)
    override val psiElement: PsiElement
        get() = call
    override val isDecompiled: Boolean = false

    override val types: List<Definition>
        get() = CachedValuesManager.getCachedValue(call, TYPES) {
            CachedValueProvider.Result.create(computeTypes(call), call)
        }
    override val exportedCallDefinitions: Definitions
        get() = CachedValuesManager.getCachedValue(call, EXPORTED_CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeExportedCallDefinitions(call), call)
        }
    override val callDefinitions: Definitions
        get() = CachedValuesManager.getCachedValue(call, CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeCallDefinitions(call), call)
        }
    override val usings: List<Using>
        get() = CachedValuesManager.getCachedValue(call, USINGS) {
            CachedValueProvider.Result.create(computeUsings(this), call)
        }

    companion object {
        private val TYPES: Key<CachedValue<List<Definition>>> = Key("elixir.modular.types")

        fun from(enclosingModular: Modular?, call: Call): Modular? =
            if (call.hasDoBlockOrKeyword() && call.resolvedModuleName() == Module.KERNEL) {
                val functionName = call.functionName()
                val arity = call.resolvedFinalArity()

                when (functionName) {
                    org.elixir_lang.semantic.implementation.Call.FUNCTION -> org.elixir_lang.semantic.implementation.Call.from(
                        enclosingModular,
                        call,
                        arity
                    )
                    org.elixir_lang.semantic.module.Call.FUNCTION -> org.elixir_lang.semantic.module.Call.from(
                        enclosingModular,
                        call,
                        arity
                    )
                    org.elixir_lang.semantic.protocol.Call.FUNCTION -> org.elixir_lang.semantic.protocol.Call.from(
                        enclosingModular,
                        call,
                        arity
                    )
                    org.elixir_lang.semantic.quote.Call.FUNCTION -> org.elixir_lang.semantic.quote.Call.from(
                        enclosingModular,
                        call,
                        arity
                    )
                    else -> null
                }
            } else {
                null
            }

        private fun computeTypes(call: Call): List<Definition> =
            call.macroChildCalls().mapNotNull(PsiElement::semantic).flatMap { semantic ->
                when (semantic) {
                    is Definition -> listOf(semantic)
                    is Use -> semantic.types
                    else -> emptyList()
                }
            }

        private val CALL_DEFINITIONS: Key<CachedValue<Definitions>> =
            Key("elixir.modular.call_definition_clauses")

        private fun computeCallDefinitions(call: Call): List<org.elixir_lang.semantic.call.Definition> =
            call
                .macroChildCalls()
                .mapNotNull(PsiElement::semantic)
                .flatMap { semantic ->
                    when (semantic) {
                        is Clause -> {
                            val definition = semantic.definition

                            if (definition.visibility == Visibility.PUBLIC) {
                                listOf(definition)
                            } else {
                                emptyList()
                            }
                        }
                        is Use -> semantic.exportedCallDefinitions
                        else -> emptyList()
                    }
                }
                .distinct()

        private val EXPORTED_CALL_DEFINITIONS: Key<CachedValue<Definitions>> =
            Key("elixir.modular.call_definition_clauses.exported")

        private fun computeExportedCallDefinitions(semantic: org.elixir_lang.semantic.modular.Call): Definitions =
            semantic
                .call
                .macroChildCalls()
                .mapNotNull(PsiElement::semantic)
                .fold(Definitions(semantic)) { acc, childSemantic ->
                    when (childSemantic) {
                        is Clause -> acc.put(childSemantic.definition)
                        is Use -> acc.merge(childSemantic.callDefinitions)
                        else -> acc
                    }
                }

        val USINGS: Key<CachedValue<List<Using>>> = Key("elixir.modular.usings")

        private fun computeUsings(modular: Modular): List<Using> =
            modular.exportedCallDefinitions.filterIsInstance<Using>()
    }
}
