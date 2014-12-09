package org.elixir_lang.local_quick_fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.text.BlockSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 12/7/14.
 */
public class RemoveSpaceInFrontOfNoParenthesesStrict extends LocalQuickFixOnPsiElement {
    /*
     * Constructors
     */

    public RemoveSpaceInFrontOfNoParenthesesStrict(PsiElement parentWithSpace) {
        super(parentWithSpace);
    }

    /*
     * Methods
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
        com.intellij.lang.ASTNode parentNode = startElement.getNode();
        com.intellij.lang.ASTNode whiteSpace = parentNode.findChildByType(TokenType.WHITE_SPACE);

        BlockSupport blockSupport = BlockSupport.getInstance(project);
        final int startOffset = whiteSpace.getStartOffset();
        final int endOffset = startOffset + whiteSpace.getTextLength();
        blockSupport.reparseRange(file, startOffset, endOffset, "");
    }
}
