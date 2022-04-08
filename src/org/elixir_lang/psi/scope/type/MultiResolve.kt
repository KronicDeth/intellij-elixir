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
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.reference.Type.Companion.typeHead
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.type.Definition

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
                is QualifiableAlias,
                is ElixirAtom,
                is ElixirAtomKeyword,
                is ElixirBitString,
                is ElixirDecimalWholeNumber,
                is ElixirList,
                is ElixirMapOperation,
                is ElixirTuple -> true
                is ElixirKeywordKey -> executeOnParameter(parameter, parameter.text, state)
                is Pipe -> (parameter.leftOperand()?.let { executeOnParameter(it, state) } ?: true)
                        && (parameter.rightOperand()?.let { executeOnParameter(it, state) } ?: true)
                // putting defaults in type specs isn't valid, but it can occur when copying the def to write the type
                is InMatch -> {
                    val nameElement = parameter.leftOperand()

                    if (nameElement != null) {
                        executeOnParameter(nameElement, state)
                    } else {
                        true
                    }
                }
                is Ternary ->
                    (parameter.leftOperand()?.let { executeOnParameter(it, state) } ?: true) &&
                            (parameter.rightOperand()?.let { executeOnParameter(it, state) } ?: true)
                is org.elixir_lang.psi.operation.Type -> {
                    val nameElement = parameter.leftOperand()

                    if (nameElement != null) {
                        executeOnParameter(nameElement, state)
                    } else {
                        true
                    }
                }
                is Two ->
                    (parameter.leftOperand()?.let { executeOnParameter(it, state) } ?: true) &&
                            (parameter.rightOperand()?.let { executeOnParameter(it, state) } ?: true)
                is QuotableKeywordList -> {
                    whileIn(parameter.quotableKeywordPairList()) { keywordPair ->
                        executeOnParameter(keywordPair.keywordValue, state)
                    }
                }
                is ElixirStructOperation -> {
                    whileIn(parameter.children.drop(1)) { child ->
                        executeOnParameter(child, state)
                    }
                }
                is ElixirMapArguments -> {
                    parameter.mapConstructionArguments?.let { executeOnParameter(it, state) } ?: true
                }
                is ElixirMapConstructionArguments -> {
                    whileIn(parameter.children) { child ->
                        executeOnParameter(child, state)
                    }
                }
                is ElixirAssociations -> {
                    whileIn(parameter.associationsBase.children) { child ->
                        executeOnParameter(child, state)
                    }
                }
                is ElixirVariable -> executeOnParameter(parameter, parameter.name, state)
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

    private fun execute(modular: Modular, state: ResolveState): Boolean =
            whileIn(modular.types) { type ->
                execute(type, state)
            }

    private fun execute(type: Definition, state: ResolveState): Boolean {
        val nameArity = type.nameArity
        val name = nameArity.name

        return if (name.startsWith(this.name)) {
            val arity = nameArity.arity
            val validResult = name == this.name && arity == this.arity

            resolveResultOrderedSet.add(type.psiElement, type.nameArity.toString(), validResult, state.visitedElementSet())

            keepProcessing()
        } else {
            true
        }
    }

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)

    private fun resolveResults(): List<ResolveResult> = resolveResultOrderedSet.toList()
    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    companion object {
        fun resolveResults(name: String, resolvedFinalArity: Int, incompleteCode: Boolean, modular: Modular, resolveState: ResolveState = ResolveState.initial()): List<ResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val entrance = modular.psiElement
            val entranceResolveState = resolveState.put(ElixirPsiImplUtil.ENTRANCE, entrance).putInitialVisitedElement(entrance)

            multiResolve.execute(modular, entranceResolveState)

            return multiResolve.resolveResults()
        }

        fun resolveResults(name: String, resolvedFinalArity: Int, incompleteCode: Boolean, entrance: PsiElement, resolveState: ResolveState = ResolveState.initial()): List<ResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val entranceResolveState = resolveState.put(ElixirPsiImplUtil.ENTRANCE, entrance).putInitialVisitedElement(entrance)

            val maxScope = entrance.containingFile

            PsiTreeUtil.treeWalkUp(multiResolve, entrance, maxScope, entranceResolveState)

            return multiResolve.resolveResults()
        }
    }
}
