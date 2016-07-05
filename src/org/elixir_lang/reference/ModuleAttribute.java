package org.elixir_lang.reference;

import com.google.common.collect.Sets;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.elixir_lang.psi.call.name.Function.DEFIMPL;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.structure_view.element.modular.Implementation.protocolNameElement;

/**
 * Created by limhoff on 12/30/15.
 */
public class ModuleAttribute extends PsiPolyVariantReferenceBase<PsiElement> {
    /*
     * CONSTANTS
     */

    private static final String BEHAVIOUR_NAME = "behaviour";
    private static final Set<String> CALLBACK_NAME_SET = Sets.newHashSet("callback", "macrocallback");
    private static final Set<String> DOCUMENTATION_NAME_SET = Sets.newHashSet("doc", "moduledoc", "typedoc");
    private static final Set<String> NON_REFERENCING_NAME_SET;
    private static final String SPECIFICATION_NAME = "spec";
    private static final Set<String> TYPE_NAME_SET = Sets.newHashSet("opaque", "type", "typep");

    static {
        NON_REFERENCING_NAME_SET = new java.util.HashSet<String>();

        NON_REFERENCING_NAME_SET.add(BEHAVIOUR_NAME);
        NON_REFERENCING_NAME_SET.addAll(CALLBACK_NAME_SET);
        NON_REFERENCING_NAME_SET.addAll(DOCUMENTATION_NAME_SET);
        NON_REFERENCING_NAME_SET.add(SPECIFICATION_NAME);
        NON_REFERENCING_NAME_SET.addAll(TYPE_NAME_SET);
    }

    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    /**
     * Whether the module attribute is used to declare function or macro callbacks for behaviours
     *
     * @return {@code true} if {@code "@callback"} or {@code "@macrocallback"}; otherwise, {@code false}.
     */
    public static boolean isCallbackName(@NotNull String name) {
        return CALLBACK_NAME_SET.contains(name);
    }

    /**
     * Whether the module attribute is used to declare function, module, or type documentation
     *
     * @return {@code true} if {@code "doc"}, {@code "moduledoc"}, or {@code "typedoc"}; otherwise, {@code false}.
     */
    public static boolean isDocumentationName(@NotNull String name) {
        return DOCUMENTATION_NAME_SET.contains(name);
    }

    /**
     * All the predefined module attributes that aren't used to define constants, but for defining behaviors, callback,
     * documents, or types.
     *
     * @param moduleAttribute the module attribute
     * @return {@code true} if the module attribute should have a {@code null} reference because it is used for
     *   library control instead of constant definition; otherwise, {@code false}.
     */
    public static boolean isNonReferencing(AtNonNumericOperation moduleAttribute) {
        String text = moduleAttribute.getText();
        String name = text.substring(1);

        return isNonReferencingName(name);
    }

    /**
     * All the predefined module attributes that aren't used to define constants, but for defining behaviors, callback,
     * documents, or types.
     *
     * @param moduleAttribute the module attribute
     * @return {@code true} if the module attribute should have a {@code null} reference because it is used for
     *   library control instead of constant definition; otherwise, {@code false}.
     */
    public static boolean isNonReferencing(ElixirAtIdentifier moduleAttribute) {
        String text = moduleAttribute.getText();
        String name = text.substring(1);

        return isNonReferencingName(name);
    }

    /**
     * Whether the module attribute is used to declare the specification for a function or macro.
     *
     * @return {@code true} if {@code "spec"}; otherwise, {@code false}
     */
    public static boolean isSpecificationName(@NotNull String name) {
        return SPECIFICATION_NAME.equals(name);
    }

    /**
     * Whether the module attribute is used to declare opaque, transparent, or private types.
     *
     * @return {@code true} if {@code "opaque"}, {@code "type"}, or {@code "typep"; otherwise, {@code false}.
     */
    public static boolean isTypeName(@NotNull String name) {
        return TYPE_NAME_SET.contains(name);
    }

    /*
     * Private Static Methods
     */

    private static boolean isNonReferencingName(@NotNull String name) {
        return NON_REFERENCING_NAME_SET.contains(name);
    }

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
        boolean isNonReferencing = false;

        if (myElement instanceof AtNonNumericOperation) {
            isNonReferencing = isNonReferencing((AtNonNumericOperation) myElement);
        } else if (myElement instanceof ElixirAtIdentifier) {
            isNonReferencing = isNonReferencing((ElixirAtIdentifier) myElement);
        }

        if (!isNonReferencing) {
            String value = getValue();

            if (value.equals("@protocol")) {
                resultList.addAll(
                        org.elixir_lang.psi.scope.module_attribute.Implementation.resolveResultList(incompleteCode, myElement)
                );
            } else {
                resultList.addAll(multiResolveUpFromElement(myElement, incompleteCode));
            }
        }

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
                String moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(atUnqualifiedNoParenthesesCall);
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
                                ElixirPsiImplUtil.moduleAttributeName(atUnqualifiedNoParenthesesCall),
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
