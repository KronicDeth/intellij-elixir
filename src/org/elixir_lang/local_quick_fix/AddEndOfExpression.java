package org.elixir_lang.local_quick_fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.text.BlockSupport;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 12/13/14.
 */
public abstract class AddEndOfExpression extends LocalQuickFixOnPsiElement {
    public AddEndOfExpression(PsiElement startElement, PsiElement endElement) {
        super(startElement, endElement);
    }

    public abstract String displayEndOfExpression();
    public abstract String endOfExpression();

    /**
     * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code inspection). For example,
     * if the name of the quickfix is "Create template &lt;filename&gt", the return value of getFamilyName() should be "Create template".
     * If the name of the quickfix does not depend on a specific element, simply return getName().
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return "Add End-of-Expression";
    }

    @NotNull
    @Override
    public String getText() {
        return "Add " + displayEndOfExpression() + " for the missing end-of-expression";
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        com.intellij.lang.ASTNode startNode = startElement.getNode();
        com.intellij.lang.ASTNode endNode = endElement.getNode();

        BlockSupport blockSupport = BlockSupport.getInstance(project);
        final int startOffset = startNode.getTextRange().getEndOffset();
        final int endOffset = endNode.getStartOffset();
        blockSupport.reparseRange(file, startOffset, endOffset, endOfExpression());
    }
}
