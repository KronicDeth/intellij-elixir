package org.elixir_lang.psi

import org.elixir_lang.psi.impl.ProcessDeclarationsImpl
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.*
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.ElixirFileType
import com.intellij.psi.scope.PsiScopeProcessor
import org.elixir_lang.psi.call.StubBased
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.stabBodyChildExpressions
import org.elixir_lang.psi.impl.childExpressions
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol
import java.util.ArrayList

class ElixirFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ElixirLanguage) {
    override fun getFileType(): FileType = ElixirFileType.INSTANCE

    override fun toString(): String = "Elixir File"

    override fun processDeclarations(processor: PsiScopeProcessor,
                                     state: ResolveState,
                                     lastParent: PsiElement?,
                                     place: PsiElement): Boolean =
            ProcessDeclarationsImpl.processDeclarations(this, processor, state, lastParent!!, place)

    override fun getContext(): PsiElement? = viewFile()?.modulars()?.singleOrNull()

    /**
     * If this file is a LEEx template (`*.html.leex`), then this is the file that should contain the view module.
     */
    fun viewFile(): ElixirFile? =
            when (virtualFile?.fileType) {
                org.elixir_lang.eex.file.Type.INSTANCE -> {
                    containingFile.parent?.let { directory ->
                        val files = directory.files

                        findUsesEExFile(files)
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
        file.childExpressions().filterIsInstance<Call>().any { call ->
            if (Module.`is`(call)) {
                moduleUsesEExFile(call)
            } else {
                false
            }
        }

    private fun moduleUsesEExFile(module: Call): Boolean =
            module.stabBodyChildExpressions()?.filterIsInstance<Qualified>()?.any { call ->
                // `function_from_file(kind, name, file, args \\ [], options \\ [])`
                if (call.qualifier().let { it as? ElixirAlias }?.name == "EEx" && call.functionName() == "function_from_file" &&
                        call.resolvedFinalArity() in 3..5) {
                    eexFunctionFromFileUsesEExFile(call)
                } else {
                    false
                }
            } ?: false

    private fun eexFunctionFromFileUsesEExFile(call: Call): Boolean =
        call.finalArguments()?.let { arguments ->
            eexFunctionFromFileArgumentUsesEExFile(arguments[2])
        } ?: false

    private fun eexFunctionFromFileArgumentUsesEExFile(argument: PsiElement): Boolean =
       when (argument) {
           is Qualified -> {
               argument.qualifier().let { it as? ElixirAlias }?.name == "Path" &&
                       argument.functionName() == "expand" &&
                       argument.resolvedFinalArity() == 2 &&
                       pathExpandUsesEExFile(argument)
           }
           else -> {
               false
           }
       }

    private fun pathExpandUsesEExFile(pathExpand: Call): Boolean =
        pathExpand.finalArguments()?.let { arguments ->
            arguments[0].text == "\"${virtualFile?.name}\"" && arguments[1].text == "__DIR__"
        } ?: false

    /**
     * @return modulars owned (declared) by this element.
     */
    fun modulars(): Array<StubBased<*>> {
        val stubBaseds = PsiTreeUtil.getChildrenOfType(
                this,
                StubBased::class.java
        )
        val modularList: MutableList<StubBased<*>> = ArrayList()
        if (stubBaseds != null) {
            for (stubBased in stubBaseds) {
                if (Implementation.`is`(stubBased) || Module.`is`(stubBased) || Protocol.`is`(stubBased)) {
                    modularList.add(stubBased)
                }
            }
        }
        return modularList.toTypedArray()
    }
}
