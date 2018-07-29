package org.elixir_lang.refactoring.module_attribute.rename

import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiNamedElement
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer
import org.elixir_lang.ElixirLanguage

import java.util.regex.Pattern

internal class Inplace (elementToRename: PsiNamedElement, editor: Editor) :
        VariableInplaceRenamer(elementToRename, editor) {
    override fun isIdentifier(newName: String, language: Language): Boolean =
            language === ElixirLanguage && isIdentifier(newName)

    companion object {
        private val IDENTIFIER_PATTERN = Pattern.compile("@[a-z_][0-9a-zA-Z_]*[?!]?")

        fun isIdentifier(newName: String): Boolean = IDENTIFIER_PATTERN.matcher(newName).matches()
    }
}
