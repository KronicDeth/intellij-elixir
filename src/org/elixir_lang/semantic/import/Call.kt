package org.elixir_lang.semantic.import

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangRangeException
import com.intellij.openapi.util.Key
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.Function
import org.elixir_lang.Arity
import org.elixir_lang.Name
import org.elixir_lang.NameArityInterval
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.Quotable
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.hasKeywordKey
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.Import
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition

class Call(val call: Call) : Import {
    override val psiElement: PsiElement
        get() = call
    override val importedCallDefinitions: List<Definition>
        get() = CachedValuesManager.getCachedValue(call, IMPORTED_CALL_DEFINITIONS) {
            CachedValueProvider.Result.create(computeImportedCallDefinitions(call), psiElement)
        }

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        private val IMPORTED_CALL_DEFINITIONS: Key<CachedValue<List<Definition>>> =
            Key("elixir.import.imported_call_definition_clauses")

        private fun computeImportedCallDefinitions(
            call: Call
        ): List<Definition> {
            val modulars = modulars(call)

            return if (modulars.isNotEmpty()) {
                val filter = filter(call)

                modulars.flatMap { modular ->
                    modular
                        .exportedCallDefinitions
                        .filter { callDefinitionClause ->
                            callDefinitionClause.nameArityInterval?.let { nameArityInterval ->
                                filter(nameArityInterval)
                            }
                                ?: true
                        }
                }
            } else {
                emptyList()
            }
        }

        /**
         * The modular that is imported by `importCall`.
         * @param importCall a [Call] that calls `import`.
         * @return `defmodule`, `defimpl`, or `defprotocol` imported by `importCall`.  It can be
         * `null` if Alias passed to `importCall` cannot be resolved.
         */
        private fun modulars(importCall: Call): Set<Modular> =
            importCall
                .finalArguments()
                ?.firstOrNull()
                ?.maybeModularNameToModulars(maxScope = importCall.parent, useCall = null, incompleteCode = false)
                ?: emptySet()

        /**
         * A function that returns `true` for name arity intervals that are imported by `importCall`
         *
         * @param importCall `import` call
         */
        private fun filter(importCall: Call): (NameArityInterval) -> Boolean {
            val finalArguments = importCall.finalArguments()

            return if (finalArguments != null && finalArguments.size >= 2) {
                optionsNameArityIntervalFilter(finalArguments[1])
            } else {
                TRUE
            }
        }


        /**
         * A [Function] that returns `true` for call definition clauses that are imported by `importCall`
         *
         * @param options options (second argument) to an `import Module, ...` call.
         */
        private fun optionsNameArityIntervalFilter(options: PsiElement?): (NameArityInterval) -> Boolean {
            var filter = TRUE

            if (options != null && options is QuotableKeywordList) {
                for (quotableKeywordPair in options.quotableKeywordPairList()) {
                    /* although using both `except` and `only` is invalid semantically, support it to handle transient code
                       and take the final option as the filter in that state */
                    if (quotableKeywordPair.hasKeywordKey("except")) {
                        filter = exceptNameArityIntervalFilter(quotableKeywordPair.keywordValue)
                    } else if (quotableKeywordPair.hasKeywordKey("only")) {
                        filter = onlyNameArityIntervalFilter(quotableKeywordPair.keywordValue)
                    }
                }
            }

            return filter
        }

        private fun exceptNameArityIntervalFilter(element: PsiElement): (NameArityInterval) -> Boolean {
            val only = onlyNameArityIntervalFilter(element)
            return { nameArityInterval -> !only(nameArityInterval) }
        }

        private fun onlyNameArityIntervalFilter(element: PsiElement): (NameArityInterval) -> Boolean {
            val aritiesByName = aritiesByNameFromNameByArityKeywordList(element)

            return { nameArityInterval ->
                aritiesByName[nameArityInterval.name]?.let { arities ->
                    arities.any { arity -> nameArityInterval.arityInterval.contains(arity) }
                } ?: false
            }
        }

        private fun aritiesByNameFromNameByArityKeywordList(element: PsiElement): Map<String, List<Int>> =
            (element.stripAccessExpression() as? ElixirList)?.let {
                aritiesByNameFromNameByArityKeywordList(it)
            } ?: emptyMap()


        private fun aritiesByNameFromNameByArityKeywordList(list: ElixirList): Map<Name, List<Arity>> {
            val aritiesByName = mutableMapOf<Name, MutableList<Int>>()

            val children = list.children

            if (children.isNotEmpty()) {
                (children.last() as? QuotableKeywordList)?.let { quotableKeywordList ->
                    for (quotableKeywordPair in quotableKeywordList.quotableKeywordPairList()) {
                        val name = keywordKeyToName(quotableKeywordPair.keywordKey)
                        val arity = keywordValueToArity(quotableKeywordPair.keywordValue)

                        if (name != null && arity != null) {
                            aritiesByName.computeIfAbsent(name) { mutableListOf() }.add(arity)
                        }
                    }
                }
            }

            return aritiesByName
        }

        private fun keywordKeyToName(keywordKey: Quotable): String? =
            (keywordKey.quote() as? OtpErlangAtom)?.atomValue()

        private fun keywordValueToArity(keywordValue: Quotable): Int? =
            (keywordValue.quote() as? OtpErlangLong)?.let { quotedKeywordValue ->
                try {
                    quotedKeywordValue.intValue()
                } catch (e: OtpErlangRangeException) {
                    Logger.error(
                        this::class.java,
                        "Arity in OtpErlangLong could not be downcast to an int",
                        keywordValue
                    )
                    null
                }
            }

        private val TRUE: (NameArityInterval) -> Boolean = { true }
    }
}
