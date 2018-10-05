package org.elixir_lang

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import org.elixir_lang.mix.Watcher
import org.elixir_lang.mix.library.Kind
import java.net.URI

/**
 * Watches the [project]'s `deps` folder for changes to `mix` deps
 *
 * * `deps/APPLICATION/{c_src,lib,priv,src}` - sources
 * * `_build/ENVIRONMENT/{consolidated,lib/APPLICATION/ebin` - classes
 */
class DepsWatcher(
        private val project: Project,
        private val moduleManager: ModuleManager,
        private val virtualFileManager: VirtualFileManager
) :
        ProjectComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        virtualFileManager.addVirtualFileListener(this, this)
    }

    /**
     * If a file is deleted, it is need to determine whether it affect no deps, 1 dep, or all deps.
     *
     * * `deps` - deleteAllLibraries(deps)
     * * `deps/APPLICATION` - deleteLibrary(deps/APPLICATION)
     *
     * Other deletes cause syncs in [syncLibraries]
     */
    override fun fileDeleted(event: VirtualFileEvent) {
        if (event.parent == project.baseDir && event.fileName == "deps") {
            deleteAllLibraries(event.file)
        } else if (event.parent?.parent == project.baseDir && event.parent?.name == "deps") {
            deleteLibrary(event.file)
        } else {
            syncLibraries(event)
        }
    }

    /**
     * If a file is created, i is needed to determined whether it affects no deps, 1 dep, or all deps
     *
     * * `deps` - syncLibraries(deps)
     * * `deps/APPLICATION` - syncLibrary(deps/APPLICATION)
     *
     * Other creates cause syncs in [syncLibraries]
     */
    override fun fileCreated(event: VirtualFileEvent) {
        if (event.fileName == "deps" && event.parent == project.baseDir) {
            syncLibraries(event.file)
        } else if (event.parent?.name == "deps" && event.parent?.parent == project.baseDir) {
            syncLibrary(event.file)
        } else {
            syncLibraries(event)
        }
    }

    override fun dispose() = disposeComponent()

    /**
     * Common syncs shared with [fileCreated] and [fileDeleted].
     *
     * * `_build` - syncLibraries(deps)
     * * `_build/ENVIRONMENT` - syncLibraries(deps)
     * * `_build/ENVIRONMENT/consolidated` - syncLibraries(deps)
     * * `_build/ENVIRONMENT/lib` - syncLibraries(deps)
     * * `deps/APPLICATION/{c_src,lib,priv,src}` - syncLibrary(deps/APPLICATION)
     * * `_build/ENVIRONMENT/lib/APPLICATION` - syncLibrary(deps/APPLICATION)
     * * `_build/ENVIRONMENT/lib/APPLICATION/ebin` - syncLibrary(deps/APPLICATION)
     */
    private fun syncLibraries(event: VirtualFileEvent) {
        val baseDir = project.baseDir
        val fileName = event.fileName

        event.parent?.let { parent ->
            if (fileName == "_build" && parent == baseDir) {
                syncLibraries(project)
            } else {
                parent.parent?.let { grandParent ->
                    if (parent.name == "_build" && grandParent == baseDir) {
                        syncLibraries(project)
                    } else {
                        grandParent.parent?.let { greatGrandParent ->
                            if (fileName in arrayOf("consolidated", "lib") && grandParent.name == "_build" && greatGrandParent == baseDir) {
                                syncLibraries(project)
                            } else if (fileName in SOURCE_NAMES && grandParent.name == "deps" && greatGrandParent == baseDir) {
                                syncLibrary(parent)
                            } else {
                                greatGrandParent.parent?.let { greatGreatGrandParent ->
                                    if (parent.name == "lib" && greatGrandParent.name == "_build" && greatGreatGrandParent == baseDir) {
                                        baseDir.findChild("deps")?.findChild(fileName)?.let { syncLibrary(it) }
                                    } else if (fileName == "ebin" && grandParent.name == "lib" && greatGreatGrandParent.name == "_build" && greatGreatGrandParent.parent == baseDir) {
                                        baseDir.findChild("deps")?.findChild(parent.name)?.let { syncLibrary(it) }
                                    } else {
                                        null
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun syncLibraries(project: Project) {
        project.baseDir.findChild("deps")?.let { syncLibraries(it) }
    }

    private fun syncLibrary(dep: VirtualFile) = syncLibraries(arrayOf(dep))

    private fun syncLibraries(deps: VirtualFile) = deps.children.let { syncLibraries(it) }

    private fun syncLibraries(deps: Array<VirtualFile>) {
        ApplicationManager.getApplication().invokeAndWait {
            ApplicationManager.getApplication().runWriteAction {
                val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

                syncLibraries(deps, libraryTable)
            }
        }

        moduleManager.modules.forEach { module ->
            module.getComponent(Watcher::class.java).syncLibraries()
        }
    }

    private fun syncLibraries(
            deps: Array<VirtualFile>,
            libraryTable: LibraryTable
    ) {
        val libraryTableModifiableModel = libraryTable.modifiableModel

        deps.forEach { dep ->
            syncLibrary(dep, libraryTable, libraryTableModifiableModel)
        }

        libraryTableModifiableModel.commit()

    }

    private fun syncLibrary(
            dep: VirtualFile,
            libraryTable: LibraryTable,
            libraryTableModifiableModel: LibraryTable.ModifiableModel
    ) {
        val depName = dep.name

        val library = libraryTable.getLibraryByName(depName)
                ?: libraryTableModifiableModel.createLibrary(dep.name, Kind)

        syncLibrary(library, dep, depName)
    }

    private fun syncLibrary(library: Library, dep: VirtualFile, depName: String) {
        val libraryModifiableModel = library.modifiableModel

        project.baseDir.findChild("_build")?.let { build ->
            build.children.filter { it.isDirectory }.forEach { environment ->
                environment.children.filter { it.isDirectory }.forEach { environmentChild ->
                    val environmentChildName = environmentChild.name

                    if (environmentChildName == "consolidated") {
                        libraryModifiableModel.addRoot(environmentChild, OrderRootType.CLASSES)
                    } else if (environmentChildName == "lib") {
                        environmentChild.findChild(depName)?.let{ depEnvironmentLibrary ->
                            depEnvironmentLibrary.findChild("ebin")?.let { ebin ->
                                if (ebin.isDirectory) {
                                    /* Mark build output as excluded when marking it as CLASSES, so that
                                       dependency will show up in External Libraries AND be pushed out into
                                       non-project results */
                                    ModuleUtil
                                            .findModuleForFile(depEnvironmentLibrary, project)
                                            ?.let { module ->
                                                ModuleRootManager.getInstance(module).modifiableModel.apply {
                                                    contentEntries.forEach { contentEntry ->
                                                        contentEntry.addExcludeFolder(depEnvironmentLibrary)
                                                    }

                                                    commit()
                                                }
                                            }

                                    libraryModifiableModel.addRoot(ebin, OrderRootType.CLASSES)
                                }
                            }
                        }
                    }
                }
            }
        }

        dep.children.filter { it.isDirectory }.forEach { child ->
            if (child.name in SOURCE_NAMES) {
                libraryModifiableModel.addRoot(child, OrderRootType.SOURCES)
            }
        }

        libraryModifiableModel.commit()
    }

    private fun deleteAllLibraries(deps: VirtualFile) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        val depsLibraries = libraryTable.libraries.filter { library ->
            // `getFile` won't return a `VirtualFile` that has been deleted, so need to use `getUrls`
            val urls = library.getUrls(OrderRootType.SOURCES)

            if (urls.isNotEmpty()) {
                val prefixURI = URI(deps.url)

                urls.all { url ->
                    val uri = URI(url)
                    val relativeURI  = prefixURI.relativize(uri)

                    !relativeURI.isAbsolute && !relativeURI.toString().startsWith("../")
                }
            } else {
                false
            }
        }

        if (depsLibraries.isNotEmpty()) {
            ApplicationManager.getApplication().invokeAndWait {
                ApplicationManager.getApplication().runWriteAction {
                    depsLibraries.forEach { libraryTable.removeLibrary(it) }
                }
            }
        }
    }

    private fun deleteLibrary(dep: VirtualFile) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
        val depName = dep.name

        libraryTable.getLibraryByName(depName)?.let { library ->
            ApplicationManager.getApplication().invokeAndWait {
                ApplicationManager.getApplication().runWriteAction {
                    libraryTable.removeLibrary(library)
                }
            }
        }
    }
}

private val SOURCE_NAMES = arrayOf("c_src", "lib", "priv", "src")
