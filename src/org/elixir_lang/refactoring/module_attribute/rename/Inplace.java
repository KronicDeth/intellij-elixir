package org.elixir_lang.refactoring.module_attribute.rename;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.rename.inplace.VariableInplaceRenamer;
import org.elixir_lang.ElixirLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

class Inplace extends VariableInplaceRenamer {
    /*
     * CONSTANTS
     */

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("@[a-z_][0-9a-zA-Z_]*[?!]?");

    /*
     * Static Methods
     */

    static boolean isIdentifier(String newName) {
        return IDENTIFIER_PATTERN.matcher(newName).matches();
    }

    /*
     * Constructors
     */

    Inplace(@NotNull PsiNamedElement elementToRename, Editor editor) {
        super(elementToRename, editor);
    }

    /*
     * Instance Methods
     */

    @Override
    protected boolean isIdentifier(String newName, Language language) {
        return language == ElixirLanguage.INSTANCE && isIdentifier(newName);
    }
}
