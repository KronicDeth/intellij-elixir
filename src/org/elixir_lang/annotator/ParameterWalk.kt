package org.elixir_lang.annotator

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAnonymousFunction
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.walk.ShapeTable

/**
 * The ancestors that decide whether an identifier is a parameter, for [Parameter.putParameterized]. [Bucket.reads] is
 * the type the walk's arm casts the ancestor to, `PsiElement` where it does not cast, so a test can check every shape
 * in a bucket is one its arm accepts.
 */
object ParameterWalk {
    enum class Bucket(val reads: Class<*>) {
        /** Holds the identifier without deciding, so ask the parent. */
        RECURSE(PsiElement::class.java),
        /** A call decides by whether it is a definition head. Every operation is a call, so this comes after them. */
        CALL(Call::class.java),
        /** An anonymous function's head makes the identifier a variable-typed parameter. */
        ANONYMOUS_FUNCTION(ElixirAnonymousFunction::class.java),
        /** Holds expressions, but none can be a parameter. */
        STOP(PsiElement::class.java),
        /** Cannot hold an expression at all, so also no parameter. */
        LEAF(PsiElement::class.java)
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.parameter }

    @JvmStatic
    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
