package org.elixir_lang.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.formatter.Block;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelBuilder implements FormattingModelBuilder {
    private static final boolean DUMP_FORMATTING_AST = true;

    /**
     * Requests building the formatting model for a section of the file containing
     * the specified PSI element and its children.
     *
     * @param element  the top element for which formatting is requested.
     * @param settings the code style settings used for formatting.
     * @return the formatting model for the file.
     */
    @NotNull
    @Override
    public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
        PsiFile containingFile = element.getContainingFile();

        if (DUMP_FORMATTING_AST) {
            System.out.println("AST tree for " + containingFile.getName() + ":");
            printAST(containingFile.getNode(), 0);
        }

        Block block = new Block(
                element.getNode(),
                Wrap.createWrap(
                        WrapType.NORMAL,
                        false
                ),
                Alignment.createAlignment(),
                createSpaceBuilder(settings)
        );

        if (DUMP_FORMATTING_AST) {
            FormattingModelDumper.dumpFormattingModel(block, 2, System.out);
        }

        return FormattingModelProvider.createFormattingModelForPsiFile(containingFile, block, settings);
    }

    @NotNull
    private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
        CommonCodeStyleSettings elixirCommonSettings = settings.getCommonSettings(ElixirLanguage.INSTANCE);
        org.elixir_lang.code_style.CodeStyleSettings elixirCustomSettings =
                settings.getCustomSettings(org.elixir_lang.code_style.CodeStyleSettings.class);
        return new SpacingBuilder(settings, ElixirLanguage.INSTANCE)
                .around(ElixirTypes.AND_WORD_OPERATOR).spaces(1)
                .around(ElixirTypes.AND_SYMBOL_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_AND_OPERATORS)
                .around(ElixirTypes.ARROW_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_ARROW_OPERATORS)
                .after(ElixirTypes.CAPTURE_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AFTER_CAPTURE_OPERATOR)
                .around(ElixirTypes.COMPARISON_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_EQUALITY_OPERATORS)
                .after(ElixirTypes.FN).spaces(1)
                // MUST specific inside *_ADDITION_OPERATION as DUAL_OPERATOR is also used IN UNARY_PREFIX_OPERATOR
                .aroundInside(
                        ElixirTypes.DUAL_OPERATOR,
                        TokenSet.create(
                                ElixirTypes.MATCHED_ADDITION_OPERATION,
                                ElixirTypes.UNMATCHED_ADDITION_OPERATION
                        )
                ).spaceIf(elixirCommonSettings.SPACE_AROUND_ADDITIVE_OPERATORS)
                .aroundInside(
                        // Cannot contain `ElixirTypes.DUAL_OPERATOR` because a space makes them invalid.
                        // Cannot contain `ElixirTypes.NOT_OPERATOR` because it MUST have a space.
                        ElixirTypes.UNARY_OPERATOR,
                        TokenSet.create(
                                ElixirTypes.MATCHED_UNARY_NON_NUMERIC_OPERATION,
                                ElixirTypes.UNARY_NUMERIC_OPERATION,
                                ElixirTypes.UNMATCHED_UNARY_NON_NUMERIC_OPERATION
                        )
                ).spaceIf(elixirCommonSettings.SPACE_AROUND_UNARY_OPERATOR)
                .around(ElixirTypes.IN_MATCH_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_IN_MATCH_OPERATORS)
                .around(ElixirTypes.IN_OPERATOR).spaces(1)
                .around(ElixirTypes.MATCH_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                /* This isn't precisely strict enough as there's no check that there's a name or qualified name to the
                   left of `/` and an integer to the right of `/` before no space is allowed */
                .aroundInside(
                        ElixirTypes.DIVISION_OPERATOR,
                        TokenSet.create(
                                ElixirTypes.MATCHED_CAPTURE_NON_NUMERIC_OPERATION,
                                ElixirTypes.UNMATCHED_CAPTURE_NON_NUMERIC_OPERATION
                        )
                ).none()
                .around(
                        TokenSet.create(ElixirTypes.DIVISION_OPERATOR, ElixirTypes.MULTIPLICATION_OPERATOR)
                ).spaceIf(elixirCommonSettings.SPACE_AROUND_MULTIPLICATIVE_OPERATORS)
                .after(ElixirTypes.NOT_OPERATOR).spaces(1)
                .around(ElixirTypes.OR_WORD_OPERATOR).spaces(1)
                .around(ElixirTypes.OR_SYMBOL_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_OR_OPERATORS)
                .around(ElixirTypes.PIPE_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_PIPE_OPERATOR)
                .around(ElixirTypes.RELATIONAL_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_RELATIONAL_OPERATORS)
                .around(ElixirTypes.STAB_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_LAMBDA_ARROW)
                .around(ElixirTypes.THREE_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_THREE_OPERATOR)
                .around(ElixirTypes.TYPE_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_TYPE_OPERATOR)
                .around(ElixirTypes.WHEN_OPERATOR).spaces(1);
    }


    /**
     * Returns the TextRange which should be processed by the formatter in order to calculate the
     * indent for a new line when a line break is inserted at the specified offset.
     *
     * @param file            the file in which the line break is inserted.
     * @param offset          the line break offset.
     * @param elementAtOffset the parameter at {@code offset}
     * @return the range to reformat, or null if the default range should be used
     */
    @Nullable
    @Override
    public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
        return null;
    }

    // @see com.jetbrains.python.formatter.PythonFormattingModelBuilder
    private static void printAST(ASTNode node, int indent) {
        while (node != null) {
            for (int i = 0; i < indent; i++) {
                System.out.print(" ");
            }

            System.out.println(node.toString() + " " + node.getTextRange().toString());
            printAST(node.getFirstChildNode(), indent + 2);
            node = node.getTreeNext();
        }
    }
}
