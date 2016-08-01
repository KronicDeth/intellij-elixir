package org.elixir_lang.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.elixir_lang.Module.concat;
import static org.elixir_lang.psi.stub.type.call.Stub.isModular;

public class Module extends PsiReferenceBase<QualifiableAlias> implements PsiPolyVariantReference {
    /*
     *
     * Static Methods
     *
     */

    /*
     * Private Static Methods
     */

    /**
     * The full name of the qualifiable alias, with any multiple aliases expanded
     */
    @Nullable
    private String resolvableName(@NotNull PsiNamedElement  namedElement) {
        String resolvableName = namedElement.getName();
        List<String> tail = null;

        if (resolvableName != null) {
            tail = new ArrayList<String>();
            tail.add(resolvableName);
        }

        return resolvableNameUp(namedElement.getParent(), tail);
    }

    @Nullable
    private List<String> resolvableNameDown(@NotNull QualifiableAlias qualifier) {
        String resolvableName = qualifier.getName();
        List<String> nameList = null;

        if (resolvableName != null) {
            nameList = new ArrayList<String>();
            nameList.add(resolvableName);
        }

        return nameList;
    }

    @Nullable
    private List<String> resolvableNameDown(@NotNull PsiElement qualifier) {
        List<String> nameList = null;

        if (qualifier instanceof ElixirAccessExpression) {
            nameList = resolvableNameDown(qualifier.getChildren());
        } else if (qualifier instanceof QualifiableAlias) {
            nameList = resolvableNameDown((QualifiableAlias) qualifier);
        }

        return nameList;
    }

    @Nullable
    private List<String> resolvableNameDown(@NotNull PsiElement[] qualifiers) {
        List<String> nameList = null;

        for (PsiElement qualifier : qualifiers) {
            List<String> qualifierNameList = resolvableNameDown(qualifier);

            if (qualifierNameList != null) {
                if (nameList == null) {
                    nameList = new ArrayList<String>(qualifierNameList.size());
                }

                nameList.addAll(qualifierNameList);
            }
        }

        return nameList;
    }

    @Nullable
    private String resolvableNameUp(@Nullable PsiElement ancestor, @Nullable List<String> tail) {
        String resolvableName = null;

        if (ancestor instanceof ElixirAccessExpression ||
                ancestor instanceof ElixirMultipleAliases) {
            resolvableName = resolvableNameUp(ancestor.getParent(), tail);
        } else if (ancestor instanceof QualifiedMultipleAliases) {
            resolvableName = resolvableNameUp((QualifiedMultipleAliases) ancestor, tail);
        } else if (tail != null) {
            resolvableName = concat(tail);
        }

        return resolvableName;
    }

    @Nullable
    private String resolvableNameUp(@NotNull QualifiedMultipleAliases ancestor, @Nullable List<String> tail) {
        PsiElement[] children = ancestor.getChildren();
        int operatorIndex = org.elixir_lang.psi.operation.Normalized.operatorIndex(children);

        PsiElement qualifier = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex);
        List<String> qualifierNameList = null;

        if (qualifier != null) {
            qualifierNameList = resolvableNameDown(qualifier);
        }

        List<String> nameList;

        if (qualifierNameList != null) {
            nameList = qualifierNameList;

            if (tail != null) {
                qualifierNameList.addAll(tail);
            }
        } else {
            nameList = tail;
        }

        String resolvableName = null;

        if (nameList != null) {
            resolvableName = concat(nameList);
        }

        return resolvableName;
    }

    /*
     * Constructors
     */

    public Module(@NotNull QualifiableAlias qualifiableAlias) {
        super(qualifiableAlias, TextRange.create(0, qualifiableAlias.getTextLength()));
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
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        final String name = resolvableName(myElement);

        if (name != null) {
            results.addAll(multiResolveUpFromElement(myElement, name));

            if (results.isEmpty()) {
                results.addAll(
                        multiResolveProject(
                                myElement.getProject(),
                                name
                        )
                );
            }
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    /*
     * Private Instance Methods
     */

    private void addMatchingAsResolveResult(@NotNull List<ResolveResult> resultList,
                                            @NotNull Named named,
                                            @NotNull String targetName) {
        String name = named.getName();

        if (name != null && name.equals(targetName)) {
            PsiElement nameIdentifier = named.getNameIdentifier();
            PsiElement resolved = named;

            if (nameIdentifier != null) {
                resolved = nameIdentifier;
            }

            resultList.add(new PsiElementResolveResult(resolved));
        }
    }

    private Collection<ResolveResult> multiResolveProject(@NotNull Project project,
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

    private List<ResolveResult> multiResolveUpFromElement(@NotNull final PsiElement element,
                                                          @NotNull final String name) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();
        PsiElement lastSibling = element;

        while (results.isEmpty() && lastSibling != null) {
            results.addAll(multiResolveSibling(lastSibling, name));

            lastSibling = lastSibling.getParent();
        }

        return results;
    }

    private List<ResolveResult> multiResolveSibling(@NotNull final PsiElement lastSibling, @NotNull final String name) {
        List<ResolveResult> results = new ArrayList<ResolveResult>();

        for (PsiElement sibling = lastSibling; sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling instanceof Named) {
                Named named = (Named) sibling;

                if (isModular(named)) {
                    addMatchingAsResolveResult(results, named, name);
                }
            }
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
        List<LookupElement> variants = new ArrayList<LookupElement>();
        return variants.toArray();
    }
}
