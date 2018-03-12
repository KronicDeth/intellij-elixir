package org.elixir_lang.psi.impl.declarations

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAnonymousFunction
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.CallImpl.hasDoBlockOrKeyword
import org.elixir_lang.psi.stub.type.call.Stub.isModular
import org.elixir_lang.structure_view.element.CallDefinitionClause

object UseScopeImpl {
    enum class UseScopeSelector {
        PARENT,
        SELF,
        SELF_AND_FOLLOWING_SIBLINGS
    }

    @JvmStatic
    fun selector(element: PsiElement): UseScopeSelector {
        var useScopeSelector = UseScopeSelector.PARENT

        if (element is AtUnqualifiedNoParenthesesCall<*>) {
            /* Module Attribute declarations can't declare variables, so this is a variable usage without declaration,
               so limit to SELF */
            useScopeSelector = UseScopeSelector.SELF
        } else if (element is ElixirAnonymousFunction) {
            useScopeSelector = UseScopeSelector.SELF
        } else if (element is Call) {
            if (element.isCalling(KERNEL, CASE) ||
                    element.isCalling(KERNEL, COND) ||
                    element.isCalling(KERNEL, IF) ||
                    element.isCalling(KERNEL, RECEIVE) ||
                    element.isCalling(KERNEL, UNLESS) ||
                    element.isCalling(KERNEL, VAR_BANG)) {
                useScopeSelector = UseScopeSelector.SELF_AND_FOLLOWING_SIBLINGS
            } else if (CallDefinitionClause.`is`(element) || isModular(element) || hasDoBlockOrKeyword(element)) {
                useScopeSelector = UseScopeSelector.SELF
            }
        }

        return useScopeSelector
    }
}
