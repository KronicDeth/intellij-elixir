package org.elixir_lang.psi

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiReference
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.ParameterizedCachedValue
import com.intellij.psi.util.ParameterizedCachedValueProvider
import org.elixir_lang.psi.__module__.Reference
import org.elixir_lang.psi.call.Call

object __MODULE__ {
    val KEY: Key<ParameterizedCachedValue<PsiReference, Call?>> = Key.create("__MODULE__REFERENCE")

    fun reference(__MODULE__Call: Call, useCall: Call? = null): PsiReference =
        CachedValuesManager
                .getManager(__MODULE__Call.project)
                .getParameterizedCachedValue(__MODULE__Call, KEY, Provider(__MODULE__Call), false, useCall)

    private class Provider(private val __MODULE__Call: Call) : ParameterizedCachedValueProvider<PsiReference, Call?> {
        override fun compute(useCall: Call?): CachedValueProvider.Result<PsiReference>? {
            val dependencies: Array<Call> = if (useCall != null) {
                arrayOf(__MODULE__Call, useCall)
            } else {
                arrayOf(__MODULE__Call)
            }

            return CachedValueProvider.Result.create(
                    Reference(call = __MODULE__Call, useCall = useCall),
                    dependencies
            )
        }
    }
}
