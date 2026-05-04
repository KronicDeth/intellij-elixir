package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.util.ProcessingContext
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.ElixirTuple
import org.elixir_lang.psi.ElixirUnmatchedExpression
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.WholeNumber
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.impl.toBigInteger

class MfaTupleReferenceProvider : PsiReferenceProvider() {
    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
        val atom = element as? ElixirAtom ?: return PsiReference.EMPTY_ARRAY

        if (atom.line != null) return PsiReference.EMPTY_ARRAY

        val tupleContext = atom.tupleContext() ?: return PsiReference.EMPTY_ARRAY
        val (tuple, atomWrapper) = tupleContext
        val tupleChildren = tuple.children.filter { it is ElixirAccessExpression || it is ElixirUnmatchedExpression }

        if (tupleChildren.size != 3 || tupleChildren[1] !== atomWrapper) {
            return PsiReference.EMPTY_ARRAY
        }

        // element[0] can be:
        //   - QualifiableAlias (Elixir-style): Enum, MyApp.Worker
        //   - unquoted ElixirAtom (Erlang-style): :math, :lists
        val rawModuleElement = tupleChildren[0].stripAccessExpression()
        val moduleElement: PsiElement = when {
            rawModuleElement is QualifiableAlias -> rawModuleElement
            rawModuleElement is ElixirAtom && rawModuleElement.line == null -> rawModuleElement
            else -> return PsiReference.EMPTY_ARRAY
        }
        val arity = extractArity(tupleChildren[2].stripAccessExpression())

        return arrayOf(MfaFunctionReference(atom, moduleElement, arity))
    }

    /**
     * Extracts the arity from element[2] of the MFA tuple.
     *
     * Returns:
     * - The integer literal value for `{Enum, :map, 2}`
     * - The list element count for `{Stack, :start_link, [arg1, arg2]}`
     * - `-1` for unknown/dynamic third elements (e.g. variables, expressions like `device.id`)
     *
     * A `-1` sentinel causes `MultiResolve` to return name-matched candidates with
     * `isValidResult=false`, enabling navigation and hover docs without arity precision.
     */
    private fun extractArity(element: PsiElement): Int = when (element) {
        is WholeNumber -> element.toBigInteger()?.toInt() ?: -1
        is ElixirList -> element.children.size
        else -> -1
    }

    /**
     * Navigates from an atom up to its containing tuple.
     *
     * The atom's immediate parent is always `ElixirAccessExpression` (because the
     * `accessExpression` BNF rule includes `atom`). The grandparent varies by context:
     * - **Container expressions** (keyword pair values, map values):
     *   `ElixirAtom → ElixirAccessExpression → ElixirTuple`
     * - **General expressions** (function bodies, assignments):
     *   `ElixirAtom → ElixirAccessExpression → ElixirUnmatchedExpression → ElixirTuple`
     *
     * Returns the tuple and the direct child of the tuple that wraps this atom.
     */
    private fun ElixirAtom.tupleContext(): Pair<ElixirTuple, PsiElement>? {
        val accessExpression = parent as? ElixirAccessExpression ?: return null

        // atom → accessExpression → tuple (container expressions)
        // atom → accessExpression → unmatchedExpression → tuple (general expressions)
        return when (val grandparent = accessExpression.parent) {
            is ElixirTuple -> grandparent to accessExpression
            is ElixirUnmatchedExpression -> {
                val tuple = grandparent.parent as? ElixirTuple ?: return null
                tuple to grandparent
            }
            else -> null
        }
    }
}
