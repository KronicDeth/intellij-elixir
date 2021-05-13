package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.isAncestor
import com.intellij.psi.util.siblings
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.call.whileInStabBodyChildExpressions
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.impl.whileInChildExpressions
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.psi.stub.type.call.Stub.isModular
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeSpecName
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol

abstract class Type : PsiScopeProcessor {
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                // typing a module attribute on line above a pre-existing one
                is AtNonNumericOperation -> execute(element, state)
                is Call -> execute(element, state)
                // Anonymous function type siganture
                is ElixirStabNoParenthesesSignature -> execute(element, state)
                // type variable in a `when key: type`
                is ElixirKeywordKey -> executeOnParameter(element, state)
                is ElixirNoParenthesesOneArgument, is ElixirAccessExpression -> executeOnChildren(element, state)
                is ElixirFile, is ElixirList, is ElixirTuple -> false
                else -> {
                    TODO("Not yet implemented")
                }
            }


    protected abstract fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean
    protected abstract fun executeOnParameter(parameter: PsiElement, state: ResolveState): Boolean
    protected abstract fun keepProcessing(): Boolean

    private fun execute(atNonNumericOperation: AtNonNumericOperation, state: ResolveState): Boolean =
        execute(atNonNumericOperation.children.last(), state)

    private fun execute(call: Call, state: ResolveState): Boolean =
            when (call) {
                is UnqualifiedNoArgumentsCall<*> -> executeOnParameter(call, state)
                is AtUnqualifiedNoParenthesesCall<*> -> execute(call, state)
                else -> if (isModular(call) && call.isAncestor(state.get(ENTRANCE), false)) {
                    val childCalls = call.macroChildCalls()
                    val childCallsKeepProcessing = whileIn(childCalls) {
                        execute(it, state)
                    }

                    // specialization
                    if (childCallsKeepProcessing && Protocol.`is`(call)) {
                        // favor the decompiled, specific module for this protocol so that only this module's `t` will
                        // be used for Find Usages instead of for all protocols as happens with the source for
                        // `defprotocol`.
                        executeOnDecompiledProtocol(call, state) &&
                          executeOnDefprotocolCall(call, state)
                    } else {
                        childCallsKeepProcessing
                    }
                } else if (Use.`is`(call)) {
                    val useState = state.put(CallDefinitionClause.USE_CALL, call).putVisitedElement(call)

                    Use.treeWalkUp(call, useState, ::execute)
                } else {
                    true
                }
            }

    private fun execute(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean {
        val identifierName = atUnqualifiedNoParenthesesCall.atIdentifier.identifierName()

        return if (isTypeSpecName(identifierName)) {
            executeOnType(atUnqualifiedNoParenthesesCall, state)
        } else {
            true
        }
    }

    private fun execute(stabNoParenthesesSignature: ElixirStabNoParenthesesSignature, state: ResolveState): Boolean =
        executeOnChildren(stabNoParenthesesSignature .noParenthesesArguments, state)

    private fun executeOnChildren(parent: PsiElement, state: ResolveState): Boolean =
            parent.whileInChildExpressions() {
                execute(it, state)
            }

    private fun executeOnDecompiledProtocol(call: Call, state: ResolveState): Boolean {
        val project = call.project

        return call.name?.let { name ->
            StubIndex.getInstance().processElements(ModularName.KEY, name, project, GlobalSearchScope.allScope(project), NamedElement::class.java) { modular ->
                // use `ModuleImpl` to only use decompiled
                if (modular is ModuleImpl<*>) {
                    val decompiled = modular.navigationElement
                    // reset the resolve state as only `defmodule` that is an ancestor of entrance will be walked
                    execute(decompiled, ResolveState.initial().put(ENTRANCE, decompiled).putInitialVisitedElement(decompiled))
                } else {
                    true
                }
            }
        } ?: true
    }

    private fun executeOnDefprotocolCall(call: Call, state: ResolveState): Boolean =
            call.reference?.let { reference ->
                when (reference) {
                    is PsiPolyVariantReference -> {
                        val resolveResults = reference.multiResolve(false)

                        whileIn(resolveResults) { resolveResult ->
                            resolveResult.element?.let { it as? Call }?.let {
                                executeOnDefprotocolDefinition(it, state)
                            } ?: true
                        }
                    }
                    else -> TODO()
                }
            } ?: true

    private fun executeOnDefprotocolDefinition(descendant: PsiElement, state: ResolveState): Boolean =
        when (descendant) {
            is Call -> descendant.whileInStabBodyChildExpressions {
                executeOnDefprotocolDefinitionExpression(it, state)
            }
            else -> {
                true
            }
        }

    private fun executeOnDefprotocolDefinitionExpression(expression: PsiElement, state: ResolveState): Boolean =
        when (expression) {
            is Qualified ->
                if (expression.isCalling("Protocol", "__protocol__", 2)) {
                    expression.reference?.let { it as PsiPolyVariantReference }?.multiResolve(false)?.let { resolveResults ->
                        whileIn(resolveResults) { resolveResult ->
                            resolveResult.element?.let { it as? Call }?.let { __protocol__ ->
                                Using.treeWalkUp(
                                        __protocol__,
                                        useCall = null,
                                        resolveState = state,
                                        keepProcessing = ::executeOn__protocol__DefinitionExpression
                                )
                            } ?: true
                        }
                    } ?: true
                } else {
                    true
                }
            else -> true
        }

    private fun executeOn__protocol__DefinitionExpression(expression: PsiElement, state: ResolveState): Boolean =
        expression.let { it as? Call }?.let { call ->
            if (Module.`is`(call)) {
                // treat the module the same as an inline quote block as the `defprotocol` will replace itself with
                // the `defmodule`
                QuoteMacro.treeWalkUp(call, state, ::execute)
            } else {
                true
            }
        } ?: true
}

