package org.elixir_lang.psi

import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.call.Call

/**
 * A trailing comma in a call's argument list is what the source says for as long as it takes to type the next
 * argument, so the parse has to survive it. Without recovery inside the parentheses the malformed list takes the call,
 * the enclosing block and every following expression with it, leaving raw tokens as siblings of the file.
 */
class TrailingCommaRecoveryTest : PlatformTestCase() {
    fun testCallKeepsItsArguments() {
        val call = outermostCall("foo(a,)\n")

        assertEquals("foo(a,)", call.text)
        assertNotNull(
            "The argument list is still an Arguments node",
            PsiTreeUtil.findChildOfType(call, Arguments::class.java)
        )
    }

    fun testEnclosingDefinitionSurvives() {
        val source = "def run do\n  foo(a,)\nend\n"

        assertEquals(source.trimEnd(), outermostCall(source).text)
    }

    fun testFollowingDefinitionSurvives() {
        val source = "defmodule M do\n  def run do\n    foo(a,)\n  end\n\n  def other, do: 1\nend\n"
        val module = outermostCall(source)

        assertEquals(
            "The whole module, including the untouched definition after the malformed call",
            source.trimEnd(),
            module.text
        )
        assertNotNull(
            "`def other` is still inside the module rather than a sibling of the file",
            PsiTreeUtil.findChildrenOfType(module, Call::class.java).find { it.text.startsWith("def other") }
        )
    }

    /*
     * A qualified call and a dot call spell the same half-typed state as `foo(a,)`, and their arguments recover the
     * same way.
     */

    fun testQualifiedCallKeepsItsArguments() {
        assertEquals("Mod.fun(a,)", outermostCall("Mod.fun(a,)\n").text)
    }

    fun testDotCallKeepsItsArguments() {
        val call = outermostCall("fun.(a,)\n")

        assertEquals("fun.(a,)", call.text)
        assertNotNull(
            "The argument list is still an Arguments node",
            PsiTreeUtil.findChildOfType(call, Arguments::class.java)
        )
    }

    fun testEnclosingDefinitionSurvivesADotCall() {
        val source = "def run do\n  fun.(a,)\nend\n"

        assertEquals(source.trimEnd(), outermostCall(source).text)
    }

    fun testFollowingDefinitionSurvivesADotCall() {
        val source = "defmodule M do\n  def run do\n    fun.(a,)\n  end\n\n  def other, do: 1\nend\n"

        assertEquals(
            "The whole module, including the untouched definition after the malformed dot call",
            source.trimEnd(),
            outermostCall(source).text
        )
    }

    /**
     * A newline inside parentheses is whitespace rather than a token of its own, so recovery reaches the `)` on the
     * next line without being allowed to eat anything but the comma.
     */
    fun testTrailingCommaBeforeAClosingParenthesisOnItsOwnLine() {
        val source = "foo(\n  a,\n)\n"

        assertEquals(source.trimEnd(), outermostCall(source).text)
    }

    /**
     * Recovery stops at the first token that is not the comma it was written for.  Reading on to the next `)`
     * anywhere in the file pulled whatever followed an unclosed parenthesis into its argument list, and eating the
     * end of line left the expression after it with no separator to start from.
     */
    fun testUnclosedParenthesesDoNotSwallowTheFollowingCall() {
        val file: PsiFile = myFixture.configureByText("unclosed.exs", "foo(a b\nbar(1)\n")

        assertEquals(
            "The unclosed argument list ends with the line its parenthesis was opened on",
            "foo(a b",
            PsiTreeUtil.findChildOfType(file, Call::class.java)!!.text
        )
        assertNotNull(
            "`bar(1)` is still a call of the file rather than tokens inside `foo`'s argument list",
            file.children.filterIsInstance<Call>().find { it.text == "bar(1)" }
        )
    }

    private fun outermostCall(source: String): Call {
        val file: PsiFile = myFixture.configureByText("trailing_comma.ex", source)

        return PsiTreeUtil.findChildOfType(file, Call::class.java)
            ?: throw AssertionError("No call parsed from:\n$source")
    }
}
