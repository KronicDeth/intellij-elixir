package org.elixir_lang.psi.scope.variable;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashMap;
import org.elixir_lang.psi.scope.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Variants extends Variable {
    /*
     * Static Methods
     */

    @Nullable
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        PsiTreeUtil.treeWalkUp(
                variants,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );

        Collection<LookupElement> lookupElementCollection = variants.getLookupElementCollection();
        List<LookupElement> lookupElementList;

        if (lookupElementCollection != null) {
            lookupElementList = new ArrayList<LookupElement>(lookupElementCollection);
        } else {
            lookupElementList = Collections.emptyList();
        }

        return lookupElementList;
    }

    /*
     * Fields
     */

    @Nullable
    private Map<PsiElement, LookupElement> lookupElementByElement = null;

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @return {@code false}, as all variables should be found.  Prefix filtering will be done later by IDEA core.
     */
    @Override
    protected boolean executeOnVariable(@NotNull PsiNamedElement match, @NotNull ResolveState state) {
        PsiReference reference = match.getReference();
        String name = null;
        PsiElement declaration = match;

        if (reference != null) {
            PsiElement resolved = reference.resolve();

            if (resolved != null) {
                declaration = resolved;

                if (resolved instanceof PsiNamedElement) {
                    PsiNamedElement namedResolved = (PsiNamedElement) resolved;

                    name = namedResolved.getName();
                }
            }
        }

        if (name == null) {
            name = match.getName();
        }

        if (name != null) {
            if (lookupElementByElement == null) {
                lookupElementByElement = new THashMap<PsiElement, LookupElement>();
            }

            if (!lookupElementByElement.containsKey(declaration)) {
                final String finalName = name;

                lookupElementByElement.put(
                        declaration,
                        LookupElementBuilder.createWithSmartPointer(
                                name,
                                declaration
                        ).withRenderer(new org.elixir_lang.code_insight.lookup.element_renderer.Variable(finalName))
                );
            }
        }

        return true;
    }

    /*
     * Private Instance Methods
     */

    @Nullable
    private Collection<LookupElement> getLookupElementCollection() {
        Collection<LookupElement> lookupElementCollection = null;

        if (lookupElementByElement != null) {
            lookupElementCollection = lookupElementByElement.values();
        }

        return lookupElementCollection;
    }

}
