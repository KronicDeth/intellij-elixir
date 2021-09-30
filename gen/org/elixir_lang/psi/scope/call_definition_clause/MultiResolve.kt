package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveResult
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.EEx.FUNCTION_FROM_FILE_ARITY_RANGE
import org.elixir_lang.EEx.FUNCTION_FROM_STRING_ARITY_RANGE
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named

import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.VisitedElementSetResolveResult
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.psi.scope.maxScope
import org.elixir_lang.structure_view.element.CallDefinitionHead
import org.elixir_lang.structure_view.element.Callback

class MultiResolve
private constructor(
        /**
         * Can be `null` when `Qualifier.unquote(variable)(...)` is used because although scope can be limited to
         * `Qualifier`, no `name` can be inferred, so all public call definition clauses in `Qualifier` should resolve,
         * but as invalid.
         */
        private val name: String?,
        /**
         * If `name` is `null`, then `resolvedPrimaryArity` must be valid or `incompleteCode` `true` or no match will be
         * found at all.
         */
        private val resolvedPrimaryArity: Int,
        private val incompleteCode: Boolean) : org.elixir_lang.psi.scope.CallDefinitionClause() {
    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean =
            nameArityInterval(element, state)
                    ?.let { addIfNameOrArityToResolveResults(element, it, state) }
                    ?: true

    override fun executeOnCallback(element: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean =
            Callback.headCall(element)
                    ?.let { CallDefinitionHead.nameArityInterval(it, state) }
                    ?.let { addIfNameOrArityToResolveResults(element, it, state) }
                    ?: true

    override fun executeOnDelegation(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.takeIf { it.size == 2 }?.let { arguments ->
            val head = arguments[0]

            CallDefinitionHead.nameArityInterval(head, state)?.let { headNameArityInterval ->
                val headName = headNameArityInterval.name
                val validArity = resolvedPrimaryArity in headNameArityInterval.arityInterval

                if ((this.name == null && (incompleteCode || validArity)) ||
                        (this.name != null && headName.startsWith(this.name))) {
                    val headValidResult = validArity && headName == this.name

                    // the defdelegate is valid or invalid regardless of whether the `to:` (and `:as` resolves as
                    // `defdelegate` still defines a function in the module with the head's name and arity even if it
                    // will fail at runtime to call the delegated function
                    addToResolveResults(element, headName, headValidResult, state)

                    element.keywordArgument("to")?.let { definingModuleName ->
                        val modulars = definingModuleName.maybeModularNameToModulars(element.containingFile, useCall = null, incompleteCode = incompleteCode)

                        if (modulars.isNotEmpty()) {
                            val nameInDefiningModule = element.keywordArgument("as")?.let { it as? ElixirAtom }?.node?.lastChildNode?.text
                                    ?: headName

                            for (modular in modulars) {
                                // Call recursively to get all the proper `for` and `use` handling.
                                val modularResolveResults = resolveResults(nameInDefiningModule, resolvedPrimaryArity, incompleteCode, modular)

                                for (modularResultResult in modularResolveResults) {
                                    modularResultResult.element?.let { it as Call }?.let { call ->
                                        addToResolveResults(call, nameInDefiningModule, modularResultResult.isValidResult, state)
                                    }
                                }

                                if (!keepProcessing()) {
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        return keepProcessing()
    }

    override fun executeOnEExFunctionFrom(element: Call, state: ResolveState): Boolean =
            element.finalArguments()?.let { arguments ->
                when (element.functionName()) {
                    FUNCTION_FROM_FILE_ARITY_RANGE.name -> {
                        arguments[1].stripAccessExpression().let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { name ->
                            if (this.name != null && name.startsWith(this.name)) {
                                val arity = if (arguments.size >= 4) {
                                    // function_from_file(kind, name, file, args)
                                    // function_from_file(kind, name, file, args, options)
                                    arguments[3].stripAccessExpression().let { it as? ElixirList }?.children?.size
                                } else {
                                    // function_from_file(kind, name, file) where args defaults to `[]`
                                    0
                                }

                                val validResult = (resolvedPrimaryArity == arity) && (name == this.name)

                                addToResolveResults(element, name, validResult, state)
                            } else {
                                true
                            }
                        } ?: true
                    }
                    FUNCTION_FROM_STRING_ARITY_RANGE.name -> TODO()
                    else -> true
                }
            } ?: true

    override fun executeOnException(element: Call, state: ResolveState): Boolean =
            whileIn(Exception.NAME_ARITY_LIST) { nameArity ->
                val name = nameArity.name
                val validArity = resolvedPrimaryArity == nameArity.arity

                addIfNameOrArityToResolveResults(element, name, validArity, state)
            }

    override fun executeOnMixGeneratorEmbed(element: Call, state: ResolveState): Boolean =
            element.finalArguments()?.first()?.stripAccessExpression()?.let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { prefix ->
                val suffix = element.functionName()!!.removePrefix("embed_")
                val name = "${prefix}_${suffix}"
                val arityRange = when (suffix) {
                    "template" -> 0..1
                    "text" -> 0..0
                    else -> TODO("Unknown suffix $suffix")
                }

                if (this.name != null && name.startsWith(this.name)) {
                    val validResult = (resolvedPrimaryArity in arityRange) && (name == this.name)

                    addToResolveResults(element, name, validResult, state)
                } else {
                    true
                }
            } ?: true

    private fun addIfNameOrArityToResolveResults(call: Call,
                                                 nameArityInterval: NameArityInterval,
                                                 state: ResolveState): Boolean {
        val name = nameArityInterval.name
        val validArity = resolvedPrimaryArity in nameArityInterval.arityInterval

        return addIfNameOrArityToResolveResults(call, name, validArity, state)
    }

    private fun addIfNameOrArityToResolveResults(call: Call, name: String, validArity: Boolean, state: ResolveState): Boolean =
            if ((this.name == null && (incompleteCode || validArity)) ||
                    (this.name != null && name.startsWith(this.name))) {
                val validResult = validArity && name == this.name

                addToResolveResults(call, name, validResult, state)
            } else {
                true
            }

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)
    fun resolveResults(): List<VisitedElementSetResolveResult> = resolveResultOrderedSet.toList()

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    private fun addToResolveResults(call: Call, name: String, validResult: Boolean, state: ResolveState): Boolean =
            (call as? Named)?.nameIdentifier?.let { nameIdentifier ->
                if (PsiTreeUtil.isAncestor(state.get(ENTRANCE), nameIdentifier, false)) {
                    resolveResultOrderedSet.add(call, name, validResult, emptySet())
                } else {
                    resolveResultOrderedSet.add(call, name, validResult, state.visitedElementSet())
                }

                keepProcessing()
            } ?: true

    companion object {
        @JvmOverloads
        @JvmStatic
        fun resolveResults(name: String?,
                           resolvedFinalArity: Int,
                           incompleteCode: Boolean,
                           entrance: PsiElement,
                           resolveState: ResolveState = ResolveState.initial()): List<VisitedElementSetResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val maxScope = maxScope(entrance)

            val entranceResolveState = resolveState
                    .put(ENTRANCE, entrance)
                    .putInitialVisitedElement(entrance)
                    .putAncestorUnquote(entrance)

            PsiTreeUtil.treeWalkUp(
                    multiResolve,
                    entrance,
                    maxScope,
                    entranceResolveState
            )

            return multiResolve.resolveResults()
        }
    }
}
