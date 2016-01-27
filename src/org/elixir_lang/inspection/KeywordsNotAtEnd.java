package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.arguments.NoParentheses;
import org.elixir_lang.psi.call.arguments.NoParenthesesOneArgument;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                        PsiElement keywordsElementNotAtEnd = null;
                        PsiElement listElement = null;

                        if (element instanceof ElixirNoParenthesesKeywords) {
                            ElixirNoParenthesesKeywords keywordsElement = (ElixirNoParenthesesKeywords) element;

                            PsiElement parent = element.getParent();

                            if (parent instanceof ElixirMatchedWhenOperation) {
                                PsiElement grandParent = parent.getParent();

                                if (grandParent instanceof ElixirNoParenthesesOneArgument) {
                                    listElement = grandParent;

                                    keywordsElementNotAtEnd = findKeywordsElementNotAtEnd(
                                            listElement,
                                            parent,
                                            keywordsElement
                                    );
                                }
                            } else if (parent instanceof ElixirNoParenthesesOneArgument) {
                                PsiElement grandParent = parent.getParent();

                                if (grandParent instanceof NoParentheses) {
                                    PsiElement greatGrandParent = grandParent.getParent();

                                    if (greatGrandParent instanceof ElixirNoParenthesesOneArgument) {
                                        listElement = greatGrandParent;

                                        keywordsElementNotAtEnd = findKeywordsElementNotAtEnd(
                                                listElement,
                                                grandParent,
                                                keywordsElement
                                        );
                                    } else if (greatGrandParent instanceof ElixirParenthesesArguments) {
                                        listElement = greatGrandParent;

                                        keywordsElementNotAtEnd = findKeywordsElementNotAtEnd(
                                                listElement,
                                                grandParent,
                                                keywordsElement
                                        );
                                    }
                                }
                            } else if (parent instanceof ElixirUnmatchedWhenOperation) {
                                PsiElement grandParent = parent.getParent();

                                if (grandParent instanceof ElixirParenthesesArguments) {
                                    listElement = grandParent;

                                    keywordsElementNotAtEnd = findKeywordsElementNotAtEnd(
                                            listElement,
                                            parent,
                                            keywordsElement
                                    );
                                }
                            } else if (parent instanceof NoParentheses) {
                                PsiElement grandParent = parent.getParent();

                                if (grandParent instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression) {
                                    PsiElement greatGrandParent = grandParent.getParent();

                                    if (greatGrandParent instanceof ElixirNoParenthesesOneArgument) {
                                        listElement = greatGrandParent;

                                        keywordsElementNotAtEnd = findKeywordsElementNotAtEnd(
                                                listElement,
                                                grandParent,
                                                keywordsElement
                                        );
                                    }
                                }
                            }
                        }

                        if (keywordsElementNotAtEnd != null) {
                            TextRange listElementTextRange = listElement.getTextRange();
                            TextRange keywordsTextRange = keywordsElementNotAtEnd.getTextRange();
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

    @Nullable
    private static PsiElement findKeywordsElementNotAtEnd(PsiElement listElement, PsiElement listChildWithKeywords, ElixirNoParenthesesKeywords keywords) {
        PsiElement keywordsElementNotAtEnd = null;

        if (listElement.getLastChild() != listChildWithKeywords) {
            keywordsElementNotAtEnd = keywords;
        }

        return keywordsElementNotAtEnd;
    }
}
