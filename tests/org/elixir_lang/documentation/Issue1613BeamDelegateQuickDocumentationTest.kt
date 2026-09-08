package org.elixir_lang.documentation

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.beam.BeamLibraryFixture
import java.io.File

/**
 * Quick Documentation for a call to a function declared by `defdelegate` whose `to:` target is a
 * **compiled** module.
 *
 * The reported case: delegating into a module that exists only as `.beam`, the way the standard
 * library delegates `Map.values/1` to `:maps`. Reuses the `queue.beam` fixture from
 * [ErlangAtomQualifierHoverDocumentationTest].
 */
class Issue1613BeamDelegateQuickDocumentationTest : QuickDocumentationTestCase() {
    override fun setUp() {
        super.setUp()
        addBeamLibrary()
    }

    override fun tearDown() {
        try {
            removeBeamLibrary()
        } finally {
            super.tearDown()
        }
    }

    fun testQuickDocOnCallDelegatedToBeamModule() {
        myFixture.configureByFiles("defdelegate_to_beam_hover.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull(
            "Quick Documentation should be shown for a call delegated to a compiled module",
            documentation
        )
        assertFalse(
            "Expected function documentation, not the delegating module's @moduledoc, got: $documentation",
            documentation!!.contains("Delegator module.")
        )
        assertTrue(
            "Expected the delegation target :queue in the documentation, got: $documentation",
            documentation.contains(":queue")
        )
    }

    /** The delegate's own `@doc` wins over the compiled target's too, not only over a source target's. */
    fun testQuickDocPrefersTheDelegatesOwnDocOverTheBeamTargets() {
        myFixture.configureByFiles("documented_delegate_to_beam.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull("Quick Documentation should be shown for a documented delegate", documentation)
        assertTrue(
            "Expected the delegate's own @doc, got: $documentation",
            documentation!!.contains("Delegator's own account of queueing.")
        )
    }

    /**
     * The control for [testQuickDocOnCallDelegatedToBeamModule]: `:queue.new()` called directly must
     * already produce documentation, so a null for the delegated call cannot be blamed on the fixture.
     */
    fun testQuickDocOnDirectBeamCall() {
        myFixture.configureByFiles("direct_beam_call_control.ex")

        val documentation = quickDocumentationAtCaret()

        assertNotNull(
            "Quick Documentation should be shown for a direct :queue.new() call - without this the " +
                "delegated-call assertion proves nothing about delegation",
            documentation
        )
        assertTrue(
            "Expected :queue in the documentation, got: $documentation",
            documentation!!.contains(":queue")
        )
    }

    private fun addBeamLibrary() {
        val beamFile = File(testDataPath, "queue.beam")
        assertTrue("queue.beam not found at ${beamFile.absolutePath}", beamFile.exists())

        val beamDir = beamFile.parentFile.absolutePath
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, beamDir)

        val beamDirVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(beamDir))
        assertNotNull("Could not find beam test data directory: $beamDir", beamDirVf)

        BeamLibraryFixture.addLibrary(project, myFixture.module, LIBRARY_NAME, listOf(beamDirVf!!))
    }

    private fun removeBeamLibrary() {
        BeamLibraryFixture.removeLibrary(project, myFixture.module, LIBRARY_NAME)
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/documentation/erlang_atom_qualifier_hover"

    companion object {
        private const val LIBRARY_NAME = "defdelegate_beam_hover_test_lib"
    }
}
