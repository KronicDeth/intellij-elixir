package org.elixir_lang.beam

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile

/**
 * Registers and removes project libraries holding `.beam` fixtures.
 *
 * [BeamLibraryTestCase] covers the common case - one `ebin/` directory as a CLASSES root - and should be
 * preferred. This exists for suites that cannot use it: those extending a different base class, and those
 * whose fixture layout is the point of the test (a SOURCES root alongside the CLASSES root, so that
 * source-over-decompiled resolution can be exercised; or a root that is not named `ebin`).
 */
object BeamLibraryFixture {
    /**
     * Creates [libraryName] with [classesRoots] and [sourcesRoots] and adds it as a module dependency.
     *
     * Both root lists are attached in a single write action, because a library committed without its roots is
     * briefly visible to the project model and can be indexed empty.
     */
    fun addLibrary(
        project: Project,
        module: Module,
        libraryName: String,
        classesRoots: List<VirtualFile>,
        sourcesRoots: List<VirtualFile> = emptyList(),
    ) {
        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            val tableModel = libraryTable.modifiableModel
            val library = tableModel.createLibrary(libraryName)

            library.modifiableModel.let { libraryModel ->
                classesRoots.forEach { libraryModel.addRoot(it, OrderRootType.CLASSES) }
                sourcesRoots.forEach { libraryModel.addRoot(it, OrderRootType.SOURCES) }
                libraryModel.commit()
            }

            tableModel.commit()

            ModuleRootModificationUtil.addDependency(module, library)
        }
    }

    /**
     * Removes [libraryName] and the module's dependency on it.
     *
     * The order matters: the order entry has to go before the library, or the module keeps an entry pointing
     * at a disposed library.
     */
    fun removeLibrary(project: Project, module: Module, libraryName: String) {
        ModuleRootModificationUtil.updateModel(module) { model ->
            model.orderEntries
                .filter { it.presentableName == libraryName }
                .forEach { model.removeOrderEntry(it) }
        }

        WriteAction.run<Throwable> {
            val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
            libraryTable.getLibraryByName(libraryName)?.let { library ->
                libraryTable.modifiableModel.let { model ->
                    model.removeLibrary(library)
                    model.commit()
                }
            }
        }
    }
}
