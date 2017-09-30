package org.elixir_lang;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.Function;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class Reference {
    @NotNull
    public static Collection<String> indexedNameCollection(@NotNull Project project) {
        return StubIndex.getInstance().getAllKeys(AllName.KEY, project);
    }

    @NotNull
    public static Stream<String> indexedNameStream(@NotNull Project project) {
        return indexedNameCollection(project).stream();
    }

    /**
     * Iterates over each navigation element for the PsiElements with {@code name} in {@code project}.
     *
     * @param project  Whose index to search for {@code name}
     * @param name     Name to search for in {@code project} StubIndex
     * @param function return {@code false} to stop iteration early
     * @return {@code true} if all calls to {@code function} returned {@code true}
     */
    public static boolean forEachNavigationElement(@NotNull Project project,
                                                   @NotNull String name,
                                                   @NotNull Function<PsiElement, Boolean> function) {
        Collection<NamedElement> namedElementCollection = namedElementCollection(project, name);

        return forEachNavigationElement(namedElementCollection, function);
    }

    /**
     * Iterates over each navigation element for the PsiElements in {@code psiElementCollection}.
     *
     * @param psiElementCollection Collection of PsiElements that aren't guaranteed to be navigation elements, such as
     *                             the binary elements in {@code .beam} files.
     * @param function             Return {@code false} to stop processing and abandon enumeration early
     * @return {@code true} if all calls to {@code function} returned {@code true}
     */
    private static boolean forEachNavigationElement(@NotNull Collection<? extends PsiElement> psiElementCollection,
                                                    @NotNull Function<PsiElement, Boolean> function) {
        boolean keepProcessing = true;

        for (PsiElement psiElement : psiElementCollection) {
            /* The psiElement may be a ModuleImpl from a .beam.  Using #getNaviationElement() ensures a source
               (either true source or decompiled) is used. */
            keepProcessing = function.fun(psiElement.getNavigationElement());

            if (!keepProcessing) {
                break;
            }
        }

        return keepProcessing;
    }

    public static Collection<NamedElement> namedElementCollection(@NotNull Project project, @NotNull String name) {
        return namedElementCollection(project, GlobalSearchScope.allScope(project), name);
    }

    public static Collection<NamedElement> namedElementCollection(@NotNull Project project,
                                                                  @NotNull GlobalSearchScope scope,
                                                                  @NotNull String name) {
        Collection<NamedElement> namedElementCollection;

        if (DumbService.isDumb(project)) {
            namedElementCollection = Collections.emptyList();
        } else {
            namedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    name,
                    project,
                    scope,
                    NamedElement.class
            );
        }

        return namedElementCollection;

    }
}
