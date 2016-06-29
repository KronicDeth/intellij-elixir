package org.elixir_lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.reference.ModuleAttribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.moduleAttributeName;

public class FoldingBuilder extends FoldingBuilderEx {
    /*
     * Instance Methods
     */

    /**
     * Builds the folding regions for the specified node in the AST tree and its children.
     *
     * @param root     the element for which folding is requested.
     * @param document the document for which folding is built. Can be used to retrieve line
     *                 numbers for folding regions.
     * @param quick    whether the result should be provided as soon as possible. Is true, when
     *                 an editor is opened and we need to auto-fold something immediately, like Java imports.
     *                 If true, one should perform no reference resolving and avoid complex checks if possible.
     * @return the array of folding descriptors.
     */
    @NotNull
    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, final boolean quick) {
        final List<FoldingDescriptor> foldingDescriptorList = new ArrayList<FoldingDescriptor>();

        PsiTreeUtil.processElements(root,
                new PsiElementProcessor() {
                    private Map<String, FoldingGroup> foldingGroupByModuleAttributeName =
                            new HashMap<String, FoldingGroup>();

                    /*
                     *
                     * Instance Methods
                     *
                     */

                    /*
                     * Public Instance Methods
                     */

                    @Override
                    public boolean execute(@NotNull PsiElement element) {
                        boolean keepProcessing = true;


                        if (element instanceof AtNonNumericOperation) {
                            keepProcessing = execute((AtNonNumericOperation) element);
                        } else if (element instanceof AtUnqualifiedNoParenthesesCall) {
                            keepProcessing = execute((AtUnqualifiedNoParenthesesCall) element);
                        } else if (element instanceof ElixirDoBlock) {
                            keepProcessing = execute((ElixirDoBlock) element);
                        } else if (element instanceof ElixirStabOperation) {
                            keepProcessing = execute((ElixirStabOperation) element);
                        }

                        return keepProcessing;
                    }

                    /*
                     * Private Instance Methods
                     */

                    private boolean execute(@NotNull AtNonNumericOperation atNonNumericOperation) {
                        boolean keepProcessing = true;

                        if (!quick) {
                            keepProcessing = slowExecute(atNonNumericOperation);
                        }

                        return keepProcessing;
                    }

                    private boolean execute(@NotNull AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
                        String moduleAttributeName = moduleAttributeName(atUnqualifiedNoParenthesesCall);
                        String name = moduleAttributeName.substring(1);

                        if (ModuleAttribute.isDocumentationName(name)) {
                            ElixirNoParenthesesOneArgument noParenthesesOneArgument =
                                    atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();

                            foldingDescriptorList.add(
                                    new FoldingDescriptor(
                                            noParenthesesOneArgument,
                                            noParenthesesOneArgument.getTextRange()
                                    )
                            );
                        }

                        return true;
                    }

                    private boolean execute(@NotNull ElixirDoBlock doBlock) {
                        foldingDescriptorList.add(new FoldingDescriptor(doBlock, doBlock.getTextRange()));

                        return true;
                    }

                    private boolean execute(@NotNull ElixirStabOperation stabOperation) {
                        int startOffset = stabOperation.operator().getTextOffset();
                        int endOffset = stabOperation.getTextRange().getEndOffset();
                        TextRange textRange = new TextRange(startOffset, endOffset);

                        foldingDescriptorList.add(new FoldingDescriptor(stabOperation, textRange));

                        return true;
                    }

                    private boolean slowExecute(@NotNull AtNonNumericOperation atNonNumericOperation) {
                        boolean keepProcessing = true;
                        PsiReference reference = atNonNumericOperation.getReference();

                        if (reference != null) {
                            keepProcessing = slowExecute(atNonNumericOperation, reference);
                        }

                        return keepProcessing;
                    }

                    private boolean slowExecute(@NotNull AtNonNumericOperation atNonNumericOperation,
                                                @NotNull PsiElement target) {
                        assert target instanceof AtUnqualifiedNoParenthesesCall;

                        final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall =
                                (AtUnqualifiedNoParenthesesCall) target;

                        String moduleAttributeName = atNonNumericOperation.moduleAttributeName();
                        FoldingGroup foldingGroup = foldingGroupByModuleAttributeName.get(moduleAttributeName);

                        if (foldingGroup == null) {
                            foldingGroup = FoldingGroup.newGroup(moduleAttributeName);
                            foldingGroupByModuleAttributeName.put(moduleAttributeName, foldingGroup);
                        }

                        foldingDescriptorList.add(
                                new FoldingDescriptor(
                                        atNonNumericOperation.getNode(),
                                        atNonNumericOperation.getTextRange(),
                                        foldingGroup,
                                        Collections.<Object>singleton(atUnqualifiedNoParenthesesCall)
                                ) {
                                    @Nullable
                                    @Override
                                    public String getPlaceholderText() {
                                        return atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument().getText();
                                    }
                                }
                        );

                        return true;
                    }

                    private boolean slowExecute(@NotNull AtNonNumericOperation atNonNumericOperation,
                                                @NotNull PsiReference reference) {
                        PsiElement target = reference.resolve();
                        boolean keepProcessing = true;

                        if (target != null) {
                            keepProcessing = slowExecute(atNonNumericOperation, target);
                        }

                        return keepProcessing;
                    }
                }
        );

        return foldingDescriptorList.toArray(new FoldingDescriptor[foldingDescriptorList.size()]);
    }

    /**
     * Returns the text which is displayed in the editor for the folding region related to the
     * specified node when the folding region is collapsed.
     *
     * @param node the node for which the placeholder text is requested.
     * @return the placeholder text.
     */
    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        PsiElement element = node.getPsi();
        String placeholderText = null;

        if (element instanceof ElixirDoBlock) {
            placeholderText = "do: ...";
        } else if (element instanceof ElixirStabOperation) {
            placeholderText = "-> ...";
        } else if (element instanceof ElixirNoParenthesesOneArgument) {
            placeholderText = "\"...\"";
        }

        return placeholderText;
    }

    /**
     * Returns the default collapsed state for the folding region related to the specified node.
     *
     * @param node the node for which the collapsed state is requested.
     * @return true if the region is collapsed by default, false otherwise.
     */
    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return true;
    }
}
