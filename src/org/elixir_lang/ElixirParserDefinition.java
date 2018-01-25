package org.elixir_lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.parser.ElixirParser;
import org.elixir_lang.psi.ElixirFile;
import org.elixir_lang.psi.ElixirTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Created by luke.imhoff on 8/2/14.
 */
public class ElixirParserDefinition implements ParserDefinition {
    public static final TokenSet COMMENTS = TokenSet.create(ElixirTypes.COMMENT);
    public static final TokenSet STRING_LITERALS = TokenSet.create(
            ElixirTypes.CHAR_LIST_FRAGMENT,
            ElixirTypes.REGEX_FRAGMENT,
            ElixirTypes.SIGIL_FRAGMENT,
            ElixirTypes.STRING_FRAGMENT,
            ElixirTypes.WORDS_FRAGMENT
    );

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new ElixirLexer();
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING_LITERALS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new ElixirParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return org.elixir_lang.psi.stub.type.File.INSTANCE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new ElixirFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        SpaceRequirements spaceRequirements;

        if (left.getElementType() == ElixirTypes.KEYWORD_PAIR_COLON) {
            spaceRequirements = SpaceRequirements.MUST;
        } else {
            spaceRequirements = SpaceRequirements.MAY;
        }

        return spaceRequirements;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return ElixirTypes.Factory.createElement(node);
    }
}
