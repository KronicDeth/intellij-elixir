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

// `Ecto.Query.API` in Elixir
object API {
    const val ECTO_QUERY_API = "Ecto.Query.API"

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
        val modulars = ectoQueryAPIModulars(call)

        return treeWalkUp(modulars, call, state, keepProcessing)
    }

    private fun treeWalkUp(modulars: List<Call>,
                           call: Call,
                           state: ResolveState,
                           keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val childState = state.put(MODULAR_CANONICAL_NAME, ECTO_QUERY_API)

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
                if (argument is Call && `is`(argument, state)) {
                    treeWalkUp(modulars, argument, state, keepProcessing)
                } else {
                    true
                }
            }

    private fun ectoQueryAPIModulars(call: Call): List<Call> {
        val project = call.project
        val globalSearchScope = GlobalSearchScope.allScope(project)
        val modulars = mutableListOf<Call>()

        StubIndex
                .getInstance()
                .processElements(
                        ModularName.KEY,
                        ECTO_QUERY_API,
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
            "!=" to 2..2,
            "*" to 2..2,
            "+" to 2..2,
            "-" to 2..2,
            "/" to 2..2,
            "<" to 2..2,
            "<=" to 2..2,
            "==" to 2..2,
            ">" to 2..2,
            ">=" to 2..2,
            "ago" to 2..2,
            "all" to 2..2,
            "and" to 2..2,
            "any" to 2..2,
            "as" to 1..1,
            "avg" to 1..1,
            "coalesce" to 2..2,
            "count" to 0..2,
            "date_add" to 3..3,
            "datetime_add" to 3..3,
            "exists" to 1..1,
            "field" to 2..2,
            "filter" to 2..2,
            "from_now" to 2..2,
            "ilike" to 2..2,
            "in" to 2..2,
            "is_nil" to 1..1,
            "json_extract_path" to 2..2,
            "like" to 2..2,
            "map" to 2..2,
            "max" to 1..1,
            "merge" to 2..2,
            "min" to 1..1,
            "not" to 1..1,
            "or" to 2..2,
            "parent_as" to 1..1,
            "struct" to 2..2,
            "sum" to 1..1,
            "type" to 2..2
            )

    private const val FRAGMENT = "fragment"
}
