package org.elixir_lang.mix.watcher

import com.intellij.openapi.progress.EmptyProgressIndicator
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.mix.Dep
import org.elixir_lang.mix.sync.MixSyncTestHelpers
import org.elixir_lang.mix.watcher.TransitiveResolution.transitiveResolution

/**
 * Pins which deps [transitiveResolution] reaches, at each of the three positions a `mix.exs` can
 * occupy: a project root, an umbrella app, and a dependency's own file.
 *
 * Every dep reached becomes a library the sync pipeline believes the project should have, so a dep
 * Mix will never fetch becomes an empty placeholder library that no `mix deps.get` can fill.
 * `only:` and `optional:` decide that, and both are **positional** - neither is a reason to drop a
 * dep on its own. Mix fetches every environment's deps for a project it is building and exempts
 * umbrella apps outright, while a dependency's own deps are resolved in `:prod` and are not entitled
 * to its optional ones.
 */
class TransitiveResolutionTest : PlatformTestCase() {

    /**
     * The dep applications reachable from [roots], which is what the sync pipeline consumes.
     *
     * The longer deadline is for the fixture, not the product: a case here walks a chain of
     * `mix.exs` files and each one is parsed from scratch, where the helper's default assumes a
     * single file.
     */
    private fun applicationsFrom(vararg roots: VirtualFile): Set<String> =
        MixSyncTestHelpers
            .runSuspendOnPooledThread(timeoutMillis = 60_000L) {
                transitiveResolution(PsiManager.getInstance(project), EmptyProgressIndicator(), *roots)
            }
            .map(Dep::application)
            .toSet()

    /**
     * Writes a `mix.exs` at [path] declaring [depTuples] verbatim, with [extraProjectKeys] added to
     * `project/0` for the `apps_path:` an umbrella root needs.
     *
     * The tuples are source text rather than names so a case can attach the option under test.
     */
    private fun mixProject(path: String, depTuples: String, extraProjectKeys: String = ""): VirtualFile {
        val directory = myFixture.tempDirFixture.findOrCreateDir(path)
        val application = path.substringAfterLast('/')
        val moduleName = application.split("_").joinToString("") { it.replaceFirstChar(Char::uppercase) }

        // DepGatherer needs `deps: deps()` in project/0 before it will read `def deps`.
        myFixture.tempDirFixture.createFile(
            "$path/mix.exs",
            "defmodule " + moduleName + ".MixProject do\n" +
                "  use Mix.Project\n" +
                "\n" +
                "  def project do\n" +
                "    [\n" +
                "      app: :" + application + ",\n" +
                "      version: \"0.1.0\",\n" +
                extraProjectKeys +
                "      deps: deps()\n" +
                "    ]\n" +
                "  end\n" +
                "\n" +
                "  def deps do\n" +
                "    [" + depTuples + "]\n" +
                "  end\n" +
                "end\n"
        )

        return directory
    }

    /**
     * Fixture directories are keyed on the test name, plus a counter so a case that asserts over
     * several declarations does not write `mix.exs` into a directory it already used.
     */
    private var fixtureCount = 0

    private fun nextFixturePath(): String = "${name}_${fixtureCount++}"

    /** Resolution when the project root itself declares [depTuple]. */
    private fun projectRootDeclaring(depTuple: String): Set<String> =
        applicationsFrom(mixProject(nextFixturePath(), depTuple))

    /** Resolution when an umbrella app declares [depTuple], resolved as the plugin resolves one. */
    private fun umbrellaAppDeclaring(depTuple: String): Set<String> {
        val path = nextFixturePath()
        val umbrella = mixProject(path, "", "      apps_path: \"apps\",\n")
        val app = mixProject("$path/apps/app_a", depTuple)

        return applicationsFrom(umbrella, app)
    }

    /** Resolution when a dependency's own `mix.exs` declares [depTuple]. Always includes `cachex`. */
    private fun dependencyDeclaring(depTuple: String): Set<String> {
        val path = nextFixturePath()
        val root = mixProject(path, "{:cachex, \">= 0.0.0\"}")
        mixProject("$path/deps/cachex", depTuple)

        return applicationsFrom(root)
    }

    private fun assertReachedAtProjectPositions(depTuple: String, application: String) {
        assertEquals(
            "A project root's own dep must be reached: $depTuple",
            setOf(application),
            projectRootDeclaring(depTuple),
        )
        assertEquals(
            "An umbrella app's own dep must be reached: $depTuple",
            setOf(application),
            umbrellaAppDeclaring(depTuple),
        )
    }

