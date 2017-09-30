package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.scope.module.Variants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    /*
     * Fields
     */

    @NotNull
    public final PsiElement maxScope;

    /*
     * Constructors
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias, @NotNull PsiElement maxScope) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
        this.maxScope = maxScope;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        return ResolveCache
                .getInstance(this.myElement.getProject())
                .resolveWithCaching(
                        this,
                        org.elixir_lang.reference.resolver.Module.INSTANCE,
                        false,
                        incompleteCode
                );
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> lookupElementList = Variants.lookupElementList(myElement);

        return lookupElementList.toArray(new Object[lookupElementList.size()]);
    }
}
