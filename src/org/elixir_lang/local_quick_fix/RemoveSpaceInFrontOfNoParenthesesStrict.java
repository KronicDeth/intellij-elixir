package org.elixir_lang.local_quick_fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.text.BlockSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by kadie.enheduanna.inanna on 12/7/14.
 */
public class RemoveSpaceInFrontOfNoParenthesesStrict extends LocalQuickFixOnPsiElement {
    /*
     * Constructors
     */

    public RemoveSpaceInFrontOfNoParenthesesStrict(PsiElement parentWithSpace) {
        super(parentWithSpace);
    }

    /*
     * Static Methods
     */

    /**
     * The space to remove is not always a child of {@code node}: when the grammar gained
     * {@code noParenthesesOneArgument}, that wrapper came to span only the parentheses, leaving the space on its
     * previous sibling instead. The children are still searched first because a call that reaches
     * {@code noParenthesesStrict} without the wrapper keeps the space inside.
     */
    @Nullable
    private static ASTNode whiteSpaceBefore(@NotNull ASTNode node) {
        ASTNode child = node.findChildByType(TokenType.WHITE_SPACE);

        if (child != null) {
            return child;
        }

        for (ASTNode ancestor = node; ancestor != null; ancestor = ancestor.getTreeParent()) {
            ASTNode previous = ancestor.getTreePrev();

            if (previous != null) {
                return previous.getElementType() == TokenType.WHITE_SPACE ? previous : null;
            }
        }

        return null;
    }

    /*
     * Instance Methods
     */

    /**
     * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
     * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
     * If the name of the quickfix does not depend on a specific element, simply return getName().
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return "Remove space";
    }

    @NotNull
    @Override
    public String getText() {
        return "Remove space between function name and parentheses";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        assert startElement == endElement;
        ASTNode whiteSpace = whiteSpaceBefore(startElement.getNode());

        if (whiteSpace == null) {
            return;
        }

        BlockSupport blockSupport = BlockSupport.getInstance(project);
        final int startOffset = whiteSpace.getStartOffset();
        final int endOffset = startOffset + whiteSpace.getTextLength();
        blockSupport.reparseRange(file, startOffset, endOffset, "");
    }
}
