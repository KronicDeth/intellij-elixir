package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.NameArityInterval
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.scope.Resolution
import org.elixir_lang.psi.scope.ResolveResultOrderedSet
import org.elixir_lang.psi.scope.VisitedElementSetResolveResult
import org.elixir_lang.psi.scope.maxScope
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.Delegation

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
    private val incompleteCode: Boolean
) : org.elixir_lang.psi.scope.CallDefinitionClause() {
    override fun execute(definition: Definition, state: ResolveState): Boolean =
        definition.nameArityInterval
            ?.let { addIfNameOrArityToResolveResults(definition, it, state) }
            ?: true

    override fun execute(delegation: Delegation, state: ResolveState): Boolean {
        delegation.nameArityInterval?.let { nameArityInterval ->
            val name = nameArityInterval.name
            val validateArity = resolvedPrimaryArity in nameArityInterval.arityInterval

            if ((this.name == null && (incompleteCode || validateArity)) ||
                (this.name != null && name.startsWith(this.name))
            ) {
                val validResult = validateArity && name == this.name

                addToResolveResults(delegation, name, validResult, state)

                delegation.delegates.forEach { callDefinitionClause ->
                    addToResolveResults(
                        callDefinitionClause,
                        callDefinitionClause.nameArityInterval!!.name,
                        true,
                        state
                    )
                }
            }
        }

        return keepProcessing()
    }

    override fun executeOnEExFunctionFrom(element: Call, state: ResolveState): Boolean =
        element.finalArguments()?.let { arguments ->
            arguments[1].stripAccessExpression().let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { name ->
                if (this.name != null && name.startsWith(this.name)) {
                    val arity = if (arguments.size >= 4) {
                        // function_from_file(kind, name, file, args)
                        // function_from_file(kind, name, file, args, options)
                        // function_from_string(kind, name, template, args)
                        // function_from_string(kind, name, template, args, options)
                        arguments[3].stripAccessExpression().let { it as? ElixirList }?.children?.size
                    } else {
                        // function_from_file(kind, name, file) where args defaults to `[]`
                        // function_from_string(kind, name, template) where args defaults to `[]`
                        0
                    }

                    val validResult = (resolvedPrimaryArity == arity) && (name == this.name)

                    addToResolveResults(element, name, validResult, state)
                } else {
                    true
                }
            } ?: true
        } ?: true

    override fun executeOnMixGeneratorEmbed(element: Call, state: ResolveState): Boolean =
        element.finalArguments()?.first()?.stripAccessExpression()
            ?.let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { prefix ->
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

    private fun addIfNameOrArityToResolveResults(
        definition: Definition,
        nameArityInterval: NameArityInterval,
        state: ResolveState
    ): Boolean =
        when (val resolution = resolution(nameArityInterval)) {
            Resolution.NO -> true
            else -> addToResolveResults(definition, nameArityInterval, resolution.toValidResult(), state)
        }

    private fun resolution(nameArityInterval: NameArityInterval): Resolution {
        val name = nameArityInterval.name
        val validArity = resolvedPrimaryArity in nameArityInterval.arityInterval

        return if ((this.name == null && (incompleteCode || validArity)) ||
            (this.name != null && name.startsWith(this.name))
        ) {
            val validResult = validArity && name == this.name
            if (validResult) {
                Resolution.VALID
            } else {
                Resolution.INVALID
            }
        } else {
            Resolution.NO
        }
    }

    private fun addToResolveResults(
        definition: Definition,
        nameArityInterval: NameArityInterval,
        validResult: Boolean,
        state: ResolveState
    ): Boolean {
        definition.clauses.forEach { clause ->
            addToResolveResults(clause, nameArityInterval, validResult, state)
        }

        return keepProcessing()
    }

    private fun addToResolveResults(
        clause: Clause,
        nameArityInterval: NameArityInterval,
        validResult: Boolean,
        state: ResolveState
    ) {
        resolveResultOrderedSet.add(
            clause.psiElement,
            nameArityInterval.toString(),
            validResult,
            state.visitedElementSet()
        )
    }

    override fun keepProcessing(): Boolean = resolveResultOrderedSet.keepProcessing(incompleteCode)
    fun resolveResults(): List<VisitedElementSetResolveResult> = resolveResultOrderedSet.toList()

    private val resolveResultOrderedSet = ResolveResultOrderedSet()

    private fun addToResolveResults(
        named: org.elixir_lang.semantic.Named,
        name: String,
        validResult: Boolean,
        state: ResolveState
    ): Boolean {
        resolveResultOrderedSet.add(named.psiElement, name, validResult, state.visitedElementSet())

        return keepProcessing()
    }

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
        fun resolveResults(
            name: String?,
            resolvedFinalArity: Int,
            incompleteCode: Boolean,
            entranceModular: Modular,
            resolveState: ResolveState = ResolveState.initial()
        ): List<VisitedElementSetResolveResult> {
            val multiResolve = MultiResolve(name, resolvedFinalArity, incompleteCode)
            val entrancePsiElement = entranceModular.psiElement

            val entranceResolveState = resolveState.put(ENTRANCE, entrancePsiElement)
                .putInitialVisitedElement(entrancePsiElement)

            multiResolve.execute(entranceModular, entranceResolveState)

            return multiResolve.resolveResults()
        }

        @JvmOverloads
        @JvmStatic
        fun resolveResults(
            name: String?,
            resolvedFinalArity: Int,
            incompleteCode: Boolean,
            entrance: PsiElement,
            resolveState: ResolveState = ResolveState.initial()
        ): List<VisitedElementSetResolveResult> {
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
