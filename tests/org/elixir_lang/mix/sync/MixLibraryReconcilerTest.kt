package org.elixir_lang.mix.sync

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.PsiTestUtil
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.mix.library.Kind as MixLibraryKind

/**
 * Tests for the startup escalation check.
 *
 * The steady-state answer is the one that matters for cost: a project whose libraries are fine must
 * not trigger a full sync, because that would reintroduce the deps/`_build` scan on every open.
 */
class MixLibraryReconcilerTest : PlatformTestCase() {

    /**
     * Each case asserts on the whole library table, so it must start empty. Cleaning only in
     * tearDown leaves the result depending on method order, which JUnit 3 does not fix.
     */
    override fun setUp() {
        super.setUp()
        MixSyncTestHelpers.removeAllLibraries(project)
    }

    override fun tearDown() {
        runAll(
            { MixTestFixtures.removeAllContentRoots(myFixture) },
            { MixSyncTestHelpers.removeAllLibraries(project) },
            { super.tearDown() }
        )
    }

    private fun reconcilerNeedsResync(): Boolean =
        MixSyncTestHelpers.runSuspendOnPooledThread { MixLibraryReconciler.needsResync(project) }

    private fun createMixLibrary(name: String, classRootUrl: String?) {
        WriteAction.run<Throwable> {
            val model = LibraryTablesRegistrar.getInstance().getLibraryTable(project).modifiableModel
            val library = model.createLibrary(name, MixLibraryKind)
            classRootUrl?.let {
                library.modifiableModel.let { lm ->
                    lm.addRoot(it, OrderRootType.CLASSES)
                    lm.commit()
                }
            }
            model.commit()
        }
    }

    fun testNoLibrariesNeedsNoResync() {
        assertFalse("An empty library table must not trigger a full sync", reconcilerNeedsResync())
    }

    /** The common case, and the one the cost argument rests on. */
    fun testCurrentRootScopedLibraryWithLiveRootsNeedsNoResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_ok")
        val ebin = myFixture.tempDirFixture.findOrCreateDir("reconcile_ok/_build/dev/lib/phoenix/ebin")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        createMixLibrary(scopedDepLibraryName(contentRootToken(project, root.url), "phoenix"), ebin.url)

        assertFalse("A library scoped to a current root with live roots must not resync", reconcilerNeedsResync())
    }

    /** A placeholder for a declared-but-unfetched dep is deliberate and has no roots to dangle. */
    fun testPlaceholderWithNoRootsNeedsNoResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_placeholder")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        createMixLibrary(scopedDepLibraryName(contentRootToken(project, root.url), "unfetched"), null)

        assertFalse("An empty placeholder must not trigger a full sync", reconcilerNeedsResync())
    }

    /** deps/ removed while the IDE was closed: the library survives, its roots do not. */
    fun testDanglingRootNeedsResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_dangling")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        createMixLibrary(
            scopedDepLibraryName(contentRootToken(project, root.url), "phoenix"),
            "${root.url}/_build/dev/lib/phoenix/ebin",
        )

        assertTrue("A library root the VFS cannot resolve must trigger a full sync", reconcilerNeedsResync())
    }

    /**
     * A library scoped by an older scheme to a root that is *still current* must be detected. This
     * is the whole-project upgrade case: every name is in the old form, so treating that form as
     * current would report the one project that most needs re-syncing as healthy.
     */
    fun testLibraryScopedByOlderSchemeToCurrentRootNeedsResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_old_scheme")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        val currentToken = contentRootToken(project, root.url)
        val olderScheme = "file:///previous/scheme/${root.name}"
        assertFalse("Fixture needs the two schemes to differ", currentToken == olderScheme)

        createMixLibrary(scopedDepLibraryName(olderScheme, "phoenix"), null)

        assertTrue(
            "A name scoped by a superseded scheme must trigger a full sync",
            reconcilerNeedsResync()
        )
    }

    /**
     * A name scoped to a root the project no longer has - which is also the shape of every name
     * written before scope tokens became project-relative, so upgrades migrate on the next open.
     */
    fun testForeignScopeTokenNeedsResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_foreign")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        createMixLibrary(scopedDepLibraryName("file:///gone/elsewhere/mix_root", "phoenix"), null)

        assertTrue("A scope token naming no current content root must trigger a full sync", reconcilerNeedsResync())
    }

    /** Unscoped and consolidated names are not this check's business. */
    fun testUnscopedAndConsolidatedNamesNeedNoResync() {
        val root = myFixture.tempDirFixture.findOrCreateDir("reconcile_unscoped")
        PsiTestUtil.addContentRoot(myFixture.module, root)

        createMixLibrary("phoenix", null)
        createMixLibrary("reconcile_unscoped (consolidated)", null)

        assertFalse("Names without a scope token must not trigger a full sync", reconcilerNeedsResync())
    }
}
