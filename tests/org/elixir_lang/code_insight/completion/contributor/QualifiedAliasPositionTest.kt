package org.elixir_lang.code_insight.completion.contributor

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completionAttemptAtCaret
import org.elixir_lang.code_insight.completionStringsAtCaret

/**
 * Where a qualified name may and may not be completed as a function.
 *
 * The contributor's pattern is only `afterLeaf(".")`, so an expression, a directive, a definer's name
 * and a struct literal all arrive identically. Only the module being named is off limits; what these
 * constructs contain is ordinary code.
 */
class QualifiedAliasPositionTest : PlatformTestCase() {
    fun testFunctionsNotOfferedInAnAliasDirective() {
        assertNoFunctionsOfferedIn("alias_directive_usage.ex")
    }

    fun testFunctionsNotOfferedInAnImportDirective() {
        assertNoFunctionsOfferedIn("import_directive_usage.ex")
    }

    fun testFunctionsNotOfferedInARequireDirective() {
        assertNoFunctionsOfferedIn("require_directive_usage.ex")
    }

    fun testFunctionsNotOfferedInAUseDirective() {
        assertNoFunctionsOfferedIn("use_directive_usage.ex")
    }

    fun testFunctionsNotOfferedInAStructName() {
        assertNoFunctionsOfferedIn("struct_literal_usage.ex")
    }

    /**
     * `%Prefix.Mod.<caret>` before the braces offers nothing because no qualified name is found at all, not
     * because the struct guard refused it: `structOperation` needs its `mapArguments`, so without the
     * braces there is no `ElixirStructOperation` ancestor for the guard to see.
     */
    fun testFunctionsNotOfferedInAStructNameWithoutBraces() {
        assertNoFunctionsOfferedIn("struct_literal_no_braces_usage.ex")
    }

    /**
     * `isDefmoduleDeclarationName` recognises a `defmodule` by its `do` block, which a name still being
     * typed has not got yet.
     */
    fun testFunctionsNotOfferedInADefmoduleName() {
        assertNoFunctionsOfferedIn("defmodule_name_usage.ex")
    }

    /**
     * With the `do` block written, `isDefmoduleDeclarationName` nulls the reference, so resolution finds
     * nothing even with the guard removed. The refusal is over-determined and this cannot distinguish the
     * two; [testFunctionsNotOfferedInADefmoduleName] is what pins the guard.
     */
    fun testFunctionsNotOfferedInADefmoduleNameWithADoBlock() {
        assertNoFunctionsOfferedIn("defmodule_name_do_block_usage.ex")
    }

    fun testFunctionsNotOfferedInADefprotocolName() {
        assertNoFunctionsOfferedIn("defprotocol_name_usage.ex")
    }

    fun testFunctionsNotOfferedInADefimplName() {
        assertNoFunctionsOfferedIn("defimpl_name_usage.ex")
    }

    fun testFunctionsOfferedInADirectiveOptionValue() {
        assertFunctionsOfferedIn("use_option_value_usage.ex")
    }

    /** A struct's field value is ordinary code, unlike its name. */
    fun testFunctionsOfferedInAStructFieldValue() {
        assertFunctionsOfferedIn("struct_field_value_usage.ex")
    }

    fun testFunctionsOfferedInsideAnExistingCall() {
        assertFunctionsOfferedIn("existing_call_usage.ex")
    }

    /**
     * Between the braces an alias-shaped dummy cannot be called, so recovery flattens the copy and no
     * qualified name survives in it to read - the one shape needing the original file.
     */
    fun testFunctionsOfferedInsideAnExistingCallInAStructFieldValue() {
        assertFunctionsOfferedIn("struct_field_existing_call_usage.ex")
    }

    /** A directive's `do` block is ordinary code, unlike the module it names. */
    fun testFunctionsOfferedInsideAUseDoBlock() {
        assertFunctionsOfferedIn("use_do_block_usage.ex")
    }

    /** `primaryArguments()` is `null` for the `None`-arity call `use do … end`, so nothing is refused. */
    fun testFunctionsOfferedInsideAnArgumentlessUseDoBlock() {
        assertFunctionsOfferedIn("use_no_arguments_do_block_usage.ex")
    }

    private fun assertFunctionsOfferedIn(usageFile: String) {
        configure(usageFile)

        val offered = myFixture.completionStringsAtCaret().orEmpty()

        assertTrue(
            "Expected the module's functions where a call is valid, got: $offered",
            offered.containsAll(FUNCTION_NAMES)
        )
    }

    /**
     * The candidates are `null` both when nothing was offered and when a lone candidate auto-inserted, so
     * the document is checked too or a silent rewrite would pass.
     */
    private fun assertNoFunctionsOfferedIn(usageFile: String) {
        configure(usageFile)

        val before = myFixture.file.text
        val (candidates, text) = myFixture.completionAttemptAtCaret()

        assertFalse(
            "A function name was offered where only a module name is valid, got: $candidates",
            candidates.orEmpty().any { it in FUNCTION_NAMES }
        )
        assertEquals(
            "Completion rewrote the document where only a module name is valid",
            before,
            text
        )
    }

    private fun configure(usageFile: String) {
        myFixture.configureByFiles(usageFile, "public_function_declaration.ex")
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/completion/contributor/call_definition_clause"

    companion object {
        private val FUNCTION_NAMES = listOf("public_function1", "public_function2")
    }
}
