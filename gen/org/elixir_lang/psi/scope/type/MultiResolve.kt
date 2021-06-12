package org.elixir_lang.psi.scope.type

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirKeywordKey
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.reference.Type.Companion.typeHead

class MultiResolve
private constructor(private val name: String,
                    private val arity: Int,
                    private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.Type() {
    override fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean =
        typeHead(definition)
                ?.let { typeHead ->
                    typeHead.functionName()?.let { name ->
                        if (name.startsWith(this.name)) {
                            val arity = typeHead.resolvedFinalArity()
                            val validResult = name == this.name && arity == this.arity

                            resolveResultOrderedSet.add(definition, typeHead.text, validResult)

                            keepProcessing()
                        } else {
                            null
                        }
                    }
                }
                ?: true

    override fun executeOnParameter(parameter: PsiElement, state: ResolveState): Boolean =
        if (this.arity == 0) {
            val name = when (parameter) {
                is UnqualifiedNoArgumentsCall<*> -> parameter.name
                is ElixirKeywordKey -> parameter.text
                else -> {
                    Logger.error(MultiResolve::class.java, "DOn't know how to get name of parameter", parameter)

                    null
                }
            }

            if (name != null && name.startsWith(this.name)) {
                val validResult = name == this.name

                resolveResultOrderedSet.add(parameter, name, validResult)

                keepProcessing()
            } else {
                null
            }
        } else {
            null
        } ?: true

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)

    private fun resolveResults(): List<ResolveResult> = resolveResultOrderedSet.toList()
    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    companion object {
        fun resolveResults(name: String, resolvedFinalArity: Int, incompleteCode: Boolean, entrance: PsiElement, resolveState: ResolveState = ResolveState.initial()): List<ResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val maxScope = entrance.containingFile
            val entranceResolveState = resolveState.put(ElixirPsiImplUtil.ENTRANCE, entrance).putInitialVisitedElement(entrance)

            PsiTreeUtil.treeWalkUp(multiResolve, entrance, maxScope, entranceResolveState)

            return multiResolve.resolveResults()
        }
    }
}