    private fun assertReachedInsideADep(depTuple: String, application: String) {
        assertEquals(
            "A dep Mix would fetch must be reached: $depTuple",
            setOf("cachex", application),
            dependencyDeclaring(depTuple),
        )
    }

    private fun assertNotReachedInsideADep(depTuple: String) {
        assertEquals(
            "A dep Mix would not fetch must not be reached: $depTuple",
            setOf("cachex"),
            dependencyDeclaring(depTuple),
        )
    }

    // -----------------------------------------------------------------
    // Baseline
    // -----------------------------------------------------------------

    /** Without this the rest assert nothing: an option cannot be filtered out of a walk that never happens. */
    fun testDepDeclaredByADepIsReached() {
        val root = mixProject("reaches", "{:cachex, \">= 0.0.0\"}")
        mixProject("reaches/deps/cachex", "{:jumper, \">= 0.0.0\"}")

        assertEquals(setOf("cachex", "jumper"), applicationsFrom(root))
    }

    /** `cachex`'s own declarations: one real dep beside two that Mix will never fetch here. */
    fun testOnlyTheFetchableDepsOfADepAreReached() {
        val root = mixProject("mixed", "{:cachex, \">= 0.0.0\"}")
        mixProject(
            "mixed/deps/cachex",
            "{:jumper, \"~> 1.0\"},\n" +
                "{:excoveralls, \"~> 0.11\", optional: true, only: [:cover]},\n" +
                "{:benchee, \"~> 1.0\", optional: true, only: [:bench]}"
        )

        assertEquals(setOf("cachex", "jumper"), applicationsFrom(root))
    }

    // -----------------------------------------------------------------
    // Where a dep's own deps are looked for
    // -----------------------------------------------------------------

    /**
     * Mix converges every hex and git dep into the top-level project's `deps_path`, which
     * `Mix.Project.deps_config/1` passes down already expanded to an absolute path. So a dep
     * declared by a dependency is checked out *beside* it under the project, never nested inside
     * it - `deps/cachex/deps/jumper` is a directory no Mix configuration produces.
     *
     * Resolving it there anyway ends the walk one level below every project root, so nothing deeper
     * than a direct dep's own deps is ever seen.
     */
    fun testTransitiveDepsAreReachedThroughTheProjectsDepsDirectory() {
        val path = nextFixturePath()
        val root = mixProject(path, "{:cachex, \">= 0.0.0\"}")
        mixProject("$path/deps/cachex", "{:jumper, \">= 0.0.0\"}")
        mixProject("$path/deps/jumper", "{:mime, \">= 0.0.0\"}")
        mixProject("$path/deps/mime", "")

        assertEquals(setOf("cachex", "jumper", "mime"), applicationsFrom(root))
    }

    /**
     * An explicit `path:` dep is the exception: `Mix.SCM.Path` expands it against the directory of
     * the project that declared it, so one declared by a dependency really is relative to that
     * dependency. Guards against resolving every dep against the project root.
     */
    fun testExplicitRelativePathDepOfADepIsResolvedAgainstTheDeclaringDep() {
        val path = nextFixturePath()
        val root = mixProject(path, "{:cachex, \">= 0.0.0\"}")
        mixProject("$path/deps/cachex", "{:vendored, path: \"../vendored\"}")
        mixProject("$path/deps/vendored", "{:deep, \">= 0.0.0\"}")
        mixProject("$path/deps/deep", "")

        assertEquals(setOf("cachex", "vendored", "deep"), applicationsFrom(root))
    }

    /**
     * Two content roots that both carry `deps/shared` must not be confused for one another, which
     * is the cross-root contamination scoped library names exist to prevent.
     */
    fun testDepResolutionStaysInTheDeclaringRoot() {
        val a = nextFixturePath()
        val b = nextFixturePath()
        val rootA = mixProject(a, "{:dep_a, \">= 0.0.0\"}")
        mixProject("$a/deps/dep_a", "{:shared, \">= 0.0.0\"}")
        mixProject("$a/deps/shared", "{:only_in_a, \">= 0.0.0\"}")
        val rootB = mixProject(b, "")
        mixProject("$b/deps/shared", "{:only_in_b, \">= 0.0.0\"}")

        val applications = applicationsFrom(rootA, rootB)

        assertTrue(
            "`shared` must resolve under the root that owns the dep declaring it: $applications",
            "only_in_a" in applications,
        )
        assertFalse(
            "`shared` must not resolve under an unrelated content root: $applications",
            "only_in_b" in applications,
        )
    }

