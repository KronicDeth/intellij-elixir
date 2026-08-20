package org.elixir_lang.beam

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.junit.Assert

/**
 * Round-trip validity coverage: decompile every `.beam` in the resolved Elixir/Erlang SDKs and
 * confirm the generated source parses as valid Elixir (the decompiled PSI has no [PsiErrorElement]).
 * This is the same check [DecompilerTest.assertParseable] applies to its golden fixtures, scaled to
 * the whole stdlib of the SDK under test - so decompiler gaps surface in CI on a version bump.
 *
 * Needs the platform PSI/parser (decompiler + Elixir ParserDefinition), so it extends
 * PlatformTestCase and sweeps within a single fixture (rather than the lightweight per-beam
 * [SdkBeamParseTest], which only exercises the platform-free chunk parser).
 */
class SdkDecompileParseableTest : PlatformTestCase() {

    fun testElixirSdkDecompilesToParseableElixir() {
        sweep(System.getenv("ELIXIR_LANG_ELIXIR_PATH"), "Elixir")
    }

    fun testErlangSdkDecompilesToParseableElixir() {
        sweep(System.getenv("ERLANG_SDK_HOME"), "Erlang")
    }

    private fun sweep(root: String?, label: String) {
        Assert.assertNotNull("$label SDK env var not set", root)
        VfsRootAccess.allowRootAccess(testRootDisposable, root!!)

        val beams = SdkBeams.forSdk(root, label.lowercase())
        Assert.assertTrue("No .beam files found under $root/lib", beams.isNotEmpty())

        val failures = mutableListOf<String>()
        for ((beamLabel, file) in beams) {
            try {
                val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
                if (virtualFile == null) {
                    failures += "$beamLabel: no VirtualFile"
                    continue
                }
                val compiled = PsiManager.getInstance(project).findFile(virtualFile)
                if (compiled !is PsiCompiledFile) {
                    failures += "$beamLabel: not a PsiCompiledFile (${compiled?.javaClass?.simpleName})"
                    continue
                }
                val decompiled = compiled.decompiledPsiFile
                val error = PsiTreeUtil.findChildOfType(decompiled, PsiErrorElement::class.java)
                if (error != null) {
                    failures += "$beamLabel: ${error.errorDescription}"
                }
            } catch (t: Throwable) {
                failures += "$beamLabel: ${t.javaClass.simpleName}: ${t.message}"
            }
        }

        val passed = beams.size - failures.size
        println("[decompile-parseable] $label: $passed/${beams.size} beams decompiled to parseable Elixir")
        Assert.assertTrue(
            "${failures.size}/${beams.size} $label beams did NOT decompile to parseable Elixir:\n" +
                failures.take(100).joinToString("\n") +
                (if (failures.size > 100) "\n… and ${failures.size - 100} more" else ""),
            failures.isEmpty()
        )
    }
}
