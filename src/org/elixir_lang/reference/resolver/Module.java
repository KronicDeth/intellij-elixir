package org.elixir_lang.reference.resolver;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.scope.module.MultiResolve;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.Reference.forEachNavigationElement;
import static org.elixir_lang.reference.module.ResolvableName.resolvableName;

public class Module implements ResolveCache.PolyVariantResolver<org.elixir_lang.reference.Module> {
    public static final Module INSTANCE = new Module();

    private List<ResolveResult> multiResolveProject(@NotNull Project project,
                                                    @NotNull String name) {
        List<ResolveResult> results = new ArrayList<>();

        forEachNavigationElement(
                project,
                name,
                navigationElement -> {
                    results.add(new PsiElementResolveResult(navigationElement));

                    return true;
                }
        );

        return results;
    }

    @NotNull
    @Override
    public ResolveResult[] resolve(@NotNull org.elixir_lang.reference.Module module, boolean incompleteCode) {
        List<ResolveResult> resolveResultList = null;
        QualifiableAlias element = module.getElement();
        final String name = resolvableName(element);

        if (name != null) {
            resolveResultList = MultiResolve.resolveResultList(name, incompleteCode, element, module.maxScope);

            if (resolveResultList == null || resolveResultList.isEmpty()) {
                resolveResultList = multiResolveProject(
                        element.getProject(),
                        name
                );
            }
        }

        ResolveResult[] resolveResults;

        if (resolveResultList == null) {
            resolveResults = ResolveResult.EMPTY_ARRAY;
        } else {
            resolveResults = resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
        }

        return resolveResults;
    }
}
