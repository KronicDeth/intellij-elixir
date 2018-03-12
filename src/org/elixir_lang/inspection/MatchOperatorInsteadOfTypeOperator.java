package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.local_quick_fix.ConvertMatchToTypeOperation;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.operation.Infix;
import org.elixir_lang.psi.operation.Match;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import static org.elixir_lang.psi.impl.ElixirAtIdentifierImplKt.identifierName;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.operatorTokenNode;
import static org.elixir_lang.reference.ModuleAttribute.isTypeName;

public class MatchOperatorInsteadOfTypeOperator extends LocalInspectionTool {
    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file,
                                         @NotNull InspectionManager manager,
                                         boolean isOnTheFly) {
        final ProblemsHolder problemsHolder = new ProblemsHolder(manager, file, isOnTheFly);

        file.accept(
                new PsiRecursiveElementWalkingVisitor() {
                    @Override
                    public void visitElement(@NotNull final PsiElement element) {
                        // See org.elixir_lang.annotator.ModuleAttribute.annotate for path of checks
                        if (element instanceof AtUnqualifiedNoParenthesesCall) {
                            visitAtUnqualifiedNoParenthesesCall((AtUnqualifiedNoParenthesesCall) element);
                        }

                        super.visitElement(element);
                    }

                    private void visitAtUnqualifiedNoParenthesesCall(
                            @NotNull final AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall
                    ) {
                        ElixirAtIdentifier atIdentifier = atUnqualifiedNoParenthesesCall.getAtIdentifier();
                        String identifier = identifierName(atIdentifier);

                        if (isTypeName(identifier)) {
                            PsiElement child = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument();
                            PsiElement[] grandChildren = child.getChildren();

                            if (grandChildren.length == 1) {
                                PsiElement grandChild = grandChildren[0];

                                if (grandChild instanceof Match) {
                                    Infix infix = (Infix) grandChild;
                                    Operator operator = infix.operator();
                                    int elementStartOffset = operator.getTextOffset();

                                    ASTNode astNode = operatorTokenNode(operator);
                                    int nodeStartOffset = astNode.getStartOffset();
                                    int nodeTextLength = astNode.getTextLength();
                                    int relativeStart = nodeStartOffset - elementStartOffset;
                                    TextRange relativeTextRange = new TextRange(
                                            relativeStart,
                                            relativeStart + nodeTextLength
                                    );

                                    LocalQuickFix localQuickFix = new ConvertMatchToTypeOperation(astNode);

                                    problemsHolder.registerProblem(
                                            operator,
                                            "Type specifications separate the name from the definition using `::`, not `=`",
                                            ProblemHighlightType.ERROR,
                                            relativeTextRange,
                                            localQuickFix
                                    );
                                }
                            }
                        }

                    }
                }
        );

        return problemsHolder.getResultsArray();
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Match operator (=) used in type spec instead of type operator (::)";
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
        return "MatchOperatorInsteadOfTypeOperator";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
