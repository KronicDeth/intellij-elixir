package org.elixir_lang.psi.scope

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.isAncestor
import org.elixir_lang.EEx
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.ecto.query.WindowAPI
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.Module
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS
import org.elixir_lang.psi.ex_unit.Case
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.hasDoBlockOrKeyword
import org.elixir_lang.psi.impl.call.*
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.siblingExpressions
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.Delegation

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
            is CallDefinitionImpl<*> -> execute(element, state)
            is ElixirFile -> execute(element, state)
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
     * Called on every [Call] where [org.elixir_lang.structure_view.element.Callback. is] is `true`
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnCallback(element: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.structure_view.element.Delegation. is] is `true` when checking tree
     * with [.execute]].
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnDelegation(element: Call, state: ResolveState): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.EEx.isFunctionFrom] is `true`.
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnEExFunctionFrom(element: Call, state: ResolveState): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.psi.Exception. is] is `true`.
     *
     * @return true to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnException(element: Call, state: ResolveState): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.mix.Generator.isEmbed] is `true`.
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnMixGeneratorEmbed(element: Call, state: ResolveState): Boolean

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
        when {
            org.elixir_lang.psi.CallDefinitionClause.`is`(element) -> executeOnCallDefinitionClause(element, state)
            Callback.`is`(element) -> executeOnCallback(element as AtUnqualifiedNoParenthesesCall<*>, state)
            Delegation.`is`(element) -> executeOnDelegation(element, state)
            Exception.`is`(element) -> executeOnException(element, state)
            For.`is`(element) -> For.treeWalkDown(element, state, ::execute)
            If.`is`(element) || Unless.`is`(element) -> {
                // If the entrance os at compile time level of `childCalls`, then only previous siblings could
                // possibly define this call and those will be handled by ElixirStabBody's processDeclarations
                val branches = Branches(element)

                val primaryChildCalls = branches.primaryChildExpressions.filterIsInstance<Call>()
                val walkPrimary = !containsCompileTimeEntranceAncestorOrSelf(primaryChildCalls, state)

                val alternativeChildCalls = branches.alternativeChildExpressions.filterIsInstance<Call>()
                val walkAlternative = !containsCompileTimeEntranceAncestorOrSelf(alternativeChildCalls, state)

                if (walkPrimary && walkAlternative) {
                    val childCalls = primaryChildCalls + alternativeChildCalls

                    for (childCall in childCalls) {
                        execute(childCall, state)
                    }
                }

                keepProcessing()
            }
            Import.`is`(element) -> {
                try {
                    Import.treeWalkUp(element, state) { call, accResolveState ->
                        execute(call, accResolveState)
                    }
                } catch (stackOverflowError: StackOverflowError) {
                    Logger.error(
                        CallDefinitionClause::class.java,
                        "StackOverflowError while processing import",
                        element
                    )
                }

                true
            }
            (Module.`is`(element) ||
                    Implementation.`is`(element) ||
                    Protocol.`is`(element) ||
                    Case.isChild(element, state))
                    && modularContainsEntrance(element, state) -> {
                val childCalls = element.macroChildCallSequence()

                // If the entrance is at compile time level of `childCalls`, then only previous siblings could possibly define
                // this call and those will be handled by ElixirStabBody's processDeclarations.
                if (!containsCompileTimeEntranceAncestorOrSelf(childCalls, state)) {
                    for (childCall in childCalls) {
                        execute(childCall, state)
                    }
                }

                // Only check MultiResolve.keepProcessing at the end of a Module to all multiple arities
                keepProcessing() &&
                        // the implicit `import Kernel` and `import Kernel.SpecialForms`
                        implicitImports(element, state)
            }
            QuoteMacro.`is`(element) -> if (!state.hasBeenVisited(element)) {
                QuoteMacro.treeWalkUp(element, state, ::execute)
            } else {
                true
            }
            Use.`is`(element) -> {
                Use.treeWalkUp(element, state, ::execute)

                true
            }
            element.isCalling(KERNEL, TRY) -> {
                element.whileInStabBodyChildExpressions { childExpression ->
                    execute(childExpression, state)
                }
            }
            org.elixir_lang.ecto.Schema.isChild(element, state) -> {
                org.elixir_lang.ecto.Schema.walkChild(element, state, ::execute)
            }
            // doesn't declare calls, but if this is the scope, then `Ecto.Query.API` is resolvable
            org.elixir_lang.ecto.Query.isChild(element, state) -> {
                org.elixir_lang.ecto.Query.walkChild(element, state, ::execute)
            }
            org.elixir_lang.ecto.query.API.`is`(element, state) -> {
                org.elixir_lang.ecto.query.API.treeWalkUp(element, state, ::execute)
            }
            WindowAPI.`is`(element, state) -> {
                WindowAPI.treeWalkUp(element, state, ::execute)
            }
            EEx.isFunctionFrom(element, state) -> executeOnEExFunctionFrom(element, state)
            org.elixir_lang.psi.mix.Generator.isEmbed(element, state) -> executeOnMixGeneratorEmbed(element, state)
            hasDoBlockOrKeyword(element) -> executeOnUnknownMacroCall(element, state)
            else -> true
        }

    private fun execute(element: ElixirFile, state: ResolveState): Boolean =
        if (element.viewFile() == null) {
            implicitImports(element, state)
        }
        // if there is a view file then it will have implicit imports, not this template
        else {
            true
        }

    protected abstract fun execute(element: CallDefinitionImpl<*>, state: ResolveState): Boolean

    private fun executeOnUnknownMacroCall(macroCall: Call, state: ResolveState): Boolean =
        if (macroCall.isAncestor(state.get(ENTRANCE), strict = true)) {
            macroCall
                .reference?.let { it as PsiPolyVariantReference }
                ?.multiResolve(false)?.asSequence()
                ?.filter(ResolveResult::isValidResult)
                ?.mapNotNull(ResolveResult::getElement)
                ?.filterIsInstance<Call>()
                ?.filter { org.elixir_lang.psi.CallDefinitionClause.isMacro(it) }
                ?.let { macroDefinitions ->
                    whileIn(macroDefinitions) { macroDefinition ->
                        executeOnUnknownMacroDefinition(macroDefinition, macroCall, state)
                    }
                }
                ?: true
        } else {
            true
        }

    private fun executeOnUnknownMacroDefinition(macroDefinition: Call, macroCall: Call, state: ResolveState): Boolean =
        org.elixir_lang.psi.CallDefinitionClause.head(macroDefinition)?.let { it as? Call }?.finalArguments()
            ?.lastOrNull()?.let { it as? QuotableKeywordList }?.let { keywords ->
                keywords.keywordValue("do")?.let { block ->
                    macroDefinition.stabBodyChildExpressions(forward = false)?.filterIsInstance<Call>()?.firstOrNull()
                        ?.takeIf { QuoteMacro.`is`(it) }?.let { quote ->
                            quote.stabBodyChildExpressions()?.filterIsInstance<Call>()?.filter { Unquote.`is`(it) }
                                ?.singleOrNull { unquote -> unquote.textMatches("unquote(${block.text})") }
                                ?.let { unquoteBlock ->
                                    unquoteBlock
                                        .siblingExpressions(forward = false, withSelf = false)
                                        .filterIsInstance<Call>()
                                        .let { QuoteMacro.treeWalkUp(it, state, ::execute) }
                                }
                        }
                }
            } ?: true

    private fun modularContainsEntrance(call: Call, state: ResolveState): Boolean =
        state.get(ENTRANCE)?.let { entrance ->
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

        val keepProcessing = implicitImport(project, KERNEL, state)

        return if (keepProcessing) {
            val modularCanonicalNameState = state.put(MODULAR_CANONICAL_NAME, KERNEL_SPECIAL_FORMS)

            implicitImport(project, KERNEL_SPECIAL_FORMS, modularCanonicalNameState)
        } else {
            false
        }
    }

    private fun implicitImport(project: Project, moduleName: String, state: ResolveState): Boolean =
        if (DumbService.isDumb(project)) {
            true
        } else {
            StubIndex
                .getInstance()
                .processElements(
                    org.elixir_lang.psi.stub.index.ModularName.KEY,
                    moduleName,
                    project,
                    GlobalSearchScope.allScope(project),
                    NamedElement::class.java
                ) { namedElement ->
                    when (namedElement) {
                        is Call -> {
                            val namedElementResolveState = state.putVisitedElement(namedElement)

                            Modular.callDefinitionClauseCallWhile(
                                namedElement, namedElementResolveState
                            ) { callDefinitionClause, accResolveState ->
                                executeOnCallDefinitionClause(
                                    callDefinitionClause,
                                    accResolveState
                                )
                            }
                        }
                        is ModuleImpl<*> -> whileIn(namedElement.callDefinitions()) {
                            execute(it, state)
                        }
                        else -> true
                    }
                }
        }

    companion object {
        val MODULAR_CANONICAL_NAME = Key<String>("MODULAR_CANONICAL_NAME")

        /**
         * The `state.get(ENTRANCE)` is one of the `childCalls` OR any calls in the way are compile-time conditional
         * logic like `if`s
         */
        private fun containsCompileTimeEntranceAncestorOrSelf(
            childCalls: Sequence<Call>,
            state: ResolveState
        ): Boolean =
            state.get(ENTRANCE).let { entrance ->
                containsCompileTimeAncestorOrSelf(childCalls, entrance)
            }

        private fun containsCompileTimeAncestorOrSelf(childCalls: Sequence<Call>, entrance: PsiElement): Boolean =
            childCalls.any { isCompileTimeAncestorOrSelf(it, entrance) }

        private fun isCompileTimeAncestorOrSelf(call: Call, entrance: PsiElement): Boolean =
            call.isEquivalentTo(entrance) || isCompileTimeAncestor(call, entrance.parent)

        private fun isCompileTimeAncestor(stop: Call, ancestor: PsiElement?): Boolean =
            when (ancestor) {
                is ElixirDoBlock,
                is ElixirBlockList, is ElixirBlockItem,
                is ElixirStab, is ElixirStabBody ->
                    isCompileTimeAncestor(stop, ancestor.parent)
                is Call -> when {
                    If.`is`(ancestor) || Unless.`is`(ancestor) ->
                        // the `stop` is an `if` or `unless`
                        ancestor.isEquivalentTo(stop) ||
                                // there is an `if` or `unless` wrapping the original `ancestor`, but need to
                                // confirm all levels above are also `if` or `unless` until `stop`.
                                isCompileTimeAncestor(stop, ancestor.parent)
                    else -> false
                }
                else -> false
            }

    }
}
