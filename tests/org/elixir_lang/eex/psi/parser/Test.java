package org.elixir_lang.eex.psi.parser;

import com.intellij.openapi.fileTypes.PlainTextParserDefinition;
import com.intellij.psi.LanguageFileViewProviders;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.templateLanguages.TemplateDataLanguagePatterns;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.eex.Language;
import org.elixir_lang.eex.ParserDefinition;
import org.elixir_lang.eex.file.view_provider.Factory;
import org.elixir_lang.parser_definition.ParsingTestCase;

public class Test extends ParsingTestCase {
    public Test() {
        super("eex", new ParserDefinition(), new ElixirParserDefinition(), new PlainTextParserDefinition());
    }

    public void testEExTemplate() {
        doTest(true);
    }

    public void testEExTemplateWithBindings() {
        doTest(true);
    }

    public void testStringSample() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return "testData/org/elixir_lang/eex/psi/parser/test";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registerApplicationService(TemplateDataLanguagePatterns.class, new TemplateDataLanguagePatterns());
        myProject.registerService(TemplateDataLanguageMappings.class, new TemplateDataLanguageMappings(myProject));
        LanguageFileViewProviders.INSTANCE.addExplicitExtension(Language.INSTANCE, new Factory());
    }
}
