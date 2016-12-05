package org.elixir_lang.local_quick_fix;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.text.BlockSupport;
import org.jetbrains.annotations.NotNull;

public class ConvertMatchToTypeOperation implements LocalQuickFix {
    @NotNull
    private final ASTNode matchOperatorASTNode;

    /*
     * Constructors
     */

    public ConvertMatchToTypeOperation(@NotNull ASTNode matchOperatorASTNode) {
        this.matchOperatorASTNode = matchOperatorASTNode;
    }

    /*
     * Instance Methods
     */

    /**
     * Called to apply the fix.
     *
     * @param project    {@link Project}
     * @param descriptor problem reported by the tool which provided this quick fix action
     */
    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        BlockSupport blockSupport = BlockSupport.getInstance(project);
        TextRange textRange = matchOperatorASTNode.getTextRange();
        blockSupport.reparseRange(
                matchOperatorASTNode.getPsi().getContainingFile(),
                textRange.getStartOffset(),
                textRange.getEndOffset(),
                "::"
        );
    }

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
    public String getName() {
        return "Replace `=` in keyword pair with `::` to convert to a valid type specification";
    }
}
