package org.elixir_lang.psi

import com.intellij.psi.PsiReference
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Prefix
import org.jetbrains.annotations.Contract

/**
 * `atPrefixOperator !numeric ...`
 */
interface AtNonNumericOperation : ModuleAttributeNameable, Prefix {
    @Contract(pure = true)
    override fun getReference(): PsiReference?
}

fun Call.isModuleAttributeNameElement(): Boolean =
        this is UnqualifiedNoArgumentsCall<*> && this.isModuleAttributeNameElement()

fun UnqualifiedNoArgumentsCall<*>.isModuleAttributeNameElement(): Boolean =
        parent is AtNonNumericOperation
