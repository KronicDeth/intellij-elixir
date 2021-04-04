package org.elixir_lang.reference.resolver

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.macroChildCallList
import org.elixir_lang.psi.impl.call.qualification.qualifiedToModular
import org.elixir_lang.psi.scope.type.MultiResolve
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.reference.Type

object Type : ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Type> {
    override fun resolve(type: Type, incompleteCode: Boolean): Array<ResolveResult> {
        val element = type.element

        return if (element is Qualified) {
            resolve(element, incompleteCode)
        } else {
            resolve(element, incompleteCode)
        }
    }

    private fun resolve(qualified: Qualified, incompleteCode: Boolean): Array<ResolveResult> =
        qualified.qualifiedToModular()?.let { modular ->
            qualified.functionName()?.let { name ->
                val resolvedFinalArity = qualified.resolvedFinalArity()

                MultiResolve.resolveResults(name, resolvedFinalArity, incompleteCode, modular)
            }
        } ?: emptyArray()

    private fun resolve(call: Call, incompleteCode: Boolean): Array<ResolveResult> =
            call
                    .functionName()
                    ?.let { name ->
                        val resolvedFinalArity = call.resolvedFinalArity()

                        val accumulator = MultiResolve.resolveResults(name, resolvedFinalArity, incompleteCode, call)

                        if (!accumulator.any(ResolveResult::isValidResult) && BUILTIN_ARITY_BY_NAME[name]?.contains(resolvedFinalArity) == true) {
                            accumulator + resolveBuiltin(call, name, resolvedFinalArity)
                        } else {
                            accumulator
                        }
                    }
                    ?: emptyArray()

    private fun resolveBuiltin(entrance: PsiElement, name: String, arity: Int): Array<ResolveResult> {
        val project = entrance.project
        val resolveResultList = mutableListOf<ResolveResult>()

        StubIndex.getInstance().processElements(ModularName.KEY, ":erlang", project, GlobalSearchScope.allScope(project), NamedElement::class.java) { namedElement ->
            namedElement.navigationElement.let { it as? Call }?.let { call ->
                resolveResultList.addAll(MultiResolve.resolveResults(name, arity, false, call))
            }

            true
        }

        return resolveResultList.toTypedArray()
    }

    @JvmStatic
    val BUILTIN_ARITY_BY_NAME: Map<String, Set<Int>> = mapOf(
            "any" to setOf(0),
            "arity" to setOf(0),
            "as_boolean" to setOf(1),
            "atom" to setOf(0),
            "binary" to setOf(0),
            "bitstring" to setOf(0),
            "boolean" to setOf(0),
            "byte" to setOf(0),
            "char" to setOf(0),
            "charlist" to setOf(0),
            "float" to setOf(0),
            "fun" to setOf(0),
            "function" to setOf(0),
            "identifier" to setOf(0),
            "integer" to setOf(0),
            "iodata" to setOf(0),
            "iolist" to setOf(0),
            "keyword" to setOf(0, 1),
            "list" to setOf(0, 1),
            "map" to setOf(0),
            "maybe_improper_list" to setOf(0, 2),
            "mfa" to setOf(0),
            "module" to setOf(0),
            "neg_integer" to setOf(0),
            "no_return" to setOf(0),
            "node" to setOf(0),
            "non_empty_list" to setOf(0, 1),
            "non_neg_integer" to setOf(0),
            "none" to setOf(0),
            "nonempty_charlist" to setOf(0),
            "nonempty_improper_list" to setOf(0, 2),
            "nonempty_maybe_improper_list" to setOf(2),
            "number" to setOf(0),
            "pid" to setOf(0),
            "port" to setOf(0),
            "pos_integer" to setOf(0),
            "reference" to setOf(0),
            "struct" to setOf(0),
            "term" to setOf(0),
            "timeout" to setOf(0),
            "tuple" to setOf(0)
    )
}
