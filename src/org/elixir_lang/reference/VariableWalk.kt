package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.walk.ShapeTable

/**
 * The ancestors that decide whether an identifier is a variable, for [Callable.isVariable]. [Bucket.reads] is the type
 * the walk's arm casts the ancestor to, `PsiElement` where it does not cast, so a test can check every shape in a
 * bucket is one its arm accepts.
 */
object VariableWalk {
    enum class Bucket(val reads: Class<*>) {
        /** The identifier is bound here, so the walk ends with `true`. */
        DECLARES(PsiElement::class.java),
        /** Holds expressions without binding anything itself, so ask the parent. */
        TRANSPARENT(PsiElement::class.java),
        /** A call decides by what it calls. Every operation is a call, so this comes after them. */
        CALL(Call::class.java),
        /** Holds expressions, but no declaration reaches through it, so the walk ends with `false`. */
        STOP(PsiElement::class.java),
        /** Cannot hold an expression at all, so also `false`. */
        LEAF(PsiElement::class.java)
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.variable }

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
