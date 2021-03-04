package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner

/**
 * An alias that may or may not be qualified.
 */
interface QualifiableAlias : MaybeModuleName, PsiNameIdentifierOwner {
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
