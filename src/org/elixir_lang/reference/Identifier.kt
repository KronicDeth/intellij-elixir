package org.elixir_lang.reference

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.resolve.ResolveCache
import org.elixir_lang.psi.ElixirIdentifier

class Identifier(identifier: ElixirIdentifier) :
        PsiReferenceBase<ElixirIdentifier>(identifier, TextRange.create(0, identifier.textLength)),
        PsiPolyVariantReference {
    override fun getVariants(): Array<LookupElement> =
            (org.elixir_lang.psi.scope.variable.Variants.lookupElementList(myElement) +
                    org.elixir_lang.psi.scope.call_definition_clause.Variants.lookupElementList(myElement))
                    .toTypedArray()

    override fun resolve(): PsiElement? = multiResolve(false).singleOrNull()?.element

    override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> =
            ResolveCache
                    .getInstance(myElement.project)
                    .resolveWithCaching(this, org.elixir_lang.reference.resolver.Identifier, false, incompleteCode)
}
