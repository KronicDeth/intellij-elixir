package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.walk.ShapeTable

/** The ancestors that lead from an element to the type-spec attribute it is written in, for [ancestorTypeSpec]. */
object TypeAscent {
    enum class Bucket {
        /** A module attribute: the spec itself when its name is a type-spec name, otherwise nothing. */
        SPEC,
        /** A shape a type is written through, so ask the parent. */
        PARENT,
        /** A shape no type specification is written in, so there is none above. */
        NONE,
        /** Cannot hold an expression at all, so also none. */
        LEAF
    }

    val classifier = ShapeTable.column(Bucket.NONE) { it.typeAscent }

    fun classify(element: PsiElement): Bucket = classifier.classify(element)
}
