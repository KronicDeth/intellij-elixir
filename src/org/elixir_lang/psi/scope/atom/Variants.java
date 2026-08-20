package org.elixir_lang.psi.scope.atom;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.containers.ContainerUtil;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.scope.Atom;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Variants extends Atom {
    @NotNull
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        return new Variants().projectLookupElementStream(entrance);
    }

    private List<LookupElement> projectLookupElementStream(@NotNull PsiElement entrance) {
        Project project = entrance.getProject();
        /* getAllKeys is not the actual keys in the actual project.  They need to be checked.
           See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207930789-StubIndex-persisting-between-test-runs-leading-to-incorrect-completions */
        Collection<String> indexedNameCollection = StubIndex.getInstance().getAllKeys(AllName.KEY, project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        Collection<String> atomNameCollection = atomNameCollection(indexedNameCollection);
        String prefix = prefix(entrance);
        Collection<String> prefixedNameCollection = prefixedNameCollection(atomNameCollection, prefix);
        List<LookupElement> lookupElementList = new ArrayList<>();

        for (String atomName : prefixedNameCollection) {
            Collection<NamedElement> atomNamedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    atomName,
                    project,
                    scope,
                    NamedElement.class
            );

            for (NamedElement atomNamedElement : atomNamedElementCollection) {
                PsiElement navigationElement = atomNamedElement.getNavigationElement();
                lookupElementList.add(
                        LookupElementBuilder.createWithSmartPointer(atomName, navigationElement)
                );
            }
        }

        return lookupElementList;
    }

    @Contract(pure = true)
    @NotNull
    private Collection<String> prefixedNameCollection(Collection<String> atomNameCollection, String prefix) {
        return ContainerUtil.filter(atomNameCollection, atomName -> atomName.startsWith(prefix));
    }

    @Contract(pure = true)
    @NotNull
    private static String prefix(PsiElement atom) {
        return atom.getText().replace("IntellijIdeaRulezzz", "");
    }

    @Contract(pure = true)
    @NotNull
    private static Collection<String> atomNameCollection(@NotNull Collection<String> indexedNameCollection) {
        return ContainerUtil.filter(indexedNameCollection, Variants::isAtomName);
    }

    @Contract(value = "null -> false", pure = true)
    private static boolean isAtomName(@Nullable String indexedName) {
        return indexedName != null && indexedName.startsWith(":");
    }
}
