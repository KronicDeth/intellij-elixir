package org.elixir_lang.annotator

import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.errorreport.Logger.error
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.When
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic
import org.jetbrains.annotations.Contract

class Parameter {
    enum class Type {
        FUNCTION_NAME, MACRO_NAME, VARIABLE;

        companion object {
            fun isCallDefinitionClauseName(type: Type?): Boolean = type == FUNCTION_NAME || type == MACRO_NAME
        }
    }

    val defaultValue: PsiElement?
    val entrance: PsiElement
    val parameterized: NavigatablePsiElement?

    @JvmField
    val type: Type?

    constructor(entrance: PsiElement) {
        defaultValue = null
        this.entrance = entrance
        parameterized = null
        type = null
    }

    private constructor(
        defaultValue: PsiElement?,
        entrance: PsiElement,
        parameterized: NavigatablePsiElement?,
        type: Type?
    ) {
        this.defaultValue = defaultValue
        this.entrance = entrance
        this.parameterized = parameterized
        this.type = type
    }

    /**
     * Whether the [.type] is call definition clause name
     *
     * @return `true` if [.type] is [Type.FUNCTION_NAME] or [Type.MACRO_NAME].
     */
    @get:Contract(pure = true)
    val isCallDefinitionClauseName: Boolean
        get() = Type.isCallDefinitionClauseName(type)

    /**
     * Whether [.entrance] represents a parameter to a [.parameterized] element
     * @return `true` if [.parameterized] is not `null`
     */
    @get:Contract(pure = true)
    val isValid: Boolean
        get() = parameterized == null

    /**
     * A Parameter that is not a parameter to anything.
     *
     * @param parameter The original [Parameter] that may or may not be parameterized
     * @return an invalid parameter
     */
    fun not(parameter: Parameter): Parameter = if (parameter.defaultValue == null && parameter.parameterized == null) {
        parameter
    } else {
        Parameter(parameter.entrance)
    }

    companion object {
        /**
         * A new [Parameter] with [.parameterized] filled in if `parameter`'s [.entrance] is a
         * parameter element.
         *
         * @return a new [Parameter] with [.parameterized] filled in if [.entrance] is a valida parameter
         * element.
         */
        @Contract(pure = true)
        fun putParameterized(parameter: Parameter): Parameter = putParameterized(parameter, parameter.entrance)


        private fun error(message: String, element: PsiElement) {
            error(Parameter::class.java, message + " (when element class is " + element.javaClass.name + ")", element)
        }

        @Contract(pure = true)
        private fun putParameterized(parameter: Parameter, ancestor: Call): Parameter {
            val parameterizedParameter: Parameter
            val ancestorSemantic = ancestor.semantic

            if ((ancestorSemantic is org.elixir_lang.semantic.call.definition.Clause &&
                        ancestorSemantic.definition.time == Time.RUN) ||
                ancestorSemantic is org.elixir_lang.semantic.call.definition.Delegation
            ) {
                parameterizedParameter = Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    parameter.parameterized ?: ancestor,
                    parameter.type ?: Type.FUNCTION_NAME
                )
            } else if (ancestorSemantic is org.elixir_lang.semantic.call.definition.Clause &&
                ancestorSemantic.definition.time == Time.COMPILE
            ) {
                parameterizedParameter = Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    parameter.parameterized ?: ancestor,
                    parameter.type ?: Type.MACRO_NAME
                )
            } else if (ancestor.hasDoBlockOrKeyword()) {
                parameterizedParameter = Parameter(
                    parameter.defaultValue,
                    parameter.entrance,
                    ancestor,
                    parameter.type ?: Type.VARIABLE
                )
            } else {
                val element = ancestor.functionNameElement()
                var updatedParameter = parameter
                if (!PsiTreeUtil.isAncestor(element, parameter.entrance, false)) {
                    updatedParameter = Parameter(
                        parameter.defaultValue,
                        parameter.entrance,
                        ancestor,
                        parameter.type ?: Type.VARIABLE
                    )
                }

                // use generic handling so that parent is checked
                parameterizedParameter = putParameterized(updatedParameter, ancestor as PsiElement)
            }
            return parameterizedParameter
        }

        @Contract(pure = true)
        private fun putParameterized(
            parameter: Parameter,
            ancestor: ElixirAnonymousFunction
        ): Parameter = Parameter(
            parameter.defaultValue,
            parameter.entrance,
            ancestor,
            parameter.type ?: Type.VARIABLE
        )

        @Contract(pure = true)
        private fun putParameterized(parameter: Parameter, ancestor: PsiElement): Parameter =
            when (val parent = ancestor.parent) {
                is When -> {
                    putParameterized(parameter, parent)
                }
                is Call -> {
                    putParameterized(
                        parameter,
                        parent
                    )
                }
                is AtOperation, is ElixirAccessExpression, is ElixirAssociations, is ElixirAssociationsBase,
                is ElixirEex, is ElixirEexTag, is ElixirBitString, is ElixirBracketArguments,
                is ElixirContainerAssociationOperation, is ElixirKeywordPair, is ElixirKeywords, is ElixirList,
                is ElixirMapArguments, is ElixirMapConstructionArguments, is ElixirMapOperation,
                is ElixirMatchedParenthesesArguments, is ElixirNoParenthesesArguments,
                is ElixirNoParenthesesKeywordPair, is ElixirNoParenthesesKeywords,
                is ElixirNoParenthesesManyStrictNoParenthesesExpression, is ElixirNoParenthesesOneArgument,
                is ElixirNoParenthesesStrict, is ElixirParenthesesArguments, is ElixirParentheticalStab, is ElixirStab,
                is ElixirStabNoParenthesesSignature, is ElixirStabBody, is ElixirStabOperation,
                is ElixirStructOperation, is ElixirTuple -> {
                    putParameterized(parameter, parent)
                }
                is ElixirAnonymousFunction -> {
                    putParameterized(parameter, parent)
                }
                is InMatch -> {
                    putParameterized(parameter, parent)
                }
                is AtUnqualifiedBracketOperation, is BracketOperation, is ElixirBlockItem, is ElixirDoBlock,
                is ElixirInterpolation, is ElixirMapUpdateArguments, is ElixirMultipleAliases, is ElixirHeredocLineBody,
                is ElixirLineBody, is PsiFile, is QualifiedAlias, is QualifiedMultipleAliases -> {
                    Parameter(parameter.entrance)
                }
                else -> {
                    error("Don't know how to check if parameter", parent)
                    Parameter(parameter.entrance)
                }
            }
    }
}
