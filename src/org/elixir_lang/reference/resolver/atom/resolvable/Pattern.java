package org.elixir_lang.reference.resolver.atom.resolvable;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import org.elixir_lang.reference.resolver.atom.Resolvable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static org.elixir_lang.Reference.indexedNameStream;
import static org.elixir_lang.Reference.namedElementCollection;

public class Pattern extends Resolvable {
    @NotNull
    private final Predicate<String> predicate;

    public Pattern(@NotNull String regex) {
        this(java.util.regex.Pattern.compile(":" + regex));
    }

    public Pattern(@NotNull java.util.regex.Pattern pattern) {
        this(pattern.asPredicate());
    }

    public Pattern(@NotNull Predicate<String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public ResolveResult[] resolve(@NotNull Project project) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        return indexedNameStream(project)
                .filter(predicate)
                .flatMap(name ->
                        namedElementCollection(project, scope, name)
                                .stream()
                                .map(PsiElement::getNavigationElement)
                                .map(navigationElement ->
                                        (ResolveResult) new PsiElementResolveResult(navigationElement, false)
                                )
                ).toArray(ResolveResult[]::new);
    }
}
