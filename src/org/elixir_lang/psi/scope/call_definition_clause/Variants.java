package org.elixir_lang.psi.scope.call_definition_clause;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashMap;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.Named;
import org.elixir_lang.psi.scope.CallDefinitionClause;
import org.elixir_lang.psi.stub.index.AllName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE;
import static org.elixir_lang.structure_view.element.CallDefinitionClause.nameArityRange;

public class Variants extends CallDefinitionClause {
    /*
     * Static Methods
     */

    @Nullable
    public static List<LookupElement> lookupElementList(@NotNull PsiElement entrance) {
        Variants variants = new Variants();
        PsiTreeUtil.treeWalkUp(
                variants,
                entrance,
                entrance.getContainingFile(),
                ResolveState.initial().put(ENTRANCE, entrance)
        );
        List<LookupElement> lookupElementList = new ArrayList<LookupElement>();
        lookupElementList.addAll(variants.getLookupElementCollection());
        variants.addProjectNameElementsTo(lookupElementList, entrance);

        return lookupElementList;
    }

    /*
     * Fields
     */

    private Map<PsiElement, LookupElement> lookupElementByPsiElement = null;

    /*
     * Protected Instance Methods
     */

    /**
     * Called on every {@link Call} where {@link org.elixir_lang.structure_view.element.CallDefinitionClause#is} is
     * {@code true} when checking tree with {@link #execute(Call, ResolveState)}
     *
     * @param element
     * @param state
     * @return {@code true} to keep searching up tree; {@code false} to stop searching.
     */
    @Override
    protected boolean executeOnCallDefinitionClause(Call element, ResolveState state) {
        addToLookupElementByPsiElement(element);

        return true;
    }

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return {@code true} to keep searching up the PSI tree; {@code false} to stop searching.
     */
    @Override
    protected boolean keepProcessing() {
        return false;
    }

    /*
     * Private Instance Methods
     */

    private void addToLookupElementByPsiElement(@NotNull Call call) {
        if (call instanceof Named) {
            Named named = (Named) call;

            PsiElement nameIdentifier = named.getNameIdentifier();

            if (nameIdentifier != null) {
                if (lookupElementByPsiElement == null || !lookupElementByPsiElement.containsKey(nameIdentifier)) {
                    if (lookupElementByPsiElement == null) {
                        lookupElementByPsiElement = new THashMap<PsiElement, LookupElement>();
                    }

                    String name = nameIdentifier.getText();

                    lookupElementByPsiElement.put(
                            nameIdentifier,
                            LookupElementBuilder.createWithSmartPointer(
                                    name,
                                    nameIdentifier
                            ).withRenderer(
                                    new org.elixir_lang.codeInsight.lookup.element_renderer.CallDefinitionClause(name)
                            )
                    );
                }
            }
        }
    }

    private void addProjectNameElementsTo(@NotNull List<LookupElement> lookupElementList,
                                          @NotNull PsiElement entrance) {
        Project project = entrance.getProject();
        /* getAllKeys is not the actual keys in the actual project.  They need to be checked.
           See https://intellij-support.jetbrains.com/hc/en-us/community/posts/207930789-StubIndex-persisting-between-test-runs-leading-to-incorrect-completions */
        Collection<String> indexedNameCollection = StubIndex.getInstance().getAllKeys(AllName.KEY, project);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        for (String indexedName : indexedNameCollection) {
            Collection<NamedElement> indexedNameNamedElementCollection = StubIndex.getElements(
                    AllName.KEY,
                    indexedName,
                    project,
                    scope,
                    NamedElement.class
            );

            for (NamedElement indexedNameNamedElement : indexedNameNamedElementCollection) {
                lookupElementList.add(
                        LookupElementBuilder.createWithSmartPointer(
                                indexedName,
                                indexedNameNamedElement
                        ).withInsertHandler(
                                new InsertHandler<LookupElement>() {
                                    @Override
                                    public void handleInsert(@NotNull InsertionContext context,
                                                             @NotNull LookupElement item) {
                                        int tailOffset = context.getTailOffset();
                                        String currentTail = context.getDocument().getText(
                                                new TextRange(tailOffset, tailOffset + 1)
                                        );
                                        char firstChar = currentTail.charAt(0);

                                        if (firstChar != ' ' && firstChar != '(' && firstChar != '[') {
                                            context.getDocument().insertString(tailOffset, "()");
                                            // + 1 to put between the `(`  and `)`
                                            context.getEditor().getCaretModel().moveToOffset(tailOffset + 1);
                                        }
                                    }
                                }
                        ).withRenderer(
                                new org.elixir_lang.codeInsight.lookup.element_renderer.CallDefinitionClause(
                                        indexedName
                                )
                        )
                );
            }
        }
    }

    private Collection<LookupElement> getLookupElementCollection() {
        return lookupElementByPsiElement.values();
    }
}
