package org.elixir_lang

import com.intellij.codeInsight.TargetElementEvaluatorEx2
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.AtNonNumericOperation
import org.elixir_lang.psi.UnqualifiedNoArgumentsCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.psi.scope.hasMapFieldOptionalityName

class TargetElementEvaluator : TargetElementEvaluatorEx2() {
    override fun isAcceptableNamedParent(parent: PsiElement): Boolean = when (parent) {
        // Don't allow the identifier of a module attribute or assign usage be a named parent.
        is UnqualifiedNoArgumentsCall<*> -> when (parent.parent) {
            is AtNonNumericOperation -> false
            else -> super.isAcceptableNamedParent(parent)
        }
        is Call -> {
            if (parent.hasMapFieldOptionalityName() && parent.ancestorTypeSpec() != null) {
                false
            } else {
                super.isAcceptableNamedParent(parent)
            }
        }
        else -> super.isAcceptableNamedParent(parent)
    }
}
