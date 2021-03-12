package org.elixir_lang.psi.scope

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.isAncestor
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.Import
import org.elixir_lang.psi.Modular
import org.elixir_lang.psi.Use
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.structure_view.element.modular.Module

abstract class CallDefinitionClause : PsiScopeProcessor {
    /*
     * Public Instance Methods
     */

    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                is Call -> execute(element, state)
                is ElixirFile -> implicitImports(element, state)
                else -> true
            }

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    /*
     * Protected Instance Methods
     */

    /**
     * Called on every [Call] where [org.elixir_lang.structure_view.element.CallDefinitionClause. is] is
     * `true` when checking tree with [.execute]
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return `true` to keep searching up the PSI tree; `false` to stop searching.
     */
    protected abstract fun keepProcessing(): Boolean

    /*
     * Private Instance Methods
     */

    private fun execute(element: Call, state: ResolveState): Boolean =
            if (org.elixir_lang.psi.CallDefinitionClause.`is`(element)) {
                executeOnCallDefinitionClause(element, state)
            } else if (Import.`is`(element)) {
                val importState = state.put(IMPORT_CALL, element).putVisitedElement(element)

                try {
                    Import.callDefinitionClauseCallWhile(element, importState) { callDefinitionClause, accResolveState ->
                        executeOnCallDefinitionClause(callDefinitionClause, accResolveState)
                    }
                } catch (stackOverflowError: StackOverflowError) {
                    Logger.error(
                            CallDefinitionClause::class.java,
                            "StackOverflowError while processing import",
                            element
                    )
                }

                true
            } else if (Module.`is`(element) && moduleContainsEntrance(element, state)) {
                val childCalls = element.macroChildCalls()

                for (childCall in childCalls) {
                    if (!execute(childCall, state)) {
                        break
                    }
                }

                // Only check MultiResolve.keepProcessing at the end of a Module to all multiple arities
                keepProcessing() &&
                        // the implicit `import Kernel` and `import Kernel.SpecialForms`
                        implicitImports(element, state)
            } else if (Use.`is`(element)) {
                val useState = state.put(USE_CALL, element).putVisitedElement(element)

                Use.callDefinitionClauseCallWhile(element, useState) { callDefinitionClause, accResolveState ->
                    executeOnCallDefinitionClause(callDefinitionClause, accResolveState)
                }

                true
            } else {
                true
            }


    private fun moduleContainsEntrance(call: Call, state: ResolveState): Boolean = state.get(ENTRANCE)?.let { entrance ->
        val callFile = call.containingFile

        if (callFile == entrance.containingFile) {
            /* Only allow scanning back down in outer nested modules for siblings.  Prevents scanning in sibling
               nested modules in https://github.com/KronicDeth/intellij-elixir/issues/1270 */
            call.isAncestor(entrance, false)
        } else {
            // done by injection or viewFile
            true
        }
    } ?: false

    private fun implicitImports(element: PsiElement, state: ResolveState): Boolean {
        val project = element.project

        var keepProcessing = org.elixir_lang.Reference.forEachNavigationElement(
                project,
                KERNEL
        ) { navigationElement ->
            var keepProcessingNavigationElements = true

            if (navigationElement is Call) {
                val navigationElementResolveState = state.putVisitedElement(navigationElement)

                keepProcessingNavigationElements = Modular.callDefinitionClauseCallWhile(
                        navigationElement, navigationElementResolveState
                ) { callDefinitionClause, accResolveState -> executeOnCallDefinitionClause(callDefinitionClause, accResolveState) }
            }

            keepProcessingNavigationElements
        }

        // the implicit `import Kernel.SpecialForms`
        if (keepProcessing) {
            val modularCanonicalNameState = state.put(MODULAR_CANONICAL_NAME, KERNEL_SPECIAL_FORMS)
            keepProcessing = org.elixir_lang.Reference.forEachNavigationElement(
                    project,
                    KERNEL_SPECIAL_FORMS
            ) { navigationElement ->
                var keepProcessingNavigationElements = true

                if (navigationElement is Call) {
                    val navigationElementResolveState = modularCanonicalNameState.putVisitedElement(navigationElement)

                    keepProcessingNavigationElements = Modular.callDefinitionClauseCallWhile(
                            navigationElement,
                            navigationElementResolveState
                    ) { callDefinitionClause, accResolveState ->
                        executeOnCallDefinitionClause(
                                callDefinitionClause,
                                accResolveState
                        )
                    }
                }

                keepProcessingNavigationElements
            }
        }

        return keepProcessing
    }

    companion object {
        @JvmStatic
        val IMPORT_CALL = Key<Call>("IMPORT_CALL")
        @JvmStatic
        val USE_CALL = Key<Call>("USE_CALL")
        val MODULAR_CANONICAL_NAME = Key<String>("MODULAR_CANONICAL_NAME")
    }
}

private val VISITED_ELEMENT_SET = Key<Set<PsiElement>>("VISITED_ELEMENTS")

fun ResolveState.hasBeenVisited(element: PsiElement): Boolean {
    return this.get(VISITED_ELEMENT_SET).contains(element)
}

fun ResolveState.putInitialVisitedElement(visitedElement: PsiElement): ResolveState {
    assert(this.get(VISITED_ELEMENT_SET) == null) {
        "VISITED_ELEMENT_SET already populated"
    }

    return this.put(VISITED_ELEMENT_SET, setOf(visitedElement))
}

fun ResolveState.putVisitedElement(visitedElement: PsiElement): ResolveState {
    val visitedElementSet = this.get(VISITED_ELEMENT_SET)

    return this.put(VISITED_ELEMENT_SET, visitedElementSet + setOf(visitedElement))
}

