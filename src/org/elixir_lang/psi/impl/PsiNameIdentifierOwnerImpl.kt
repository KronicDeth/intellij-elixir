package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.structure_view.element.CallDefinitionSpecification
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol

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
            if (keywordKey.charListLine != null) {
                null
            } else {

                if (keywordKey.stringLine != null) {
                    null
                } else {
                    keywordKey
                }
            }

    @JvmStatic
    fun getNameIdentifier(variable: ElixirVariable): PsiElement = variable

    @JvmStatic
    fun getNameIdentifier(named: org.elixir_lang.psi.call.Named): PsiElement? {
        val nameIdentifier: PsiElement?

        /* can't be a {@code public static PsiElement getNameIdentifier(@NotNull Operation operation)} because it leads
           to "reference to getNameIdentifier is ambiguous" */
        if (named is Operation) {
            val operation = named as Operation
            nameIdentifier = operation.operator()
        } else if (CallDefinitionClause.`is`(named)) {
            nameIdentifier = CallDefinitionClause.nameIdentifier(named)
        } else if (CallDefinitionSpecification.`is`(named)) {
            nameIdentifier = CallDefinitionSpecification.nameIdentifier(named)
        } else if (Callback.`is`(named)) {
            nameIdentifier = Callback.nameIdentifier(named)
        } else if (Implementation.`is`(named)) {
            /* have to set to null so that {@code else} clause doesn't return the {@code defimpl} element as the name
               identifier */
            nameIdentifier = null
        } else if (Module.`is`(named)) {
            nameIdentifier = Module.nameIdentifier(named)
        } else if (Protocol.`is`(named)) {
            nameIdentifier = Module.nameIdentifier(named)
        } else if (org.elixir_lang.structure_view.element.Type.`is`(named)) {
            nameIdentifier = org.elixir_lang.structure_view.element.Type.nameIdentifier(named)
        } else if (named is AtUnqualifiedNoParenthesesCall<*>) { // module attribute
            nameIdentifier = named.atIdentifier
        } else {
            nameIdentifier = named.functionNameElement()
        }

        return nameIdentifier
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
