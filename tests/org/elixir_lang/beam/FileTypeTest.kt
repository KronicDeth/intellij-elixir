package org.elixir_lang.beam

import com.intellij.openapi.fileTypes.FileTypeManager
import org.elixir_lang.PlatformTestCase
import org.junit.Assert

/**
 * `.beam` must stay registered as a **binary** file type.
 *
 * `StubTreeLoaderImpl`'s staleness check compares a file's indexed content length against its current
 * one only when both are non-negative, and it reports any binary file's current length as `-1`. Since
 * `_build/<env>/lib/<app>/ebin` is deliberately left indexed (for Phoenix EEx template line
 * breakpoints) and `mix compile` rewrites it from outside the IDE, a `.beam` that stopped being binary
 * would re-enter that comparison and every recompile could raise "Outdated stub in index".
 */
class FileTypeTest : PlatformTestCase() {
    fun testBeamExtensionMapsToBeamFileType() {
        Assert.assertSame(
            FileType.INSTANCE,
            FileTypeManager.getInstance().getFileTypeByFileName("Elixir.Enum.beam")
        )
    }

    fun testBeamFileTypeIsBinary() {
        Assert.assertTrue(
            "BEAM must be binary so the platform skips its stub-index content-length comparison",
            FileTypeManager.getInstance().getFileTypeByFileName("Elixir.Enum.beam").isBinary
        )
    }
}
