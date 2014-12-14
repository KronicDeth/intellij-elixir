package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.psi.ElixirAdjacentExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 12/13/14.
 */
public class MissingEndOfExpression extends LocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Missing End-of-Expression";
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
        return "MissingEndOfExpression";
    }

    public boolean isEnabledByDefault() {
        return true;
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);

        file.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        if (element instanceof ElixirAdjacentExpression) {
                            ProblemDescriptor problemDesription = manager.createProblemDescriptor(
                                    element.getPrevSibling(),
                                    element,
                                    "Missing `;` or new line.",
                                    ProblemHighlightType.ERROR,
                                    isOnTheFly
                            );

                            problemsHolder.registerProblem(problemDesription);
                        }

                        super.visitElement(element);
                    }
                }
        );

        return problemsHolder.getResultsArray();
    }
}
