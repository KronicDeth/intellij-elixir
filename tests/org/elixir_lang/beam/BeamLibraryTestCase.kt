package org.elixir_lang.beam

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import java.io.File

/**
 * Base for tests that need `.beam` fixtures resolvable as library classes.
 *
 * Registers [ebinDirectory] as a CLASSES root of a project library in [setUp] and removes it in
 * [tearDown], so subclasses only supply their own `getTestDataPath()`. The library membership matters: a
 * `.beam` outside a library root is not treated as compiled library code, so decompilation, navigation and
 * highlighting all behave differently from the real IDE.
 */
abstract class BeamLibraryTestCase : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        addBeamLibrary()
    }

    @Throws(Exception::class)
    override fun tearDown() {
        runAll(
            { removeBeamLibrary() },
            { super.tearDown() },
        )
    }

    /**
     * The CLASSES root registered as the fixture library, `<testDataPath>/ebin` by default. Override it to
     * point at a shared root such as [ERLANG_STDLIB_EBIN], so a `.beam` several suites need is stored once.
     */
    protected open val ebinDirectory: File
        get() = File(testDataPath, EBIN).absoluteFile

    /**
     * Opens the decompiled view of [beamName] from this test's [ebinDirectory].
     *
     * Every caller currently passes `queue.beam`, so the IDE reports [beamName] as always the same value.
     * Do not inline it: subclasses ship different fixtures - `BeamModuleGotoDeclarationTest` has only
     * `Elixir.Code.beam`, and the type suites also carry `erlang.beam`.
     */
    protected fun openBeam(beamName: String) {
        val beamIo = File(ebinDirectory, beamName)
        val beamVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(beamIo)
        assertNotNull("Could not find decompiled $beamName in VFS", beamVf)
        myFixture.configureFromExistingVirtualFile(beamVf!!)
    }

    /** [openBeam], then puts the caret on the last character of [anchor] within the decompiled text. */
    protected fun openBeamAndMoveCaretTo(beamName: String, anchor: String) {
        openBeam(beamName)

        val anchorIndex = myFixture.editor.document.text.indexOf(anchor)
        assertTrue("Anchor '$anchor' not found in decompiled $beamName", anchorIndex >= 0)
        myFixture.editor.caretModel.moveToOffset(anchorIndex + anchor.length - 1)
    }

    private fun addBeamLibrary() {
        val ebinDir = ebinDirectory
        assertTrue("Fixture directory not found at ${ebinDir.absolutePath}", ebinDir.isDirectory)

        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, ebinDir.path)

        val ebinVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find fixture directory ${ebinDir.absolutePath} in VFS", ebinVf)

        BeamLibraryFixture.addLibrary(project, myFixture.module, BEAM_LIBRARY_NAME, listOf(ebinVf!!))
    }

    private fun removeBeamLibrary() {
        BeamLibraryFixture.removeLibrary(project, myFixture.module, BEAM_LIBRARY_NAME)
    }

    companion object {
        /** Shared root for real Erlang stdlib `.beam` fixtures wanted by more than one suite. */
        val ERLANG_STDLIB_EBIN = File("testData/org/elixir_lang/beam/erlang_stdlib/ebin").absoluteFile

        private const val EBIN = "ebin"
        private const val BEAM_LIBRARY_NAME = "beam-fixture-lib"
    }
}
