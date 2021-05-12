package org.elixir_lang.ecto

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import org.elixir_lang.NameArityRange
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.whileInChildExpressions
import org.elixir_lang.psi.operation.In
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.resolvesToModularName

object Query {
    val CALL: Key<Call> = Key.create("Ecto.Query")

    fun isDeclaringMacro(call: Call, state: ResolveState): Boolean =
        call.functionName()?.let { functionName ->
            DECLARING_MACRO_ARITY_RANGES_BY_NAME[functionName]?.let { arityRange ->
                call.resolvedFinalArity() in arityRange && resolvesToEctoQuery(call, state)
            }
        } ?: false

    private fun resolvesToEctoQuery(call: Call, state: ResolveState): Boolean =
            resolvesToModularName(call, state, "Ecto.Query")

    fun treeWalkUp(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.functionName()?.let { functionName ->
                when (functionName) {
                    FROM_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in FROM_NAME_ARITY_RANGE.arityRange) {
                            executeOnFrom(call, state, keepProcessing)
                        } else {
                            true
                        }
                    JOIN_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in JOIN_NAME_ARITY_RANGE.arityRange) {
                            executeOnJoin(call, state, keepProcessing)
                        } else {
                            true
                        }
                    SELECT_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in SELECT_NAME_ARITY_RANGE.arityRange) {
                            executeOnSelect(call, state, keepProcessing)
                        } else {
                            true
                        }
                    WHERE_NAME_ARITY_RANGE.name ->
                        if (call.resolvedFinalArity() in WHERE_NAME_ARITY_RANGE.arityRange) {
                            executeOnWhere(call, state, keepProcessing)
                        } else {
                            true
                        }
                    else -> true
                }
            } ?: true

    fun executeOnFrom(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        call.finalArguments()?.let { finalArguments ->
            executeOnIn(finalArguments[0], state, keepProcessing) &&
                    ((finalArguments.size < 2) ||
                            executeOnFromKeywords(finalArguments[1], state, keepProcessing))
        } ?: true

    fun executeOnIn(fromIn: PsiElement,
                    state: ResolveState,
                    keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        //  If the query needs a reference to the data source in any other part of the expression, then an in must be
        //  used to create a reference variable.
        fromIn.let { it as? In }?.leftOperand()?.let { executeOnInReference(it, state, keepProcessing) } ?: true

    private fun executeOnInReference(
            inReference: PsiElement,
            state: ResolveState,
            keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (inReference) {
            is UnqualifiedNoArgumentsCall<*> ->
                inReference.resolvedFinalArity() != 0 || keepProcessing(inReference, state)
            else -> {
                Logger.error(logger, "Don't know how to find reference variables in Ecto query", inReference)

                true
            }
        }

    fun executeOnFromKeywords(fromKeywords: PsiElement,
                              state: ResolveState,
                              keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (fromKeywords) {
            is QuotableKeywordList -> {
                whileIn(fromKeywords.quotableKeywordPairList()) { quotableKeywordPair ->
                    executeOnFromKeywords(quotableKeywordPair, state, keepProcessing)
                }
            }
            is QuotableKeywordPair -> {
                when (val keywordKeyText = fromKeywords.keywordKey.text) {
                    "cross_join", "full_join", "inner_join", "inner_lateral_join", "join", "left_join",
                    "left_lateral_join", "right_join" -> executeOnIn(fromKeywords.keywordValue, state, keepProcessing)
                    // Cannot declare a reference variable
                    "as", "distinct", "group_by", "on", "order_by", "select", "update", "where" -> true
                    else -> {
                        Logger.error(logger, "Don't know how to find reference variables for keyword key $keywordKeyText", fromKeywords)

                        true
                    }
                }
            }
            else -> {
                Logger.error(logger, "Don't kow how to find reference variables in keyword arguments of from/2", fromKeywords)

                true
            }
        }

    fun executeOnJoin(call: Call,
                      state: ResolveState,
                      keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        call.finalArguments()?.let { arguments ->
            // `join(query, qual, binding \\ [], expr, opts \\ [])`
            when (call.resolvedFinalArity()) {
                // `join(query, qual, expr)` or `|> join(qual, expr)`
                3 -> executeOnIn(arguments[arguments.lastIndex], state, keepProcessing)
                // `join(query, qual, binding, expr)` or `|> join(qual, binding, expr)`
                4 -> executeOnBinding(arguments[arguments.lastIndex - 1], state, keepProcessing) &&
                        executeOnIn(arguments[arguments.lastIndex], state, keepProcessing)
                // `join(query, qual, binding, expr, opts)` or `|> join(qual, binding, expr, opts)`
                5 -> executeOnBinding(arguments[arguments.lastIndex - 2], state, keepProcessing) &&
                        executeOnIn(arguments[arguments.lastIndex - 1], state, keepProcessing)
                else -> {
                    Logger.error(logger, "join arity outside of range (3..5)", call)
                    null
                }
            }
        } ?: true

    private tailrec fun executeOnBinding(
            element: PsiElement,
            state: ResolveState,
            keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
        when (element) {
            is ElixirAccessExpression -> executeOnBinding(element.stripAccessExpression(), state, keepProcessing)
            is ElixirList -> {
                element.whileInChildExpressions(forward = false) {
                    executeOnBinding(it, state, keepProcessing)
                }
            }
            is QuotableKeywordList -> {
                val keywordValues = element.quotableKeywordPairList().mapNotNull(QuotableKeywordPair::getKeywordValue)

                whileIn(keywordValues) {
                    executeOnBinding(it, state, keepProcessing)
                }
            }
            // `{^assoc, a}` inside of `[{^assoc, a}]`
            is ElixirTuple -> {
                val elements = element.children

                if (elements.size == 2) {
                    executeOnBinding(elements[1], state, keepProcessing)
                } else {
                    Logger.error(
                            logger,
                            "Don't know how to find reference variables in binding tuple when arity is not 2",
                            element
                    )

                    true
                }
            }
            is UnqualifiedNoArgumentsCall<*> -> element.resolvedFinalArity() != 0 || keepProcessing(element, state)
            else -> {
                Logger.error(logger, "Don't know how to find reference variables in binding", element)

                true
            }
        }

    /**
     * Whether `call` is an `assoc/2` call inside of a `join` statement
     */
    fun isAssoc(call: Call): Boolean =
        call is UnqualifiedParenthesesCall<*> && call.functionName() == "assoc" && call.resolvedFinalArity() == 2 &&
            call.parent?.let { it as? In }
                    ?.let { isJoin(it.parent, ResolveState().put(ENTRANCE, call).putInitialVisitedElement(call)) }
                ?: true

    private fun executeOnSelect(call: Call, state: ResolveState, keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                // `select(query, binding \\ [], expr)`
                when (call.resolvedFinalArity()) {
                    // `select(query, expr)` or `|> select(expr)`
                    2 -> true
                    // `select(query, binding, expr)` or `|> select(binding, expr)`
                    3 -> executeOnBinding(arguments[arguments.lastIndex - 1], state, keepProcessing)
                    else -> {
                        Logger.error(logger, "select arity outside of range (2..3)", call)

                        null
                    }
                }
            } ?: true

    private fun executeOnWhere(call: Call,
                               state: ResolveState,
                               keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments ->
                // where(query, binding \\ [], expr)
                when (call.resolvedFinalArity()) {
                    // `where(query, expr)` or `|> where(expr)`
                    2 ->
                        // Check for Ecto.Query.API when resolving calls
                        keepProcessing(arguments[arguments.lastIndex], state)
                    // `where(query, binding, expr)` or `|> where(binding, expr)`
                    3 -> executeOnBinding(arguments[arguments.lastIndex - 1], state, keepProcessing) &&
                            // Check for Ecto.Query.API when resolving calls
                            keepProcessing(arguments[arguments.lastIndex], state.put(CALL, call))
                    else -> {
                        Logger.error(logger, "where arity outside of range (2..3)", call)

                        null
                    }
                }
            } ?: true

    /**
     * Whether the `parent` is a `join(...)` macro call or `join: ...` keyword in a from
     */
    private tailrec fun isJoin(ancestor: PsiElement, state: ResolveState): Boolean =
            when (ancestor) {
                is ElixirKeywordPair -> {
                    when (ancestor.keywordKey.text) {
                        "cross_join", "full_join", "inner_join", "inner_lateral_join", "join", "left_join",
                        "left_lateral_join", "right_join" -> true
                        else -> false
                    }
                }
                is ElixirParenthesesArguments,
                is ElixirMatchedParenthesesArguments -> isJoin(ancestor.parent, state)
                is Call -> isJoin(ancestor, state)
                else -> false
            }

    /**
     * Whether the `call` is a `join(...)` macro call
     */
    private fun isJoin(call: Call, state: ResolveState): Boolean =
        call.functionName() == JOIN_NAME_ARITY_RANGE.name &&
                call.resolvedFinalArity() in JOIN_NAME_ARITY_RANGE.arityRange &&
                resolvesToEctoQuery(call, state)

    private val FROM_NAME_ARITY_RANGE = NameArityRange("from", 1..2)
    private val JOIN_NAME_ARITY_RANGE = NameArityRange("join", 3..5)
    private val SELECT_NAME_ARITY_RANGE = NameArityRange("select", 2..3)
    private val WHERE_NAME_ARITY_RANGE = NameArityRange("where", 2..3)
    private val DECLARING_MACRO_NAME_ARITY_RANGES = arrayOf(FROM_NAME_ARITY_RANGE, JOIN_NAME_ARITY_RANGE, SELECT_NAME_ARITY_RANGE, WHERE_NAME_ARITY_RANGE)
    private val DECLARING_MACRO_ARITY_RANGES_BY_NAME = DECLARING_MACRO_NAME_ARITY_RANGES.map { it.name to it.arityRange }.toMap()

    private val logger by lazy { com.intellij.openapi.diagnostic.Logger.getInstance(Query::class.java) }
}
