package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.scope.Module;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.treeWalkUp;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Variants extends Module {
    /*
     * Public Static Methods
     */

    @Nullable
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        treeWalkUp(
                variants,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );

        return variants.getLookupElementList();
    }

    /*
     * Fields
     */

    private Collection<String> nameCollection = null;
    private Project project = null;
    private List<LookupElement> lookupElementList = null;

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @param match
     * @param aliasedName
     * @param state
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    @Override
    protected boolean executeOnAliasedName(@NotNull PsiNamedElement match, @NotNull final String aliasedName, @NotNull ResolveState state) {
        if (lookupElementList == null) {
            lookupElementList = new ArrayList<LookupElement>();
        }

        lookupElementList.add(
                LookupElementBuilder.createWithSmartPointer(
                        aliasedName,
                        match
                )
        );

        final String unaliasedName = match.getName();

        if (unaliasedName != null) {
            List<String> unaliasedNestedNames = ContainerUtil.findAll(nameCollection(match.getProject()), new Condition<String>() {
                @Override
                public boolean value(String indexedName) {
                    return indexedName.startsWith(unaliasedName) && indexedName.length() > unaliasedName.length();
                }
            });
            List<String> aliasedNestedNames = ContainerUtil.map(unaliasedNestedNames, new Function<String, String>() {
                @Override
                public String fun(String unaliasedNestedName) {
                    return unaliasedNestedName.replaceFirst(unaliasedName, aliasedName);
                }
            });
            List<LookupElement> aliasedNesteNameLookElements = ContainerUtil.map(aliasedNestedNames, new Function<String, LookupElement>() {
                @Override
                public LookupElement fun(String aliasedNestedName) {
                    return LookupElementBuilder.create(aliasedNestedName);
                }
            });
            lookupElementList.addAll(aliasedNesteNameLookElements);
        }

        return true;
    }

    /*
     * Private Instance Methods
     */

    private List<LookupElement> getLookupElementList() {
        return lookupElementList;
    }

    /**
     * Caches {@code StubIndex.getAllKeys(AllName.KEY, project)}
     * @return
     */
    private Collection<String> nameCollection(Project project) {
        if (project != this.project || nameCollection == null) {
            nameCollection = StubIndex.getInstance().getAllKeys(AllName.KEY, project);
        }

        return nameCollection;
    }
}
