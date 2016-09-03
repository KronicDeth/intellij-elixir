package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.HashSet;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.scope.Module;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.reference.module.UnaliasedName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.intellij.psi.util.PsiTreeUtil.treeWalkUp;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;

public class Variants extends Module {
    /*
     * Public Static Methods
     */

    @NotNull
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        treeWalkUp(
                variants,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );
        List<LookupElement> lookupElementList = variants.getLookupElementList();

        if (lookupElementList == null) {
            lookupElementList = new ArrayList<LookupElement>();
        }

        variants.addProjectNameElementsTo(lookupElementList, entrance.getProject());

        return lookupElementList;
    }

    /*
     * Fields
     */

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

        String unaliasedName = UnaliasedName.unaliasedName(match);

        if (unaliasedName != null) {
            Project project = match.getProject();

            Collection<String> indexedNameCollection = StubIndex
                    .getInstance()
                    .getAllKeys(AllName.KEY, project);
            List<String> unaliasedNestedNames = ContainerUtil.findAll(
                    indexedNameCollection,
                    ProperStartsWith.properStartsWith(unaliasedName)
            );

            if (unaliasedNestedNames.size() > 0) {
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);

                for (String unaliasedNestedName : unaliasedNestedNames) {
                    Collection<NamedElement> unaliasedNestedNamedElementCollection = StubIndex.getElements(
                            AllName.KEY,
                            unaliasedNestedName,
                            project,
                            scope,
                            NamedElement.class
                    );

                    if (unaliasedNestedNamedElementCollection.size() > 0) {
                        String aliasedNestedName = unaliasedNestedName.replaceFirst(unaliasedName, aliasedName);

                        for (NamedElement unaliasedNestedNamedElement : unaliasedNestedNamedElementCollection) {
                            lookupElementList.add(
                                    LookupElementBuilder.createWithSmartPointer(
                                            aliasedNestedName,
                                            unaliasedNestedNamedElement
                                    )
                            );
                        }
                    }
                }
            }
        }

        return true;
    }

    /*
     * Private Instance Methods
     */

    private void addProjectNameElementsTo(List<LookupElement> lookupElementList, Project project) {
        /* getAllKeys is not the actual keys in the actual project.  They need to be checked.
           See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207930789-StubIndex-persisting-between-test-runs-leading-to-incorrect-completions */
        Collection<String> indexedNameCollection = StubIndex.getInstance().getAllKeys(AllName.KEY, project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        for (String indexedName : indexedNameCollection) {
            Collection<NamedElement> indexedNameNamedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    indexedName,
                    project,
                    scope,
                    NamedElement.class
            );

            for (NamedElement indexedNameNamedElement : indexedNameNamedElementCollection) {
                lookupElementList.add(
                        LookupElementBuilder.createWithSmartPointer(
                                indexedName,
                                indexedNameNamedElement
                        )
                );
            }
        }
    }

    private List<LookupElement> getLookupElementList() {
        return lookupElementList;
    }
}
