package org.elixir_lang.beam

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.PsiCompiledFile
import com.intellij.psi.PsiManager
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.junit.Assert

/**
 * Mirror coverage: every exported definition in every `.beam` the resolved SDKs ship must come out of
 * `ModuleImpl.setMirror` with a mirror.
 *
 * [SdkDecompileParseableTest] sweeps the same beams but stops one step short of this. Mirror mapping
 * runs *after* the parse, matching each exported stub against the decompiled source by name and
 * arity, so a definition the decompiled source never produced a matching clause for parses fine and
 * passes that sweep silently. It surfaces only at runtime, as a "No decompiled source function with
 * name" warning and a navigation target that goes nowhere.
 *
 * A mirror-less *unexported* definition is ordinary and is not asserted here - compiler-generated
 * comprehension helpers are never emitted into decompiled source and `setMirror` deliberately skips
 * them (see `MirrorlessNavigationElementTest`, where 139 of `gl.beam`'s 1068 definitions are exactly
 * that).
 */
class SdkMirrorCoverageTest : PlatformTestCase() {
    fun testElixirSdkExportedDefinitionsAllGetMirrors() {
        sweep(System.getenv("ELIXIR_LANG_ELIXIR_PATH"), "Elixir")
    }

    fun testErlangSdkExportedDefinitionsAllGetMirrors() {
        sweep(System.getenv("ERLANG_SDK_HOME"), "Erlang")
    }

    private fun sweep(root: String?, label: String) {
        Assert.assertNotNull("$label SDK env var not set", root)
        VfsRootAccess.allowRootAccess(testRootDisposable, root!!)

        val beams = SdkBeams.forSdk(root, label.lowercase())
        Assert.assertTrue("No .beam files found under $root/lib", beams.isNotEmpty())

        val state = ResolveState.initial()
        val misses = mutableListOf<String>()
        var exported = 0

        for ((beamLabel, file) in beams) {
            try {
                val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file) ?: continue
                val compiled = PsiManager.getInstance(project).findFile(virtualFile)

                if (compiled !is PsiCompiledFile) continue

                // Building the mirror is what populates the per-element mirrors asserted below.
                compiled.decompiledPsiFile

                val modules = PsiTreeUtil.findChildrenOfType(compiled, ModuleImpl::class.java)
                    .ifEmpty { compiled.children.filterIsInstance<ModuleImpl<*>>() }

                for (module in modules) {
                    for (callDefinition in (module as ModuleImpl<*>).callDefinitions()) {
                        if (!callDefinition.isExported) continue

                        exported++

                        if (callDefinition.mirror == null) {
                            misses += "$beamLabel: ${nameArity(callDefinition, state)}"
                        }
                    }
                }
            } catch (t: Throwable) {
                misses += "$beamLabel: ${t.javaClass.simpleName}: ${t.message}"
            }
        }

        println("[mirror-coverage] $label: ${exported - misses.size}/$exported exported definitions got a mirror")
        Assert.assertTrue(
            "${misses.size} exported $label definitions decompiled without a mirror, so navigating to them " +
                "lands nowhere:\n" +
                misses.take(100).joinToString("\n") +
                (if (misses.size > 100) "\n… and ${misses.size - 100} more" else ""),
            misses.isEmpty()
        )
    }

    private fun nameArity(callDefinition: CallDefinitionImpl<*>, state: ResolveState): String =
        try {
            "${callDefinition.exportedName()}/${callDefinition.exportedArity(state)}"
        } catch (t: Throwable) {
            callDefinition.exportedName()
        }
}
