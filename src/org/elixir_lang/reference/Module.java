package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.elixir_lang.beam.Beam;
import org.elixir_lang.beam.chunk.Atoms;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.scope.module.MultiResolve;
import org.elixir_lang.psi.scope.module.Variants;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elixir_lang.psi.call.name.Module.stripElixirPrefix;
import static org.elixir_lang.reference.module.ResolvableName.resolvableName;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    @NotNull
    private PsiElement maxScope;

    /*
     * Constructors
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias, @NotNull PsiElement maxScope) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
        this.maxScope = maxScope;
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> resolveResultList = null;
        final String name = resolvableName(myElement);

        if (name != null) {
            resolveResultList = MultiResolve.resolveResultList(name, incompleteCode, myElement, maxScope);

            if (resolveResultList == null || resolveResultList.isEmpty()) {
                resolveResultList = multiResolveProject(
                        myElement.getProject(),
                        name
                );
            }

            if (resolveResultList == null || resolveResultList.isEmpty()) {
                resolveResultList = multiResolveSDK(
                        myElement.getProject(),
                        name
                );
            }
        }

        ResolveResult[] resolveResults;

        if (resolveResultList == null) {
            resolveResults = new ResolveResult[0];
        } else {
            resolveResults = resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
        }

        return resolveResults;
    }

    /*
     * Private Instance Methods
     */

    @NotNull
    private List<ResolveResult> multiResolveBeams(@NotNull final PsiManager psiManager,
                                                  @NotNull VirtualFile parentDirectory,
                                                  @NotNull final String name) {
        final List<ResolveResult> results = new ArrayList<ResolveResult>();

        VfsUtilCore.visitChildrenRecursively(
                parentDirectory,
                new VirtualFileVisitor() {
                    /**
                     * Simple visiting method.
                     * On returning {@code true} a visitor will proceed to file's children, on {@code false} - to file's
                     * next sibling.
                     *
                     * @param file a file to visit.
                     * @return {@code true} to proceed to file's children, {@code false} to skip to file's next sibling.
                     */
                    @Override
                    public boolean visitFile(@NotNull VirtualFile file) {
                        if (Beam.is(file)) {
                            Beam beam = Beam.from(file);

                            if (beam != null) {
                                Atoms atoms = beam.atoms();

                                if (atoms != null) {
                                    String moduleName = atoms.moduleName();

                                    if (moduleName != null) {
                                        String resolvedName = stripElixirPrefix(moduleName);
                                        Boolean validResult = null;

                                        if (name.equals(resolvedName)) {
                                            validResult = true;
                                        } else if (name.startsWith(resolvedName)) {
                                            validResult = false;
                                        }

                                        if (validResult != null) {
                                            PsiFile psiFile = psiManager.findFile(file);

                                            if (psiFile != null) {
                                                results.add(new PsiElementResolveResult(psiFile, validResult));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        return true;
                    }
                }
        );

        return results;
    }

    private List<ResolveResult> multiResolveProject(@NotNull Project project,
                                                    @NotNull String name) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        Collection<NamedElement> namedElementCollection = StubIndex.getElements(
                AllName.KEY,
                name,
                project,
                GlobalSearchScope.allScope(project),
                NamedElement.class
        );

        for (NamedElement namedElement : namedElementCollection) {
            results.add(new PsiElementResolveResult(namedElement));
        }

        return results;
    }

    @Nullable
    private List<ResolveResult> multiResolveSDK(@NotNull Project project, @NotNull String name) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();

        List<ResolveResult> results = null;

        if (sdk != null) {
            results = multiResolveSDK(PsiManager.getInstance(project), sdk, name);
        }

        return results;
    }

    @Nullable
    private List<ResolveResult> multiResolveSDK(@NotNull PsiManager psiManager,
                                                @NotNull Sdk sdk,
                                                @NotNull String name) {
        VirtualFile sdkHomeDirectory = sdk.getHomeDirectory();
        List<ResolveResult> results = null;

        if (sdkHomeDirectory != null) {
            results = multiResolveBeams(psiManager, sdkHomeDirectory, name);
        }

        return results;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> lookupElementList = Variants.lookupElementList(myElement);

        return lookupElementList.toArray(new Object[lookupElementList.size()]);
    }
}