    /**
     * An umbrella app shares the umbrella's `deps`, `_build` and lock file, so it has no `deps` of
     * its own and Mix never creates one. A directory there is debris from before the app joined the
     * umbrella, and must not be preferred over the umbrella's.
     */
    fun testUmbrellaAppDepsResolveUnderTheUmbrella() {
        val u = nextFixturePath()
        val umbrella = mixProject(u, "", "      apps_path: \"apps\",\n")
        val app = mixProject("$u/apps/app_a", "{:shared, \">= 0.0.0\"}")
        mixProject("$u/deps/shared", "{:from_umbrella, \">= 0.0.0\"}")
        mixProject("$u/apps/app_a/deps/shared", "{:from_app, \">= 0.0.0\"}")

        val applications = applicationsFrom(umbrella, app)

        assertTrue("The umbrella's deps own the app's deps: $applications", "from_umbrella" in applications)
        assertFalse("An app's own deps directory is debris: $applications", "from_app" in applications)
    }

    /**
     * A content root that is not an umbrella app is its own Mix project, so it keeps its own `deps`
     * rather than deferring to a root it happens to sit inside. Guards against answering the case
     * above by always taking the outermost root.
     */
    fun testNestedProjectRootKeepsItsOwnDeps() {
        val p = nextFixturePath()
        val outer = mixProject(p, "")
        val inner = mixProject("$p/vendor/lib_a", "{:shared, \">= 0.0.0\"}")
        mixProject("$p/deps/shared", "{:from_outer, \">= 0.0.0\"}")
        mixProject("$p/vendor/lib_a/deps/shared", "{:from_inner, \">= 0.0.0\"}")

        val applications = applicationsFrom(outer, inner)

        assertTrue("A standalone nested project owns its deps: $applications", "from_inner" in applications)
        assertFalse("It must not borrow the enclosing root's deps: $applications", "from_outer" in applications)
    }

    /**
     * The umbrella root is not always handed in. An umbrella imported one module per app gives each
     * app module only `apps/<app>` as a content root, so that is the whole root set the resolver
     * sees, and the app's deps still live under the umbrella. Refs
     * [#3986](https://github.com/KronicDeth/intellij-elixir/issues/3986).
     */
    fun testUmbrellaAppDepsResolveUnderTheUmbrellaWhenOnlyTheAppIsHandedIn() {
        val u = nextFixturePath()
        mixProject(u, "", "      apps_path: \"apps\",\n")
        val app = mixProject("$u/apps/app_a", "{:shared, \">= 0.0.0\"}")
        mixProject("$u/deps/shared", "{:from_umbrella, \">= 0.0.0\"}")

        val applications = applicationsFrom(app)

        assertTrue("The umbrella owns the app's deps: $applications", "from_umbrella" in applications)
    }

    /**
     * `mix new --umbrella` writes `deps_path: "../../deps"` into every app, and
     * `Mix.Project.deps_path/1` expands it, so the app states outright where its deps live. Reading
     * it settles the case without inferring anything from directory names.
     */
    fun testDeclaredDepsPathIsHonouredWhenOnlyTheAppIsHandedIn() {
        val u = nextFixturePath()
        mixProject(u, "", "      apps_path: \"apps\",\n")
        val app = mixProject(
            "$u/apps/app_a",
            "{:shared, \">= 0.0.0\"}",
            "      deps_path: \"../../deps\",\n"
        )
        mixProject("$u/deps/shared", "{:from_umbrella, \">= 0.0.0\"}")

        val applications = applicationsFrom(app)

        assertTrue("A declared deps_path names the deps directory: $applications", "from_umbrella" in applications)
    }

    /**
     * The deps directory an app resolves through owns everything reached through it, however deep.
     *
     * A two-level chain passes as soon as the app itself can find the umbrella's `deps`, so it
     * cannot tell whether that answer carries. The third level is the discriminating one: its
     * declaring root is `deps/<dep>`, which no handed-in root is an ancestor of. Refs
     * [#3986](https://github.com/KronicDeth/intellij-elixir/issues/3986).
     */
    fun testDepsOfDepsResolveAtEveryDepthWhenOnlyTheAppIsHandedIn() {
        val u = nextFixturePath()
        mixProject(u, "", "      apps_path: \"apps\",\n")
        val app = mixProject("$u/apps/app_a", "{:level_one, \">= 0.0.0\"}")
        mixProject("$u/deps/level_one", "{:level_two, \">= 0.0.0\"}")
        mixProject("$u/deps/level_two", "{:level_three, \">= 0.0.0\"}")

        val applications = applicationsFrom(app)

        assertTrue("The app's own dep is reached: $applications", "level_one" in applications)
        assertTrue("A dep of that dep is reached: $applications", "level_two" in applications)
        assertTrue("The chain does not stop at two levels: $applications", "level_three" in applications)
    }

