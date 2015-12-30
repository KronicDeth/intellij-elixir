package org.elixir_lang.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirMatchedAtNonNumericOperation;
import org.elixir_lang.psi.Quotable;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limhoff on 12/30/15.
 */
public class ModuleAttribute extends PsiPolyVariantReferenceBase<ElixirMatchedAtNonNumericOperation> {
    /*
     * Constructors
     */

    public ModuleAttribute(@NotNull final ElixirMatchedAtNonNumericOperation matchedAtNonNumericOperation) {
        super(matchedAtNonNumericOperation, TextRange.create(0, matchedAtNonNumericOperation.getTextLength()));

    }

    /*
     * Public Instance Methods
     */

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     *                       being resolved is considered incomplete, and the method may return additional
     *                       invalid results.
     * @return the array of results for resolving the reference.
     */
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> resultList = new ArrayList<ResolveResult>();

        resultList.addAll(multiResolveUpFromElement(myElement, incompleteCode));

        return resultList.toArray(new ResolveResult[resultList.size()]);
    }

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    /*
     * Private Instance Methods
     */

    private List<ResolveResult> multiResolveSibling(PsiElement lastSibling, boolean incompleteCode) {
        List<ResolveResult> resultList = new ArrayList<ResolveResult>();

        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof AtUnqualifiedNoParenthesesCall) {
                AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) sibling;
                String moduleAttributeName = atUnqualifiedNoParenthesesCall.moduleAttributeName();
                String value = getValue();

                if (moduleAttributeName.equals(value)) {
                    resultList.add(new PsiElementResolveResult(atUnqualifiedNoParenthesesCall));
                } else if (incompleteCode && moduleAttributeName.startsWith(value)) {
                    resultList.add(new PsiElementResolveResult(atUnqualifiedNoParenthesesCall, false));
                }
            }
        }

        return resultList;
    }

    private List<ResolveResult> multiResolveUpFromElement(@NotNull final PsiElement element, boolean incompleteCode) {
        List<ResolveResult> resultList = new ArrayList<ResolveResult>();
        PsiElement lastSibling = element;

        while (lastSibling != null) {
            resultList.addAll(multiResolveSibling(lastSibling, incompleteCode));

            lastSibling = lastSibling.getParent();
        }

        return resultList;
    }

}
