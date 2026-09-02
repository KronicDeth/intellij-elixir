package org.elixir_lang.code_insight.completion.contributor

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.completeCandidateAtCaret
import org.elixir_lang.code_insight.completionAttemptAtCaret

/**
 * Completion of a struct name - `%Us<caret>{}` completes to `%User{}`.
 *
 * Nothing special-cases the struct-literal context. `%User{}`'s `User` parses as a
 * [org.elixir_lang.psi.QualifiableAlias], so `computeReference` attaches a
 * [org.elixir_lang.reference.Module] to it like any other non-declaration alias, and the variants
 * come from [org.elixir_lang.psi.scope.module.Variants] - the same mechanism that completes
 * `alias Us<caret>`.
 *
 * That generality is why this is worth pinning, and it is also what decides the shape of this class.
 * The cases are indexed on the branches of `computeReference` rather than on a list of places a
 * struct name can be written:
 *
 * | `computeReference` branch | Reached by | Pinned here |
 * |---|---|---|
 * | otherwise -> `Module(this)` | every unqualified struct name | construction, update, pattern |
 * | declaration name -> `null` | `defmodule Us<caret>` | the negative case |
 * | the two qualified branches | `%My<caret>.User{}`, `%My.Us<caret>{}` | no - see below |
 *
 * The first branch covers construction, update and pattern positions identically, so those three are
 * pinned separately only because a future contributor could gate completion on the enclosing
 * expression and break one without touching the others.
 *
 * **Every case accepts a candidate and asserts the resulting source**, rather than asserting over the
 * offered lookup strings. A candidate can be offered under a string that inserts something else, or
 * over a replacement range that does not cover what it replaces - both of which an assertion on the
 * strings passes. This class has a live example to hand: at `%My<caret>.User{}` the qualifier's own
 * candidates are offered fully qualified but replace only the head segment, so accepting one yields
 * `%My.User.User{}`, which every string-level assertion in this class would have called correct.
 *
 * **The qualified branches are deliberately absent.** Neither completes today, and neither failure is
 * specific to a struct name: `%My.Us<caret>{}`, `alias My.Us<caret>` and `My.Us<caret>.foo()` all
 * offer nothing, alongside the insertion fault above. Those belong to qualified-alias completion
 * rather than to struct names, and are filed as
 * [#3987](https://github.com/KronicDeth/intellij-elixir/issues/3987).
 */
class StructNameCompletionTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()

        myFixture.addFileToProject(
            "lib/user.ex",
            """
                defmodule User do
                  defstruct [:first_name, :last_name]
                end
            """.trimIndent()
        )
        myFixture.addFileToProject(
            "lib/user_profile.ex",
            """
                defmodule UserProfile do
                  defstruct [:bio]
                end
            """.trimIndent()
        )
        myFixture.addFileToProject(
            "lib/my.ex",
            """
                defmodule My.User do
                  defstruct [:id]
                end

                defmodule My.UserProfile do
                  defstruct [:bio]
                end
            """.trimIndent()
        )
    }

    /** `%Us<caret>{}` - the case [#2691](https://github.com/KronicDeth/intellij-elixir/issues/2691) asks for. */
    fun testStructConstructionCompletesToTheStructName() {
        configure(
            """
                defmodule Test do
                  def run do
                    %Us<caret>{}
                  end
                end
            """
        )

        assertCompletesTo(
            """
                defmodule Test do
                  def run do
                    %User{}
                  end
                end
            """
        )
    }

    /**
     * `%Us<caret>{user | bio: nil}` - the update expression puts the alias in front of a `|`, which
     * parses differently from an empty map body.
     */
    fun testStructUpdateCompletesToTheStructName() {
        configure(
            """
                defmodule Test do
                  def run(user) do
                    %Us<caret>{user | bio: nil}
                  end
                end
            """
        )

        assertCompletesTo(
            """
                defmodule Test do
                  def run(user) do
                    %User{user | bio: nil}
                  end
                end
            """
        )
    }

    /**
     * `def run(%Us<caret>{})` - a struct name in a pattern, where the enclosing call is a definition
     * head rather than a body expression.
     */
    fun testStructPatternInFunctionHeadCompletesToTheStructName() {
        configure(
            """
                defmodule Test do
                  def run(%Us<caret>{}) do
                    :ok
                  end
                end
            """
        )

        assertCompletesTo(
            """
                defmodule Test do
                  def run(%User{}) do
                    :ok
                  end
                end
            """
        )
    }

    /**
     * `alias My.UserProfile` then `%Us<caret>{}` - completion goes through the `alias`, so accepting
     * the candidate writes the short name the alias introduced rather than the fully-qualified one.
     */
    fun testAliasedStructNameCompletesToTheShortName() {
        configure(
            """
                defmodule Test do
                  alias My.UserProfile

                  def run do
                    %Us<caret>{}
                  end
                end
            """
        )

        assertCompletesTo(
            """
                defmodule Test do
                  alias My.UserProfile

                  def run do
                    %UserProfile{}
                  end
                end
            """,
            lookupString = "UserProfile"
        )
    }

    /**
     * `alias Us<caret>` - the control. Struct-name completion is a side effect of alias completion,
     * so if this ever diverges from the struct cases above the shared mechanism is the thing that
     * changed.
     */
    fun testAliasDirectiveCompletesTheSameWay() {
        configure(
            """
                defmodule Test do
                  alias Us<caret>
                end
            """
        )

        assertCompletesTo(
            """
                defmodule Test do
                  alias User
                end
            """
        )
    }

    /**
     * `defmodule Us<caret> do` - the negative. A declaration name deliberately carries no reference
     * (`ModuleSymbolDeclarationProvider` anchors it instead), so nothing may be offered there. This
     * is the case that pins the *shape* of the rule rather than one more instance of it: a change
     * that made every [org.elixir_lang.psi.QualifiableAlias] complete would pass every other test
     * here and fail this one.
     *
     * Asserts on the document as well as on the candidates, because "no popup opened" and "a lone
     * candidate auto-inserted" are the same observation through the lookup strings alone - and the
     * second would mean the editor had silently rewritten a module's declared name.
     */
    fun testDefmoduleDeclarationNameCompletesNothing() {
        val source = """
            defmodule Us<caret> do
            end
        """.trimIndent().trim()

        configure(source)

        val attempt = myFixture.completionAttemptAtCaret()

        assertEquals(
            "Completion in a `defmodule` declaration name must leave the source alone",
            source.replace("<caret>", ""),
            attempt.text
        )
        assertFalse(
            "A `defmodule` declaration name must not offer existing modules, got: ${attempt.candidates}",
            attempt.candidates.orEmpty().any { it == "User" || it == "UserProfile" }
        )
    }

    private fun configure(text: String) {
        myFixture.configureByText("test.ex", text.trimIndent().trim())
    }

    private fun assertCompletesTo(expected: String, lookupString: String = "User") {
        assertEquals(expected.trimIndent().trim(), myFixture.completeCandidateAtCaret(lookupString))
    }
}
