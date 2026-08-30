package org.elixir_lang.beam.psi.impl

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import java.io.File

/**
 * `getNavigationElement()` must not throw for a decompiled element whose mirror was never set.
 *
 * `ModuleImpl.setMirror` logs-and-skips any definition the decompiled source lacks, so a mirror-less
 * `CallDefinitionImpl` is ordinary: 139 of `gl.beam`'s 1068 are compiler-generated comprehension helpers.
 */
class MirrorlessNavigationElementTest : PlatformTestCase() {
    fun testMirrorlessCallDefinitionNavigatesToItself() {
        val (withoutMirror, _) = callDefinitionsOf(FIXTURE)

        assertFalse(
            "$FIXTURE no longer has a call definition without a mirror, so it cannot cover this. Delete the " +
                "test if the decompiler now renders them, or swap in OTP-PUB-KEY.beam, which had 1112",
            withoutMirror.isEmpty()
        )

        for (callDefinition in withoutMirror) {
            assertSame(
                "getNavigationElement() on the mirror-less ${callDefinition.exportedName()} should fall back to " +
                    "the element itself",
                callDefinition,
                callDefinition.navigationElement
            )
        }
    }

    fun testMirroredCallDefinitionStillNavigatesToItsMirror() {
        val (_, withMirror) = callDefinitionsOf(FIXTURE)

        assertFalse("$FIXTURE decompiled to no mirrored call definitions at all", withMirror.isEmpty())

        for (callDefinition in withMirror) {
            assertSame(
                "getNavigationElement() on the mirrored ${callDefinition.exportedName()} should still be its mirror",
                callDefinition.mirror,
                callDefinition.navigationElement
            )
        }
    }

    /** Decompiles [beamName] and splits its call definitions by whether `setMirror` reached them. */
    private fun callDefinitionsOf(beamName: String): Pair<List<CallDefinitionImpl<*>>, List<CallDefinitionImpl<*>>> {
        val directory = File(DECOMPILER_TEST_DATA).absoluteFile
        VfsRootAccess.allowRootAccess(testRootDisposable, directory.path)

        val beam = File(directory, beamName)
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(beam)
        assertNotNull("Could not find $beamName in VFS at ${beam.absolutePath}", virtualFile)

        val compiledFile = PsiManager.getInstance(project).findFile(virtualFile!!)
        assertTrue("$beamName is not a PsiCompiledFile", compiledFile is PsiCompiledFile)

        // Builds the mirror, which is what populates the per-element mirrors this test partitions on.
        (compiledFile as PsiCompiledFile).decompiledPsiFile

        val modules = PsiTreeUtil.findChildrenOfType(compiledFile, ModuleImpl::class.java)
            .ifEmpty { compiledFile.children.filterIsInstance<ModuleImpl<*>>() }
        assertFalse("$beamName decompiled to no modules", modules.isEmpty())

        val callDefinitions = modules.flatMap { (it as ModuleImpl<*>).callDefinitions().asIterable() }

        return callDefinitions.partition { it.mirror == null }
    }

    companion object {
        private const val DECOMPILER_TEST_DATA = "testData/org/elixir_lang/beam/decompiler"

        /** 139 of its 1068 call definitions are `-name/arity-lbc$^0/2-0-` helpers the decompiler does not emit. */
        private const val FIXTURE = "gl.beam"
    }
}
