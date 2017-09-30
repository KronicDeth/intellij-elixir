package org.elixir_lang.reference.resolver.atom.resolvable;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import org.elixir_lang.reference.resolver.atom.Resolvable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.Reference.forEachNavigationElement;

public class Exact extends Resolvable {
    @NotNull
    private final String name;

    public Exact(@NotNull String name) {
        this.name = name;
    }

    @Override
    public ResolveResult[] resolve(@NotNull Project project) {
        List<ResolveResult> resolveResultList = new ArrayList<>();

        forEachNavigationElement(
                project,
                name,
                navigationElement -> {
                    resolveResultList.add(new PsiElementResolveResult(navigationElement));

                    return true;
                }
        );

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }
}
