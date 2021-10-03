package org.elixir_lang.psi.scope.type

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Pipe
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.reference.Type.Companion.typeHead

class MultiResolve
private constructor(private val name: String,
                    private val arity: Int,
                    private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.Type() {
    override fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean =
            typeHead(definition)
                    ?.let { executeOnTypeHead(definition, it, state) }
                    ?: true

    private fun executeOnTypeHead(definition: Call, typeHead: Call, state: ResolveState): Boolean =
            executeOnTypeHeadArguments(typeHead, state)
                    && executeOnTypeHeadName(definition, typeHead, state)

    private fun executeOnTypeHeadArguments(typeHead: Call, state: ResolveState): Boolean =
            typeHead.finalArguments()?.let { executeOnTypeHeadArguments(it, state) }
                    ?: true

    private fun executeOnTypeHeadArguments(arguments: Array<PsiElement>, state: ResolveState): Boolean =
            whileIn(arguments) {
                executeOnParameter(it, state)
            }

    private fun executeOnTypeHeadName(definition: Call, typeHead: Call, state: ResolveState): Boolean =
            typeHead.functionName()?.let { name ->
                if (name.startsWith(this.name)) {
                    val arity = typeHead.resolvedFinalArity()
                    val validResult = name == this.name && arity == this.arity

                    resolveResultOrderedSet.add(definition, typeHead.text, validResult, state.visitedElementSet())

                    keepProcessing()
                } else {
                    null
                }
            } ?: true

    override fun executeOnParameter(parameter: PsiElement, state: ResolveState): Boolean =
            when (parameter) {
                is ElixirAccessExpression -> executeOnParameter(parameter.stripAccessExpression(), state)
                is ElixirParentheticalStab ->
                    parameter.stab?.stabOperationList?.singleOrNull()
                            ?.leftOperand().let { it as? ElixirStabNoParenthesesSignature }
                            ?.noParenthesesArguments?.noParenthesesOneArgument
                            ?.children
                            ?.let { anonymousFunctionParameters ->

                                whileIn(anonymousFunctionParameters) {
                                    executeOnParameter(it, state)
                                }
                            } ?: true
                is UnqualifiedNoArgumentsCall<*> -> executeOnParameter(parameter, parameter.name, state)
                // A qualified type cannot be declared here
                is Qualified,
                // Parenthetical calls must be a usage instead of a declaration.
                is UnqualifiedParenthesesCall<*>,
                // Literals
                is ElixirAtom,
                is ElixirAtomKeyword,
                is ElixirList,
                is ElixirMapOperation,
                is ElixirTuple -> true
                is ElixirKeywordKey -> executeOnParameter(parameter, parameter.text, state)
                is Pipe -> (parameter.leftOperand()?.let { executeOnParameter(it, state) } ?: true)
                        && (parameter.rightOperand()?.let { executeOnParameter(it, state) } ?: true)
                is Type -> {
                    val nameElement = parameter.leftOperand()

                    if (nameElement != null) {
                        executeOnParameter(nameElement, state)
                    } else {
                        true
                    }
                }
                else -> {
                    Logger.error(MultiResolve::class.java, "Don't know how to get name of parameter", parameter)

                    true
                }
            }

    private fun executeOnParameter(parameter: PsiElement, name: String?, state: ResolveState): Boolean =
            if (this.arity == 0 && name != null && name.startsWith(this.name)) {
                val validResult = name == this.name

                resolveResultOrderedSet.add(parameter, name, validResult, state.visitedElementSet())

                keepProcessing()
            } else {
                true
            }

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
