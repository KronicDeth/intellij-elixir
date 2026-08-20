package org.elixir_lang.psi

import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Infix
import org.elixir_lang.psi.operation.infix.Normalized

/**
 * An alias that may or may not be qualified.
 *
 * Implements [PsiExternalReferenceHost] (a no-method marker) so aliases can host new-model
 * [com.intellij.model.psi.PsiSymbolReference]s contributed via `psi.symbolReferenceProvider`
 * (e.g. a module usage → the `defmodule` it names). This does not change behavior on its own;
 * providers that return no references leave navigation unchanged.
 */
@Suppress("UnstableApiUsage")
interface QualifiableAlias : MaybeModuleName, PsiNameIdentifierOwner, PsiExternalReferenceHost {
    fun fullyQualifiedName(): String
}

fun QualifiableAlias.qualifier(): PsiElement? = when (this) {
    is ElixirAlias -> {
        when (val parent = this.parent) {
            is QualifiedAlias -> parent.qualifier()
            else -> null
        }
    }
    is QualifiedAlias -> this.qualifier()
    else -> null
}

tailrec fun QualifiableAlias.outerMostQualifiableAlias(): QualifiableAlias {
    val parent = parent!!

    return if (parent is QualifiableAlias) {
        parent.outerMostQualifiableAlias()
    } else {
        val grandParent = parent.parent

        if (grandParent is QualifiableAlias) {
            grandParent.outerMostQualifiableAlias()
        } else {
            this
        }
    }
}
