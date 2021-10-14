package org.elixir_lang.module

import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression

object RegisterAttribute {
    fun `is`(element: PsiElement): Boolean =
            element is Call && `is`(element)

    @JvmStatic
    fun `is`(call: Call): Boolean =
            call.functionName()?.let { functionName ->
                functionName == "register_attribute" &&
                        call.resolvedFinalArity() == 3 &&
                        call.resolvedModuleName() == "Module"
            } ?: false

    fun name(call: Call): String? =
            when (val nameIdentifier = nameIdentifier(call)) {
                is ElixirAtom -> "@${nameIdentifier.name}"
                else -> null
            }

    fun nameIdentifier(call: Call): PsiElement? =
            call
                    .finalArguments()
                    ?.let { arguments ->
                        arguments[arguments.lastIndex - 1]
                    }
                    ?.stripAccessExpression()
}
