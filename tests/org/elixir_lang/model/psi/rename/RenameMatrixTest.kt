package org.elixir_lang.model.psi.rename

import com.intellij.openapi.command.WriteCommandAction
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.renameTargetAtCaret
import java.io.File

/**
 * Specification-driven rename matrix: for every legal Elixir construct a user could reasonably
 * expect Rename (Shift+F6) to handle, a rename is attempted FROM EVERY occurrence of the name in
 * the fixture - the declaration and every usage form alike - and every attempt must produce the
 * same golden `*_after.ex` result. That encodes the user's mental model: rename is symmetric; it
 * should not matter which occurrence the caret is on.
 *
 * These tests deliberately encode EXPECTED user-facing behavior, not current implementation
 * behavior - some are red by design. A failing caret position is a functionality gap to triage,
 * not necessarily a test bug. Each scenario aggregates all its caret positions and reports every
 * failing one, so a single run maps the whole gap surface.
 *
 * Mechanics are user-facing only: the file is opened, the caret is placed on an occurrence (as a
 * click would), and the rename runs through the platform pipeline (see
 * [org.elixir_lang.code_insight.renameTargetAtCaret]); assertions compare resulting text.
 */
class RenameMatrixTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/model/psi/rename/matrix"

    // -- Modules ----------------------------------------------------------------------------

    /** `defmodule`, `alias`, `require`, `import`, `use`, qualified call. */
    fun testModuleDirect() = doTestFromEveryOccurrence("module_direct", "Renamee", "Fresh", expectedCarets = 6)

    /** Nested name `Outer.Renamee`; a bare aliased reference follows the rename. */
    fun testModuleNested() = doTestFromEveryOccurrence("module_nested", "Renamee", "Outer.Fresh", expectedCarets = 3)

    /** Multi-alias `alias Grouped.{Renamee, Sibling}`. */
    fun testModuleMultiAlias() =
        doTestFromEveryOccurrence("module_multi_alias", "Renamee", "Grouped.Fresh", expectedCarets = 3)

    /** Struct owner: `%Renamee{}` build and match forms. */
    fun testModuleStruct() = doTestFromEveryOccurrence("module_struct", "Renamee", "Fresh", expectedCarets = 3)

    /** `@behaviour Renamee` reference. */
    fun testModuleBehaviourReference() =
        doTestFromEveryOccurrence("module_behaviour", "Renamee", "Fresh", expectedCarets = 2)

    /** `defimpl ..., for: Renamee` and a struct pattern inside the impl. */
    fun testModuleDefimplFor() =
        doTestFromEveryOccurrence("module_defimpl_for", "Renamee", "Fresh", expectedCarets = 3)

    /** `alias AliasAs.Renamee, as: R` - the `as` alias `R` must survive unchanged. */
    fun testModuleAliasAs() =
        doTestFromEveryOccurrence("module_alias_as", "Renamee", "AliasAs.Fresh", expectedCarets = 2)

    /**
     * Renaming `One.Two.Three` - a MULTI-SEGMENT member `Two.Three` of `alias One.{Two.Three,
     * Four}` - rewrites the member relative to the group qualifier (`Two.Fresh`) and the bare
     * `Three.a()` reference to `Fresh.a()`.
     */
    fun testModuleGroupDeepMember() =
        doTestFromEveryOccurrence("module_group_deep_member", "Three", "One.Two.Fresh", expectedCarets = 3)

    /** Renaming `One.Four`, the plain sibling member of the same group, rewrites only its slot. */
    fun testModuleGroupSibling() =
        doTestFromEveryOccurrence("module_group_sibling", "Four", "One.Fresh", expectedCarets = 3)

    // Hierarchical namespace rename (caret on a qualifier segment renames the prefix as a
    // package would) is planned but not implemented - spec and design live in
    // IdeaProjects/docs/ideas.md ("Hierarchical module rename"). Its scenarios
    // (module_namespace_segment / module_namespace_root) will be restored with that feature.

    // -- Functions and macros ---------------------------------------------------------------

    /** `@spec`, `def`, unqualified call, qualified call. */
    fun testFunctionEverySite() = doTestFromEveryOccurrence("function_every_site", "renamee", "fresh", expectedCarets = 4)

    /** Multiple clauses of one function plus a recursive call. */
    fun testFunctionMultiClause() =
        doTestFromEveryOccurrence("function_multi_clause", "renamee", "fresh", expectedCarets = 3)

    /** Local `&renamee/1` and remote `&Mod.renamee/1` captures. */
    fun testFunctionCapture() = doTestFromEveryOccurrence("function_capture", "renamee", "fresh", expectedCarets = 3)

    /** Pipe calls `x |> renamee() |> Mod.renamee()` (arity is shifted by the pipe). */
    fun testFunctionPipe() = doTestFromEveryOccurrence("function_pipe", "renamee", "fresh", expectedCarets = 3)

    /** A def with a default argument spans two arities; both call sites rename. */
    fun testFunctionDefaultArgs() =
        doTestFromEveryOccurrence("function_default_args", "renamee", "fresh", expectedCarets = 3)

    /**
     * Two same-named functions of different arity are DIFFERENT symbols: renaming from a
     * `renamee/1` occurrence (spec, def, call) must leave `renamee/2` untouched, and renaming
     * from a `renamee/2` occurrence (its def or its call - carets #2 and #4) must leave
     * `renamee/1` untouched, hence the per-caret golden overrides.
     */
    fun testFunctionArityFamilies() =
        doTestFromEveryOccurrence(
            "function_arity_families", "renamee", "fresh", expectedCarets = 5,
            goldenOverrides = mapOf(
                2 to "function_arity_families_after_arity2.ex", // caret on `def renamee(x, y)`
                4 to "function_arity_families_after_arity2.ex"  // caret on `renamee(x, x)`
            )
        )

    /** `defp` and its call. */
    fun testFunctionPrivate() = doTestFromEveryOccurrence("function_private", "renamee", "fresh", expectedCarets = 2)

    /** `defmacro` and its call through `import`. */
    fun testMacroImport() = doTestFromEveryOccurrence("macro_import", "renamee", "fresh", expectedCarets = 2)

    /** `defdelegate renamee(x), to: Target` stays in sync with the target def. */
    fun testFunctionDefdelegate() =
        doTestFromEveryOccurrence("function_defdelegate", "renamee", "fresh", expectedCarets = 2)

    /** `apply(Mod, :renamee, [x])` atom follows the function. */
    fun testFunctionApplyAtom() =
        doTestFromEveryOccurrence("function_apply_atom", "renamee", "fresh", expectedCarets = 2)

    /** `defguard` and its use in a `when` clause. */
    fun testFunctionDefguard() = doTestFromEveryOccurrence("function_defguard", "renamee", "fresh", expectedCarets = 2)

    // -- Callbacks --------------------------------------------------------------------------

    /** `@callback` and the implementing def - renaming from either keeps them in sync. */
    fun testCallbackEverySite() =
        doTestFromEveryOccurrence("callback_every_site", "renamee", "fresh", expectedCarets = 2)

    /** `@macrocallback` and the implementing defmacro. */
    fun testMacrocallbackEverySite() =
        doTestFromEveryOccurrence("macrocallback_every_site", "renamee", "fresh", expectedCarets = 2)

    /** `@callback`, the default def inside `__using__`, and the `defoverridable` entry. */
    fun testCallbackDefoverridable() =
        doTestFromEveryOccurrence("callback_defoverridable", "renamee", "fresh", expectedCarets = 3)

    // -- Protocols --------------------------------------------------------------------------

    /** `defprotocol` def, `defimpl` def, and dispatching call site. */
    fun testProtocolEverySite() =
        doTestFromEveryOccurrence("protocol_every_site", "renamee", "fresh", expectedCarets = 3)

    // -- Module attributes ------------------------------------------------------------------

    /** Declaration and read. */
    fun testAttributeEverySite() =
        doTestFromEveryOccurrence("attribute_every_site", "renamee", "fresh", expectedCarets = 2)

    /** Re-declared (accumulated/overridden) attribute: both declarations plus the read. */
    fun testAttributeAccumulate() =
        doTestFromEveryOccurrence("attribute_accumulate", "renamee", "fresh", expectedCarets = 3)

    // -- Variables --------------------------------------------------------------------------

    /** Declaration and reads. */
    fun testVariableEverySite() =
        doTestFromEveryOccurrence("variable_every_site", "renamee", "fresh", expectedCarets = 3)

    /** Function parameter and its reads. */
    fun testVariableParameter() =
        doTestFromEveryOccurrence("variable_parameter", "renamee", "fresh", expectedCarets = 3)

    /** Rebinding chain `renamee = x; renamee = renamee + 1; renamee`. */
    fun testVariableRebind() = doTestFromEveryOccurrence("variable_rebind", "renamee", "fresh", expectedCarets = 4)

    /** Pin operator `^renamee` in a case pattern. */
    fun testVariablePin() = doTestFromEveryOccurrence("variable_pin", "renamee", "fresh", expectedCarets = 2)

    /** Binding introduced by a case pattern and used in the clause body. */
    fun testVariableCasePattern() =
        doTestFromEveryOccurrence("variable_case_pattern", "renamee", "fresh", expectedCarets = 2)

    /** Parameter captured by a closure (`fn -> renamee + 1 end`). */
    fun testVariableClosure() = doTestFromEveryOccurrence("variable_closure", "renamee", "fresh", expectedCarets = 2)

    /** Comprehension generator binding `for renamee <- list`. */
    fun testVariableComprehension() =
        doTestFromEveryOccurrence("variable_comprehension", "renamee", "fresh", expectedCarets = 2)

    /** `with {:ok, renamee} <- ...` binding and body use. */
    fun testVariableWith() = doTestFromEveryOccurrence("variable_with", "renamee", "fresh", expectedCarets = 2)

    // -- Types ------------------------------------------------------------------------------

    /** `@type` declaration and its references inside a `@spec`. */
    fun testTypeEverySite() = doTestFromEveryOccurrence("type_every_site", "renamee", "fresh", expectedCarets = 3)

    /** `@opaque` declaration and a `@spec` reference. */
    fun testTypeOpaque() = doTestFromEveryOccurrence("type_opaque", "renamee", "fresh", expectedCarets = 2)

    /** Remote type references `RemoteTypeDef.renamee` from another module's spec. */
    fun testTypeRemote() = doTestFromEveryOccurrence("type_remote", "renamee", "fresh", expectedCarets = 3)

    /** A `@type` referenced from another `@type`'s body. */
    fun testTypeInType() = doTestFromEveryOccurrence("type_in_type", "renamee", "fresh", expectedCarets = 2)

    // -- Type variables ---------------------------------------------------------------------

    /** `@type box(renamee)` head parameter and its body use. */
    fun testTypeVariableEverySite() =
        doTestFromEveryOccurrence("type_variable_every_site", "renamee", "fresh", expectedCarets = 2)

    /** `@spec ... when renamee: term()` binding plus head and return uses. */
    fun testSpecWhenEverySite() =
        doTestFromEveryOccurrence("spec_when_every_site", "renamee", "fresh", expectedCarets = 3)

    // -- MFA atoms --------------------------------------------------------------------------

    /** `def`, MFA-tuple atom `:renamee`, and a plain call - all in sync from any caret. */
    fun testMfaEverySite() = doTestFromEveryOccurrence("mfa_every_site", "renamee", "fresh", expectedCarets = 3)

    // -- Harness ----------------------------------------------------------------------------

    /**
     * Renames [oldName] to [newName] once per occurrence of [oldName] in `[fixtureName].ex`,
     * with the caret placed on that occurrence, and asserts each attempt yields exactly
     * `[fixtureName]_after.ex` - or, for caret indices present in [goldenOverrides], the golden
     * named there (for fixtures where same-named occurrences are different symbols, e.g. two
     * arities of one function name). Aggregates all failing caret positions into one assertion so
     * a run reports the complete gap map for the scenario. [expectedCarets] guards the fixture
     * itself: if the occurrence count drifts, the matrix silently loses coverage.
     */
    private fun doTestFromEveryOccurrence(
        fixtureName: String,
        oldName: String,
        newName: String,
        expectedCarets: Int,
        goldenOverrides: Map<Int, String> = emptyMap()
    ) {
        val before = loadFixture("$fixtureName.ex")
        val defaultAfter = loadFixture("${fixtureName}_after.ex")
        val occurrences = wordOccurrences(before, oldName)
        assertEquals(
            "$fixtureName.ex should contain exactly $expectedCarets occurrences of '$oldName'",
            expectedCarets,
            occurrences.size
        )

        val failures = mutableListOf<String>()
        occurrences.forEachIndexed { index, offset ->
            val after = goldenOverrides[index]?.let(::loadFixture) ?: defaultAfter
            myFixture.configureByText("${fixtureName}_caret$index.ex", before)
            val virtualFile = myFixture.file.virtualFile
            myFixture.editor.caretModel.moveToOffset(offset + 1)
            try {
                myFixture.renameTargetAtCaret(newName)
                val actual = myFixture.file.text
                if (actual != after) {
                    failures += "caret #$index (${describeCaret(before, offset)}): ${firstDifference(actual, after)}"
                }
            } catch (e: Throwable) {
                val reason = e.message?.lineSequence()?.firstOrNull() ?: e.javaClass.simpleName
                failures += "caret #$index (${describeCaret(before, offset)}): ${e.javaClass.simpleName}: $reason"
            } finally {
                // Each iteration must start from a clean project: a leftover (partially) renamed
                // copy would offer duplicate rename targets to the next iteration.
                WriteCommandAction.runWriteCommandAction(project) { virtualFile.delete(this) }
            }
        }

        if (failures.isNotEmpty()) {
            fail(
                "Renaming '$oldName' -> '$newName' in $fixtureName.ex failed from " +
                    "${failures.size} of ${occurrences.size} caret positions:\n\n" +
                    failures.joinToString("\n\n")
            )
        }
    }

    private fun loadFixture(name: String): String =
        File(testDataPath, name).readText().replace("\r\n", "\n")

    /** Start offsets of every whole-word occurrence of [word] (not part of a longer identifier). */
    private fun wordOccurrences(text: String, word: String): List<Int> =
        Regex("(?<![A-Za-z0-9_])${Regex.escape(word)}(?![A-Za-z0-9_?!])")
            .findAll(text)
            .map { it.range.first }
            .toList()

    private fun describeCaret(text: String, offset: Int): String {
        val line = text.substring(0, offset).count { it == '\n' } + 1
        val lineText = text.lines()[line - 1].trim()
        return "line $line: `$lineText`"
    }

    private fun firstDifference(actual: String, expected: String): String {
        val actualLines = actual.lines()
        val expectedLines = expected.lines()
        val index = (0 until maxOf(actualLines.size, expectedLines.size))
            .firstOrNull { actualLines.getOrNull(it) != expectedLines.getOrNull(it) }
            ?: return "texts differ only in trailing whitespace"
        return "first difference at line ${index + 1}: " +
            "expected `${expectedLines.getOrNull(index)}`, actual `${actualLines.getOrNull(index)}`"
    }
}
