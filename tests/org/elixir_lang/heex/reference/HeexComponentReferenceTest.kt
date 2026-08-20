package org.elixir_lang.heex.reference

import com.intellij.codeInspection.htmlInspections.XmlEntitiesInspection
import com.intellij.ide.impl.HeadlessDataManager
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertGotoDeclarationLandsIn
import org.elixir_lang.heex.HeexLanguage
import org.elixir_lang.heex.inspections.HTMLInspectionSuppressor
import org.elixir_lang.heex.xml.HeexComponentResolver
import org.elixir_lang.injection.ElixirSigilInjector
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.SigilHeredocLiteral
import org.elixir_lang.settings.ElixirExperimentalSettings

/**
 * Behavior-level tests for HEEx component tags resolving to the `def`/`defp` they name: Go To
 * Declaration for `<.button>` and `<Module.button>`, tag-name completion, and the narrowed
 * [HTMLInspectionSuppressor] fallback.
 */
class HeexComponentReferenceTest : PlatformTestCase() {
    private var originalEnableHtmlInjection = false

    override fun setUp() {
        super.setUp()
        HeadlessDataManager.fallbackToProductionDataManager(myFixture.testRootDisposable)
        val settings = ElixirExperimentalSettings.instance
        originalEnableHtmlInjection = settings.state.enableHtmlInjection
        settings.state.enableHtmlInjection = true
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

    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/reference/heex_component"

    /** `<.button>` resolves to the sibling view module's `def button/1` (the LiveView convention). */
    fun testLocalComponentResolvesToSiblingModule() {
        myFixture.configureByFiles("sibling/page_live.html.heex", "sibling/page_live.ex")
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** `<.button>` resolves through an explicit `import` in the view module. */
    fun testLocalComponentResolvesThroughImport() {
        myFixture.configureByFiles(
            "import/page_live.html.heex",
            "import/page_live.ex",
            "import/core_components.ex"
        )
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** `<.button>` resolves through `use MyAppWeb, :html` expanding to `import MyAppWeb.CoreComponents`. */
    fun testLocalComponentResolvesThroughUseHtml() {
        myFixture.configureByFiles(
            "use_html/page_live.html.heex",
            "use_html/page_live.ex",
            "use_html/web.ex",
            "use_html/core_components.ex"
        )
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** `<MyAppWeb.CoreComponents.button>` resolves to the fully-qualified module's `def button/1`. */
    fun testRemoteComponentResolvesFullyQualified() {
        myFixture.configureByFiles("remote_qualified/page_live.html.heex", "remote_qualified/core_components.ex")
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** `<Widgets.button>` resolves through `alias MyAppWeb.CoreComponents, as: Widgets`. */
    fun testRemoteComponentResolvesThroughAlias() {
        myFixture.configureByFiles(
            "remote_alias/page_live.html.heex",
            "remote_alias/page_live.ex",
            "remote_alias/core_components.ex"
        )
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** The Phoenix 1.7+ `embed_templates` convention: `controllers/page_html/index.html.heex` -> `controllers/page_html.ex`. */
    fun testLocalComponentResolvesThroughEmbedTemplatesConvention() {
        myFixture.configureByFiles(
            "embed_templates/controllers/page_html/index.html.heex",
            "embed_templates/controllers/page_html.ex"
        )
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** The Phoenix 1.6 `templates/` -> `views/` convention: `templates/page/index.html.heex` -> `views/page_view.ex`. */
    fun testLocalComponentResolvesThroughTemplatesViewsConvention() {
        myFixture.configureByFiles(
            "templates_views/templates/page/index.html.heex",
            "templates_views/views/page_view.ex"
        )
        myFixture.assertGotoDeclarationLandsIn("button", "a def clause") { CallDefinitionClause.`is`(it) }
    }

    /** `<.button>` inside a `~H` sigil resolves to `def button/1` in the same module. */
    fun testLocalComponentResolvesInsideHSigil() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def render(assigns) do
                    ~H'''
                    <.button>Click</.button>
                    '''
                  end

                  def button(assigns) do
                    ~H''
                  end
                end
            """.trimIndent()
        )

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredocLiteral::class.java)
        checkNotNull(sigilHeredoc) { "Sigil host not found in test file" }
        val injected = InjectedLanguageManager.getInstance(project).getInjectedPsiFiles(sigilHeredoc).orEmpty()
        val heexRoot = injected
            .map { it.first.containingFile }
            .firstOrNull { it.language.isKindOf(HeexLanguage.INSTANCE) }
        checkNotNull(heexRoot) { "Expected a HEEx injection, got: ${injected.map { it.first.containingFile.language }}" }

        val htmlRoot = heexRoot.viewProvider.getPsi(com.intellij.lang.html.HTMLLanguage.INSTANCE)
        val tag = PsiTreeUtil.findChildOfType(htmlRoot, XmlTag::class.java)!!
        assertEquals(".button", tag.name)

        val declaration = HeexComponentResolver.resolveDeclaration(tag)
        assertNotNull("Expected <.button> to resolve inside the ~H sigil", declaration)
        assertEquals("button", declaration!!.text)
    }

    /**
     * The candidate list [HeexComponentTagNameProvider] offers is the view module's arity-1 defs,
     * and only those - tested directly against [HeexComponentResolver], not through the completion
     * popup: tag-name completion does not fire inside a `.heex` file even for built-in HTML tags,
     * a separate gap in the multi-root HTML wiring.
     */
    fun testLocalComponentDefinitionsAreArity1Defs() {
        myFixture.configureByFiles("completion/page_live.html.heex", "completion/page_live.ex")
        val tag = PsiTreeUtil.findChildOfType(
            myFixture.file.viewProvider.getPsi(com.intellij.lang.html.HTMLLanguage.INSTANCE),
            XmlTag::class.java
        )!!

        val names = HeexComponentResolver.localComponentDefinitions(tag).mapNotNull { CallDefinitionClause.nameIdentifier(it)?.text }

        assertTrue("names=$names", "button" in names)
        assertTrue("icon" in names)
        assertFalse("wrong_arity" in names)
    }

    /**
     * [HTMLInspectionSuppressor] suppresses `HtmlUnknownTag` for a resolved and an unresolved but
     * syntactically valid component tag, and not for a dotted name that is not component syntax.
     */
    fun testInspectionSuppressorIsNarrowedToComponentSyntax() {
        myFixture.configureByText(
            "suppressor_check.html.heex",
            "<.button>Click</.button><.nonexistent>Click</.nonexistent><Foo.Bar>Click</Foo.Bar>"
        )
        val suppressor = HTMLInspectionSuppressor()

        assertTrue(
            "a resolved component should be suppressed",
            suppressor.isSuppressedFor(startTagNameElement(".button"), XmlEntitiesInspection.TAG_SHORT_NAME)
        )
        assertTrue(
            "an unresolved but syntactically valid component should still be suppressed",
            suppressor.isSuppressedFor(startTagNameElement(".nonexistent"), XmlEntitiesInspection.TAG_SHORT_NAME)
        )
        assertFalse(
            "a dotted name that is not valid HEEx component syntax must not be suppressed",
            suppressor.isSuppressedFor(startTagNameElement("Foo.Bar"), XmlEntitiesInspection.TAG_SHORT_NAME)
        )
    }

    private fun startTagNameElement(tagName: String): com.intellij.psi.PsiElement {
        val htmlRoot = myFixture.file.viewProvider.getPsi(com.intellij.lang.html.HTMLLanguage.INSTANCE)
        val tag = PsiTreeUtil.findChildrenOfType(htmlRoot, XmlTag::class.java).first { it.name == tagName }
        return tag.node.findChildByType(com.intellij.psi.xml.XmlTokenType.XML_NAME)?.psi ?: tag
    }
}