internal tailrec fun PsiElement.ancestorTypeSpec(): AtUnqualifiedNoParenthesesCall<*>? =
        when (this) {
            is AtUnqualifiedNoParenthesesCall<*> -> {
                val identifierName = this.atIdentifier.identifierName()

                if (ModuleAttribute.isTypeSpecName(identifierName)) {
                    this
                } else {
                    null
                }
            }
            is Arguments,
            is ElixirAccessExpression,
            is ElixirKeywords,
            is ElixirKeywordPair,
            is ElixirMatchedParenthesesArguments,
            is ElixirStructOperation,
            is ElixirNoParenthesesOneArgument,
            is ElixirNoParenthesesArguments,
            is ElixirNoParenthesesKeywords,
            is ElixirNoParenthesesKeywordPair,
            is ElixirNoParenthesesManyStrictNoParenthesesExpression,
                // For function type
            is ElixirParentheticalStab, is ElixirStab, is ElixirStabOperation, is ElixirStabNoParenthesesSignature,
                // containers
            is ElixirList, is ElixirTuple,
                // maps
            is ElixirMapOperation, is ElixirMapArguments, is ElixirMapConstructionArguments,
            is ElixirAssociations, is ElixirAssociationsBase, is ElixirContainerAssociationOperation,
                // types
            is Type, is Call -> parent.ancestorTypeSpec()
            // `fn` anonymous function type just uses parentheses and `->`, like `(type1, type2 -> type3)`
            is ElixirAnonymousFunction, is ElixirStabParenthesesSignature,
                // BitStrings use `::` like types, but cannot contain type parameters or declarations
            is ElixirBitString,
                // Types can't be declared inside of bracket operations where they would be used as keys
            is BracketOperation, is ElixirBracketArguments,
                // Types cannot be declared in `else`, `rescue`, or `after`
            is ElixirBlockList, is ElixirBlockItem,
            is ElixirDoBlock,
                // No types in EEx
            is ElixirEex, is ElixirEexTag,
                // No types in interpolation
            is ElixirInterpolation,
                // Map updates aren't used in type specifications unlike `ElixirMapConstructionArguments`
            is ElixirMapUpdateArguments,
                // `@{:__aliases__, _meta, _args}` in `defmacro @{:__aliases__, _meta, _args} do...`
            is ElixirMatchedAtOperation,
                // types can't be defined at the file level and must be inside modules.
            is ElixirFile,
                // Any stab body has to be parent of a type
            is ElixirStabBody,
            // __MODULE__.* does not matter that it is in a type
            is QualifiableAlias -> null
            else -> {
                Logger.error(PsiElement::class.java, "Don't know how to find ancestorTypeSpec", this)

                null
            }
        }

val OPTIONALITIES = arrayOf("optional", "required")

fun Call.isTypeSpecPseudoFunction(): Boolean = hasMapFieldOptionalityName() || hasListRepetitionName()

fun Call.hasMapFieldOptionalityName(): Boolean =
    parent is ElixirContainerAssociationOperation && functionName() in OPTIONALITIES && resolvedFinalArity() == 1

fun Call.hasListRepetitionName(): Boolean =
        parent is ElixirList && functionName() == "..."
