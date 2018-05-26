package org.elixir_lang.refactoring.variable.rename

import com.intellij.openapi.project.Project
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiElement
import com.intellij.refactoring.rename.RenameInputValidator
import com.intellij.refactoring.rename.RenameInputValidatorEx
import com.intellij.util.ProcessingContext
import org.elixir_lang.psi.call.Call
import org.elixir_lang.refactoring.variable.rename.Handler.Companion.isAvailableOnResolved
import org.elixir_lang.refactoring.variable.rename.Inplace.isIdentifier

class InputValidator : RenameInputValidatorEx {
    /**
     * Called only if all input validators ([RenameInputValidator]) accept
     * the new name in [.isInputValid]
     * and name is a valid identifier for a language of the element
     *
     * @param newName
     * @param project
     * @return null if newName is a valid name, custom error message otherwise
     */
    override fun getErrorMessage(newName: String, project: Project): String? =
            if (!isIdentifier(newName)) {
                "`" + newName + "` is not a valid variable name: variables (1) MUST start with a " +
                        "lowercase letter or underscore (`_`) and (2) then any combination of digits (`0` - `9`), " +
                        "lowercase letters (`a` - `z`), uppercase letters (`A` - `Z`) and underscore in the " +
                        "middle and (3) optional, end with `?` or `!`."
            } else {
                null
            }

    override fun getPattern(): ElementPattern<out PsiElement> {
        return object : ElementPattern<Call> {
            override fun accepts(o: Any?): Boolean = false

            override fun accepts(o: Any?, context: ProcessingContext): Boolean =
                    o is PsiElement && isAvailableOnResolved(o)

            override fun getCondition(): ElementPatternCondition<Call>? = null
        }
    }

    /**
     * Is invoked for elements accepted by pattern [.getPattern].
     * Should return true if [.getErrorMessage] is intended to return custom error message,
     * otherwise default message "newName is not a valid identifier" would be shown
     */
    override fun isInputValid(newName: String, element: PsiElement, context: ProcessingContext): Boolean =
            // allow defer to getErrorMessage
            true
}
