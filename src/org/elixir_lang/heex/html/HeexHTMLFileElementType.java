package org.elixir_lang.heex.html;

import com.intellij.lang.*;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.HtmlFileElementType;
import org.jetbrains.annotations.NotNull;

public class HeexHTMLFileElementType extends HtmlFileElementType {
    public static final HeexHTMLFileElementType INSTANCE = new HeexHTMLFileElementType();

    /** @see com.intellij.psi.tree.ILazyParseableElementType#doParseContents */
    @Override
    public ASTNode parseContents(ASTNode chameleon) {
        PsiElement psi = chameleon.getPsi();

        assert psi != null : "Bad chameleon: " + chameleon;

        Project project = psi.getProject();
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, new HeexHTMLLexer(), HTMLLanguage.INSTANCE, chameleon.getChars());
        PsiParser parser = (LanguageParserDefinitions.INSTANCE.forLanguage(HTMLLanguage.INSTANCE)).createParser(project);
        ASTNode node = parser.parse(this, builder);

        return node.getFirstChildNode();
    }

    @Override
    public @NotNull Language getLanguage() {
        return HTMLLanguage.INSTANCE;
    }
}
