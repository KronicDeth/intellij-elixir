package org.elixir_lang.beam

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiManager
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.impl.ModuleImpl
import java.io.File

/**
 * `ModuleImpl.setMirror` matches compiled stubs to decompiled call definitions by name and arity, so a stub
 * with no counterpart is skipped; pairing them by position instead threw `InvalidMirrorException` and left
 * the whole module without a mirror. `Decompiler.definitionLimit` puts both cases in existing fixtures:
 * above it private functions are never decompiled and their stubs cannot match, below it every stub must.
 */
class StubMirrorTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/beam/decompiler"

    fun testOtpPubKeyMirrorsDespiteUnmatchedStubs() {
        assertMirrorsDespiteUnmatchedStubs("OTP-PUB-KEY")
    }

    fun testGlMirrorsDespiteUnmatchedStubs() {
        assertMirrorsDespiteUnmatchedStubs("gl")
    }

    /**
     * Nothing else asserts that matching still matches: the golden fixtures compare decompiler text and
     * `assertParseable` only looks for parse errors, so this could break entirely and leave a green suite.
     */
    fun testQueueMirrorsEveryStub() {
        val callDefinitions = mirroredModule("queue").callDefinitions()
        assertTrue("queue.beam has no call definitions", callDefinitions.isNotEmpty())

        val unmatched = callDefinitions.filter { it.mirror == null }

        assertEmpty(
            "queue.beam is under Decompiler.definitionLimit, so all ${callDefinitions.size} stubs should " +
                "have a mirror. Unmatched: " + unmatched.take(10).joinToString { it.exportedName() },
            unmatched
        )
    }

    private fun assertMirrorsDespiteUnmatchedStubs(name: String) {
        val callDefinitions = mirroredModule(name).callDefinitions()
        val unmatched = callDefinitions.filter { it.mirror == null }
        val distinctMirrors = callDefinitions.mapNotNull { it.mirror }.distinct()

        // Raising Decompiler.definitionLimit past this fixture decompiles its private functions too, closing
        // the gap and leaving the case below asserting nothing. Repoint it at a larger fixture.
        assertTrue(
            "$name.beam decompiled to ${distinctMirrors.size} call definitions for ${callDefinitions.size} " +
                "stubs, so it no longer exercises a stub/mirror mismatch. Was Decompiler.definitionLimit " +
                "raised past ${callDefinitions.size}?",
            distinctMirrors.size != callDefinitions.size && unmatched.isNotEmpty()
        )

        // An unmatched exported function is the case ModuleImpl.setMirror warns about, and would make this
        // a test about that warning instead.
        val unmatchedExported = unmatched.filter { it.isExported }
        assertEmpty(
            "Unmatched exported functions in $name: " + unmatchedExported.take(10).joinToString { it.exportedName() },
            unmatchedExported
        )

        assertTrue(
            "$name: all ${callDefinitions.size} stubs were unmatched, expected some to be mirrored",
            unmatched.size < callDefinitions.size
        )
    }

    /**
     * `BeamFileImpl.getMirror` turns an `InvalidMirrorException` into `LOGGER.error`, which the default
     * `LoggedErrorProcessor` rethrows - so building the mirror is itself the assertion that neither
     * `setMirror` call site rejected the decompiled source.
     */
    private fun mirroredModule(name: String): ModuleImpl<*> {
        val beam = File(testDataPath, "$name.beam")
        VfsRootAccess.allowRootAccess(testRootDisposable, beam.parentFile.absolutePath)

        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(beam)
        assertNotNull("No VirtualFile for $name.beam", virtualFile)

        val compiledFile = PsiManager.getInstance(project).findFile(virtualFile!!)
        assertInstanceOf(compiledFile, PsiCompiledFile::class.java)
        assertNotNull("Decompiled PSI file is null for $name", (compiledFile as PsiCompiledFile).decompiledPsiFile)

        val module = compiledFile.children.singleOrNull() as? ModuleImpl<*>
        assertNotNull("$name.beam has no ModuleImpl child", module)
        assertNotNull("$name's module element has no mirror", module!!.mirror)

        return module
    }
}
