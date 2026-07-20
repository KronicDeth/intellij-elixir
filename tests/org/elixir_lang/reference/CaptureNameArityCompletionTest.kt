package org.elixir_lang.reference

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completeSoleCandidateAtCaret
import org.elixir_lang.code_insight.completionStringsAtCaret

/**
 * Completion for function-capture names: `&<prefix>/arity` should offer the callables that could
 * complete a capture at the caret.
 *
 * A capture references a *function* of a specific arity - `&foo/2` is only valid when `foo/2` exists.
 * [org.elixir_lang.reference.CaptureNameArity.getVariants] offers the in-scope callables of the
 * requested arity: for an unqualified capture, the enclosing module's public and private functions
 * (including ones defined later); for a qualified capture (`&Mod.fun/arity`), the target module's
 * public functions. Each name is inserted bare (no parentheses), and the requested `/arity` filters
 * the offered names.
 *
 * These tests drive the real completion popup / auto-insert and assert that user-visible behaviour:
 *   - local public and private functions are offered,
 *   - forward-referenced (defined-later) functions are offered,
 *   - a sole candidate auto-inserts,
 *   - the requested `/arity` filters the offered names,
 *   - qualified captures (`&Mod.fun/arity`) offer the remote module's functions,
 * and the negatives guard that a capture never offers local variables or leaks an unrelated module's
 * unqualified functions.
 */
class CaptureNameArityCompletionTest : PlatformTestCase() {
    /* Positive: local functions of the requested arity are offered. */

    fun testOffersLocalPublicAndPrivateFunctions() {
        myFixture.configureByText("test.ex", inRun("&fetch<caret>/0"))

        val strings = myFixture.completionStringsAtCaret().orEmpty()

        assertTrue("Expected public fetch_one offered, got: $strings", strings.contains("fetch_one"))
        assertTrue("Expected public fetch_two offered, got: $strings", strings.contains("fetch_two"))
        assertTrue("Expected private fetch_secret offered, got: $strings", strings.contains("fetch_secret"))
    }

    fun testForwardReferencedFunctionsAreOffered() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def run do
                    &lat<caret>/0
                  end

                  def later_one, do: :ok
                  def later_two, do: :ok
                end
            """.trimIndent()
        )

        val strings = myFixture.completionStringsAtCaret().orEmpty()

        assertTrue("A capture may reference a function defined later in the module; got: $strings",
            strings.containsAll(listOf("later_one", "later_two")))
    }

    /* Sole candidate: a unique prefix auto-inserts the name (no popup), leaving `&name/arity`. */

    fun testSoleCandidateAutoInserts() {
        myFixture.configureByText("test.ex", inRun("&uni<caret>/0"))

        myFixture.completeSoleCandidateAtCaret()

        myFixture.checkResult(inRun("&unique_name/0"))
    }

    /* Arity awareness: `/arity` is part of the capture syntax, so only names with a clause of that
     * arity should be offered - offering an arity-mismatched name suggests an invalid capture. */

    fun testFiltersByRequestedArity() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def gather_first, do: :ok
                  def gather_second, do: :ok
                  def gather_single(a), do: a

                  def run do
                    &gather<caret>/0
                  end
                end
            """.trimIndent()
        )

        val strings = myFixture.completionStringsAtCaret().orEmpty()

        assertTrue("gather_first has a /0 clause and must be offered for &gather/0; got: $strings",
            strings.containsAll(listOf("gather_first", "gather_second")))
        assertFalse("gather_single has no /0 clause and must not be offered for &gather/0; got: $strings",
            strings.contains("gather_single"))
    }

    /* Qualified capture: `&Mod.fun/arity` should offer the remote module's functions. */

    fun testQualifiedCaptureOffersRemoteFunctions() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Remote do
                  def greet_one(name), do: name
                  def greet_two(name), do: name
                end

                defmodule Test do
                  def run do
                    &Remote.greet<caret>/1
                  end
                end
            """.trimIndent()
        )

        val strings = myFixture.completionStringsAtCaret().orEmpty()

        assertTrue("Qualified capture should offer Remote's functions; got: $strings",
            strings.containsAll(listOf("greet_one", "greet_two")))
    }

    /* Negatives: a fix must not offer things that are not valid capture names. */

    fun testDoesNotOfferLocalVariables() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Test do
                  def run do
                    xray = :value
                    &x<caret>/0
                  end
                end
            """.trimIndent()
        )

        assertFalse(
            "A capture references a function, not a variable, so the local `xray` must not be offered",
            myFixture.completionStringsAtCaret().orEmpty().contains("xray")
        )
    }

    fun testDoesNotOfferUnrelatedModuleUnqualifiedFunctions() {
        myFixture.configureByText(
            "test.ex",
            """
                defmodule Other do
                  def other_fn, do: :ok
                end

                defmodule Test do
                  def run do
                    &other<caret>/0
                  end
                end
            """.trimIndent()
        )

        assertFalse(
            "Another module's function is not in unqualified scope and must not be offered",
            myFixture.completionStringsAtCaret().orEmpty().contains("other_fn")
        )
    }

    private fun inRun(captureLine: String): String =
        """
            defmodule Test do
              def fetch_one, do: :ok
              def fetch_two, do: :ok
              defp fetch_secret, do: :ok
              def unique_name, do: :ok

              def run do
                $captureLine
              end
            end
        """.trimIndent()
}
