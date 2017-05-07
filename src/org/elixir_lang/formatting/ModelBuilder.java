package org.elixir_lang.formatting;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.formatter.Block;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelBuilder implements FormattingModelBuilder {
    private static final boolean DUMP_FORMATTING_AST = false;

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
                .around(ElixirTypes.COMPARISON_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_EQUALITY_OPERATORS)
                .around(ElixirTypes.IN_MATCH_OPERATOR).spaceIf(elixirCustomSettings.SPACE_AROUND_IN_MATCH_OPERATORS)
                .around(ElixirTypes.MATCH_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_ASSIGNMENT_OPERATORS)
                .around(ElixirTypes.RELATIONAL_OPERATOR).spaceIf(elixirCommonSettings.SPACE_AROUND_RELATIONAL_OPERATORS);
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
