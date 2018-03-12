package org.elixir_lang.inspection;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.elixir_lang.local_quick_fix.ConvertKeywordPairToTypeOperation;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.elixir_lang.psi.impl.ElixirAtIdentifierImplKt.identifierName;
import static org.elixir_lang.reference.ModuleAttribute.isTypeName;

public class KeywordPairColonInsteadOfTypeOperator extends LocalInspectionTool {
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

                                if (grandChild instanceof QuotableKeywordList) {
                                    QuotableKeywordList quotableKeywordList = (QuotableKeywordList) grandChild;
                                    List<QuotableKeywordPair> quotableKeywordPairList =
                                            quotableKeywordList.quotableKeywordPairList();

                                    if (quotableKeywordPairList.size() == 1) {
                                        QuotableKeywordPair quotableKeywordPair = quotableKeywordPairList.get(0);
                                        Quotable keywordKey = quotableKeywordPair.getKeywordKey();
                                        PsiElement keywordPairColon = keywordKey.getNextSibling();

                                        LocalQuickFix localQuickFix = new ConvertKeywordPairToTypeOperation(
                                                /* Can't be KEYWORD_PAIR_COLON because caret is never on the single
                                                   character in editor mode, only to left or right */
                                                keywordPairColon
                                        );

                                        problemsHolder.registerProblem(
                                                keywordPairColon,
                                                "Type specifications separate the name from the definition using `::`, not `:`",
                                                ProblemHighlightType.ERROR,
                                                localQuickFix
                                        );
                                    }
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
        return "Keyword pair colon (:) used in type spec instead of type operator (::)";
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
        return "KeywordPairColonInsteadOfTypeOperator";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }
}
