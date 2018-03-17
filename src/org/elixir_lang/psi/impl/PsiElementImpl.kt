package org.elixir_lang.psi.impl

import com.intellij.openapi.editor.Document
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Function.CREATE
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.operation.Match
import org.jetbrains.annotations.Contract
import java.util.*

fun PsiElement.document(): Document? = containingFile.viewProvider.document

/**
 *
 * @param call
 * @return `null` if call is at top-level
 */
@Contract(pure = true)
fun PsiElement.enclosingMacroCall(): Call? {
    var enclosingMacroCall: Call? = null
    val parent = parent

    if (parent is ElixirDoBlock) {
        val grandParent = parent.getParent()

        if (grandParent is Call) {
            enclosingMacroCall = grandParent
        }
    } else if (parent is ElixirStabBody) {
        val grandParent = parent.getParent()

        if (grandParent is ElixirStab) {
            val greatGrandParent = grandParent.getParent()

            if (greatGrandParent is ElixirBlockItem) {
                val greatGreatGrandParent = greatGrandParent.getParent()

                if (greatGreatGrandParent is ElixirBlockList) {
                    val greatGreatGreatGrandParent = greatGreatGrandParent.getParent()

                    if (greatGreatGreatGrandParent is ElixirDoBlock) {
                        val greatGreatGreatGreatGrandParent = greatGreatGreatGrandParent.getParent()

                        if (greatGreatGreatGreatGrandParent is Call) {
                            enclosingMacroCall = greatGreatGreatGreatGrandParent
                        }
                    }
                }
            } else if (greatGrandParent is ElixirDoBlock) {
                val greatGreatGrandParent = greatGrandParent.getParent()

                if (greatGreatGrandParent is Call) {
                    enclosingMacroCall = greatGreatGrandParent
                }
            } else if (greatGrandParent is ElixirParentheticalStab) {
                val greatGreatGrandParent = greatGrandParent.getParent()

                if (greatGreatGrandParent is ElixirAccessExpression) {
                    enclosingMacroCall = greatGreatGrandParent.enclosingMacroCall()
                }
            }
        } else if (grandParent is ElixirStabOperation) {
            val stabOperationParent = grandParent.getParent()

            if (stabOperationParent is ElixirStab) {
                val stabParent = stabOperationParent.getParent()

                if (stabParent is ElixirAnonymousFunction) {
                    val anonymousFunctionParent = stabParent.getParent()

                    if (anonymousFunctionParent is ElixirAccessExpression) {
                        val accessExpressionParent = anonymousFunctionParent.getParent()

                        if (accessExpressionParent is Arguments) {
                            val argumentsParent = accessExpressionParent.getParent()

                            if (argumentsParent is ElixirMatchedParenthesesArguments) {
                                val matchedParenthesesArgumentsParent = argumentsParent.getParent()

                                if (matchedParenthesesArgumentsParent is Call) {

                                    if (matchedParenthesesArgumentsParent.isCalling("Enum", "map") || matchedParenthesesArgumentsParent.isCalling("Enum", "each")) {
                                        enclosingMacroCall = matchedParenthesesArgumentsParent.enclosingMacroCall()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else if (parent is Arguments ||
            // See https://github.com/elixir-lang/elixir/blob/v1.5/lib/elixir/lib/protocol.ex#L633
            parent is AtUnqualifiedNoParenthesesCall<*> ||
            // See https://github.com/phoenixframework/phoenix/blob/v1.2.4/lib/phoenix/template.ex#L380-L392
            parent is ElixirAccessExpression ||
            // See https://github.com/absinthe-graphql/absinthe/blob/v1.3.0/lib/absinthe/schema/notation/writer.ex#L24-L44
            parent is ElixirList ||
            parent is ElixirMatchedParenthesesArguments ||
            // See https://github.com/absinthe-graphql/absinthe/blob/v1.3.0/lib/absinthe/schema/notation/writer.ex#L96
            parent is ElixirNoParenthesesManyStrictNoParenthesesExpression ||
            parent is ElixirTuple ||
            parent is Match ||
            parent is QualifiedAlias ||
            parent is QualifiedMultipleAliases) {
        enclosingMacroCall = parent.enclosingMacroCall()
    } else if (parent is Call) {

        if (parent.isCalling(KERNEL, ALIAS)) {
            enclosingMacroCall = parent
        } else if (parent.isCalling(org.elixir_lang.psi.call.name.Module.MODULE, CREATE, 3)) {
            enclosingMacroCall = parent
        }
    } else if (parent is QuotableKeywordPair) {

        if (parent.hasKeywordKey("do")) {
            val grandParent = parent.getParent()

            if (grandParent is QuotableKeywordList) {
                val greatGrandParent = grandParent.getParent()

                if (greatGrandParent is ElixirNoParenthesesOneArgument) {
                    val greatGreatGrandParent = greatGrandParent.getParent()

                    if (greatGreatGrandParent is Call) {
                        enclosingMacroCall = greatGreatGrandParent
                    }
                }
            }
        }
    }

    return enclosingMacroCall
}

fun PsiElement.getModuleName(): String? {
    val isModuleName = { c: PsiElement -> c is MaybeModuleName && c.isModuleName }

    return PsiTreeUtil.findFirstParent(this) { e ->
        e.children.any(isModuleName)
    }?.let { moduleDefinition ->
        moduleDefinition.children.firstOrNull(isModuleName)?.let { moduleName ->
            moduleDefinition.parent.getModuleName()?.let { parentModuleName ->
                "$parentModuleName.${moduleName.text}"
            } ?: moduleName.text
        }
    }
}

fun PsiElement.macroChildCallList(): MutableList<Call> {
    val callList: MutableList<Call>

    if (this is ElixirAccessExpression) {
        callList = this@macroChildCallList.getFirstChild().macroChildCallList()
    } else if (this is ElixirList || this is ElixirStabBody) {
        callList = ArrayList()

        var child: PsiElement? = firstChild
        while (child != null) {
            if (child is Call) {
                callList.add(child)
            } else if (child is ElixirAccessExpression) {
                callList.addAll(child.macroChildCallList())
            }
            child = child.nextSibling
        }
    } else {
        callList = mutableListOf()
    }

    return callList
}

/**
 * @return [Call] for the `defmodule`, `defimpl`, or `defprotocol` that defines
 * `maybeAlias` after it is resolved through any `alias`es.
 */
@Contract(pure = true)
fun PsiElement.maybeModularNameToModular(maxScope: PsiElement): Call? {
    val strippedMaybeModuleName = stripAccessExpression()

    return when (strippedMaybeModuleName) {
        is ElixirAtom -> strippedMaybeModuleName.maybeModularNameToModular()
        is QualifiableAlias -> strippedMaybeModuleName.maybeModularNameToModular(maxScope)
        else -> null
    }
}

fun PsiElement.moduleWithDependentsScope(): GlobalSearchScope {
    val virtualFile = containingFile.virtualFile
    val project = project
    val module = ModuleUtilCore.findModuleForFile(
            virtualFile,
            project
    )

    // module can be null for scratch files
    return if (module != null) {
        GlobalSearchScope.moduleWithDependentsScope(module)
    } else {
        GlobalSearchScope.allScope(project)
    }
}

@Contract(pure = true)
fun PsiElement.siblingExpression(function: (PsiElement) -> PsiElement): PsiElement? {
    var expression = this

    do {
        expression = function(expression)
    } while (expression is ElixirEndOfExpression ||
            expression is LeafPsiElement ||
            expression is PsiComment ||
            expression is PsiWhiteSpace)

    return expression
}

@Contract(pure = true)
fun PsiElement.stripAccessExpression(): PsiElement = (this as? ElixirAccessExpression)?.stripOnlyChildParent() ?: this

@Contract(pure = true)
fun PsiElement.stripOnlyChildParent(): PsiElement = children.singleOrNull() ?: this
