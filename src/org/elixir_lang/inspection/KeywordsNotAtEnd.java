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

public class KeywordsNotAtEnd extends LocalInspectionTool {
    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Keywords not at end";
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
        return "KeywordsNotAtEnd";
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
                        PsiElement keywordsElement = null;
                        PsiElement listElement = null;

                        if (element instanceof ElixirNoParenthesesKeywords) {
                            PsiElement previousAncestor = null;
                            PsiElement ancestor = element.getParent();

                            while (ancestor != null) {
                                if (ancestor instanceof ElixirMatchedWhenOperation) {
                                    PsiElement whenParent = ancestor.getParent();

                                    if (whenParent instanceof ElixirNoParenthesesOneArgument) {
                                        listElement = whenParent;
                                        PsiElement[] listChildren = listElement.getChildren();

                                        // -1 because if keywords are the last argument, then they are valid
                                        for (int i = 0; i < listChildren.length - 1; i++) {
                                            PsiElement listChild = listChildren[i];

                                            if (listChild.equals(whenParent)) {
                                                keywordsElement = element;
                                                break;
                                            }
                                        }

                                        break;
                                    }
                                } else if (ancestor instanceof ElixirNoParenthesesOneArgument) {
                                    PsiElement call = ancestor.getParent();

                                    if (call instanceof NoParentheses) {
                                        PsiElement callParent = call.getParent();

                                        if (callParent instanceof ElixirNoParenthesesOneArgument) {
                                            listElement = callParent;
                                            PsiElement[] listChildren = listElement.getChildren();

                                            // -1 because if keywords are the last argument, then they are valid
                                            for (int i = 0; i < listChildren.length - 1; i++) {
                                                PsiElement listChild = listChildren[i];

                                                if (listChild.equals(callParent)) {
                                                    keywordsElement = element;
                                                    break;
                                                }
                                            }

                                            break;
                                        }
                                    }
                                } else if (ancestor instanceof ElixirNoParenthesesStrict ||
                                        ancestor instanceof ElixirParentheticalStab) {
                                    /* the keyword arguments are part of a call with parentheses around it, so they
                                       are not ambiguous.

                                       @see https://github.com/KronicDeth/intellij-elixir/issues/195 */
                                    break;
                                } else if (ancestor instanceof ElixirParenthesesArguments) {
                                    listElement = ancestor;
                                    PsiElement[] listChildren = listElement.getChildren();

                                    // -1 because if keywords are the last argument, then they are valid
                                    for (int i = 0; i < listChildren.length - 1; i++) {
                                        PsiElement listChild = listChildren[i];

                                        if (listChild.equals(previousAncestor)) {
                                            keywordsElement = element;
                                            break;
                                        }
                                    }

                                    break;
                                } else if (ancestor instanceof ElixirUnqualifiedNoParenthesesManyArgumentsCall) {
                                    PsiElement call = ancestor;

                                    PsiElement callParent = call.getParent();

                                    if (callParent instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression) {
                                        PsiElement callGrandParent = callParent.getParent();

                                        if (callGrandParent instanceof ElixirNoParenthesesOneArgument) {
                                            listElement = callGrandParent;
                                            PsiElement[] listChildren = listElement.getChildren();

                                            // -1 because if keywords are the last argument, then they are valid
                                            for (int i = 0; i < listChildren.length - 1; i++) {
                                                PsiElement listChild = listChildren[i];

                                                if (listChild.equals(callGrandParent)) {
                                                    keywordsElement = element;
                                                    break;
                                                }
                                            }

                                            break;
                                        }
                                    }
                                }

                                previousAncestor = ancestor;
                                ancestor = ancestor.getParent();
                            }
                        }

                        if (keywordsElement != null) {
                            TextRange listElementTextRange = listElement.getTextRange();
                            TextRange keywordsTextRange = keywordsElement.getTextRange();
                            int listElementStartOffset = listElementTextRange.getStartOffset();
                            TextRange relativeTextRange = new TextRange(
                                    keywordsTextRange.getStartOffset() - listElementStartOffset,
                                    keywordsTextRange.getEndOffset() - listElementStartOffset
                            );

                            problemsHolder.registerProblem(
                                    listElement,
                                    "Keywords appear before the end of list.  Move keywords after positional arguments.",
                                    ProblemHighlightType.ERROR,
                                    relativeTextRange
                            );
                        }

                        super.visitElement(element);
                    }
                }
        );
    }
}
