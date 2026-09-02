package org.elixir_lang.editor

import com.intellij.application.options.CodeStyle
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.CommenterOption
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider.SettingsType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.PlatformTestCase

/**
 * Pins "Comment with Line Comment" honouring the Elixir Code Generation settings.
 *
 * The two halves need each other. Placing the marker at the code's indent is platform behaviour that
 * has always worked for any language with a `Commenter`, so asserting the document alone would have
 * passed before the option was reachable and says nothing about this plugin. Offering the option
 * alone says nothing about what flipping it does. Together they pin the chain a user walks: the
 * checkbox exists in Elixir's Code Generation tab, and setting it changes the text.
 *
 * They also fail to different mutations, which is what makes the pair discriminating rather than one
 * test with two assertions - dropping the `lang.commenter` registration kills the document half while
 * the surface half stays green, and dropping the `COMMENTER_SETTINGS` branch from
 * [org.elixir_lang.formatter.settings.LanguageCodeStyleSettingsProvider] does the reverse.
 */
class CommentByLineCommentTest : PlatformTestCase() {
    fun testFirstColumnIsTheDefault() {
        assertCommentLine(
            lineCommentAtFirstColumn = true,
            lineCommentAddSpace = false,
            before = """
                def something() do
                  <caret>res = func(x, y)
                end
            """,
            after = """
                def something() do
                #  res = func(x, y)
                end
            """
        )
    }

    fun testMarkerGoesToTheCodeIndentWhenFirstColumnIsOff() {
        assertCommentLine(
            lineCommentAtFirstColumn = false,
            lineCommentAddSpace = false,
            before = """
                def something() do
                  <caret>res = func(x, y)
                end
            """,
            after = """
                def something() do
                  #res = func(x, y)
                end
            """
        )
    }

    fun testAddSpaceSeparatesTheMarkerFromTheCode() {
        assertCommentLine(
            lineCommentAtFirstColumn = false,
            lineCommentAddSpace = true,
            before = """
                def something() do
                  <caret>res = func(x, y)
                end
            """,
            after = """
                def something() do
                  # res = func(x, y)
                end
            """
        )
    }

    /**
     * The reporter's second ask on
     * [#2872](https://github.com/KronicDeth/intellij-elixir/issues/2872): a new comment lines up with
     * an already-commented line above it rather than with its own code.
     *
     * `CommentByLineCommentHandler.computeMinIndent` reads the line before the block and folds that
     * comment's own indent in with `IndentData.min`, so the marker lands at the shallower column and
     * the code keeps the rest of its whitespace. Same setting as the cases above - no separate
     * switch, which is why the issue closes whole rather than leaving this half open.
     */
    fun testMarkerLinesUpWithACommentedLineAbove() {
        assertCommentLine(
            lineCommentAtFirstColumn = false,
            lineCommentAddSpace = false,
            before = """
                def something() do
                  #first = func(x, y)
                    <caret>second = func(x, y)
                end
            """,
            after = """
                def something() do
                  #first = func(x, y)
                  #  second = func(x, y)
                end
            """
        )
    }

    /**
     * The discriminating half of [testMarkerLinesUpWithACommentedLineAbove]: same line at the same
     * indent, uncommented neighbour. Without it the inheriting case is satisfied by commenting at the
     * line's own indent and proves nothing.
     */
    fun testMarkerIgnoresAnUncommentedLineAbove() {
        assertCommentLine(
            lineCommentAtFirstColumn = false,
            lineCommentAddSpace = false,
            before = """
                def something() do
                  first = func(x, y)
                    <caret>second = func(x, y)
                end
            """,
            after = """
                def something() do
                  first = func(x, y)
                    #second = func(x, y)
                end
            """
        )
    }

    /**
     * `getSupportedFields` runs the plugin's own `customizeSettings` through a collecting consumer,
     * so this asserts the Code Generation tab reaches the user rather than re-asserting the constant
     * list the tab is built from.
     */
    fun testCodeGenerationOffersTheCommenterOptions() {
        val supportedFields =
            LanguageCodeStyleSettingsProvider.forLanguage(ElixirLanguage)!!
                .getSupportedFields(SettingsType.COMMENTER_SETTINGS)

        assertContainsElements(
            supportedFields,
            CommenterOption.LINE_COMMENT_AT_FIRST_COLUMN.name,
            CommenterOption.LINE_COMMENT_ADD_SPACE.name,
            CommenterOption.LINE_COMMENT_ADD_SPACE_ON_REFORMAT.name,
            CommenterOption.BLOCK_COMMENT_AT_FIRST_COLUMN.name,
            CommenterOption.BLOCK_COMMENT_ADD_SPACE.name
        )
    }

    private fun assertCommentLine(
        lineCommentAtFirstColumn: Boolean,
        lineCommentAddSpace: Boolean,
        before: String,
        after: String
    ) {
        myFixture.configureByText("test.ex", before.trimIndent().trim())

        CodeStyle.doWithTemporarySettings(project, CodeStyle.getSettings(myFixture.file)) { settings ->
            settings.getCommonSettings(ElixirLanguage).apply {
                LINE_COMMENT_AT_FIRST_COLUMN = lineCommentAtFirstColumn
                LINE_COMMENT_ADD_SPACE = lineCommentAddSpace
            }

            myFixture.performEditorAction(IdeActions.ACTION_COMMENT_LINE)
        }

        myFixture.checkResult(after.trimIndent().trim())
    }
}
