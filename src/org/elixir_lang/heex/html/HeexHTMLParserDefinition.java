package org.elixir_lang.heex.html;

import com.intellij.lang.html.HTMLParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLParserDefinition extends HTMLParserDefinition {
    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new HeexHTMLLexer();
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new HeexHTMLFileImpl(viewProvider);
    }
}
