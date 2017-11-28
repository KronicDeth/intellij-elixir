package org.elixir_lang.eex.psi.parser;

import com.intellij.ide.highlighter.FileTypeRegistrator;
import com.intellij.ide.highlighter.HtmlFileType;
import com.intellij.ide.util.AppPropertiesComponentImpl;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.html.HTMLParserDefinition;
import com.intellij.lexer.EmbeddedTokenTypesProvider;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.fileTypes.impl.FileTypeManagerImpl;
import com.intellij.openapi.vfs.newvfs.FileAttribute;
import com.intellij.psi.*;
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings;
import com.intellij.psi.templateLanguages.TemplateDataLanguagePatterns;
import com.intellij.testFramework.MockSchemeManagerFactory;
import com.intellij.util.messages.MessageBus;
import org.elixir_lang.ElixirLanguage;
import org.elixir_lang.ElixirParserDefinition;
import org.elixir_lang.eex.Language;
import org.elixir_lang.eex.ParserDefinition;
import org.elixir_lang.eex.file.Type;
import org.elixir_lang.eex.file.view_provider.Factory;
import org.elixir_lang.parser_definition.ParsingTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.BeforeClass;
import org.picocontainer.MutablePicoContainer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Test extends ParsingTestCase {
    public Test() {
        super("eex", new ParserDefinition(), new ElixirParserDefinition(), new HTMLParserDefinition(), new PlainTextParserDefinition());
    }

    private static void registerFileType(@NotNull FileTypeManager fileTypeManager, FileType fileType) {
        fileTypeManager.registerFileType(fileType, Collections.singletonList(new ExtensionFileNameMatcher(fileType.getDefaultExtension())));
    }

    private static void unregisterAutoDetectionCacheAttribute() {
        try {
            Field ourRegisteredIdsField = FileAttribute.class.getDeclaredField("ourRegisteredIds");
            ourRegisteredIdsField.setAccessible(true);
            Set<String> ourRegisteredIds = (Set<String>) ourRegisteredIdsField.get(null);
            ourRegisteredIds.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Don't care
        }
    }

    public void testPhoenixTemplatesLayoutApp() {
        this.myFileExt = "html.eex";

        doTest();
    }

    public void testEExTemplate() {
        doTest();
    }

    public void testEExTemplateWithBindings() {
        doTest();
    }

    public void testStringSample() {
        doTest();
    }

    public void testEExTokenizerTestStringWithEmbeddedCode() {
        doTest();
    }

    public void testEExTokenizerTestStringsWithMoreThanOneLine() {
        doTest();
    }

    public void testEExTokenizerTestStringsWithMoreThanOneLineAndExpressionWithMoreThanOneLine(){
        doTest();
    }

    public void testEExTokenizerTestQuotation() {
        doTest();
    }

    public void testEExTokenizerTestQuotationWithDoEnd() {
        doTest();
    }

    public void testEExTokenizerTestQuotationWithInterpolation1() {
        doTest();
    }

    public void testEExTokenizerTestQuotationWithInterpolation2() {
        doTest();
    }

    public void testEExTokenizerTestComments() {
        doTest();
    }

    public void testEExTokenizerTestCommentsWithDoEnd() {
        doTest();
    }

    public void testEExTokenizerTestStringsWithEmbeddedDoEnd() {
        doTest();
    }

    public void testEExTokenizerTestStringsWithEmbeddedStabOperatorEnd() {
        doTest();
    }

    public void testEExTokenizerTestStringsWithEmbeddedKeywordsBlocks() {
        doTest();
    }

    public void testEExTokenizerTestTrimMode() {
        doTest();
    }

    public void testEExTokenizerTestTrimModeWithComment() {
       doTest();
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

    private void doTest() {
        doTest(true);

        assertWithoutLocalError();
    }

    @Override
    public void configureFromParserDefinition(@NotNull com.intellij.lang.ParserDefinition parserDefinition,
                                              @NotNull String extension) {
        myLanguage = parserDefinition.getFileNodeType().getLanguage();
        myFileExt = extension;
        addExplicitExtension(LanguageParserDefinitions.INSTANCE, myLanguage, parserDefinition);

        registerExtensionPoint(FileTypeRegistrator.EP_NAME, FileTypeRegistrator.class);

        MutablePicoContainer picoContainer = getApplication().getPicoContainer();
        MessageBus messageBus = messageBus(picoContainer);

        registerApplicationService(PropertiesComponent.class, new AppPropertiesComponentImpl());

        registerExtensionPoint(EmbeddedTokenTypesProvider.EXTENSION_POINT_NAME, EmbeddedTokenTypesProvider.class);

        unregisterAutoDetectionCacheAttribute();
        FileTypeManagerImpl fileTypeManager =
                new FileTypeManagerImpl(messageBus, new MockSchemeManagerFactory(), new AppPropertiesComponentImpl());

        registerFileType(fileTypeManager, HtmlFileType.INSTANCE);
        registerFileType(fileTypeManager, Type.INSTANCE);

        registerComponentInstance(picoContainer, FileTypeManager.class, fileTypeManager);
    }
}
