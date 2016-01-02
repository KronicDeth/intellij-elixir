package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.AtNonNumericOperation;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElementFactory;
import org.elixir_lang.psi.ElixirAtIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by limhoff on 12/30/15.
 */
public class ModuleAttribute extends PsiPolyVariantReferenceBase<PsiElement> {
    /*
     * Constructors
     */

    public ModuleAttribute(@NotNull final PsiElement psiElement) {
        super(psiElement, TextRange.create(0, psiElement.getTextLength()));
    }

    /*
     * Public Instance Methods
     */

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
        List<LookupElement> lookupElementList = getVariantsUpFromElement(myElement);

        return lookupElementList.toArray(new Object[lookupElementList.size()]);
    }

    @Override
    public PsiElement handleElementRename(String newModuleAttributeName) throws IncorrectOperationException {
        PsiElement renamedElement = myElement;

        if (myElement instanceof AtNonNumericOperation) {
            PsiElement moduleAttributeUsage = ElementFactory.createModuleAttributeUsage(
                    myElement.getProject(),
                    newModuleAttributeName
            );
            renamedElement = myElement.replace(moduleAttributeUsage);
        } else if (myElement instanceof ElixirAtIdentifier) {
            // do nothing; handled by setName on ElixirAtUnqualifiedNoParenthesesCall
        } else {
            throw new NotImplementedException(
                    "Renaming module attribute reference on " + myElement.getClass().getCanonicalName() +
                            " PsiElements is not implemented yet.  Please open an issue " +
                            "(https://github.com/KronicDeth/intellij-elixir/issues/new) with the class name and the " +
                            "sample text:\n" + myElement.getText());
        }

        return renamedElement;
    }

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

    private List<LookupElement> getVariantsSibling(PsiElement lastSibling) {
        List<LookupElement> lookupElementList = new ArrayList<LookupElement>();

        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof AtUnqualifiedNoParenthesesCall) {
                AtUnqualifiedNoParenthesesCall  atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) sibling;

                lookupElementList.add(
                        LookupElementBuilder.createWithSmartPointer(
                                atUnqualifiedNoParenthesesCall.moduleAttributeName(),
                                atUnqualifiedNoParenthesesCall
                        )
                );
            }
        }

        return lookupElementList;
    }

    private List<LookupElement> getVariantsUpFromElement(PsiElement element) {
        List<LookupElement> lookupElementList = new ArrayList<LookupElement>();
        PsiElement lastSibling = element;

        while (lastSibling != null) {
            lookupElementList.addAll(getVariantsSibling(lastSibling));

            lastSibling = lastSibling.getParent();
        }

        return lookupElementList;
    }
}
