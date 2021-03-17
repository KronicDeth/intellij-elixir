package org.elixir_lang.psi

import org.elixir_lang.psi.impl.ProcessDeclarationsImpl
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.ElixirFileType
import com.intellij.psi.ResolveState
import com.intellij.psi.PsiElement
import com.intellij.psi.scope.PsiScopeProcessor
import org.elixir_lang.psi.call.StubBased
import com.intellij.psi.util.PsiTreeUtil
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
    fun viewFile(): ElixirFile? = if (virtualFile?.fileType == org.elixir_lang.leex.file.Type.INSTANCE) {
        containingFile.parent?.let { directory ->
            val nameWithoutExtension = containingFile.name.removeSuffix(".html.leex")
            val liveName = "${nameWithoutExtension}.ex"

            directory.findFile(liveName) as? ElixirFile
        }
    } else {
        null
    }

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
