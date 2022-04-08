package org.elixir_lang.psi.impl.declarations

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.CallImpl.hasDoBlockOrKeyword
import org.elixir_lang.psi.impl.call.macroDefinitionClauseForArgument
import org.elixir_lang.psi.impl.moduleWithDependentsScope
import org.elixir_lang.reference.Callable.Companion.isBitStreamSegmentOption
import org.elixir_lang.reference.Callable.Companion.isParameter
import org.elixir_lang.reference.Callable.Companion.isParameterWithDefault
import org.elixir_lang.reference.Callable.Companion.isVariable
import org.elixir_lang.reference.Callable.Companion.variableUseScope
import org.elixir_lang.reference.ModuleAttribute.Companion.isNonReferencing
import org.elixir_lang.semantic.branching.Cond
import org.elixir_lang.semantic.semantic
import org.jetbrains.annotations.Contract

fun PsiElement.selfAndFollowingSiblingsSearchScope(): LocalSearchScope {
    val selfAndFollowingSiblingList = ArrayList<PsiElement>()
    selfAndFollowingSiblingList.add(this)

    var previousSibling = this
    var followingSibling: PsiElement? = previousSibling.nextSibling

    while (followingSibling != null) {
        // Does not exclude PsiComment as Search In Comments is an option
        if (followingSibling !is ElixirEndOfExpression && followingSibling !is PsiWhiteSpace) {
            selfAndFollowingSiblingList.add(followingSibling)
        }

        previousSibling = followingSibling
        followingSibling = previousSibling.nextSibling
    }

    return LocalSearchScope(selfAndFollowingSiblingList.toTypedArray())
}

object UseScopeImpl {
    enum class UseScopeSelector {
        PARENT,
        SELF,
        SELF_AND_FOLLOWING_SIBLINGS
    }

    /**
     * Returns the scope in which references to this element are searched.
     *
     * @return the search scope instance.
     * @see {@link com.intellij.psi.search.PsiSearchHelper.getUseScope
     */
    @Contract(pure = true)
    @JvmStatic
    fun get(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): SearchScope =
        if (isNonReferencing(atUnqualifiedNoParenthesesCall.atIdentifier)) {
            atUnqualifiedNoParenthesesCall.moduleWithDependentsScope()
        } else {
            atUnqualifiedNoParenthesesCall.selfAndFollowingSiblingsSearchScope()
        }

    /**
     * Returns the scope in which references to this element are searched.
     *
     * @return the search scope instance.
     * @see {@link com.intellij.psi.search.PsiSearchHelper.getUseScope
     */
    @Contract(pure = true)
    @JvmStatic
    fun get(unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>): SearchScope {
        val useScope: SearchScope

        if (isBitStreamSegmentOption(unqualifiedNoArgumentsCall)) {
            // Bit Stream Segment Options aren't variables or even real functions, so no use scope
            useScope = LocalSearchScope.EMPTY
        } else if (isParameter(unqualifiedNoArgumentsCall) || isParameterWithDefault(unqualifiedNoArgumentsCall)) {
            var ancestor = unqualifiedNoArgumentsCall.parent

            while (true) {
                if (ancestor is Call) {
                    when (val ancestorSemantic = ancestor.semantic) {
                        is org.elixir_lang.semantic.call.definition.clause.Call -> {
                            val macroDefinitionClause = ancestor.macroDefinitionClauseForArgument()

                            if (macroDefinitionClause != null) {
                                ancestor = macroDefinitionClause
                            }

                            break
                        }
                        is org.elixir_lang.semantic.call.definition.Delegation -> break
                        else -> if (ancestor.hasDoBlockOrKeyword()) {
                            break
                        }
                    }
                } else if (ancestor is ElixirStabOperation) {
                    break
                } else if (ancestor is PsiFile) {
                    Logger.error(
                        UnqualifiedNoArgumentsCall::class.java,
                        "Use scope for parameter not found before reaching file scope",
                        unqualifiedNoArgumentsCall
                    )
                    break
                }

                ancestor = ancestor.parent
            }

            useScope = LocalSearchScope(ancestor)
        } else if (isVariable(unqualifiedNoArgumentsCall)) {
            useScope = variableUseScope(unqualifiedNoArgumentsCall)
        } else {
            // if the type of callable isn't known, fallback to default scope
            useScope = unqualifiedNoArgumentsCall.moduleWithDependentsScope()
        }

        return useScope
    }

    @JvmStatic
    fun selector(element: PsiElement): UseScopeSelector {
        var useScopeSelector = UseScopeSelector.PARENT

        when (element) {
            is AtUnqualifiedNoParenthesesCall<*> -> {
                /* Module Attribute declarations */
                useScopeSelector = UseScopeSelector.SELF_AND_FOLLOWING_SIBLINGS
            }
            is ElixirAnonymousFunction -> {
                useScopeSelector = UseScopeSelector.SELF
            }
            is Call -> {
                when (element.semantic) {
                    is org.elixir_lang.semantic.branching.conditional.Case,
                    is Cond,
                    is org.elixir_lang.semantic.branching.conditional.If,
                    is org.elixir_lang.semantic.branching.Receive,
                    is org.elixir_lang.semantic.branching.conditional.Unless,
                    is org.elixir_lang.semantic.variable.Bang ->
                        useScopeSelector = UseScopeSelector.SELF_AND_FOLLOWING_SIBLINGS
                    is org.elixir_lang.semantic.call.definition.clause.Call,
                    is org.elixir_lang.semantic.Modular ->
                        useScopeSelector = UseScopeSelector.SELF
                    else -> if (hasDoBlockOrKeyword(element)) {
                        useScopeSelector = UseScopeSelector.SELF
                    }
                }
            }
        }

        return useScopeSelector
    }
}
