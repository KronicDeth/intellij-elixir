package org.elixir_lang.psi

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangRangeException
import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewTypeLocation
import com.intellij.util.Function
import org.elixir_lang.Arity
import org.elixir_lang.Name
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.IMPORT
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.hasKeywordKey
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.impl.stripAccessExpression

/**
 * An `import` call
 */
object Import {
    /**
     * Calls `function` on each call definition clause imported by `importCall` while `function`
     * returns `true`.  Stops the first time `function` returns `false`
     *
     * @param importCall an `import` [Call] (should have already been checked with [.is].
     * @param function For `import Module`, called on all call definition clauses in `Module`; for
     * `import Module, only: [...]` called on only the call definition clauses matching names in
     * `:only` list; for `import Module, except: [...]` called on all call definition clauses expect those
     * matching names in `:except` list.
     */
    @JvmStatic
    fun callDefinitionClauseCallWhile(importCall: Call, resolveState: ResolveState, function: (Call, ResolveState) -> Boolean): Boolean {
        val modulars = try {
            modulars(importCall)
        } catch (stackOverflowError: StackOverflowError) {
            Logger.error(Import::class.java, "StackoverflowError while finding modular for import", importCall)
            emptyList<Call>()
        }

        return if (modulars.isNotEmpty()) {
            val optionsFilter = callDefinitionClauseCallFilter(importCall)
            var keepProcessing = true

            for (modular in modulars) {
                keepProcessing = Modular.callDefinitionClauseCallWhile(modular, resolveState) { call, accResolveState ->
                    !optionsFilter(call) || function(call, accResolveState)
                }

                if (!keepProcessing) {
                    break
                }
            }

            keepProcessing
        } else {
            true
        }
    }

    fun elementDescription(call: Call, location: ElementDescriptionLocation): String? =
            when {
                location === UsageViewTypeLocation.INSTANCE -> "import"
                location === UsageViewNodeTextLocation.INSTANCE -> call.text
                else -> null
            }

    /**
     * Whether `call` is an `import Module` or `import Module, opts` call
     */
    @JvmStatic
    fun `is`(call: Call): Boolean = call.isCalling(KERNEL, IMPORT) && call.resolvedFinalArity() in 1..2

    private val TRUE: (Call) -> Boolean = { true }

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

    private fun aritiesByNameFromNameByArityKeywordList(element: PsiElement): Map<String, List<Int>> =
        (element.stripAccessExpression() as? ElixirList)?.let {
            aritiesByNameFromNameByArityKeywordList(it)
        } ?:
        emptyMap()

    /**
     * A function that returns `true` for call definition clauses that are imported by `importCall`
     *
     * @param importCall `import` call
     */
    private fun callDefinitionClauseCallFilter(importCall: Call): (Call) -> Boolean {
        val finalArguments = importCall.finalArguments()

        return if (finalArguments != null && finalArguments.size >= 2) {
            optionsCallDefinitionClauseCallFilter(finalArguments[1])
        } else {
            TRUE
        }
    }

    private fun exceptCallDefinitionClauseCallFilter(element: PsiElement): (Call) -> Boolean {
        val only = onlyCallDefinitionClauseCallFilter(element)
        return { call -> !only(call) }
    }

    private fun keywordKeyToName(keywordKey: Quotable): String? = (keywordKey.quote() as? OtpErlangAtom)?.atomValue()

    private fun keywordValueToArity(keywordValue: Quotable): Int? =
        (keywordValue.quote() as? OtpErlangLong)?.let { quotedKeywordValue ->
            try {
                quotedKeywordValue.intValue()
            } catch (e: OtpErlangRangeException) {
                Logger.error(
                        Import::class.java,
                        "Arity in OtpErlangLong could not be downcast to an int",
                        keywordValue
                )
                null
            }
        }

    private fun onlyCallDefinitionClauseCallFilter(element: PsiElement): (Call) -> Boolean {
        val aritiesByName = aritiesByNameFromNameByArityKeywordList(element)

        return { call ->
            nameArityRange(call)?.let { (callName, callArityRange) ->
                aritiesByName[callName]?.let { arities ->
                    arities.any { callArityRange.contains(it) }
                }
            } ?: false
        }
    }

    /**
     * The modular that is imported by `importCall`.
     * @param importCall a [Call] where [is] is `true`.
     * @return `defmodule`, `defimpl`, or `defprotocol` imported by `importCall`.  It can be
     * `null` if Alias passed to `importCall` cannot be resolved.
     */
    private fun modulars(importCall: Call): Set<Call> =
            importCall
                    .finalArguments()
                    ?.firstOrNull()
                    ?.maybeModularNameToModulars(maxScope = importCall.parent, useCall = null, incompleteCode = false)
                    ?: emptySet()

    /**
     * A [Function] that returns `true` for call definition clauses that are imported by `importCall`
     *
     * @param options options (second argument) to an `import Module, ...` call.
     */
    private fun optionsCallDefinitionClauseCallFilter(options: PsiElement?): (Call) -> Boolean {
        var filter = TRUE

        if (options != null && options is QuotableKeywordList) {
            for (quotableKeywordPair in options.quotableKeywordPairList()) {
                /* although using both `except` and `only` is invalid semantically, support it to handle transient code
                   and take the final option as the filter in that state */
                if (quotableKeywordPair.hasKeywordKey("except")) {
                    filter = exceptCallDefinitionClauseCallFilter(quotableKeywordPair.keywordValue)
                } else if (quotableKeywordPair.hasKeywordKey("only")) {
                    filter = onlyCallDefinitionClauseCallFilter(quotableKeywordPair.keywordValue)
                }
            }
        }

        return filter
    }
}
