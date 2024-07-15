import com.intellij.application.options.CodeStyle
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.formatter.settings.CodeGenerationPanel

class CodeGenerationPanelTest : BasePlatformTestCase() {
    fun testGetSupportedCommenterStandardOptionNames() {
        val options = CodeGenerationPanel.getSupportedCommenterStandardOptionNames()

        // Always included options
        assertTrue(options.contains("LINE_COMMENT_AT_FIRST_COLUMN"))
        assertTrue(options.contains("LINE_COMMENT_ADD_SPACE"))
        assertTrue(options.contains("BLOCK_COMMENT_AT_FIRST_COLUMN"))
        assertTrue(options.contains("LINE_COMMENT_ADD_SPACE_ON_REFORMAT"))

        assertTrue(options.contains("BLOCK_COMMENT_ADD_SPACE"))
    }

    fun testPanelCreation() {
        val settings = CodeStyle.createTestSettings()
        val panel = CodeGenerationPanel(settings)

        assertNotNull(panel.panel)
        assertNotNull(panel.getPanelInner())
    }
}