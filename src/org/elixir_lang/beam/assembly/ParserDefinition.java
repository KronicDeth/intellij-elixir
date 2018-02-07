package org.elixir_lang.beam.assembly;

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
import org.elixir_lang.beam.assembly.psi.Types;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserDefinition implements com.intellij.lang.ParserDefinition {
    private static final IFileElementType FILE = new IFileElementType(Language.INSTANCE);
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        return Types.Factory.createElement(node);
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new File(viewProvider);
    }

    @NotNull
    @Override
    public Lexer createLexer(@Nullable Project project) {
        return new org.elixir_lang.beam.assembly.FlexLexerAdapter();
    }

    @Override
    public PsiParser createParser(Project project) {
        return new BEAMAssemblyParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    @Override
    public TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @NotNull
    @Override
    public TokenSet getStringLiteralElements() {
        return TokenSet.EMPTY;
    }

    @NotNull
    // Not @Override in IntelliJ < 2017.3.  Default implementation in >= 2017.3
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @Override
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return null;
    }
}
