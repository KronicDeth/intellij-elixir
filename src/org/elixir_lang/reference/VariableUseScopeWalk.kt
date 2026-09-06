package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.walk.ShapeTable

/**
 * The ancestors that decide a variable's use scope, for [Callable.variableUseScope]. [Bucket.reads] is the type the
 * walk's arm casts the ancestor to, `PsiElement` where it does not cast, so a test can check every shape in a bucket
 * is one its arm accepts.
 */
object VariableUseScopeWalk {
    enum class Bucket(val reads: Class<*>) {
        /** Holds the variable without scoping it, so ask the parent. */
        PARENT(PsiElement::class.java),
        /** The scope is this ancestor itself. */
        SELF(PsiElement::class.java),
        /** Scopes each statement in it to itself and what follows: a binding is visible from where it is made. */
        FOLLOWING(PsiElement::class.java),
        /** A match scopes by where it sits. Every operation is a call, so this comes before `CALL`. */
        MATCH(Match::class.java),
        /** A call scopes by what it calls. */
        CALL(Call::class.java),
        /** No variable can be declared inside these, so the use has no scope. */
        EMPTY(PsiElement::class.java),
        /** Cannot hold an expression at all, so also no scope. */
        LEAF(PsiElement::class.java)
    }

    val classifier = ShapeTable.column(Bucket.EMPTY) { it.useScope }

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
