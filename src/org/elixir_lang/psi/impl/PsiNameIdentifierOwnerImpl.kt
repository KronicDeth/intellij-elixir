package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.semantic.semantic
import org.elixir_lang.semantic.type.definition.source.Callback
import org.elixir_lang.semantic.type.definition.source.Specification

object PsiNameIdentifierOwnerImpl {
    @JvmStatic
    fun getNameIdentifier(alias: ElixirAlias): PsiElement? {
        val parent = alias.parent

        return when (parent) {
            is ElixirAccessExpression -> {
                val grandParent = parent.parent

                // alias is first alias in chain of qualifiers
                if (grandParent is QualifiableAlias) {
                    grandParent.nameIdentifier
                } else {
                    /* NamedStubbedPsiElementBase#getTextOffset assumes getNameIdentifier is null when the NameIdentifier is
                       the element itself. */
                    // alias is single, unqualified alias
                    null
                }
            }
            // alias is last in chain of qualifiers
            is QualifiableAlias -> parent.nameIdentifier
            else -> null
        }
    }

    @JvmStatic
    fun getNameIdentifier(keywordKey: ElixirKeywordKey): PsiElement? =
        if (keywordKey.line != null) {
            null
        } else {
            keywordKey
        }

    @JvmStatic
    fun getNameIdentifier(variable: ElixirVariable): PsiElement = variable

    @JvmStatic
    fun getNameIdentifier(named: org.elixir_lang.psi.call.Named): PsiElement? =
        when (val semantic = named.semantic) {
            is org.elixir_lang.semantic.call.definition.clause.Call -> semantic.nameIdentifier
            is Specification -> semantic.nameIdentifier
            is Callback -> semantic.nameIdentifier
            is org.elixir_lang.semantic.call.definition.Delegation -> semantic.nameIdentifier
            is org.elixir_lang.semantic.module.Call -> semantic.nameIdentifier
            is org.elixir_lang.semantic.protocol.Call -> semantic.nameIdentifier
            is org.elixir_lang.semantic.type.definition.Source -> semantic.nameIdentifier
            /* have to set to null so that {@code else} clause doesn't return the {@code defimpl} element as the
               name identifier */
            is org.elixir_lang.semantic.implementation.Call -> null
            is org.elixir_lang.semantic.module_attribute.definition.Literal -> semantic.nameIdentifier
            is org.elixir_lang.semantic.module_attribute.definition.Dynamic -> semantic.nameIdentifier
            else -> when (named) {
                is Operation -> {
                    val operation = named as Operation
                    operation.operator()
                }
                else -> named.functionNameElement()
            }
        }

    /**
     *
     * @param qualifiableAlias
     * @return null if qualifiableAlias is its own name identifier
     * @return PsiElement if qualifiableAlias is a subalias of a bigger qualified alias
     */
    @JvmStatic
    fun getNameIdentifier(qualifiableAlias: QualifiableAlias): PsiElement? =
        (qualifiableAlias.parent as? QualifiableAlias)?.nameIdentifier
}
