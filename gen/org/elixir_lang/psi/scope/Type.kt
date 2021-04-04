package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.isAncestor
import com.intellij.psi.util.siblings
import com.intellij.util.xml.Resolve
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.functionName
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.stub.type.UnmatchedUnqualifiedNoArgumentsCall
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.reference.ModuleAttribute.Companion.isSpecificationName
import org.elixir_lang.reference.ModuleAttribute.Companion.isTypeName
import org.elixir_lang.structure_view.element.modular.Module

abstract class Type : PsiScopeProcessor {
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                is Call -> execute(element, state)
                // Anonymous function type siganture
                is ElixirStabNoParenthesesSignature -> execute(element, state)
                is ElixirNoParenthesesOneArgument, is ElixirAccessExpression -> executeOnChildren(element, state)
                is ElixirFile, is ElixirList -> false
                else -> {
                    TODO("Not yet implemented")
                }
            }


    protected abstract fun executeOnType(definition: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean
    protected abstract fun executeOnParameter(parameter: UnqualifiedNoArgumentsCall<*>, state: ResolveState): Boolean
    protected abstract fun keepProcessing(): Boolean

    private fun execute(call: Call, state: ResolveState): Boolean =
            when (call) {
                is UnqualifiedNoArgumentsCall<*> -> executeOnParameter(call, state)
                is AtUnqualifiedNoParenthesesCall<*> -> execute(call, state)
                else -> if (Module.`is`(call) && call.isAncestor(state.get(ENTRANCE), false)) {
                    val childCalls = call.macroChildCalls()

                    for (childCall in childCalls) {
                        if (!execute(childCall, state)) {
                            break
                        }
                    }

                    keepProcessing()
                } else if (Use.`is`(call)) {
                    val useState = state.put(CallDefinitionClause.USE_CALL, call).putVisitedElement(call)

                    Use.treeWalkUp(call, useState, ::execute)
                } else {
                    true
                }
            }

    private fun execute(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean {
        val identifierName = atUnqualifiedNoParenthesesCall.atIdentifier.identifierName()

        return if (isTypeName(identifierName) || isSpecificationName(identifierName)) {
            executeOnType(atUnqualifiedNoParenthesesCall, state)
        } else {
            true
        }
    }

    private fun execute(stabNoParenthesesSignature: ElixirStabNoParenthesesSignature, state: ResolveState): Boolean =
        executeOnChildren(stabNoParenthesesSignature .noParenthesesArguments, state)

    private fun executeOnChildren(parent: PsiElement, state: ResolveState): Boolean =
            parent
                    .firstChild
                    .siblings()
                    .filter { it.node is CompositeElement }
                    .map { execute(it, state) }
                    .takeWhile { it }
                    .lastOrNull()
                    ?: true
}

internal tailrec fun PsiElement.ancestorTypeSpec(): AtUnqualifiedNoParenthesesCall<*>? =
        when (this) {
            is AtUnqualifiedNoParenthesesCall<*> -> {
                val identifierName = this.atIdentifier.identifierName()

                if (ModuleAttribute.isCallbackName(identifierName) || isTypeName(identifierName) || isSpecificationName(identifierName)) {
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
                // types can't be defined at the file level and must be inside modules.
            is ElixirFile,
                // Any stab body has to be parent of a type
            is ElixirStabBody -> null
            else -> {
                TODO()
            }
        }

val OPTIONALITIES = arrayOf("optional", "required")

fun Call.hasMapFieldOptionalityName(): Boolean =
    parent is ElixirContainerAssociationOperation && functionName() in OPTIONALITIES && resolvedFinalArity() == 1
