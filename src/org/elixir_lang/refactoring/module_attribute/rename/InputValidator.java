package org.elixir_lang.refactoring.module_attribute.rename;

import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.ElementPatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.refactoring.rename.RenameInputValidatorEx;
import com.intellij.util.ProcessingContext;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Nullable;

import static org.elixir_lang.refactoring.module_attribute.rename.Handler.isAvailable;
import static org.elixir_lang.refactoring.module_attribute.rename.Inplace.isIdentifier;

public class InputValidator implements RenameInputValidatorEx {
    /*
     * Instance Methods
     */

    /**
     * Called only if all input validators ({@link RenameInputValidator}) accept
     * the new name in {@link #isInputValid(String, PsiElement, ProcessingContext)}
     * and name is a valid identifier for a language of the element
     *
     * @param newName
     * @param project
     * @return null if newName is a valid name, custom error message otherwise
     */
    @Nullable
    @Override
    public String getErrorMessage(String newName, Project project) {
        String errorMessage = null;

        if (!isIdentifier(newName)) {
            errorMessage = "`" + newName + "` is not a valid module attribute name: module attributes (1) MUST " +
                    "start with an @ symbol which (2) MUST be followed by lowercase letter or underscore (`_`) and " +
                    "(3) then any combination of digits (`0` - `9`), lowercase letters (`a` - `z`), " +
                    "uppercase letters (`A` - `Z`) and underscore in the middle and (4) optional, end with `?` or `!`.";
        }

        return errorMessage;
    }

    @Override
    public ElementPattern<? extends PsiElement> getPattern() {
        return new ElementPattern<Call>() {
            @Override
            public boolean accepts(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean accepts(@Nullable Object o, ProcessingContext context) {
                return o instanceof PsiElement && isAvailable((PsiElement) o);
            }

            @Nullable
            @Override
            public ElementPatternCondition<Call> getCondition() {
                return null;
            }
        };
    }

    /**
     * Is invoked for elements accepted by pattern {@link #getPattern()}.
     * Should return true if {@link #getErrorMessage(String, Project)} is intended to return custom error message,
     * otherwise default message "newName is not a valid identifier" would be shown
     */
    @Override
    public boolean isInputValid(String newName, PsiElement element, ProcessingContext context) {
        // allow defer to getErrorMessage
        return true;
    }
}
