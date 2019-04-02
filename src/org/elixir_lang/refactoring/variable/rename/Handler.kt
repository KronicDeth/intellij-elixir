package org.elixir_lang.refactoring.variable.rename

import com.intellij.codeInsight.TargetElementUtil.adjustOffset
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.PsiReference
import com.intellij.refactoring.rename.RenameHandler
import com.intellij.refactoring.rename.RenameHandlerRegistry
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer
import org.elixir_lang.find_usages.toPsiElementList
import org.elixir_lang.psi.call.Call
import org.elixir_lang.reference.Callable


/**
 * A custom rename handler, so that [.createRenamer] can be return [Inplace], so
 * [Inplace.isIdentifier] won't use [com.intellij.lang.refactoring.NamesValidator], whose
 * [com.intellij.lang.refactoring.NamesValidator.isIdentifier] can't be customized to the
 * element.
 */
// Can't subclass `VariableInplaceRenameHandler` because it declares `isAvailableOnDataContext` as `final`
class Handler : RenameHandler {
    // called during rename action update. should not perform any user interactions
    override fun isAvailableOnDataContext(dataContext: DataContext): Boolean {
        val editor = CommonDataKeys.EDITOR.getData(dataContext)
        val file = CommonDataKeys.PSI_FILE.getData(dataContext)

        return reference(editor, file) != null
    }

    // called on rename actionPerformed. Can obtain additional info from user
    override fun isRenaming(dataContext: DataContext): Boolean = isAvailableOnDataContext(dataContext)

    /**
     * Invokes refactoring action from editor. The refactoring obtains
     * all data from editor selection.
     *
     * @param project     the project in which the refactoring is invoked.
     * @param editor      editor that refactoring is invoked in
     * @param file        file should correspond to <code>editor</code>
     * @param dataContext can be null for some but not all of refactoring action handlers
     *                    (it is recommended to pass DataManager.getDataContext() instead of null)
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext?) {
        val nonNullDataContext = dataContext ?: DataManager.getInstance().dataContext
        val elements = reference(editor, file)?.toPsiElementList()?.toTypedArray() ?: emptyArray()

        invoke(editor, elements, nonNullDataContext)
    }

    /**
     * Invokes refactoring action from elsewhere (not from editor). Some refactorings
     * do not implement this method.
     *
     * @param project     the project in which the refactoring is invoked.
     * @param elements    list of elements that refactoring should work on. Refactoring-dependent.
     * @param dataContext can be null for some but not all of refactoring action handlers
     *                    (it is recommended to pass DataManager.getDataContext() instead of null)
     */
    override fun invoke(project: Project, elements: Array<out PsiElement>, dataContext: DataContext?) {
        val nonNullDataContext = dataContext ?: DataManager.getInstance().dataContext

        invoke(nonNullDataContext.let(CommonDataKeys.EDITOR::getData), elements, nonNullDataContext)
    }

    private fun createRenamer(elementToRename: PsiElement, editor: Editor?): VariableInplaceRenamer? =
            Inplace(elementToRename as PsiNamedElement, editor)

    // See `com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler.doRename`
    private fun invoke(editor: Editor?, element: PsiElement, dataContext: DataContext) {
        val renamer = createRenamer(element, editor)
        val startedRename = renamer?.performInplaceRename() ?: false

        if (!startedRename) {
            performDialogRename(element, editor, dataContext)
        }
    }

    private fun invoke(editor: Editor?, elements: Array<out PsiElement>, dataContext: DataContext) {
        elements.forEach { element ->
            invoke(editor, element, dataContext)
        }
    }

    private fun isAvailableOnReference(psiReference: PsiReference) =
            psiReference
                    .toPsiElementList()
                    .any { isAvailableOnResolved(it) }

    private fun performDialogRename(element: PsiElement, editor: Editor?, dataContext: DataContext) {
        RenameHandlerRegistry
                .getInstance()
                .getRenameHandler(dataContext)!!
                .invoke(
                        element.project,
                        editor,
                        element.containingFile,
                        dataContext
                )
    }

    private fun reference(editor: Editor?, file: PsiFile?): PsiReference? =
            if (editor != null && file != null) {
                // matches logic in `UsageTargetProvider`, but with null checks for editor and file
                val document = editor.document
                val offset = editor.caretModel.offset

                val adjustOffset = adjustOffset(file, document, offset)

                file.findReferenceAt(adjustOffset)?.let { reference ->
                    if (isAvailableOnReference(reference)) {
                        reference
                    } else {
                        null
                    }
                }
            } else {
                null
            }

    companion object {
        internal fun isAvailableOnResolved(element: PsiElement): Boolean = element is Call && (
                Callable.isVariable(element) || Callable.isParameter(element)
                )
    }
}
