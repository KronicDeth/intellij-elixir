package org.elixir_lang.heex;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.heex.File;
import org.elixir_lang.heex.Parser;
import org.elixir_lang.heex.file.ElementType;
import org.elixir_lang.heex.lexer.LookAhead;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.NotNull;

public class ParserDefinition implements com.intellij.lang.ParserDefinition {
    private static final TokenSet COMMENT_TOKENS = TokenSet.create(Types.COMMENT);
    private static final TokenSet STRING_LITERAL_ELEMENTS = TokenSet.EMPTY;
    private static final TokenSet WHITESPACE_TOKENS = TokenSet.create(TokenType.WHITE_SPACE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new LookAhead();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new Parser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return ElementType.INSTANCE;
    }

    @NotNull
    @Override
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE_TOKENS;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return COMMENT_TOKENS;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return STRING_LITERAL_ELEMENTS;
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode astNode) {
        return Types.Factory.createElement(astNode);
    }

    @Override
    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new File(fileViewProvider);
    }

    @Override
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MUST_NOT;
    }
}
