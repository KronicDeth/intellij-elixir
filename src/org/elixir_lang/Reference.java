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
