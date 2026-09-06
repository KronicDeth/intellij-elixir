package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.walk.ShapeTable

/** The ancestors that decide whether an identifier is a variable, for [Callable.isVariable]. */
object VariableWalk {
    enum class Bucket {
        /** The identifier is bound here, so the walk ends with `true`. */
        DECLARES,
        /** Holds expressions without binding anything itself, so ask the parent. */
        TRANSPARENT,
        /** A call decides by what it calls. Every operation is a call, so this comes after them. */
        CALL,
        /** Holds expressions, but no declaration reaches through it, so the walk ends with `false`. */
        STOP,
        /** Cannot hold an expression at all, so also `false`. */
        LEAF
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.variable }

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
