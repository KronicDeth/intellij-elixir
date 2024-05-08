package org.elixir_lang.formatter.settings

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.IndentOptionsEditor
import com.intellij.application.options.SmartIndentOptionsEditor
import com.intellij.lang.Language
import com.intellij.openapi.util.BuildNumber
import com.intellij.psi.codeStyle.*
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.*
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.code_style.CodeStyleSettings

class LanguageCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {
    override fun createCustomSettings(settings: com.intellij.psi.codeStyle.CodeStyleSettings): CustomCodeStyleSettings =
        CodeStyleSettings(settings)

    override fun createConfigurable(
        baseSettings: com.intellij.psi.codeStyle.CodeStyleSettings,
        modelSettings: com.intellij.psi.codeStyle.CodeStyleSettings
    ): CodeStyleConfigurable = object : CodeStyleAbstractConfigurable(baseSettings, modelSettings, "Elixir") {
        override fun createPanel(settings: com.intellij.psi.codeStyle.CodeStyleSettings): CodeStyleAbstractPanel =
            ElixirCodeStyleMainPanel(currentSettings, settings)
    }

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> customizeSpaceSettings(consumer)
            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> customizeWrappingAndBracesSettings(consumer)
            SettingsType.LANGUAGE_SPECIFIC -> customizeLanguageSpecific(consumer)
            SettingsType.COMMENTER_SETTINGS -> customizeCodeGenerationSettings(consumer)
            SettingsType.BLANK_LINES_SETTINGS,
            SettingsType.INDENT_SETTINGS -> Unit
        }
    }

    private fun customizeCodeGenerationSettings(consumer: CodeStyleSettingsCustomizable) {
        consumer.showStandardOptions(*CodeGenerationPanel.getSupportedCommenterStandardOptionNames().toTypedArray())
    }

    private fun customizeSpaceSettings(consumer: CodeStyleSettingsCustomizable) {
        consumer.showStandardOptions( // SPACE_BEFORE_PARENTHESES group
            /* SPACE_BEFORE_METHOD_PARENTHESES - Disabled because space between function name and call arguments
                   is invalid for parenthesized arguments */
            /* SPACE_BEFORE_METHOD_CALL_PARENTHESES - Disabled because space between function name and call
                   arguments is invalid for parenthesized arguments */
            /* SPACE_BEFORE_IF_PARENTHESES - Disabled because `if` is not special, so no specific setting for it
                   since it's not a keyword unlike in Java */
            /* SPACE_BEFORE_FOR_PARENTHESES - Disabled because `for` is a special form, but you wouldn't put
                   parentheses around the `<-` clauses */
            /* SPACE_BEFORE_WHILE_PARENTHESES - There is no `while` in Elixir */ /* SPACE_BEFORE_SWITCH_PARENTHESES - `switch` in Java would be `case` in Elixir and no style
                   recommends parentheses for it */
            /* SPACE_BEFORE_CATCH_PARENTHESES - `catch` in Java would be `rescue` in Elixir, but `rescue` uses
                   `->` clauses, so its rules would apply */
            /* SPACE_BEFORE_SYNCHRONIZED_PARENTHESES - no `synchronized` in Elixir */ /* SPACE_BEFORE_ANNOTATION_PARAMETER_LIST - module attribute calls follow normal call rules and a
                   space isn't allowed between the function name and parenthesized arguments */
            // SPACE_AROUND_OPERATORS group
            "SPACE_AROUND_ASSIGNMENT_OPERATORS",  /* SPACE_AROUND_LOGICAL_OPERATORS - logical operators are mixed with bitwise operator in
                   AND_OPERATOR (and, &&, &&) and OR_OPERATOR (or, ||, |||), so there's no way to just space around
                   the logical version without inspecting the text value, but SpacingBuilder works at the ASTNode
                   level.  Additionally, all the operators can be overridden, so they don't HAVE to be logical even if Kernel defines them that way. */
            "SPACE_AROUND_EQUALITY_OPERATORS",
            "SPACE_AROUND_RELATIONAL_OPERATORS",  /* SPACE_AROUND_BITWISE_OPERATORS - Bitwise operators are mixed into tokens used for other purposes,
                   so they can't be spaced independently */
            "SPACE_AROUND_ADDITIVE_OPERATORS",
            "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",  /* SPACE_AROUND_SHIFT_OPERATORS - no dedicated shift operators in Elixir */
            "SPACE_AROUND_UNARY_OPERATOR",
            "SPACE_AROUND_LAMBDA_ARROW",  // SPACES_WITHIN group
            "SPACE_WITHIN_BRACES",
            "SPACE_WITHIN_BRACKETS",
            "SPACE_WITHIN_PARENTHESES",  // OTHER group
            "SPACE_BEFORE_COMMA",
            "SPACE_AFTER_COMMA"
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AFTER_CAPTURE_OPERATOR",
            "Capture operator (&)",
            SPACE_AFTER_OPERATORS
        )
        consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Match operator (=)")
        consumer.renameStandardOption(
            "SPACE_AROUND_EQUALITY_OPERATORS",
            "Comparison operators (!=, ==, =~, !==, ===)"
        )
        consumer.renameStandardOption("SPACE_AROUND_MULTIPLICATIVE_OPERATORS", "Multiplicative operators (*, /)")
        consumer.renameStandardOption("SPACE_AROUND_UNARY_OPERATOR", "Unary operators (!, ^, ~~~)")
        consumer.renameStandardOption("SPACE_AROUND_LAMBDA_ARROW", "Stab operator (->)")
        consumer.renameStandardOption("SPACE_WITHIN_BRACES", "Map (%{}), Struct (%Alias{}), and Tuple ({}) braces")
        consumer.renameStandardOption("SPACE_WITHIN_PARENTHESES", "Parentheses")
        val codeStyleSettingsCustomizableOptions = CodeStyleSettingsCustomizableOptions.getInstance()
        val spacesAroundOperators = codeStyleSettingsCustomizableOptions.SPACES_AROUND_OPERATORS
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_AND_OPERATORS",
            "And operators (&&, &&&)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_ARROW_OPERATORS",
            "Arrow operators (<~, |>, ~>, <<<, <<~, <|>, <~>, >>>, ~>>)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_ASSOCIATION_OPERATOR",
            "Association operator (=>)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_IN_MATCH_OPERATORS",
            "In match operators (<-, \\\\)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_OR_OPERATORS",
            "Or operators (||, |||)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_PIPE_OPERATOR",
            "Pipe operator (|)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_RANGE_OPERATOR",
            "Range operator (..)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_THREE_OPERATOR",
            "Three operator (^^^)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_TWO_OPERATORS",
            "Two operator (++, --, <>)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_AROUND_TYPE_OPERATOR",
            "Type operator (::)",
            spacesAroundOperators
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "SPACE_WITHIN_BITS",
            "Bit strings and binaries (<<>>)",
            codeStyleSettingsCustomizableOptions.SPACES_WITHIN
        )
    }

    private fun customizeWrappingAndBracesSettings(consumer: CodeStyleSettingsCustomizable) {
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "ALIGN_BOOLEAN_OPERANDS",
            "Align operands of `and` and `or` operators",
            null
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "ALIGN_PIPE_OPERANDS",
            "Align operands of pipe operator (|)",
            null
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "ALIGN_UNMATCHED_CALL_DO_BLOCKS",
            "Align unmatched call `do` blocks to",
            null, arrayOf(
                CodeStyleSettings.UnmatchedCallDoBlockAlignment.CALL.name,
                CodeStyleSettings.UnmatchedCallDoBlockAlignment.LINE.name
            ), intArrayOf(
                CodeStyleSettings.UnmatchedCallDoBlockAlignment.CALL.value,
                CodeStyleSettings.UnmatchedCallDoBlockAlignment.LINE.value
            )
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "ALIGN_TWO_OPERANDS",
            "Align operands of two operator (++, --, <>)",
            null
        )
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "ALIGN_TYPE_DEFINITION_TO_RIGHT_OF_OPERATOR",
            "Align type definition to right of operator (::)",
            null
        )
    }

    private fun customizeLanguageSpecific(consumer: CodeStyleSettingsCustomizable) {
        consumer.showCustomOption(
            CodeStyleSettings::class.java,
            "MIX_FORMAT",
            "Format files with `mix format`",
            "General"
        )
    }

    override fun getCodeSample(settingsType: SettingsType): String =
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> SPACING_CODE_SAMPLE
            SettingsType.WRAPPING_AND_BRACES_SETTINGS -> WRAPPING_AND_BRACES_SETTINGS_CODE_SAMPLE
            SettingsType.INDENT_SETTINGS -> INDENT_CODE_SAMPLE
            SettingsType.LANGUAGE_SPECIFIC ->
                INDENT_CODE_SAMPLE + SPACING_CODE_SAMPLE + WRAPPING_AND_BRACES_SETTINGS_CODE_SAMPLE
            SettingsType.BLANK_LINES_SETTINGS,
            SettingsType.COMMENTER_SETTINGS -> TODO()
        }

    override fun customizeDefaults(
        commonSettings: CommonCodeStyleSettings,
        indentOptions: CommonCodeStyleSettings.IndentOptions
    ) {
        indentOptions.INDENT_SIZE = 2
        indentOptions.CONTINUATION_INDENT_SIZE = 2
        indentOptions.TAB_SIZE = 2
    }

    override fun getIndentOptionsEditor(): IndentOptionsEditor = SmartIndentOptionsEditor()

    override fun getLanguage(): Language = ElixirLanguage

    companion object {
        private const val INDENT_CODE_SAMPLE = "defmodule Foo do\n" +
                "  @spec foo :: one |\n" +
                "               two |\n" +
                "               three\n" +
                "  def foo do\n" +
                "    receive do\n" +
                "      {:ok, value} -> value\n" +
                "      {:error, reason} -> reason\n" +
                "    end\n" +
                "  end\n" +
                "end"
        private const val SPACE_AFTER_OPERATORS = "After Operators"
        private const val SPACING_CODE_SAMPLE = "# Addition Operators\n" +
                "1 + 1\n" +
                "1 - 1\n" +
                "\n" +
                "# And Operators\n" +
                "& &1 && &2\n" +
                "true && true\n" +
                "& &1 &&& &2\n" +
                "0b11 &&& 0b01\n" +
                "\n" +
                "# Arrow Operators\n" +
                "a <~ b\n" +
                "a\n" +
                "|> b()\n" +
                "a ~> b\n" +
                "a <<< b\n" +
                "a <<~ b\n" +
                "a <|> b\n" +
                "a <~> b\n" +
                "a >>> b\n" +
                "a ~>> b\n" +
                "\n" +
                "# Association Operator\n" +
                "%{key => value}\n" +
                "%{map | key => updated_value}\n" +
                "\n" +
                "# Bit Strings and Binaries\n" +
                "<<one>>\n" +
                "\n" +
                "# Brackets\n" +
                "map[:key]\n" +
                "[head | tail]\n" +
                "\n" +
                "# Capture Operators\n" +
                "& &1 + &2\n" +
                "&Kernel.||/2\n" +
                "&foo/0\n" +
                "\n" +
                "# Comma\n" +
                "one(two: 2,)\n" +
                "one(two, three)\n" +
                "one two, three\n" +
                "one two, three: 3\n" +
                "[one: 1,]\n" +
                "[one: 1, two: 2]\n" +
                "%{one: 1,}\n" +
                "%{one: 1, two: 2}\n" +
                "%{one => 1,}\n" +
                "%{one => 1, two => 2}\n" +
                "\n" +
                "# Comparison Operators\n" +
                "1 != 2\n" +
                "1 == 1.0\n" +
                "\"abcd\" =~ ~r/c(d)/\n" +
                "1 !== 1.0\n" +
                "1.00 === 1.0\n" +
                "\n" +
                "# Curly Braces\n" +
                "{one}\n" +
                "%{key: :value}\n" +
                "%{key => value}\n" +
                "%One{field: value}\n" +
                "%One{field => value}\n" +
                "\n" +
                "# In Match Operators\n" +
                "param \\\\ default\n" +
                "{:ok, value} <- elements\n" +
                "\n" +
                "# Match Operator\n" +
                "a = 1\n" +
                "\n" +
                "# Multiplication Operators\n" +
                "0 * 1\n" +
                "0 / 1\n" +
                "\n" +
                "# Or Operators\n" +
                "false || true\n" +
                "0b10 ||| 0b01\n" +
                "\n" +
                "# Parentheses\n" +
                "()\n" +
                "(one)\n" +
                "one()\n" +
                "one(two)\n" +
                "\n" +
                "# Pipe Operator\n" +
                "[head | tail]\n" +
                "%{map | a: 1}\n" +
                "\n" +
                "# Range Operator\n" +
                "first..last\n" +
                "\n" +
                "# Relationship Operators\n" +
                "3 < 4\n" +
                "3 <= 3\n" +
                "3 >= 3\n" +
                "4 > 3\n" +
                "\n" +
                "# Stab Operator\n" +
                "fn a -> a == true end\n" +
                "\n" +
                "# Type Operator\n" +
                "@type a :: term\n" +
                "@type a(b) :: b\n" +
                "\n" +
                "# Two Operators\n" +
                "first_list ++ second_list\n" +
                "full_list -- removal_list\n" +
                "\"Hello\" <> \", world!\"\n" +
                "\n" +
                "# Three Operator\n" +
                "register ^^^ register\n" +
                "\n" +
                "# Unary Operators\n" +
                "\n" +
                "## Numeric Operands\n" +
                "+1\n" +
                "-1\n" +
                "!1\n" +
                "^1\n" +
                "~~~1\n" +
                "\n" +
                "## Non-numeric Operands\n" +
                "+negative\n" +
                "-positive\n" +
                "!false\n" +
                "^pinned\n" +
                "~~~mask\n" +
                "\n" +
                "# Word Operators\n" +
                "true and true\n" +
                "false or true\n" +
                "\n" +
                "## Numeric Operands\n" +
                "not 1\n" +
                "\n" +
                "## Non-numeric Operands\n" +
                "not true\n" +
                ""
        private const val WRAPPING_AND_BRACES_SETTINGS_CODE_SAMPLE = "defmodule Calcinator do\n" +
                "  def can(%__MODULE__{authorization_module: authorization_module, subject: subject}, action, target)\n" +
                "      when action in @actions and\n" +
                "           not is_nil(authorization_module) and\n" +
                "           (is_atom(target) or is_map(target) or is_list(target)) do\n" +
                "  end\n" +
                "\n" +
                "  defp authorized(%__MODULE__{authorization_module: authorization_module, subject: subject}, unfiltered, pagination)\n" +
                "       when is_list(unfiltered) and\n" +
                "            (is_nil(pagination) or is_map(pagination)) do\n" +
                "    {shallow_filtered, filtered_pagination} = case authorization_module.filter_can(unfiltered, subject, :show) do\n" +
                "      ^unfiltered ->\n" +
                "        {unfiltered, pagination}\n" +
                "      filtered_can ->\n" +
                "        {filtered_can, pagination}\n" +
                "    end\n" +
                "\n" +
                "    deep_filtered = authorization_module.filter_associations_can(shallow_filtered, subject, :show)\n" +
                "\n" +
                "    {deep_filtered, filtered_pagination}\n" +
                "  end\n" +
                "end\n" +
                "\n" +
                "defmodule Calcinator.Resources.Page do\n" +
                "  defstruct ~w(number size)a\n" +
                "\n" +
                "  @type t :: %__MODULE__{\n" +
                "               number: pos_integer,\n" +
                "               size: pos_integer,\n" +
                "             }\n" +
                "end\n"
    }
}