    /**
     * `deps_path:` is not umbrella-only - any project may move its deps directory, and then
     * `deps/<name>` beside the `mix.exs` is not where Mix checks anything out.
     */
    fun testDeclaredDepsPathIsHonouredOutsideAnUmbrella() {
        val p = nextFixturePath()
        val root = mixProject(p, "{:shared, \">= 0.0.0\"}", "      deps_path: \"vendor_deps\",\n")
        mixProject("$p/vendor_deps/shared", "{:from_vendor, \">= 0.0.0\"}")

        val applications = applicationsFrom(root)

        assertTrue("A relocated deps directory is searched: $applications", "from_vendor" in applications)
    }

    // -----------------------------------------------------------------
    // Reached at every position
    // -----------------------------------------------------------------

    fun testUnrestrictedDepIsReachedEverywhere() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\"}", "jason")
        assertReachedInsideADep("{:jason, \">= 0.0.0\"}", "jason")
    }

    /** `:prod` is the environment a dep's own deps are resolved in, so this excludes nothing. */
    fun testProdOnlyDepIsReachedEverywhere() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\", only: [:prod]}", "jason")
        assertReachedInsideADep("{:jason, \">= 0.0.0\", only: [:prod]}", "jason")
    }

    fun testSingleAtomProdOnlyDepIsReachedEverywhere() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\", only: :prod}", "jason")
        assertReachedInsideADep("{:jason, \">= 0.0.0\", only: :prod}", "jason")
    }

    fun testOnlyListIncludingProdIsReachedEverywhere() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\", only: [:dev, :prod]}", "jason")
        assertReachedInsideADep("{:jason, \">= 0.0.0\", only: [:dev, :prod]}", "jason")
    }

    fun testOptionalFalseDepIsReachedEverywhere() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\", optional: false}", "jason")
        assertReachedInsideADep("{:jason, \">= 0.0.0\", optional: false}", "jason")
    }

    /**
     * Every `only:` shape the plugin cannot read must keep the dep at every position. Dropping a dep
     * that is physically present costs resolution and completion; keeping one Mix never fetches
     * costs an empty placeholder library, which is the status quo.
     */
    fun testUnreadableOnlyValuesAreReachedEverywhere() {
        listOf(
            "only: :\"prod\"",
            "only: true",
            "only: @envs",
            "only: Mix.env()",
            "only: [:dev] ++ other()",
        ).forEach { option ->
            assertReachedInsideADep("{:jason, \">= 0.0.0\", $option}", "jason")
        }
    }

    // -----------------------------------------------------------------
    // Reached at a project position, dropped inside a dep
    // -----------------------------------------------------------------

    fun testEnvironmentRestrictedDepIsReachedAtProjectPositions() {
        assertReachedAtProjectPositions("{:mox, \">= 0.0.0\", only: [:test]}", "mox")
    }

    fun testEnvironmentRestrictedDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:mox, \">= 0.0.0\", only: [:test]}")
    }

    fun testSingleAtomEnvironmentRestrictedDepIsReachedAtProjectPositions() {
        assertReachedAtProjectPositions("{:mox, \">= 0.0.0\", only: :test}", "mox")
    }

    fun testSingleAtomEnvironmentRestrictedDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:mox, \">= 0.0.0\", only: :test}")
    }

    fun testMultiEnvironmentRestrictedDepIsReachedAtProjectPositions() {
        assertReachedAtProjectPositions("{:mox, \">= 0.0.0\", only: [:dev, :test]}", "mox")
    }

    fun testMultiEnvironmentRestrictedDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:mox, \">= 0.0.0\", only: [:dev, :test]}")
    }

    fun testOptionalDepIsReachedAtProjectPositions() {
        assertReachedAtProjectPositions("{:jason, \">= 0.0.0\", optional: true}", "jason")
    }

    fun testOptionalDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:jason, \">= 0.0.0\", optional: true}")
    }

    /** The two gates are independent: clearing the environment one does not rescue an optional dep. */
    fun testOptionalProdOnlyDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:jason, \">= 0.0.0\", optional: true, only: [:prod]}")
    }

    /**
     * An `in_umbrella:` dep inside a third-party dependency is an app of *that* umbrella and can
     * never be a module of this project. Left reachable it becomes an invalid module order entry in
     * the user's `.iml`, which is what a `subdir:` monorepo dep would otherwise produce.
     */
    fun testInUmbrellaDepIsReachedAtProjectPositions() {
        assertReachedAtProjectPositions("{:sib, in_umbrella: true}", "sib")
    }

    fun testInUmbrellaDepOfADepIsNotReached() {
        assertNotReachedInsideADep("{:sib, in_umbrella: true}")
    }
}
