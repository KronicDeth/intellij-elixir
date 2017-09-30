package org.elixir_lang.psi.scope.module;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.operation.Normalized;
import org.elixir_lang.psi.scope.Module;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.reference.module.UnaliasedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.intellij.psi.util.PsiTreeUtil.treeWalkUp;
import static org.elixir_lang.Module.concat;
import static org.elixir_lang.Module.split;
import static org.elixir_lang.Reference.indexedNameCollection;
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

        variants.addProjectNameElementsTo(lookupElementList, entrance);

        return lookupElementList;
    }

    /*
     * Private Static Methods
     */

    /**
     * Filters to only those names that work as Alias, that is those that start with a capital letter
     */
    @NotNull
    private static Collection<String> filterIndexedNameCollection(@NotNull Collection<String> indexedNameCollection) {
        return ContainerUtil.filter(
                indexedNameCollection,
                new Condition<String>() {
                    @Override
                    public boolean value(String indexedName) {
                        boolean value = false;

                        if (indexedName != null) {
                            value = Character.isUpperCase(indexedName.codePointAt(0));
                        }

                        return value;
                    }
                }
        );
    }

    @NotNull
    private static Collection<String> filterIndexedNameCollection(@NotNull Collection<String> indexedNameCollection,
                                                                  @Nullable final String prefix) {
        Collection<String> filteredIndexNameCollection = indexedNameCollection;

        if (prefix != null) {
            filteredIndexNameCollection = ContainerUtil.filter(
                    indexedNameCollection,
                    new Condition<String>() {
                        @Override
                        public boolean value(@Nullable String indexedName) {
                            boolean value = false;

                            if (indexedName != null) {
                                value = indexedName.startsWith(prefix);
                            }

                            return value;
                        }
                    }
            );
        }

        return filteredIndexNameCollection;
    }

    @Nullable
    private static String indexedNamePrefix(@Nullable ElixirMultipleAliases multipleAliases) {
        String prefix = null;

        if (multipleAliases != null) {
            QualifiedMultipleAliases parent = (QualifiedMultipleAliases) multipleAliases.getParent();
            PsiElement[] children = parent.getChildren();
            int operatorIndex = Normalized.operatorIndex(children);
            Quotable qualifierQuotable = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(
                    children,
                    operatorIndex
            );

            prefix = qualifierToIndexNamePrefix(qualifierQuotable);
        }

        return prefix;
    }

    @NotNull
    private static String lookupName(@NotNull String indexedName, @Nullable String prefix) {
        String lookupName = indexedName;

        if (prefix != null) {
            if (indexedName.startsWith(prefix)) {
                lookupName = indexedName.substring(prefix.length());
            }
        }

        return lookupName;
    }

    @Nullable
    private static String qualifierToIndexNamePrefix(@NotNull ElixirAccessExpression qualifier) {
        PsiElement[] children = qualifier.getChildren();
        String prefix = null;

        if (children.length == 1) {
            prefix = qualifierToIndexNamePrefix(children[0]);
        }

        return prefix;
    }

    @Nullable
    private static String qualifierToIndexNamePrefix(@NotNull PsiElement qualifier) {
        String prefix = null;

        if (qualifier instanceof ElixirAccessExpression) {
            prefix = qualifierToIndexNamePrefix((ElixirAccessExpression) qualifier);
        } else if (qualifier instanceof QualifiableAlias) {
            prefix = qualifierToIndexNamePrefix((QualifiableAlias) qualifier);
        }

        return prefix;
    }


    @Nullable
    private static String qualifierToIndexNamePrefix(@NotNull QualifiableAlias qualifier) {
        String qualifierFullyQualifiedName = qualifier.fullyQualifiedName();
        String prefix = null;

        if (qualifierFullyQualifiedName != null) {
            prefix = qualifierFullyQualifiedName + ".";
        }

        return prefix;
    }

    /*
     * Fields
     */

    private ElixirMultipleAliases multipleAliases = null;
    private List<LookupElement> lookupElementList = null;

    /*
     * Public Instance Methods
     */

    @Override
    public boolean execute(@NotNull PsiElement match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (match instanceof ElixirMultipleAliases) {
            keepProcessing = execute((ElixirMultipleAliases) match, state);
        } else if (match instanceof Named) {
            keepProcessing = execute((Named) match, state);
        }

        return keepProcessing;
    }

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

            Collection<String> indexedNameCollection = indexedNameCollection(project);
            List<String> unaliasedNestedNames = ContainerUtil.findAll(
                    indexedNameCollection,
                    new org.elixir_lang.Module.IsNestedUnder(unaliasedName)
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
                        List<String> unaliasedNestedNamePartList = split(unaliasedNestedName);
                        List<String> unaliasedNamePartList = split(unaliasedName);
                        List<String> aliasedNamePartList = split(aliasedName);
                        List<String> aliasedNestedNamePartList = new ArrayList<String>();

                        aliasedNestedNamePartList.addAll(aliasedNamePartList);

                        for (int i = unaliasedNamePartList.size(); i < unaliasedNestedNamePartList.size(); i++) {
                            aliasedNestedNamePartList.add(unaliasedNestedNamePartList.get(i));
                        }

                        String aliasedNestedName = concat(aliasedNestedNamePartList);

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

    private void addProjectNameElementsTo(List<LookupElement> lookupElementList, PsiElement entrance) {
        Project project = entrance.getProject();
        /* getAllKeys is not the actual keys in the actual project.  They need to be checked.
           See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207930789-StubIndex-persisting-between-test-runs-leading-to-incorrect-completions */
        Collection<String> indexedNameCollection = StubIndex.getInstance().getAllKeys(AllName.KEY, project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        Collection<String> aliasNameCollection = filterIndexedNameCollection(indexedNameCollection);

        String prefix = indexedNamePrefix(multipleAliases);
        Collection<String> prefixedNameCollection = filterIndexedNameCollection(
                aliasNameCollection,
                prefix
        );

        for (String prefixedName : prefixedNameCollection) {
            Collection<NamedElement> prefixedNameNamedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    prefixedName,
                    project,
                    scope,
                    NamedElement.class
            );

            String lookupName = lookupName(prefixedName, prefix);

            for (NamedElement prefixedNameNamedElement : prefixedNameNamedElementCollection) {
                /* Generalizes over whether the prefixedNameNamedElement is a source element or a compiled element as
                   the navigation element is defined to be always be a source element */
                PsiElement navigationElement = prefixedNameNamedElement.getNavigationElement();

                lookupElementList.add(
                        LookupElementBuilder.createWithSmartPointer(
                                lookupName,
                                navigationElement
                        )
                );
            }
        }
    }

    private boolean execute(@NotNull ElixirMultipleAliases match,
                            @NotNull @SuppressWarnings("unused") ResolveState state) {
        multipleAliases = match;

        return false;
    }

    private List<LookupElement> getLookupElementList() {
        return lookupElementList;
    }
}
