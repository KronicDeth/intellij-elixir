package org.elixir_lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FoldingBuilder extends FoldingBuilderEx {
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
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        Map<String, FoldingGroup> foldingGroupByModuleAttributeName = new HashMap<String, FoldingGroup>();

        List<FoldingDescriptor> foldingDescriptorList = new ArrayList<FoldingDescriptor>();

        Collection<AtNonNumericOperation> atNonNumericOperationCollection = PsiTreeUtil.collectElementsOfType(
                root, AtNonNumericOperation.class
        );

        for (final AtNonNumericOperation atNonNumericOperation : atNonNumericOperationCollection) {
            PsiElement target = atNonNumericOperation.getReference().resolve();

            if (target != null) {
                assert target instanceof AtUnqualifiedNoParenthesesCall;

                final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) target;

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
            }
        }

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
        return null;
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
