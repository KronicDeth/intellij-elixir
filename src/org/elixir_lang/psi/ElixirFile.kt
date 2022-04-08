package org.elixir_lang.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.ElixirFileType
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Module
import org.elixir_lang.semantic.semantic

class ElixirFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ElixirLanguage) {
    override fun getFileType(): FileType = ElixirFileType.INSTANCE

    override fun toString(): String = "Elixir File"

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ): Boolean =
        ProcessDeclarationsImpl.processDeclarations(this, processor, state, lastParent!!, place)

    override fun getContext(): PsiElement? = viewFile()?.modulars()?.singleOrNull()

    /**
     * If this file is a LEEx template (`*.html.leex`), then this is the file that should contain the view module.
     */
    fun viewFile(): ElixirFile? =
        when (virtualFile?.fileType) {
            org.elixir_lang.eex.file.Type.INSTANCE -> {
                containingFile.parent?.let { directory ->
                    directory.parentDirectory?.let { directoryDirectory ->
                        if (directoryDirectory.name == "templates") {
                            val viewFile = directoryDirectory
                                .parentDirectory
                                ?.findSubdirectory("views")
                                ?.findFile("${directory.name}_view.ex") as? ElixirFile

                            viewFile
                        } else {
                            null
                        }
                    } ?: findUsesEExFile(directory.files)
                }
            }
            org.elixir_lang.leex.file.Type.INSTANCE -> {
                containingFile.parent?.let { directory ->
                    val nameWithoutExtension = containingFile.name.removeSuffix(".html.leex")
                    val liveName = "${nameWithoutExtension}.ex"

                    directory.findFile(liveName) as? ElixirFile
                }
            }
            else -> null
        }

    private fun findUsesEExFile(files: Array<PsiFile>): ElixirFile? =
        files.filterIsInstance<ElixirFile>().find { elixirFile ->
            usesEExFile(elixirFile)
        }

    private fun usesEExFile(file: ElixirFile): Boolean =
        file
            .childExpressions()
            .map(PsiElement::semantic)
            .filterIsInstance<Module>()
            .flatMap { it.callDefinitions.asSequence() }
            .filterIsInstance<org.elixir_lang.semantic.call.definition.eex.FunctionFromFile>()
            .any { it.usesEExFile }

    /**
     * @return modulars owned (declared) by this element.
     */
    fun modulars(): Array<StubBased<*>> =
        PsiTreeUtil
            .getChildrenOfType(this, StubBased::class.java)
            .orEmpty()
            .filter { it.semantic is Modular }
            .toTypedArray()
}
