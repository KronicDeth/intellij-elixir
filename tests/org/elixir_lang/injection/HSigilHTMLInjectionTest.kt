package org.elixir_lang.injection

import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.psi.SigilHeredoc
import org.elixir_lang.settings.ElixirExperimentalSettings

class HSigilHTMLInjectionTest : BasePlatformTestCase() {

    private var originalEnableHtmlInjection: Boolean = false

    override fun getTestDataPath(): String = "testData/org/elixir_lang/injection"

    override fun setUp() {
        super.setUp()
        // Enable HTML injection for sigils during tests
        val settings = ElixirExperimentalSettings.instance
        originalEnableHtmlInjection = settings.state.enableHtmlInjection
        settings.state.enableHtmlInjection = true
    }

    override fun tearDown() {
        try {
            // Restore original setting
            ElixirExperimentalSettings.instance.state.enableHtmlInjection = originalEnableHtmlInjection
        } finally {
            super.tearDown()
        }
    }

    fun testHSigilHasHTMLInjection() {
        myFixture.configureByFile("html_injection.ex")

        // Find the ~H sigil heredoc in the file
        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredoc::class.java)
        assertNotNull("Should find a SigilHeredoc element", sigilHeredoc)

        // Verify it's an ~H sigil
        assertEquals('H', sigilHeredoc!!.sigilName())

        // Check for injected content
        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)
        val injectedPsiFiles = injectedLanguageManager.getInjectedPsiFiles(sigilHeredoc)

        assertNotNull("Should have injected PSI files", injectedPsiFiles)
        assertTrue("Should have at least one injection", injectedPsiFiles!!.isNotEmpty())

        // Verify the injected language is HTML
        val injectedFile = injectedPsiFiles[0].first
        assertEquals("Injected language should be HTML", HTMLLanguage.INSTANCE, injectedFile.language)
    }

    fun testHSigilInjectedContentContainsHTML() {
        myFixture.configureByFile("html_injection.ex")

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredoc::class.java)
        assertNotNull(sigilHeredoc)

        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)
        val injectedPsiFiles = injectedLanguageManager.getInjectedPsiFiles(sigilHeredoc!!)

        assertNotNull(injectedPsiFiles)
        assertTrue(injectedPsiFiles!!.isNotEmpty())

        // Get the injected file content
        val injectedFile = injectedPsiFiles[0].first
        val injectedText = injectedFile.text

        // Verify HTML content is present
        assertTrue("Should contain <div> tag", injectedText.contains("<div>") || injectedText.contains("div"))
        assertTrue("Should contain <a> tag", injectedText.contains("<a") || injectedText.contains("href"))
    }

    fun testFindInjectedElementAtOffset() {
        myFixture.configureByFile("html_injection.ex")

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredoc::class.java)
        assertNotNull(sigilHeredoc)

        // Find the offset of "div" in the file
        val fileText = myFixture.file.text
        val divOffset = fileText.indexOf("<div>")
        assertTrue("Should find <div> in file", divOffset > 0)

        // Try to find injected element at the offset (inside the div tag)
        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)
        val injectedElement = injectedLanguageManager.findInjectedElementAt(myFixture.file, divOffset + 1)

        // The injected element should exist and be in HTML (or XML, since HTML elements are XML)
        assertNotNull("Should find injected element at offset", injectedElement)
        // HTML inherits from XML, so the element might be parsed as XML
        val language = injectedElement!!.language
        assertTrue(
            "Injected element should be in HTML or XML, was: $language",
            language == HTMLLanguage.INSTANCE || language.baseLanguage == HTMLLanguage.INSTANCE ||
            language.id == "XML" || language.id == "HTML"
        )
    }

    fun testSigilHeredocIsValidHost() {
        myFixture.configureByFile("html_injection.ex")

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredoc::class.java)
        assertNotNull(sigilHeredoc)

        // SigilHeredoc should be a valid injection host
        assertTrue("SigilHeredoc should be a valid host", sigilHeredoc!!.isValidHost)
    }

    fun testGetTopLevelFile() {
        myFixture.configureByFile("html_injection.ex")

        val sigilHeredoc = PsiTreeUtil.findChildOfType(myFixture.file, SigilHeredoc::class.java)
        assertNotNull(sigilHeredoc)

        val injectedLanguageManager = InjectedLanguageManager.getInstance(project)
        val injectedPsiFiles = injectedLanguageManager.getInjectedPsiFiles(sigilHeredoc!!)
        assertNotNull(injectedPsiFiles)
        assertTrue(injectedPsiFiles!!.isNotEmpty())

        val injectedFile = injectedPsiFiles[0].first

        // Getting the top-level file from injected content should return the host file
        val topLevelFile = injectedLanguageManager.getTopLevelFile(injectedFile)
        assertEquals("Top level file should be the Elixir file", myFixture.file, topLevelFile)
    }
}
