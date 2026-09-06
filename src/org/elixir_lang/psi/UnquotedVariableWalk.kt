package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.walk.ShapeTable

/**
 * The parents of an unquoted variable that lead to the value it carries, for [Unquote]. Following a declaration out of
 * a container to its match over-approximates: every name destructured from one value claims that value's definitions.
 */
object UnquotedVariableWalk {
    enum class Bucket {
        /** `variable = value` binds the variable, so follow the value. A match is a call, so this comes first. */
        MATCH,
        /** Nothing to follow. Parentheses arguments are `QuotableArguments`, so this comes before `RECURSE`. */
        STOP,
        /** A container a variable can be declared through, which this walk does not enter yet. Answers as `STOP`. */
        UNFOLLOWED,
        /** A wrapper the value passes through, so ask the parent. */
        RECURSE,
        /** Cannot hold an expression at all, so nothing is bound through it. */
        LEAF
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.unquote }

    fun classify(parent: PsiElement): Bucket = classifier.classify(parent)
}
