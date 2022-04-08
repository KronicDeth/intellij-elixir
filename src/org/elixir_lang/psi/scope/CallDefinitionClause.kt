package org.elixir_lang.psi.scope

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.isAncestor
import org.elixir_lang.EEx
import org.elixir_lang.ecto.query.WindowAPI
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.KERNEL_SPECIAL_FORMS
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.hasDoBlockOrKeyword
import org.elixir_lang.psi.impl.call.*
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.semantic.*
import org.elixir_lang.semantic.Quote
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.type.definition.source.Callback

interface ValidResultable {
    fun toValidResult(): Boolean
}

enum class Resolution : ValidResultable {
    NO {
        override fun toValidResult(): Boolean {
            throw IllegalStateException()
        }
    },
    INVALID {
        override fun toValidResult(): Boolean = false
    },
    VALID {
        override fun toValidResult(): Boolean = true
    }
}

abstract class CallDefinitionClause : PsiScopeProcessor, ScopeProcessor {
    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
        element.semantic?.let { semantic ->
            execute(semantic, state)
        } ?: false

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    override fun execute(semantic: Semantic, state: ResolveState): Boolean =
        when (semantic) {
            is Quote -> whileIn(semantic.exportedCallDefinitions) {
                execute(it, state)
            }
            is Modular -> if (containsCompileTimeEntranceAncestorOrSelf(semantic, state)) {
                whileIn(semantic.callDefinitions) {
                    execute(it, state)
                }

                keepProcessing()
            } else {
                true
            }
            is Callback -> execute(semantic.callDefinition, state)
            is Clause -> execute(semantic.definition, state)
            is org.elixir_lang.semantic.call.definition.Delegation -> execute(semantic, state)
            is Exception -> whileIn(semantic.definitions) {
                execute(it, state)
            }
            is org.elixir_lang.semantic.chain.For -> {
                execute(semantic.body, state)
            }
            is Branching -> {
                // If the entrance is at compile time level of `childCalls`, then only previous siblings could
                // possibly define this call and those will be handled by ElixirStabBody's processDeclarations
                val branches = semantic.branches

                val walkBranches = branches.none { branch -> containsCompileTimeEntranceAncestorOrSelf(branch, state) }

                if (walkBranches) {
                    whileIn(branches) {
                        execute(it, state)
                    }
                }

                keepProcessing()
            }
            is org.elixir_lang.semantic.Import -> {
                whileIn(semantic.importedCallDefinitions) {
                    execute(it, state)
                }
            }
            is org.elixir_lang.semantic.Use -> {
                whileIn(semantic.exportedCallDefinitions) {
                    execute(it, state)
                }
            }
            is Try -> execute(semantic.body, state)
            is File -> execute(semantic, state)
            is org.elixir_lang.semantic.call.usage.Call -> execute(semantic.psiElement, state)
            else -> TODO("Not yet implemented")
        }

    protected abstract fun execute(definition: org.elixir_lang.semantic.call.Definition, state: ResolveState): Boolean

    protected abstract fun execute(
        delegation: org.elixir_lang.semantic.call.definition.Delegation,
        state: ResolveState
    ): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.EEx.isFunctionFrom] is `true`.
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnEExFunctionFrom(
        element: org.elixir_lang.psi.call.Call,
        state: ResolveState
    ): Boolean

    /**
     * Called on every [Call] where [org.elixir_lang.mix.Generator.isEmbed] is `true`.
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    protected abstract fun executeOnMixGeneratorEmbed(
        element: org.elixir_lang.psi.call.Call,
        state: ResolveState
    ): Boolean

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return `true` to keep searching up the PSI tree; `false` to stop searching.
     */
    protected abstract fun keepProcessing(): Boolean

    /*
     * Private Instance Methods
     */

    private fun execute(element: org.elixir_lang.psi.call.Call, state: ResolveState): Boolean = when {
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

    private fun execute(file: File, state: ResolveState): Boolean =
        if (file.let { it as? org.elixir_lang.semantic.file.Source }?.elixirFile?.viewFile() == null) {
            implicitImports(file.psiElement.project, state)
        }
        // if there is a view file then it will have implicit imports, not this template
        else {
            true
        }

    private fun executeOnUnknownMacroCall(macroCall: org.elixir_lang.psi.call.Call, state: ResolveState): Boolean =
        if (macroCall.isAncestor(state.get(ENTRANCE), strict = true)) {
            macroCall
                .reference?.let { it as PsiPolyVariantReference }
                ?.multiResolve(false)?.asSequence()
                ?.filter(ResolveResult::isValidResult)
                ?.mapNotNull(ResolveResult::getElement)
                ?.map(PsiElement::semantic)
                ?.filterIsInstance<Clause>()
                ?.map(Clause::definition)
                ?.filter { it.time == Time.COMPILE }
                ?.flatMap { it.clauses.asSequence() }
                ?.let { macroDefinitionClauses ->
                    whileIn(macroDefinitionClauses) { macroDefinitionClause ->
                        executeOnUnknownMacroDefinitionClause(macroDefinitionClause, macroCall, state)
                    }
                }
                ?: true
        } else {
            true
        }

    private fun executeOnUnknownMacroDefinitionClause(
        macroDefinitionClause: Clause,
        macroCall: org.elixir_lang.psi.call.Call,
        state: ResolveState
    ): Boolean {
        TODO()
    }

    private fun implicitImports(project: Project, state: ResolveState): Boolean =
        IMPLICIT_IMPORT_NAMES.all { name -> implicitImport(project, name, state) }

    private fun implicitImport(project: Project, unaliasedName: String, state: ResolveState): Boolean =
        Alias.modulars(project, unaliasedName).asSequence().flatMap { it.exportedCallDefinitions.asSequence() }
            .let { callDefinitions ->
                whileIn(callDefinitions) { callDefinition ->
                    execute(callDefinition, state)
                }
            }

    companion object {
        private val IMPLICIT_IMPORT_NAMES = arrayOf(KERNEL, KERNEL_SPECIAL_FORMS)

        val MODULAR_CANONICAL_NAME = Key<String>("MODULAR_CANONICAL_NAME")

        private fun containsCompileTimeEntranceAncestorOrSelf(semantic: Semantic, state: ResolveState): Boolean {
            val entrance = state.get(ENTRANCE)
            return isCompileTimeAncestorOrSelf(semantic.psiElement, entrance)
        }

        private fun isCompileTimeAncestorOrSelf(ancestor: PsiElement, entrance: PsiElement): Boolean =
            ancestor.isEquivalentTo(entrance) || isCompileTimeAncestor(ancestor, entrance.parent)

        private fun isCompileTimeAncestor(stop: PsiElement, ancestor: PsiElement?): Boolean =
            when (ancestor) {
                is ElixirDoBlock,
                is ElixirBlockList, is ElixirBlockItem,
                is ElixirStab, is ElixirStabBody ->
                    isCompileTimeAncestor(stop, ancestor.parent)
                is org.elixir_lang.psi.call.Call -> when {
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
