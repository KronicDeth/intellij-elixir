package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.psi.CallDefinitionClause.nameArityInterval
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.macroChildCallSequence
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.structure_view.element.Timed
import org.elixir_lang.util.AccumulatorContinue

object Using {
    fun treeWalkUp(
        using: PsiElement,
        use: Call?,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean =
        when (using) {
            is Call -> treeWalkUp(using, use, resolveState, keepProcessing)
            is CallDefinitionImpl<*> -> TODO()
            else -> true
        }

    private fun treeWalkUp(
        using: Call,
        use: Call?,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean =
        using
            .stabBodyChildExpressions(forward = false)
            ?.filterIsInstance<Call>()
            // Because `forward = false`, `firstOrNull` gets the last Call in the `do` block
            ?.firstOrNull()
            ?.takeUnlessHasBeenVisited(resolveState)
            ?.let { lastChildCall -> treeWalkUpFromLastChildCall(lastChildCall, use, resolveState, keepProcessing) }
            ?: true

    private fun treeWalkUpFromLastChildCall(
        lastChildCall: Call,
        useCall: Call?,
        resolveState: ResolveState,
        keepProcessing: (PsiElement, ResolveState) -> Boolean
    ): Boolean {
        val resolvedModuleName = lastChildCall.resolvedModuleName()
        val functionName = lastChildCall.functionName()

        return if (resolvedModuleName != null && functionName != null) {
            when {
                resolvedModuleName == KERNEL && functionName == QUOTE -> {
                    QuoteMacro.treeWalkUp(lastChildCall, resolveState, keepProcessing)
                }

                resolvedModuleName == KERNEL && functionName == APPLY -> {
                    lastChildCall.finalArguments()?.let { arguments ->
                        // TODO pipelines to apply/3
                        if (arguments.size == 3) {
                            arguments[0].let { maybeModularName ->
                                val modulars = maybeModularName.maybeModularNameToModulars(
                                    maxScope = maybeModularName.containingFile,
                                    useCall = useCall,
                                    incompleteCode = false
                                )

                                var accumlatedKeepProcessing = true

                                for (modular in modulars) {
                                    val modularResolveState = resolveState.putVisitedElement(modular)

                                    val name = useCall?.finalArguments()?.let { arguments ->
                                        if (arguments.size == 2) {
                                            when (val which = arguments[1].stripAccessExpression()) {
                                                is ElixirAtom -> if (which.line == null) {
                                                    which.lastChild.text
                                                } else {
                                                    null
                                                }
                                                else -> null
                                            }
                                        } else {
                                            null
                                        }
                                    }

                                    accumlatedKeepProcessing = if (name != null) {
                                        Modular.callDefinitionClauseCallFoldWhile(
                                            modular,
                                            name,
                                            modularResolveState
                                        ) { callDefinitionClauseCall, _, _, accResolveState ->
                                            val finalContinue = treeWalkUp(
                                                callDefinitionClauseCall,
                                                useCall,
                                                accResolveState,
                                                keepProcessing
                                            )
                                            AccumulatorContinue(accResolveState, finalContinue)
                                        }.`continue`
                                    } else {
                                        Modular.callDefinitionClauseCallWhile(
                                            modular,
                                            modularResolveState
                                        ) { callDefinitionClauseCall, accResolveState ->
                                            if (CallDefinitionClause.isFunction(callDefinitionClauseCall)) {
                                                treeWalkUp(
                                                    callDefinitionClauseCall,
                                                    useCall,
                                                    accResolveState,
                                                    keepProcessing
                                                )
                                            } else {
                                                true
                                            }
                                        }
                                    }
                                }

                                accumlatedKeepProcessing
                            }
                        } else {
                            true
                        }
                    } ?: true
                }
                resolvedModuleName == KERNEL && functionName == CASE -> {
                    val lastChildCallResolveState = resolveState.putVisitedElement(lastChildCall)

                    Case.treeWalkUp(lastChildCall, lastChildCallResolveState, keepProcessing)
                }
                else -> {
                    var accumulatedKeepProcessing = true

                    for (reference in lastChildCall.references) {
                        val resolvedList: List<PsiElement> = if (reference is PsiPolyVariantReference) {
                            reference
                                .multiResolve(false)
                                .mapNotNull { it.element }
                        } else {
                            reference.resolve()?.let { listOf(it) } ?: emptyList()
                        }.filter { !resolveState.hasBeenVisited(it) }

                        for (resolved in resolvedList) {
                            accumulatedKeepProcessing = if (resolved is Call && CallDefinitionClause.`is`(resolved)) {
                                val resolvedResolveSet = resolveState.putVisitedElement(resolved)

                                treeWalkUp(
                                    using = resolved,
                                    use = useCall,
                                    resolveState = resolvedResolveSet,
                                    keepProcessing = keepProcessing
                                )
                            } else {
                                true
                            }

                            if (!accumulatedKeepProcessing) {
                                break
                            }
                        }

                        if (!accumulatedKeepProcessing) {
                            break
                        }
                    }

                    accumulatedKeepProcessing
                }
            }
        } else {
            true
        }
    }

    @RequiresReadLock
    fun definers(modular: PsiElement): Sequence<PsiElement> =
        when (modular) {
            is Call -> definers(modular)
            is ModuleImpl<*> -> definers(modular)
            else -> emptySequence()
        }

    @RequiresReadLock
    fun definers(modularCall: Call): Sequence<Call> =
        modularCall
            .macroChildCallSequence()
            .filter { isDefiner(it) }

    fun definers(moduleImpl: ModuleImpl<*>): Sequence<CallDefinitionImpl<*>> =
        moduleImpl
            .callDefinitions()
            .asSequence()
            .filter { isDefiner(it) }

    /**
     * Returns true if [modular] is `ExUnit.CaseTemplate` itself or a module that (directly or
     * transitively) uses `ExUnit.CaseTemplate`.
     *
     * This is needed because `ExUnit.CaseTemplate.__using__/1` generates a `defmacro __using__`
     * inside the target module at *compile time*.  That generated macro never appears in the
     * module's source `.ex` file, so [definers] returns an empty sequence for such modules and the
     * scope walker gives up without importing `describe`, `test`, etc.  Detecting the CaseTemplate
     * pattern lets [Use.treeWalkUp] substitute `ExUnit.Case.__using__/1` directly instead.
     *
     * Cycle-safety is provided by [resolveState]'s visited-element set.
     */
    @RequiresReadLock
    fun isCaseTemplate(modular: PsiElement, resolveState: ResolveState): Boolean {
        if (resolveState.hasBeenVisited(modular)) return false
        return when (modular) {
            is ModuleImpl<*> -> modular.name == EXUNIT_CASE_TEMPLATE
            is Call -> {
                val updatedState = resolveState.putVisitedElement(modular)
                modular.name == EXUNIT_CASE_TEMPLATE ||
                modular.macroChildCallSequence()
                    .filter { Use.`is`(it) }
                    .any { useCall ->
                        Use.modulars(useCall).any { inner ->
                            isCaseTemplate(inner, updatedState)
                        }
                    }
            }
            else -> false
        }
    }

    /**
     * Locates the `__using__/1` definers of `ExUnit.Case` via the stub index.
     *
     * Used as a substitute in [Use.treeWalkUp] when the target module is a CaseTemplate: because the
     * generated `__using__` is not in the source PSI and the `unquote(__MODULE__).__proxy__` call inside
     * it cannot be statically resolved, we skip the opaque generated layer and go straight to
     * `ExUnit.Case.__using__/1`, which is what every CaseTemplate chain ultimately delegates to.
     */
    @RequiresReadLock
    fun exUnitCaseDefiners(context: PsiElement): Sequence<PsiElement> =
        StubIndex
            .getElements(
                ModularName.KEY,
                EXUNIT_CASE,
                context.project,
                GlobalSearchScope.allScope(context.project),
                NamedElement::class.java
            )
            .asSequence()
            .flatMap { definers(it) }

    private const val EXUNIT_CASE = "ExUnit.Case"
    private const val EXUNIT_CASE_TEMPLATE = "ExUnit.CaseTemplate"
    private const val ARITY = 1
    private const val USING = "__using__"

    private fun isDefiner(call: Call): Boolean =
        call.isCalling(KERNEL, DEFMACRO) &&
                nameArityInterval(call, ResolveState.initial())?.let { nameArityRange ->
                    nameArityRange.name == USING && nameArityRange.arityInterval.contains(ARITY)
                }
                ?: false

    private fun isDefiner(callDefinitionImpl: CallDefinitionImpl<*>): Boolean =
        callDefinitionImpl.time == Timed.Time.COMPILE &&
                callDefinitionImpl.name == USING &&
                callDefinitionImpl.exportedArity(ResolveState.initial()) == ARITY
}
