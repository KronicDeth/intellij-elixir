package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.local_quick_fix.RemoveSpaceInFrontOfNoParenthesesStrict;
import org.elixir_lang.psi.ElixirNoParenthesesStrict;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 12/5/14.
 */
public class NoParenthesesStrict extends LocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Ambiguous parentheses";
    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return "Elixir";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "NoParenthesesStrict";
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);
        checkFile(file, problemsHolder);
        return problemsHolder.getResultsArray();
    }

    private static void checkFile(final PsiFile file, final ProblemsHolder problemsHolder) {
        file.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof ElixirNoParenthesesStrict) {
                            LocalQuickFix localQuickFix = new RemoveSpaceInFrontOfNoParenthesesStrict(
                                    element.getParent()
                            );
                            problemsHolder.registerProblem(
                                    element,
                                    "unexpected parenthesis. If you are making a " +
                                    "function call, do not insert spaces in between the function name and the " +
                                    "opening parentheses.",
                                    ProblemHighlightType.ERROR,
                                    localQuickFix
                            );
                        }
                        super.visitElement(element);
                    }
                }
        );
    }
}
