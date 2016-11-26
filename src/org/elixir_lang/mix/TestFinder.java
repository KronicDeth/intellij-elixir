package org.elixir_lang.mix;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.StubBased;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.enclosingMacroCall;

public class TestFinder implements com.intellij.testIntegration.TestFinder {
    @Nullable
    @Override
    public Call findSourceElement(@NotNull PsiElement from) {
        Call enclosingMacroCall = enclosingMacroCall(from);
        Call sourceElement = null;

        if (enclosingMacroCall != null && CallDefinitionClause.is(enclosingMacroCall)) {
            sourceElement = CallDefinitionClause.enclosingModularMacroCall(enclosingMacroCall);
        }

        return sourceElement;
    }

    @NotNull
    @Override
    public Collection<PsiElement> findTestsForClass(@NotNull PsiElement element) {
        Call sourceElement = findSourceElement(element);
        Collection<PsiElement> testCollection = new ArrayList<PsiElement>();

        if (sourceElement != null && sourceElement instanceof StubBased){
            StubBased sourceStubBased = (StubBased) sourceElement;
            Set<String> canonicalNameSet = sourceStubBased.canonicalNameSet();

            if (!canonicalNameSet.isEmpty()) {
                Project project = element.getProject();
                GlobalSearchScope scope = GlobalSearchScope.projectScope(project);


                for (String canonicalName : canonicalNameSet) {
                    String testCanonicalName = canonicalName + "Test";

                    Collection<NamedElement> testElements = StubIndex.getElements(
                            AllName.KEY,
                            testCanonicalName,
                            project,
                            scope,
                            NamedElement.class
                    );

                    for (NamedElement testElement : testElements) {
                        if (testElement instanceof Call) {
                            Call testCall = (Call) testElement;

                            if (Module.is(testCall)) {
                                testCollection.add(testCall);
                            }
                        }
                    }
                }
            }
        }

        return testCollection;
    }

    @NotNull
    @Override
    public Collection<PsiElement> findClassesForTest(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public boolean isTest(@NotNull PsiElement element) {
//        PsiFile containingFile = findSourceElement(element);
//
//        if (!(containingFile instanceof ErlangFile)) return false;
//        return ErlangPsiImplUtil.isEunitTestFile((ErlangFile) containingFile);
        Call sourceElement = findSourceElement(element);
        boolean isTest = false;

        if (sourceElement != null && sourceElement instanceof StubBased) {
            StubBased sourceStubBased = (StubBased) sourceElement;
            Set<String> canonicalNameSet = sourceStubBased.canonicalNameSet();

            if (!canonicalNameSet.isEmpty()) {
                for (String canonicalName : canonicalNameSet) {
                    if (canonicalName.endsWith("Test")) {
                        isTest = true;
                        break;
                    }
                }
            }
        }

        return isTest;
    }
}
