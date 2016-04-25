package org.elixir_lang.refactoring.module_attribute.rename;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A custom rename handler, so that {@link #createRenamer(PsiElement, Editor)} can be return {@link Inplace}, so
 * {@link Inplace#isIdentifier(String, Language)} won't use {@link com.intellij.lang.refactoring.NamesValidator}, whose
 * {@link com.intellij.lang.refactoring.NamesValidator#isIdentifier(String, Project)} can't be customized to the
 * element.
 */
public class Handler extends VariableInplaceRenameHandler {
    /*
     * Static Methods
     */

    static boolean isAvailable(@NotNull PsiElement element) {
        return element instanceof AtUnqualifiedNoParenthesesCall;
    }

    /*
     * Instance Methods
     */

    @Nullable
    @Override
    protected VariableInplaceRenamer createRenamer(@NotNull PsiElement elementToRename, Editor editor) {
        return new Inplace((PsiNamedElement)elementToRename, editor);
    }

    @Override
    protected boolean isAvailable(PsiElement element, Editor editor, PsiFile file) {
       return editor.getSettings().isVariableInplaceRenameEnabled() && isAvailable(element);
    }
}
