package org.elixir_lang.mix

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import org.elixir_lang.NameArity
import org.elixir_lang.psi.AccumulatorContinue
import org.elixir_lang.psi.CallDefinitionClause.isFunction
import org.elixir_lang.psi.CallDefinitionClause.isPublicFunction
import org.elixir_lang.psi.CallDefinitionClause.nameArityRange
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.QuotableKeywordList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.foldChildrenWhile
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.stripAccessExpression

/**
 * Watches the [module]'s `mix.exs` for changes to the `deps`, so that [com.intellij.openapi.roots.Libraries.Library]
 * created by [org.elixir_lang.DepsWatcher] can be added to the correct [Module].
 */
class Watcher(
        private val module: Module,
        private val project: Project,
        private val moduleRootManager: ModuleRootManager,
        private val psiManager: PsiManager,
        private val virtualFileManager: VirtualFileManager
) :
        ModuleComponent, Disposable, VirtualFileListener {
    override fun initComponent() {
        moduleRootManager
                .contentRoots
                .mapNotNull { contentRoot ->
                    contentRoot.findChild("mix.exs")
                }
                .forEach { mix ->
                   syncLibraries(mix)
                }

        virtualFileManager.addVirtualFileListener(this, this)
    }

    override fun contentsChanged(event: VirtualFileEvent) {
        if (event.fileName == "mix.exs" && event.file.parent == module.moduleFile?.parent) {
            syncLibraries(event.file)
        }
    }

    override fun dispose() = disposeComponent()

    private fun syncLibraries(mix: VirtualFile) {
        psiManager.findFile(mix)?.let { psiFile ->
            syncLibraries(psiFile)
        }
    }

    private fun syncLibraries(psiFile: PsiFile) {
        psiFile.accept(object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile?) {
                if (file is ElixirFile) {
                    file.acceptChildren(this)
                }
            }

            override fun visitElement(element: PsiElement?) {
               if (element is Call && org.elixir_lang.structure_view.element.modular.Module.`is`(element)) {
                   val childCalls = element.macroChildCalls()

                   val deps = childCalls.foldDepsDefinersWhile(listOf<Dep>()) { depsDefiner, acc ->
                       AccumulatorContinue(acc + depsDefiner.deps(project), true)
                   }.accumulator

                   ApplicationManager.getApplication().invokeLater {
                       ApplicationManager.getApplication().runWriteAction {
                           val libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project)
                           val missingDeps = mutableListOf<Dep>()

                           for (dep in deps) {
                               when (dep.type) {
                                   Dep.Type.MODULE -> {
                                       val depModule = ModuleManager.getInstance(project).findModuleByName(dep.application)

                                       if (depModule != null) {
                                           ModuleRootModificationUtil.addDependency(module, depModule)
                                       } else {
                                           missingDeps.add(dep)
                                       }
                                   }
                                   Dep.Type.LIBRARY -> {
                                       val depLibrary = libraryTable.getLibraryByName(dep.application)

                                       if (depLibrary != null) {
                                           ModuleRootModificationUtil.addDependency(module, depLibrary)
                                       } else {
                                           missingDeps.add(dep)
                                       }
                                   }
                               }
                           }

                           assert(missingDeps.size >= 0)
                       }
                   }
               }
            }
        })
    }
}

private fun Call.deps(project: Project): List<Dep> = lastList()?.deps(project) ?: emptyList()

private fun ElixirList.deps(project: Project): List<Dep> =
    children.map { it.stripAccessExpression() }.mapNotNull { Dep.from(it) }

private fun Call.lastList(): ElixirList? =
    foldChildrenWhile(null as ElixirList?) { child, acc ->
        if (child is ElixirList) {
            AccumulatorContinue(child, true)
        } else {
            AccumulatorContinue(acc, true)
        }
    }.accumulator

private fun <R> Array<Call>.foldDepsDefinersWhile(
        initial: R,
        operation: (Call, acc: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> {
    var final = AccumulatorContinue(initial, true)

    depsNameArity()?.let { depsNameArity ->
        for (childCall in this) {
            if (isDefining(childCall, depsNameArity)) {
                final = operation(childCall, final.accumulator)

                if (!final.`continue`) {
                    break
                }
            }
        }
    }

    return final
}

private fun isDefining(call: Call, nameArity: NameArity): Boolean =
        if (isFunction(call)) {
            nameArityRange(call)?.let { definedNameArityRange ->
                if (definedNameArityRange.name == nameArity.name && definedNameArityRange.arityRange.contains(nameArity.arity)) {
                    true
                } else {
                    null
                }
            }
        } else {
            null
        } ?: false

private fun Array<Call>.depsNameArity(): NameArity? {
    var nameArity: NameArity? = null

    for (call in this) {
        if (isDefiningProject(call)) {
            nameArity = call.lastKeywordList()?.depsNameArity()

            if (nameArity != null) {
                break
            }
        }
    }

    return nameArity
}

private fun QuotableKeywordList.depsNameArity(): NameArity? =
        keywordValue("deps")
                ?.let { it as? Call }
                ?.let { depsCall ->
                    depsCall
                            .functionName()
                            ?.let { name ->
                                NameArity(name, depsCall.resolvedFinalArity())
                            }
                }

private fun Call.lastKeywordList(): QuotableKeywordList? =
        foldChildrenWhile(null as QuotableKeywordList?) { projectChild, acc ->
                    if (projectChild is ElixirList) {
                        val lastKeywordList = projectChild.children.last { it is QuotableKeywordList } as QuotableKeywordList?
                        AccumulatorContinue(lastKeywordList, true)
                    } else {
                        AccumulatorContinue(acc, true)
                    }
                }
                .accumulator

private fun isDefiningProject(call: Call): Boolean =
        if (isPublicFunction(call)) {
            nameArityRange(call)?.let { nameArityRange ->
                if (nameArityRange.name == "project" && nameArityRange.arityRange.contains(0)) {
                    true
                } else {
                    null
                }
            }
        } else {
            null
        } ?: false
