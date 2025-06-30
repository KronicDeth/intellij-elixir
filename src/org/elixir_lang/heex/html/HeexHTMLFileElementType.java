package org.elixir_lang.heex.html;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.HtmlFileElementType;

public class HeexHTMLFileElementType extends HtmlFileElementType {
    public static final HeexHTMLFileElementType INSTANCE = new HeexHTMLFileElementType();

    /** @see com.intellij.psi.tree.ILazyParseableElementType#doParseContents */
    @Override
    public ASTNode parseContents(ASTNode chameleon) {
        PsiElement psi = chameleon.getPsi();

        assert psi != null : "Bad chameleon: " + chameleon;

        Project project = psi.getProject();
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, HeexHTMLLanguage.INSTANCE, chameleon.getChars());
        PsiParser parser = (LanguageParserDefinitions.INSTANCE.forLanguage(HeexHTMLLanguage.INSTANCE)).createParser(project);
        ASTNode node = parser.parse(this, builder);

        return node.getFirstChildNode();
    }
}
