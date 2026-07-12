package org.elixir_lang.inspection

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import org.elixir_lang.PlatformTestCase
import java.io.File

class UnresolvableTypeTest : PlatformTestCase() {
    override fun setUp() {
        super.setUp()
        myFixture.enableInspections(UnresolvableType::class.java)
    }

    fun testMissingTypeOnResolvedModuleFlagged() {
        myFixture.configureByFiles("missing_type_on_resolved_module_flagged.ex", "other.ex")
        myFixture.checkHighlighting()
    }

    fun testResolvedTypeNotFlagged() {
        myFixture.configureByFiles("resolved_type_not_flagged.ex", "other.ex")
        myFixture.checkHighlighting()
    }

    fun testMissingLocalTypeFlagged() {
        myFixture.configureByFiles("missing_local_type_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testTypeVariableNotFlagged() {
        myFixture.configureByFiles("type_variable_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSpecBareMissingTypeFlagged() {
        myFixture.configureByFiles("spec_bare_missing_type_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testTypeBodyUnboundVariableFlagged() {
        myFixture.configureByFiles("type_body_unbound_variable_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSpecWhenBoundVariableNotFlagged() {
        myFixture.configureByFiles("spec_when_bound_variable_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testBuiltinTypeNotFlagged() {
        myFixture.configureByFiles("builtin_type_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testUnresolvableModuleNotFlaggedByTypeInspection() {
        myFixture.configureByFiles("unresolvable_module_not_flagged_by_type.ex")
        myFixture.checkHighlighting()
    }

    fun testNamedTypeLabelNotFlagged() {
        myFixture.configureByFiles("named_type_label_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSelfQualifiedTypeNotFlagged() {
        myFixture.configureByFiles("self_qualified_type_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    fun testSpecHeadTypeReferenceNotFlagged() {
        myFixture.configureByFiles("spec_head_type_reference_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    /**
     * A qualified type from a decompiled BEAM module (`:queue.queue()` from the Erlang stdlib)
     * resolves to a [org.elixir_lang.beam.psi.impl.TypeDefinitionImpl], not a source `@type` [org.elixir_lang.psi.call.Call].
     * The inspection must recognise it as resolved via [org.elixir_lang.model.psi.type.TypeReference.resolvesToType],
     * not the source-only [org.elixir_lang.model.psi.type.TypeReference.resolveSymbols], so it is not flagged.
     */
    /**
     * A qualified type from a decompiled BEAM module (`:queue.queue()` from the Erlang stdlib) cannot be
     * enumerated: only `:erlang` built-in types are materialised as resolvable type definitions, so the
     * inspection must not claim a decompiled module's types are undefined. Guarded by the "qualifier resolves
     * to a [org.elixir_lang.beam.psi.impl.ModuleImpl]" check in the inspection.
     */
    /**
     * A qualified type from a decompiled BEAM module (`:queue.queue()` from the Erlang stdlib) resolves to a
     * [org.elixir_lang.beam.psi.impl.TypeDefinitionImpl] built from the beam's type data, so the inspection must
     * not flag it as undefined.
     */
    fun testBeamQualifiedTypeNotFlagged() {
        addBeamLibrary()
        myFixture.configureByFiles("beam_qualified_type_not_flagged.ex")
        myFixture.checkHighlighting()
    }

    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/inspection/unresolvable_type"

    @Throws(Exception::class)
    override fun tearDown() {
        try {
            removeBeamLibrary()
        } finally {
            super.tearDown()
        }
    }

    private fun addBeamLibrary() {
        val ebinDir = File(testDataPath, "ebin").absoluteFile
        assertTrue("ebin/ fixture directory not found at ${ebinDir.absolutePath}", ebinDir.isDirectory)

        VfsRootAccess.allowRootAccess(myFixture.testRootDisposable, ebinDir.path)

        val ebinVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ebinDir)
        assertNotNull("Could not find ebin/ fixture directory in VFS", ebinVf)

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val model = libraryTable.modifiableModel
            val library = model.createLibrary(BEAM_LIBRARY_NAME)
            val libModel = library.modifiableModel
            libModel.addRoot(ebinVf!!, OrderRootType.CLASSES)
            libModel.commit()
            model.commit()

            ModuleRootModificationUtil.addDependency(myFixture.module, library)
        }
    }

    /**
     * The light project module is shared across test methods and other suites, so drop the added
     * library order entry and project library in tearDown to avoid leaking `:queue` into later tests.
     */
    private fun removeBeamLibrary() {
        ModuleRootModificationUtil.updateModel(myFixture.module) { model ->
            model.orderEntries
                .filter { it.presentableName == BEAM_LIBRARY_NAME }
                .forEach { model.removeOrderEntry(it) }
        }

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            libraryTable.getLibraryByName(BEAM_LIBRARY_NAME)?.let { library ->
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }

    companion object {
        private const val BEAM_LIBRARY_NAME = "unresolvable-type-beam-lib"
    }
}
