package org.elixir_lang.formatter

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.codeStyle.CodeStyleManager
import org.elixir_lang.PlatformTestCase

/**
 * Regression tests for https://github.com/KronicDeth/intellij-elixir/issues/1075,
 * "accessExpressions should be flattened with buildAccessExpressionChildren".
 *
 * `Block`'s constructor asserts it is never handed an `ACCESS_EXPRESSION` - those are flattened
 * through `buildAccessExpressionChildren` instead - and `Block.java` checks for that everywhere it
 * builds a child. `buildMapUpdateArgumentsChildren` missed the check on its **left** operand, the map
 * being updated. The tail side, after the pipe, was always fine: it goes through
 * `buildMapTailArgumentsChildChildren`, whose keyword pairs do check.
 *
 * Two things kept this hidden for eight years:
 *
 * - The left operand is an `ACCESS_EXPRESSION` for a literal or an aggregate - an alias, a map,
 *   list or tuple literal, a string, atom or number, an anonymous function, or anything
 *   parenthesised. It is something else for the shapes reached for first: a bare name is a
 *   `MATCHED_UNQUALIFIED_NO_ARGUMENTS_CALL`, `a.b` a `MATCHED_QUALIFIED_NO_ARGUMENTS_CALL`,
 *   `a[:b]` a `MATCHED_UNQUALIFIED_BRACKET_OPERATION`, `f(a)` a
 *   `MATCHED_UNQUALIFIED_PARENTHESES_CALL`, `@attr` a `MATCHED_AT_OPERATION`.
 * - It fires through `adjustLineIndent` - what a paste and Auto-Indent Lines use, and what the
 *   reported stack trace came through - but **not** through a whole-file `reformat`, which is what
 *   the existing formatter fixtures exercise.
 */
class MapUpdateAccessExpressionTest : PlatformTestCase() {
    private val prefix = "defmodule M do\n  def f(a, b) do\n    "

    private fun adjustLineIndent(body: String): String {
        myFixture.configureByText("a.ex", "$prefix$body\n  end\nend\n")

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).adjustLineIndent(myFixture.file, prefix.length + 1)
        }

        return myFixture.file.text
    }

    private fun assertIndentLeavesUnchanged(body: String) =
        assertEquals("$prefix$body\n  end\nend\n", adjustLineIndent(body))

    /** The reported crash. */
    fun testParenthesisedLeftOperandOfAMapUpdate() = assertIndentLeavesUnchanged("%{(a) | k: 1}")

    /** Same construct with a struct name in front. */
    fun testParenthesisedLeftOperandOfAStructUpdate() = assertIndentLeavesUnchanged("%User{(a) | k: 1}")

    /** A parenthesised expression rather than a parenthesised name. */
    fun testParenthesisedExpressionAsLeftOperand() = assertIndentLeavesUnchanged("%{(a || b) | k: 1}")

    /** A literal or aggregate on the left takes the same path. */
    fun testAliasLeftOperand() = assertIndentLeavesUnchanged("%{Foo | k: 1}")

    fun testMapLiteralLeftOperand() = assertIndentLeavesUnchanged("%{%{a} | k: 1}")

    fun testListLiteralLeftOperand() = assertIndentLeavesUnchanged("%{[a] | k: 1}")

    fun testTupleLiteralLeftOperand() = assertIndentLeavesUnchanged("%{{a} | k: 1}")

    fun testAtomLeftOperand() = assertIndentLeavesUnchanged("%{:atom | k: 1}")

    fun testAnonymousFunctionLeftOperand() = assertIndentLeavesUnchanged("%{fn -> a end | k: 1}")

    /** Controls: shapes that parse as something other than an access expression. */

    fun testBareNameLeftOperand() = assertIndentLeavesUnchanged("%{a | k: 1}")

    fun testDottedLeftOperand() = assertIndentLeavesUnchanged("%{a.b | k: 1}")

    fun testBracketLeftOperand() = assertIndentLeavesUnchanged("%{a[:b] | k: 1}")

    fun testParenthesesCallLeftOperand() = assertIndentLeavesUnchanged("%{f(a) | k: 1}")

    fun testModuleAttributeLeftOperand() = assertIndentLeavesUnchanged("%{@attr | k: 1}")
}
