package org.elixir_lang.annotator

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.walk.ShapeTable

/** The ancestors that decide whether an identifier is a parameter, for [Parameter.putParameterized]. */
object ParameterWalk {
    enum class Bucket {
        /** Holds the identifier without deciding, so ask the parent. */
        RECURSE,
        /** A call decides by whether it is a definition head. Every operation is a call, so this comes after them. */
        CALL,
        /** An anonymous function's head makes the identifier a variable-typed parameter. */
        ANONYMOUS_FUNCTION,
        /** Holds expressions, but none can be a parameter. */
        STOP,
        /** Cannot hold an expression at all, so also no parameter. */
        LEAF
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.parameter }

    @JvmStatic
    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
