package org.elixir_lang.injection

import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.heex.HeexLanguage
import org.elixir_lang.psi.ElixirUnmatchedAtOperation
import org.elixir_lang.psi.SigilHeredocLiteral
import org.elixir_lang.settings.ElixirExperimentalSettings
import org.elixir_lang.eex.Language as EexLanguage

/**
 * `~H` sigils must inject the same three-root HEEx tree a `.heex` file gets - HTML data, Elixir
 * code, and the HEEx root that layers them - not plain HTML text. `~E` and `~L` stay plain EEx.
 */
class HeexSigilInjectionTest : PlatformTestCase() {
    private var originalEnableHtmlInjection = false

    override fun setUp() {
        super.setUp()
        val settings = ElixirExperimentalSettings.instance
        originalEnableHtmlInjection = settings.state.enableHtmlInjection
        settings.state.enableHtmlInjection = true
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        InjectedLanguageManager
            .getInstance(project)
            .registerMultiHostInjector(ElixirSigilInjector(), testRootDisposable)
    }

    override fun tearDown() {
        try {
            ElixirExperimentalSettings.instance.state.enableHtmlInjection = originalEnableHtmlInjection
        } finally {
            super.tearDown()
        }
    }

    fun testHSigilInjectsHeexWithHtmlAndElixirRoots() {
        val injectedFile = injectedHeexFile(
            """
                defmodule Test do
                  def render(assigns) do
                    ~H'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        assertEquals(
            "The injected base language must be HEEx, not plain HTML",
            HeexLanguage.INSTANCE,
            injectedFile.language
        )

        val viewProvider = injectedFile.viewProvider
        assertTrue(
            "Expected an HTML data root in the injected view provider, got: ${viewProvider.languages}",
            viewProvider.languages.any { it.isKindOf(HTMLLanguage.INSTANCE) }
        )
        assertTrue(
            "Expected an Elixir root in the injected view provider, got: ${viewProvider.languages}",
            viewProvider.languages.any { it.isKindOf(ElixirLanguage) }
        )
    }

    fun testHSigilHtmlRootIsRealHtmlPsiWithNoErrors() {
        val injectedFile = injectedHeexFile(
            """
                defmodule Test do
                  def render(assigns) do
                    ~H'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val htmlRoot = injectedFile.viewProvider.getPsi(HTMLLanguage.INSTANCE)
        assertNotNull("Expected an HTML root", htmlRoot)
        val divTag = PsiTreeUtil.findChildOfType(htmlRoot, XmlTag::class.java)
        assertNotNull("Expected the <div> to parse as real HTML PSI, not text", divTag)
        assertEquals("div", divTag!!.name)

        for (root in injectedFile.viewProvider.allFiles) {
            assertEquals(
                "No PsiErrorElement expected in ${root.language} root, found: " +
                        PsiTreeUtil.findChildrenOfType(root, PsiErrorElement::class.java)
                            .joinToString { it.errorDescription },
                0,
                PsiTreeUtil.findChildrenOfType(root, PsiErrorElement::class.java).size
            )
        }
    }

    fun testHSigilElixirExpressionProducesElixirPsi() {
        val injectedFile = injectedHeexFile(
            """
                defmodule Test do
                  def render(assigns) do
                    ~H'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val elixirRoot = injectedFile.viewProvider.getPsi(ElixirLanguage)
        assertNotNull("Expected an Elixir root", elixirRoot)
        // The embedded Elixir root parses as an "Unmatched" fragment (an expression, not a full
        // module) - the same shape EEx's own embedded tags use - so `@x` is
        // ElixirUnmatchedAtOperation, not the top-level-file ElixirAtIdentifier.
        val atOperation = PsiTreeUtil.findChildOfType(elixirRoot, ElixirUnmatchedAtOperation::class.java)
        assertNotNull(
            "Expected `@x` inside <%= %> to parse as Elixir PSI, not HTML text",
            atOperation
        )
    }

    fun testESigilInjectsEexNotHeex() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~E'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc).orEmpty()
        assertTrue(
            "Expected an EEx injection for ~E, got: ${injected.map { it.first.let { p -> p.containingFile.language } }}",
            injected.any { it.first.containingFile.language.isKindOf(EexLanguage.INSTANCE) }
        )
        assertFalse(
            "~E must not inject HEEx",
            injected.any { it.first.containingFile.language.isKindOf(HeexLanguage.INSTANCE) }
        )
    }

    fun testESigilInjectsNothingWhenSettingDisabled() {
        ElixirExperimentalSettings.instance.state.enableHtmlInjection = false

        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~E'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc)
        assertTrue(
            "Expected no injection while the experimental setting is off, got: $injected",
            injected.isNullOrEmpty()
        )
    }

    fun testLSigilStillInjectsEexNotHeex() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~L'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc).orEmpty()
        assertTrue(
            "Expected an EEx injection for ~L, got: ${injected.map { it.first.let { p -> p.containingFile.language } }}",
            injected.any { it.first.containingFile.language.isKindOf(EexLanguage.INSTANCE) }
        )
        assertFalse(
            "~L must not inject HEEx",
            injected.any { it.first.containingFile.language.isKindOf(HeexLanguage.INSTANCE) }
        )
    }

    fun testHSigilInjectsNothingWhenSettingDisabled() {
        ElixirExperimentalSettings.instance.state.enableHtmlInjection = false

        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~H'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc)
        assertTrue(
            "Expected no injection while the experimental setting is off, got: $injected",
            injected.isNullOrEmpty()
        )
    }

    fun testLSigilInjectsNothingWhenSettingDisabled() {
        ElixirExperimentalSettings.instance.state.enableHtmlInjection = false

        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~L'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc)
        assertTrue(
            "Expected no injection while the experimental setting is off, got: $injected",
            injected.isNullOrEmpty()
        )
    }

    fun testLSigilDataRootIsHtmlNotPlainText() {
        val viewProvider = injectedEexViewProvider("L")

        assertEquals(
            "~L is LiveView's HTML template sigil, so its data root must be HTML, not plain text",
            HTMLLanguage.INSTANCE,
            viewProvider.templateDataLanguage
        )

        val divTag = PsiTreeUtil.findChildOfType(viewProvider.getPsi(HTMLLanguage.INSTANCE), XmlTag::class.java)
        assertNotNull("Expected the <div> to parse as real HTML PSI, not text", divTag)
        assertEquals("div", divTag!!.name)
    }

    fun testESigilDataRootIsHtmlNotPlainText() {
        val viewProvider = injectedEexViewProvider("E")

        assertEquals(
            "Phoenix.HTML's ~E is an HTML template sigil, so its data root must be HTML, not plain text",
            HTMLLanguage.INSTANCE,
            viewProvider.templateDataLanguage
        )

        val divTag = PsiTreeUtil.findChildOfType(viewProvider.getPsi(HTMLLanguage.INSTANCE), XmlTag::class.java)
        assertNotNull("Expected the <div> to parse as real HTML PSI, not text", divTag)
        assertEquals("div", divTag!!.name)
    }

    private fun injectedEexViewProvider(sigilName: String): TemplateLanguageFileViewProvider {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~$sigilName'''
                    <div><%= @x %></div>
                    '''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc).orEmpty()
        val eexRoot = injected
            .map { it.first.containingFile }
            .firstOrNull { it.language.isKindOf(EexLanguage.INSTANCE) }
        checkNotNull(eexRoot) { "Expected an EEx injection, got: ${injected.map { it.first.containingFile.language }}" }

        return eexRoot.viewProvider as TemplateLanguageFileViewProvider
    }

    private fun injectedHeexFile(text: String): PsiFile {
        myFixture.configureByText("test.ex", text)
        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }

        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc).orEmpty()
        val heexRoot = injected
            .map { it.first.containingFile }
            .firstOrNull { it.language.isKindOf(HeexLanguage.INSTANCE) }
        checkNotNull(heexRoot) { "Expected a HEEx injection, got: ${injected.map { it.first.containingFile.language }}" }

        return heexRoot
    }
}
