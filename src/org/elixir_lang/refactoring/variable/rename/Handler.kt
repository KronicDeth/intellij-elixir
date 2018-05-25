package org.elixir_lang.refactoring.variable.rename

import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable


/**
 * A custom rename handler, so that [.createRenamer] can be return [Inplace], so
 * [Inplace.isIdentifier] won't use [com.intellij.lang.refactoring.NamesValidator], whose
 * [com.intellij.lang.refactoring.NamesValidator.isIdentifier] can't be customized to the
 * element.
 */
class Handler : VariableInplaceRenameHandler() {
    override fun createRenamer(elementToRename: PsiElement, editor: Editor): VariableInplaceRenamer? =
            Inplace(elementToRename as PsiNamedElement, editor)

    override fun isAvailable(element: PsiElement, editor: Editor, file: PsiFile): Boolean =
            editor.settings.isVariableInplaceRenameEnabled && isAvailable(element)

    companion object {

        internal fun isAvailable(element: PsiElement): Boolean = element is Call && Callable.isVariable(element)
    }
}
