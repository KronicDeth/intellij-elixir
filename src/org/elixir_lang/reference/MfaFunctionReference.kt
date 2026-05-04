package org.elixir_lang.reference

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.impl.maybeModularNameToModulars
import org.elixir_lang.psi.scope.call_definition_clause.MultiResolve as CallDefinitionClauseMultiResolve
import org.elixir_lang.reference.Resolver as ReferenceResolver

class MfaFunctionReference(
    atom: ElixirAtom,
    /**
     * The module element from the MFA tuple - either:
     * - a [org.elixir_lang.psi.QualifiableAlias] for Elixir-style modules (`Enum`, `MyApp.Worker`)
     * - an [ElixirAtom] (unquoted) for Erlang-style modules (`:math`, `:lists`)
     */
    private val moduleElement: PsiElement,
    val arity: Int
) : PsiReferenceBase<ElixirAtom>(atom, contentTextRange(atom)), PsiPolyVariantReference {
    private val functionName: String?
        get() = myElement.node.lastChildNode?.text

    override fun getVariants(): Array<Any> = emptyArray()

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
        ApplicationManager.getApplication().assertReadAccessAllowed()

        return ResolveCache
            .getInstance(myElement.project)
            .resolveWithCaching(this, Resolver, false, incompleteCode)
    }

    override fun resolve(): PsiElement? =
        ReferenceResolver.preferred(myElement, false, multiResolve(false).toList())
            .let { ReferenceResolver.preferSourceElement(it) }
            .firstOrNull()
            ?.element

    override fun isSoft(): Boolean = true

    private object Resolver : ResolveCache.PolyVariantResolver<MfaFunctionReference> {
        override fun resolve(reference: MfaFunctionReference, incompleteCode: Boolean): Array<ResolveResult> {
            val name = reference.functionName ?: return ResolveResult.EMPTY_ARRAY
            // Module resolution is always a definite lookup - use incompleteCode=false so the
            // resolver stops at the first valid module rather than exhaustively scanning all
            // partial matches (completion mode).  Only the function-within-module walk below
            // should see the outer incompleteCode.  This matches the established pattern in
            // qualifiedToModulars() (QualifiedImpl.kt) which also hard-codes false here.
            val modulars = reference.moduleElement.maybeModularNameToModulars(
                maxScope = reference.myElement.containingFile,
                useCall = null,
                incompleteCode = false
            )

            if (modulars.isEmpty()) return ResolveResult.EMPTY_ARRAY

            return modulars
                .flatMap { modular ->
                    CallDefinitionClauseMultiResolve.resolveResults(name, reference.arity, incompleteCode, modular)
                }
                .map { visitedResult ->
                    PsiElementResolveResult(visitedResult.element, visitedResult.isValidResult)
                }
                .toTypedArray()
        }
    }
}

private fun contentTextRange(atom: ElixirAtom): TextRange {
    val atomNode = atom.node
    val lastChildNode = atomNode.lastChildNode ?: return TextRange(0, atom.textLength)
    val start = lastChildNode.startOffset - atomNode.startOffset
    val end = start + lastChildNode.textLength

    return TextRange(start, end)
}
