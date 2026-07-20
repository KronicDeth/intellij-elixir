package org.elixir_lang.reference.mfa_tuple

import com.intellij.model.psi.PsiSymbolReferenceService
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.beam.psi.impl.CallDefinitionImpl
import org.elixir_lang.model.psi.atom.AtomReference
import org.elixir_lang.psi.ElixirAtom
import java.io.File

/**
 * Tests that Erlang-style MFA tuples (`{:math, :sqrt, 1}`) resolve the function atom
 * to the corresponding BEAM module function definition.
 *
 * Validates Gap #6 of the MFA tuple resolution plan: Erlang modules referenced via atom
 * syntax in MFA tuples (`:module` as element[0]) resolve correctly when the module comes
 * from a BEAM file.
 */
class ErlangMfaTupleReferenceTest : PlatformTestCase() {

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

    /**
     * `:sqrt` in `{:math, :sqrt, 1}` should resolve to the `sqrt/1` function
     * defined in the `:math` BEAM module.
     */
    fun testErlangMfaFunctionAtomResolvesFromBeam() {
        val reference = atomReferenceAtCaret("erlang_mfa.ex")

        val resolveResults = reference.multiResolve(false)
        assertTrue(
            "Expected {:math, :sqrt, 1} to resolve - BEAM module lookup failed",
            resolveResults.isNotEmpty()
        )
        val validResult = resolveResults.firstOrNull { it.isValidResult }
        assertNotNull("Expected at least one valid result for :math.sqrt/1", validResult)

        val element = validResult!!.element
        assertNotNull("Valid resolve result has null element", element)
        assertTrue(
            "Expected BEAM CallDefinitionImpl, got ${element?.javaClass?.simpleName}",
            element is CallDefinitionImpl<*>
        )
        val resolvedName = (element as CallDefinitionImpl<*>).nameArityInterval.name
        assertEquals("sqrt", resolvedName)
    }

    /**
     * `resolve()` must return non-null for `:sqrt` in `{:math, :sqrt, 1}`.
     */
    fun testErlangMfaResolveReturnsSingleElement() {
        val reference = atomReferenceAtCaret("erlang_mfa.ex")

        val resolved = reference.resolve()
        assertNotNull("resolve() returned null for Erlang MFA {:math, :sqrt, 1}", resolved)
        assertTrue(
            "Expected BEAM CallDefinitionImpl, got ${resolved?.javaClass?.simpleName}",
            resolved is CallDefinitionImpl<*>
        )
        assertEquals("sqrt", (resolved as CallDefinitionImpl<*>).nameArityInterval.name)
    }

    /**
     * The reference must be soft so that MFA tuples with unresolvable Erlang modules
     * do not show unresolved-reference errors.
     */
    fun testErlangMfaReferenceIsSoft() {
        val reference = atomReferenceAtCaret("erlang_mfa.ex")

        assertTrue("AtomReference must be soft", reference.isSoft)
    }

    private fun atomReferenceAtCaret(fileName: String): AtomReference {
        myFixture.configureByFile(fileName)

        val elementAtCaret = myFixture.file.findElementAt(myFixture.caretOffset)
            ?: error("No element at caret in $fileName")
        val atom = PsiTreeUtil.getParentOfType(elementAtCaret, ElixirAtom::class.java, false)
            ?: error("No ElixirAtom at caret in $fileName")

        @Suppress("UnstableApiUsage")
        return PsiSymbolReferenceService.getService()
            .getReferences(atom)
            .filterIsInstance<AtomReference>()
            .singleOrNull()
            ?: error("Expected AtomReference at caret in $fileName")
    }

    private fun addBeamLibrary() {
        val beamFile = File(testDataPath, "math.beam")
        assertTrue("math.beam not found at ${beamFile.absolutePath}", beamFile.exists())

        val beamDir = beamFile.parentFile.absolutePath
        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, beamDir)

        val beamDirVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(beamDir))
        assertNotNull("Could not find beam test data directory: $beamDir", beamDirVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(beamDirVf!!, OrderRootType.CLASSES)
            libModel.commit()
            model.commit()

            ModuleRootModificationUtil.addDependency(myFixture.module, library)
        }
    }

    private fun removeBeamLibrary() {
        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val library = libraryTable.getLibraryByName(LIBRARY_NAME)
            if (library != null) {
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/reference/mfa_tuple"

    companion object {
        private const val LIBRARY_NAME = "erlang_mfa_test_lib"
    }
}
