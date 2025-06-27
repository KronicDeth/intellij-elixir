package org.elixir_lang.heex.element_type;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.IFileElementType;
import org.elixir_lang.ElixirLanguage;

/**
 * Both Elixir and enough of the HEEx tags and {@link org.elixir_lang.heex.psi.Types#DATA}, so that Elixir parses
 * correctly, such as separating {@link org.elixir_lang.heex.psi.Types#ELIXIR} inside {@code <%= %>} tags, so that the
 * Elixir.bnf parses it like it was an interpolated expression separated by an outer string instead of adjacent Elixir
 * expressions.
 */
public class EmbeddedElixir extends IFileElementType {
    public EmbeddedElixir() {
        super(ElixirLanguage.INSTANCE);
    }

    @Override
    public ASTNode parseContents(ASTNode chameleon) {
        PsiElement psi = chameleon.getPsi();

        assert psi != null : "Bad chameleon: " + chameleon;

        Project project = psi.getProject();
        Language languageForParser = this.getLanguageForParser(psi);
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(
                project,
                chameleon,
                new org.elixir_lang.heex.lexer.EmbeddedElixir(project),
                languageForParser,
                chameleon.getChars()
        );
        PsiParser parser = LanguageParserDefinitions.INSTANCE.forLanguage(languageForParser).createParser(project);
        ASTNode node = parser.parse(this, builder);
        return node.getFirstChildNode();
    }
}
