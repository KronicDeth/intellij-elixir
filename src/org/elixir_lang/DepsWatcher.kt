package org.elixir_lang

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager

class DepsWatcher(private val project: Project, private val virtualFileManager: VirtualFileManager) :
        ProjectComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        if (project.baseDir.findChild("mix.exs") != null) {
            project.baseDir.findChild("deps")?.let { deps ->
                syncLibraries(deps)
            }
        }

        virtualFileManager.addVirtualFileListener(this, this)
    }

    override fun fileDeleted(event: VirtualFileEvent) {
        event.parent?.let { parent ->
            if (parent == project.baseDir && event.fileName == "deps") {
                deleteAllLibraries(event.file)
            } else if (event.parent == project.baseDir.findChild("deps")) {
                deleteLibrary(event.file)
            }
        }
    }

    override fun fileCreated(event: VirtualFileEvent) {
        event.parent?.let { parent ->
            if (parent == project.baseDir && event.fileName == "deps") {
                syncLibraries(event.file)
            } else if (event.parent == project.baseDir.findChild("deps")) {
                syncLibrary(event.file)
            }
        }
    }

    override fun dispose() = disposeComponent()

    private fun syncLibraries(deps: VirtualFile) = deps.children.forEach { syncLibrary(it) }

    private fun syncLibrary(dep: VirtualFile) {
        if (dep.isDirectory) {
            ApplicationManager.getApplication().invokeLater {
                ApplicationManager.getApplication().runWriteAction {
                    val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                    val depName = dep.name

                    val library = libraryTable.getLibraryByName(depName)
                            ?: LibraryTablesRegistrar.getInstance().getLibraryTable(project).createLibrary(dep.name)

                    val modifiableModel = library.modifiableModel

                    project.baseDir.findChild("_build")?.let { build ->
                        build.children.filter { it.isDirectory }.forEach { environment ->
                            environment.children.filter { it.isDirectory }.forEach { environmentChild ->
                                val environmentChildName = environmentChild.name

                                if (environmentChildName == "consolidated") {
                                    modifiableModel.addRoot(environmentChild, OrderRootType.CLASSES)
                                } else if (environmentChildName == "lib") {
                                    environmentChild.findChild(depName)?.findChild("ebin")?.let { ebin ->
                                        if (ebin.isDirectory) {
                                            modifiableModel.addRoot(ebin, OrderRootType.CLASSES)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    dep.children.filter { it.isDirectory }.forEach { child ->
                        nameToRootType(child.name)?.let { rootType ->
                            modifiableModel.addRoot(child, rootType)
                        }
                    }

                    modifiableModel.commit()
                }
            }
        }
    }

    private fun nameToRootType(name: String): OrderRootType? =
            when (name) {
                "c_src", "src", "lib", "priv" -> OrderRootType.SOURCES
                else -> null
            }

    private fun deleteAllLibraries(deps: VirtualFile) {
        val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)

        for (library in libraryTable.libraryIterator) {
            TODO()
        }
    }

    private fun deleteLibrary(dep: VirtualFile) {
        TODO()
    }
}
