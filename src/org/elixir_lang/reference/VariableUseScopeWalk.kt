package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.walk.ShapeTable

/** The ancestors that decide a variable's use scope, for [Callable.variableUseScope]. */
object VariableUseScopeWalk {
    enum class Bucket {
        /** Holds the variable without scoping it, so ask the parent. */
        PARENT,
        /** The scope is this ancestor itself. */
        SELF,
        /** Scopes each statement in it to itself and what follows: a binding is visible from where it is made. */
        FOLLOWING,
        /** A match scopes by where it sits. Every operation is a call, so this comes before `CALL`. */
        MATCH,
        /** A call scopes by what it calls. */
        CALL,
        /** No variable can be declared inside these, so the use has no scope. */
        EMPTY,
        /** Cannot hold an expression at all, so also no scope. */
        LEAF
    }

    val classifier = ShapeTable.column(Bucket.EMPTY) { it.useScope }

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
