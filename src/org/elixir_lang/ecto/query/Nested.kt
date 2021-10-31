package org.elixir_lang.ecto.query

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.ArityRange
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.scope.CallDefinitionClause
import org.elixir_lang.psi.scope.WhileIn
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.reference.Resolver

open class Nested(val name: String, private val arityRangesByName: Map<String, ArityRange>) {
    fun `is`(call: Call, state: ResolveState): Boolean =
            hasNameArity(call) && state.get(org.elixir_lang.ecto.Query.CALL) != null

    private fun hasNameArity(call: Call): Boolean =
            call.functionName()
                    ?.let { functionName ->
                        hasNameArity(call, functionName)
                    }
                    ?: false

    protected open fun hasNameArity(call: Call, functionName: String): Boolean =
                arityRangesByName[functionName]?.let { arityRange ->
                    call.resolvedFinalArity() in arityRange
                } == true

    fun treeWalkUp(call: Call,
                   state: ResolveState,
                   keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val (modulars, modularsState) = modulars(call, state)

        return treeWalkUp(modulars, call, modularsState, keepProcessing)
    }

    private fun treeWalkUp(modulars: List<Call>,
                           call: Call,
                           state: ResolveState,
                           keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val childState = state.put(CallDefinitionClause.MODULAR_CANONICAL_NAME, name)

        val checkArguments = WhileIn.whileIn(modulars) { modular ->
            modular.whileInStabBodyChildExpressions { childExpression ->
                keepProcessing(childExpression, childState)
            }
        }

        return checkArguments && walkArguments(call, state, keepProcessing)
    }

    private fun walkArguments(call: Call,
                              state: ResolveState,
                              keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            call.finalArguments()?.let { arguments -> walk(arguments, state, keepProcessing) } ?: true


    private fun walk(arguments: Array<PsiElement>,
                     state: ResolveState,
                     keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean =
            WhileIn.whileIn(arguments) { argument ->
                if (argument is Call) {
                    if (API.`is`(argument, state)) {
                        API.treeWalkUp(argument, state, keepProcessing)
                    } else if (WindowAPI.`is`(argument, state)) {
                        WindowAPI.treeWalkUp(argument, state, keepProcessing)
                    } else {
                        true
                    }
                } else {
                    true
                }
            }

    private fun modulars(call: Call, initialState: ResolveState): Pair<List<Call>, ResolveState> {
        val initialModularsByName = initialState.get(MODULARS_BY_NAME) ?: emptyMap()
        val cachedModulars = initialModularsByName[name]

        return if (cachedModulars != null) {
            cachedModulars to initialState
        } else {
            val computedModulars = modulars(call)
            val finalModularsByName = initialModularsByName + (name to computedModulars)
            val finalState = initialState.put(MODULARS_BY_NAME, finalModularsByName)

            computedModulars to finalState
        }
    }

    private fun modulars(call: Call): List<Call> {
        val project = call.project
        val globalSearchScope = GlobalSearchScope.allScope(project)
        val modulars = mutableListOf<Call>()

        StubIndex
                .getInstance()
                .processElements(
                        ModularName.KEY,
                        name,
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

    private val MODULARS_BY_NAME = Key<Map<String, List<Call>>>("MODULARS_BY_NAME")
}
