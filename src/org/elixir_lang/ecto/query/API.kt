package org.elixir_lang.ecto.query

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.navigation.isDecompiled
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.psi.stub.type.call.Stub.isModular

// `Ecto.Query.API` in Elixir
object API {
    fun `is`(call: Call, state: ResolveState): Boolean =
            call.functionName()?.let { functionName ->
                ARITY_RANGES_BY_NAME[functionName]?.let { arityRange ->
                    call.resolvedFinalArity() in arityRange && state.get(org.elixir_lang.ecto.Query.CALL) != null
                }
            } ?: false

    fun treeWalkUp(call: Call,
                   state: ResolveState,
                   keepProcessing: (element: PsiElement, state: ResolveState) -> Boolean): Boolean {
        val modulars = ectoQueryAPIModulars(call.project)

        return whileIn(modulars) { modular ->
            modular.whileInStabBodyChildExpressions { childExpression ->
                keepProcessing(childExpression, state)
            }
        }
    }

    private fun ectoQueryAPIModulars(project: Project): List<Call> {
        val globalSearchScope = GlobalSearchScope.allScope(project)
        val modulars = mutableListOf<Call>()

        StubIndex
                .getInstance()
                .processElements(
                        ModularName.KEY,
                        "Ecto.Query.API",
                        project,
                        globalSearchScope,
                        NamedElement::class.java) { namedElement ->
                    if (namedElement is Call && isModular(namedElement) && !namedElement.isDecompiled()) {
                        modulars.add(namedElement)
                    }

                    true
                }

        return modulars
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
            "fragment" to 1..1,
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
}
