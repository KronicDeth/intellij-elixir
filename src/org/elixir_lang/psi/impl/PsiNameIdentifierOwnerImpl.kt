package org.elixir_lang.psi.impl

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.*
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.structure_view.element.CallDefinitionSpecification
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.Delegation
import org.elixir_lang.structure_view.element.Type
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
    fun getNameIdentifier(named: org.elixir_lang.psi.call.Named): PsiElement? =
            when {
                /* can't be a {@code public static PsiElement getNameIdentifier(@NotNull Operation operation)} because
                   it leads to "reference to getNameIdentifier is ambiguous" */
                named is Operation -> {
                    val operation = named as Operation
                    operation.operator()
                }
                CallDefinitionClause.`is`(named) -> CallDefinitionClause.nameIdentifier(named)
                CallDefinitionSpecification.`is`(named) -> CallDefinitionSpecification.nameIdentifier(named)
                Callback.`is`(named) -> Callback.nameIdentifier(named)
                Delegation.`is`(named) -> Delegation.nameIdentifier(named)
                /* have to set to null so that {@code else} clause doesn't return the {@code defimpl} element as the
                   name identifier */
                Implementation.`is`(named) -> null
                Module.`is`(named) -> Module.nameIdentifier(named)
                Protocol.`is`(named) -> Module.nameIdentifier(named)
                Type.`is`(named) -> Type.nameIdentifier(named)
                // module attribute
                named is AtUnqualifiedNoParenthesesCall<*> ->  named.atIdentifier
                else -> named.functionNameElement()
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
