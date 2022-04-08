package org.elixir_lang.reference

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.resolver.CallDefinitionClause
import org.elixir_lang.semantic.type.definition.source.Specification

/**
 * @param call `foo(arg1, ...) :: return1` in `@spec foo(arg1, ...) :: return1`
 * @param specification `@spec foo(arg1, ...) ... return1`, so that the tree doesn't have to be walked up
 * again if there is a `when`.
 */
class CallDefinitionClause(call: Call, val specification: Specification) :
    PsiReferenceBase<Call?>(call, TextRange.create(0, call.textLength)), PsiPolyVariantReference {
    /*
     *
     * Constructors
     *
     */
    /*
     *
     * Instance Methods
     *
     */
    /*
     * Public Instance Methods
     */

    override fun getVariants(): Array<Any> = emptyArray()

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     * being resolved is considered incomplete, and the method may return additional
     * invalid results.
     * @return the array of results for resolving the reference.
     */
    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        return ResolveCache
            .getInstance(myElement!!.project)
            .resolveWithCaching(
                this,
                CallDefinitionClause,
                false,
                incompleteCode
            )
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or null if it was not possible to resolve the reference to a valid target.
     */
    override fun resolve(): PsiElement? {
        val resolveResults = multiResolve(false)
        return if (resolveResults.size == 1) resolveResults[0].element else null
    }
}
