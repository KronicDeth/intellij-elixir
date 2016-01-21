package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 12/5/14.
 */
public class NoParenthesesManyStrict extends LocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Ambiguous nested calls";
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
        return "NoParenthesesManyStrict";
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
                        PsiElement elementWithAmbiguousComma = null;

                        if (element instanceof ElixirUnqualifiedNoParenthesesManyArgumentsCall) {
                            PsiElement[] children = element.getChildren();

                            if (children.length > 1) {
                                PsiElement parent = element.getParent();

                                if (parent instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression) {
                                  elementWithAmbiguousComma = element;
                                }
                            }
                        } else if (element instanceof ElixirNoParenthesesOneArgument) {
                            PsiElement[] children = element.getChildren();

                            if (children.length > 1) {
                                PsiElement parent = element.getParent();

                                if (parent instanceof NoParentheses) {
                                    PsiElement grandParent = parent.getParent();

                                    if (grandParent instanceof Arguments) {
                                        PsiElement[] grandParentChildren = grandParent.getChildren();

                                        if (grandParentChildren.length > 1) {
                                            elementWithAmbiguousComma = element;
                                        }
                                    } else if (grandParent instanceof ElixirKeywordPair) {
                                        elementWithAmbiguousComma = element;
                                    }
                                }
                            }
                        }

                        if (elementWithAmbiguousComma != null) {
                            int ambiguousCommaIndex = elementWithAmbiguousComma.getText().indexOf(",");
                            TextRange ambiguousCommaTextRange = new TextRange(
                                    ambiguousCommaIndex,
                                    ambiguousCommaIndex + 1
                            );

                            problemsHolder.registerProblem(
                                    element,
                                    "unexpected comma.  Parentheses are required to solve ambiguity in nested calls.",
                                    ProblemHighlightType.ERROR,
                                    ambiguousCommaTextRange
                            );
                        }

                        super.visitElement(element);
                    }
                }
        );
    }
}
