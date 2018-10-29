package org.elixir_lang

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
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
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Libraries in deps", true) {
                override fun run(indicator: ProgressIndicator) {
                    syncLibraries(event.file, indicator)
                }
            })
        } else if (event.parent?.name == "deps" && event.parent?.parent == project.baseDir) {
            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Library in deps/${event.fileName}", true) {
                override fun run(indicator: ProgressIndicator) {
                    syncLibrary(event.file, indicator)
                }
            })
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
                ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Libraries in _build", true) {
                    override fun run(indicator: ProgressIndicator) {
                        syncLibraries(project, indicator)
                    }
                })
            } else {
                parent.parent?.let { grandParent ->
                    if (parent.name == "_build" && grandParent == baseDir) {
                        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Libraries in _build/$fileName", true) {
                            override fun run(indicator: ProgressIndicator) {
                                syncLibraries(project, indicator)
                            }
                        })
                    } else {
                        grandParent.parent?.let { greatGrandParent ->
                            if (fileName in arrayOf("consolidated", "lib") && grandParent.name == "_build" && greatGrandParent == baseDir) {
                                ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Libraries in _build/${parent.name}/$fileName", true) {
                                    override fun run(indicator: ProgressIndicator) {
                                        syncLibraries(project, indicator)
                                    }
                                })
                            } else if (fileName in SOURCE_NAMES && grandParent.name == "deps" && greatGrandParent == baseDir) {
                                ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Library in deps/${parent.name}", true) {
                                    override fun run(indicator: ProgressIndicator) {
                                        syncLibrary(parent, indicator)
                                    }
                                })
                            } else {
                                greatGrandParent.parent?.let { greatGreatGrandParent ->
                                    if (parent.name == "lib" && greatGrandParent.name == "_build" && greatGreatGrandParent == baseDir) {
                                        baseDir.findChild("deps")?.findChild(fileName)?.let {
                                            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Library in _build/${grandParent.name}/lib/$fileName", true) {
                                                override fun run(indicator: ProgressIndicator) {
                                                    syncLibrary(it, indicator)
                                                }
                                            })
                                        }
                                    } else if (fileName == "ebin" && grandParent.name == "lib" && greatGreatGrandParent.name == "_build" && greatGreatGrandParent.parent == baseDir) {
                                        baseDir.findChild("deps")?.findChild(parent.name)?.let {
                                            ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Syncing Library in _build/lib/${parent.name}/ebin", true) {
                                                override fun run(indicator: ProgressIndicator) {
                                                    syncLibrary(it, indicator)
                                                }
                                            })
                                        }
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

    fun syncLibraries(project: Project, progressIndicator: ProgressIndicator) {
        project.baseDir.findChild("deps")?.let { syncLibraries(it, progressIndicator) }
    }

    private fun syncLibrary(dep: VirtualFile, progressIndicator: ProgressIndicator) = syncLibraries(arrayOf(dep), progressIndicator)

    private fun syncLibraries(deps: VirtualFile, progressIndicator: ProgressIndicator) = deps.children.let { syncLibraries(it, progressIndicator) }

    private fun syncLibraries(deps: Array<VirtualFile>, progressIndicator: ProgressIndicator) {
        if (deps.isNotEmpty()) {
            ApplicationManager.getApplication().invokeAndWait {
                ApplicationManager.getApplication().runWriteAction {
                    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

                    syncLibraries(deps, libraryTable, progressIndicator)
                }
            }

            for (module in moduleManager.modules) {
                if (progressIndicator.isCanceled) {
                    break
                }

                module.getComponent(Watcher::class.java).syncLibraries(progressIndicator)
            }
        }
    }

    private fun syncLibraries(
            deps: Array<VirtualFile>,
            libraryTable: LibraryTable,
            progressIndicator: ProgressIndicator
    ) {
        val libraryTableModifiableModel = libraryTable.modifiableModel

        for (dep in deps) {
            if (progressIndicator.isCanceled) {
                break
            }

            syncLibrary(dep, libraryTable, libraryTableModifiableModel, progressIndicator)
        }

        libraryTableModifiableModel.commit()
    }

    private fun syncLibrary(
            dep: VirtualFile,
            libraryTable: LibraryTable,
            libraryTableModifiableModel: LibraryTable.ModifiableModel,
            progressIndicator: ProgressIndicator
    ) {
        val depName = dep.name

        val library = libraryTable.getLibraryByName(depName)
                ?: libraryTableModifiableModel.createLibrary(dep.name, Kind)

        syncLibrary(library, dep, depName, progressIndicator)
    }

    private fun syncLibrary(library: Library, dep: VirtualFile, depName: String, progressIndicator: ProgressIndicator) {
        val libraryModifiableModel = library.modifiableModel

        project.baseDir.findChild("_build")?.let { build ->
            build.children.filter { it.isDirectory }.forEach { environment ->
                environment.children.filter { it.isDirectory }.forEach { environmentChild ->
                    val environmentChildName = environmentChild.name

                    if (environmentChildName == "consolidated") {
                        libraryModifiableModel.addRoot(environmentChild, OrderRootType.CLASSES)
                    } else if (environmentChildName == "lib") {
                        environmentChild.findChild(depName)?.let { depEnvironmentLibrary ->
                            depEnvironmentLibrary.findChild("ebin")?.let { ebin ->
                                if (ebin.isDirectory) {
                                    /* Mark build output as excluded when marking it as CLASSES, so that
                                       dependency will show up in External Libraries AND be pushed out into
                                       non-project results */
                                    ModuleUtil
                                            .findModuleForFile(depEnvironmentLibrary, project)
                                            ?.let { module ->
                                                ModuleRootManager.getInstance(module).modifiableModel.apply {
                                                    progressIndicator.text2 = "Excluding _build/lib/$depName/ebin from project so it is treated as an External Library"

                                                    for (contentEntry in contentEntries) {
                                                        if (progressIndicator.isCanceled) {
                                                            break
                                                        }

                                                        contentEntry.addExcludeFolder(depEnvironmentLibrary)
                                                    }

                                                    commit()
                                                }
                                            }

                                    if (!progressIndicator.isCanceled) {
                                        progressIndicator.text2 = "Adding _build/lib/$depName/ebin as a Classes root for $"
                                        libraryModifiableModel.addRoot(ebin, OrderRootType.CLASSES)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (child in dep.children) {
            if (progressIndicator.isCanceled) {
                break
            }

            if (child.isDirectory) {
                val childName = child.name

                if (childName in SOURCE_NAMES) {
                    progressIndicator.text2 = "Adding $childName as Source root for $depName"
                    libraryModifiableModel.addRoot(child, OrderRootType.SOURCES)
                }
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
