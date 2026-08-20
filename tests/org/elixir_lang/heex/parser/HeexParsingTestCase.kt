package org.elixir_lang.heex.parser

import com.intellij.lang.LanguageASTFactory
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.html.HTMLParserDefinition
import com.intellij.lang.xml.XMLLanguage
import com.intellij.lang.xml.XmlASTFactory
import com.intellij.lang.xml.XmlTemplateTreePatcher
import com.intellij.lexer.EmbeddedTokenTypesProvider
import com.intellij.psi.LanguageFileViewProviders
import com.intellij.psi.templateLanguages.TemplateDataElementType
import com.intellij.psi.templateLanguages.TemplateDataLanguageMappings
import com.intellij.psi.templateLanguages.TemplateDataLanguagePatterns
import com.intellij.psi.xml.StartTagEndTokenProvider
import com.intellij.testFramework.ParsingTestCase
import com.intellij.xml.testFramework.XmlElementTypeServiceHelper.registerXmlElementTypeServices
import org.elixir_lang.ElixirParserDefinition
import org.elixir_lang.heex.HeexLanguage
import org.elixir_lang.heex.ParserDefinition
import org.elixir_lang.heex.file.view_provider.Factory
import org.elixir_lang.heex.html.HeexHTMLOuterLanguageRangePatcher

/**
 * Multi-root parsing tests for HEEx (`.html.heex`, three PSI roots: HEEx, HTML, Elixir).
 * Registration recipe copied from `MarkdownParsingTestCase` - the platform's own in-repo example
 * of a template language + HTML data root ParsingTestCase (verified byte-identical on this
 * plugin's minimum supported platform, 2025.3.6 / build 253, and on master).
 *
 * `checkAllPsiRoots()` defaults to `true` on the platform base class, so `checkResult` writes one
 * expected tree per `provider.getLanguages()` entry, named `<testName>.<LanguageID>.txt`.
 */
abstract class HeexParsingTestCase : ParsingTestCase(
    "org/elixir_lang/heex/parser",
    "html.heex",
    true,
    ParserDefinition(),
    HTMLParserDefinition(),
    ElixirParserDefinition()
) {
    override fun setUp() {
        super.setUp()
        registerXmlElementTypeServices(application, testRootDisposable)
        // Not part of the Markdown recipe: Markdown's own view provider hard-codes HTML as its data
        // language, so it never calls this. HEEx's view provider (org.elixir_lang.heex.file.ViewProvider)
        // asks TemplateDataLanguageMappings.getInstance(project) first, and a bare MockProjectEx has
        // no @Service(Level.PROJECT) auto-registration, so that call returns null (NPE) unless the
        // service is registered explicitly here.
        project.registerService(TemplateDataLanguageMappings::class.java, TemplateDataLanguageMappings(project))
        // TemplateDataLanguageMappings.getMapping falls through to
        // TemplateDataLanguagePatterns.getInstance() (an application-level service) when there is no
        // per-file override - same registration gap as above, one level up. Its assoc table is empty
        // by default (it is normally populated from a persisted user Settings choice), so this need
        // not be populated: an empty lookup is exactly what makes ViewProvider.templateDataLanguage
        // fall through to its final default, HeexLanguage.defaultTemplateLanguageFileType() = HTML.
        application.registerService(TemplateDataLanguagePatterns::class.java, TemplateDataLanguagePatterns())
        registerExtensionPoint(EmbeddedTokenTypesProvider.EXTENSION_POINT_NAME, EmbeddedTokenTypesProvider::class.java)
        registerExtensionPoint(StartTagEndTokenProvider.EP_NAME, StartTagEndTokenProvider::class.java)
        addExplicitExtension(LanguageFileViewProviders.INSTANCE, HeexLanguage.INSTANCE, Factory())
        addExplicitExtension(LanguageASTFactory.INSTANCE, XMLLanguage.INSTANCE, XmlASTFactory())
        addExplicitExtension(TemplateDataElementType.TREE_PATCHER, XMLLanguage.INSTANCE, XmlTemplateTreePatcher())
        // A bare ParsingTestCase never loads plugin.xml; without the production patcher the outer
        // ranges get a different default replacement and the HTML trees differ.
        addExplicitExtension(
            TemplateDataElementType.OuterLanguageRangePatcher.EXTENSION,
            HTMLLanguage.INSTANCE,
            HeexHTMLOuterLanguageRangePatcher()
        )
    }

    override fun getTestDataPath(): String = "testData"
}
