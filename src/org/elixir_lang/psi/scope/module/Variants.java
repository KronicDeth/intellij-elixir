package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.scope.Module;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        variants.addProjectNamesTo(lookupElementList, entrance.getProject());

        return lookupElementList;
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
            List<String> unaliasedNestedNames = ContainerUtil.findAll(
                    nameCollection(match.getProject()),
                    ProperStartsWith.properStartsWith(unaliasedName)
            );
            List<String> aliasedNestedNames = ContainerUtil.map(
                    unaliasedNestedNames,
                    new ReplaceFirst(unaliasedName, aliasedName)
            );
            List<LookupElement> aliasedNesteNameLookElements = ContainerUtil.map(
                    aliasedNestedNames,
                    CreateLookupElement.INSTANCE
            );
            lookupElementList.addAll(aliasedNesteNameLookElements);
        }

        return true;
    }

    /*
     * Private Instance Methods
     */

    private void addProjectNamesTo(List<LookupElement> lookupElementList, Project project) {
        Collection<String> projectNameCollection = nameCollection(project);

        lookupElementList.addAll(
                ContainerUtil.map(projectNameCollection, CreateLookupElement.INSTANCE)
        );
    }

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
