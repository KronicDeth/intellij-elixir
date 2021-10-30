package org.elixir_lang.ecto.query

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.scope.CallDefinitionClause.Companion.MODULAR_CANONICAL_NAME
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.reference.Resolver

// `Ecto.Query.WindowAPI` in Elixir
object WindowAPI {
    const val ECTO_QUERY_WINDOW_API = "Ecto.Query.WindowAPI"

    fun `is`(call: Call, state: ResolveState): Boolean =
            hasNameArity(call) && state.get(org.elixir_lang.ecto.Query.CALL) != null

    private fun hasNameArity(call: Call): Boolean =
            call.functionName()
                    ?.let { functionName ->
                        (functionName == FRAGMENT) ||
                                (ARITY_RANGES_BY_NAME[functionName]?.let { arityRange ->
                                    call.resolvedFinalArity() in arityRange
                                } == true)
                    }
                    ?: false

    fun treeWalkUp(call: Call,
                   state: ResolveState,
                   keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val modulars = ectoQueryWindowAPIModulars(call)

        return treeWalkUp(modulars, call, state, keepProcessing)
    }

    private fun treeWalkUp(modulars: List<Call>,
                           call: Call,
                           state: ResolveState,
                           keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val childState = state.put(MODULAR_CANONICAL_NAME, ECTO_QUERY_WINDOW_API)

        val checkArguments = whileIn(modulars) { modular ->
            modular.whileInStabBodyChildExpressions { childExpression ->
                keepProcessing(childExpression, childState)
            }
        }

        return checkArguments && walkArguments(modulars, call, state, keepProcessing)
    }

    private fun walkArguments(modulars: List<Call>,
                              call: Call,
                              state: ResolveState,
                              keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments -> walk(modulars, arguments, state, keepProcessing) } ?: true

    private fun walk(modulars: List<Call>,
                     arguments: Array<PsiElement>,
                     state: ResolveState,
                     keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            whileIn(arguments) { argument ->
                if (argument is Call) {
                    if (`is`(argument, state)) {
                        treeWalkUp(modulars, argument, state, keepProcessing)
                    } else if (API.`is`(argument, state)) {
                        API.treeWalkUp(argument, state, keepProcessing)
                    } else {
                        true
                    }
                } else {
                    true
                }
            }

    private fun ectoQueryWindowAPIModulars(call: Call): List<Call> {
        val project = call.project
        val globalSearchScope = GlobalSearchScope.allScope(project)
        val modulars = mutableListOf<Call>()

        StubIndex
                .getInstance()
                .processElements(
                        ModularName.KEY,
                        ECTO_QUERY_WINDOW_API,
                        project,
                        globalSearchScope,
                        NamedElement::class.java) { namedElement ->
                    if (namedElement is Call) {
                        modulars.add(namedElement)
                    }

                    true
                }

        return Resolver.preferUnderSameModule(call, modulars).let { Resolver.preferSource(it) }
    }

    private val ARITY_RANGES_BY_NAME = mapOf(
            "avg" to 1..1,
            "count" to 0..1,
            "cume_dist" to 0..0,
            "dense_rank" to 0..0,
            "filter" to 2..2,
            "first_value" to 1..1,
            "lag" to 1..3,
            "last_value" to 1..1,
            "lead" to 1..3,
            "max" to 1..1,
            "min" to 1..1,
            "nth_value" to 2..2,
            "ntile" to 1..1,
            "over" to 1..2,
            "percent_rank" to 0..0,
            "rank" to 0..0,
            "row_number" to 0..0,
            "sum" to 1..1
            )

    private const val FRAGMENT = "fragment"
}
