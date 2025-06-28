package org.elixir_lang.heex.html;

import com.intellij.lang.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.ParsingDiagnostics;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.HtmlFileElementType;

public class HeexHTMLFileElementType extends HtmlFileElementType {
    /** @see com.intellij.psi.tree.ILazyParseableElementType#doParseContents */
    @Override
    public ASTNode parseContents(ASTNode chameleon) {
        PsiElement psi = chameleon.getPsi();

        assert psi != null : "Bad chameleon: " + chameleon;

        Project project = psi.getProject();
        Language languageForParser = this.getLanguageForParser(psi);
        PsiBuilder builder = PsiBuilderFactory.getInstance().createBuilder(project, chameleon, null, HeexHTMLLanguage.INSTANCE, chameleon.getChars());
        PsiParser parser = (LanguageParserDefinitions.INSTANCE.forLanguage(HeexHTMLLanguage.INSTANCE)).createParser(project);
        long startTime = System.nanoTime();
        ASTNode node = parser.parse(this, builder);
        ParsingDiagnostics.registerParse(builder, languageForParser, System.nanoTime() - startTime);

        return node.getFirstChildNode();
    }
}
