package org.elixir_lang.local_quick_fix;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.text.BlockSupport;
import org.jetbrains.annotations.NotNull;

public class ConvertKeywordPairToTypeOperation extends LocalQuickFixOnPsiElement {
    /*
     * Constructors
     */

    public ConvertKeywordPairToTypeOperation(@NotNull PsiElement keywordPairColon) {
        super(keywordPairColon);
    }

    /*
     * Instance Methods
     */

    /**
     * @return text to appear in "Apply Fix" popup when multiple Quick Fixes exist (in the results of batch code
     * inspection). For example, * if the name of the quickfix is "Create template &lt;filename&gt", the return value of
     * getFamilyName() should be "Create template". * If the name of the quickfix does not depend on a specific element,
     * simply return getName().
     */
    @NotNull
    @Override
    public String getFamilyName() {
        return "Fix type specification";
    }

    @NotNull
    @Override
    public String getText() {
        return "Replace `:` in keyword pair with ` ::` to convert to a valid type specification";
    }

    @Override
    public void invoke(@NotNull Project project,
                       @NotNull PsiFile file,
                       @NotNull PsiElement startElement,
                       @NotNull PsiElement endElement) {
        assert startElement == endElement;

        BlockSupport blockSupport = BlockSupport.getInstance(project);
        TextRange textRange = startElement.getTextRange();
        int startOffset = textRange.getStartOffset();
        int endOffset = textRange.getEndOffset();
        blockSupport.reparseRange(file, startOffset, endOffset, " ::");
    }
}
