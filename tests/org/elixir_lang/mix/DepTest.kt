package org.elixir_lang.mix

import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirTuple

/**
 * Pins how [Dep.from] sorts a Mix dep's options.
 *
 * Every option lands in one of three arms: ignored because it cannot move the dep's directory,
 * handled because it can, or reported through [org.elixir_lang.errorreport.Logger] because the
 * plugin has never seen it. The reported arm is the only one a user ever notices, and none of the
 * three was asserted anywhere before this class - an ignored option and an unknown one both return
 * `deps/<name>`, so the logged error is the only difference between them.
 *
 * The option names here are [Dep.from]'s own list, so dropping one from either without the other
 * fails.
 */
class DepTest : PlatformTestCase() {
    /**
     * Options Mix accepts that genuinely cannot change where the dep is checked out.
     *
     * `sha`, `commit` and `organization` are absent from `Mix.Tasks.Deps`' documented set. The two
     * named constants are misspellings that widely installed packages ship, referenced by name rather
     * than spelled out so a future reader does not "correct" them.
     *
     * `sparse` and `subdir` are on [Dep.from]'s ignored list but are deliberately absent here,
     * because they do not belong on it: `Mix.SCM.Git` joins each onto the dep's destination, so Mix
     * checks the dep out at `deps/<name>/<option>`. Adding either here would assert that the current
     * answer is correct. Tracked on #3928; when it is fixed they join the handled options below.
     */
    private val pathNeutralOptions = listOf(
        "allow_pre", "app", "branch", "commit", "compile", "depth", "env", "git", "github", "hex",
        "manager", "only", "optional", "organization", "override", "ref", "repo", "runtime",
        GUARDIAN_RUNTIME_TYPO, "sha", "submodules", "system_env", "tag", "targets",
        EDELIVER_DISTILLERY_WARN_MISSING, "warn_if_outdated"
    )

    fun testPathNeutralOptionsAreNotReported() {
        val depTuples = pathNeutralOptions.joinToString(",\n") { "{:dep_$it, \"~> 1.0\", $it: true}" }

        val (deps, errorTitles) = depsFrom(depTuples)

        assertEmpty("No path-neutral option may be reported as unknown", errorTitles)
        assertEquals("Expected every dep tuple to parse", pathNeutralOptions.size, deps.size)

        pathNeutralOptions.forEachIndexed { index, option ->
            assertEquals("`$option` must not change the dep path", "deps/dep_$option", deps[index]?.path)
            assertEquals("`$option` must not change the dep type", Dep.Type.LIBRARY, deps[index]?.type)
        }
    }

    fun testUnknownOptionIsReported() {
        val (deps, errorTitles) = depsFrom("{:my_dep, \"~> 1.0\", not_a_mix_dep_option: true}")

        assertEquals(
            listOf(
                "Don't know if Mix.Dep option `not_a_mix_dep_option` is important for determining " +
                        "location of dependency"
            ),
            errorTitles
        )
        assertEquals("An unknown option must still leave the accumulated dep", "deps/my_dep", deps.single()?.path)
    }

    fun testPathOptionReplacesDepsPath() {
        val (deps, errorTitles) = depsFrom("{:my_dep, path: \"../my_dep\"}")

        assertEmpty("`path` is handled, so it must not be reported", errorTitles)
        assertEquals("../my_dep", deps.single()?.path)
    }

    fun testInUmbrellaOptionUsesUmbrellaApplicationPath() {
        val (deps, errorTitles) = depsFrom("{:my_dep, in_umbrella: true}")

        assertEmpty("`in_umbrella` is handled, so it must not be reported", errorTitles)
        assertEquals("apps/my_dep", deps.single()?.path)
        assertEquals(Dep.Type.MODULE, deps.single()?.type)
    }

    // ---------------------------------------------------------------------
    // Options written as an explicit list
    // ---------------------------------------------------------------------

    fun testBracketedPathIsHonoured() {
        val (deps, errorTitles) = depsFrom("{:d, [path: \"../d\"]}")

        assertEmpty("`path` is handled, bracketed or not", errorTitles)
        assertEquals("../d", deps.single()?.path)
    }

    fun testBracketedInUmbrellaIsHonoured() {
        val (deps, _) = depsFrom("{:d, [in_umbrella: true]}")

        assertEquals("apps/d", deps.single()?.path)
        assertEquals(Dep.Type.MODULE, deps.single()?.type)
    }

    /** An unknown option must still be reported when bracketed, or the tripwire has a blind spot. */
    fun testBracketedUnknownOptionIsReported() {
        val (_, errorTitles) = depsFrom("{:d, \"~> 1.0\", [not_a_mix_dep_option: true]}")

        assertEquals(
            listOf(
                "Don't know if Mix.Dep option `not_a_mix_dep_option` is important for determining " +
                        "location of dependency"
            ),
            errorTitles
        )
    }

    /**
     * Parses [depTuples] as the `deps` of a `mix.exs` and runs [Dep.from] over each tuple, returning
     * the deps in source order alongside the title of every error [Dep] logged.
     */
    private fun depsFrom(depTuples: String): Pair<List<Dep?>, List<String>> {
        val (deps, loggedErrors) = captureLoggedErrors {
            val psiFile = myFixture.configureByText(
                "mix.exs",
                """
                defmodule Sample.MixProject do
                  def project do
                    [
                      deps: [
                        $depTuples
                      ]
                    ]
                  end
                end
                """.trimIndent()
            )

            PsiTreeUtil.findChildrenOfType(psiFile, ElixirTuple::class.java).map { Dep.from(it) }
        }

        return Pair(
            deps,
            loggedErrors.filter { it.category == Dep::class.java.name }.map { it.title ?: it.message }
        )
    }
}
