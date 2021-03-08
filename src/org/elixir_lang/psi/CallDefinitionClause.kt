package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArityRange
import org.elixir_lang.find_usages.Provider
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.structure_view.element.CallDefinitionHead

object CallDefinitionClause {
    /**
     * Description of element used in [Provider].
     *
     * @param call a [Call] that has already been checked with [.is]
     * @param location where the description will be used
     * @return
     */
    fun elementDescription(call: Call, location: ElementDescriptionLocation): String? =
            when {
                isFunction(call) -> functionElementDescription(call, location)
                isMacro(call) -> macroElementDescription(location)
                else -> null
            }

    /**
     * The head of the call definition.
     *
     * @param call a call that [.is].
     * @return element for `name(arg, ...) when ...` in `def* name(arg, ...) when ...`
     */
    @JvmStatic
    fun head(call: Call): PsiElement? = call.primaryArguments()?.firstOrNull()

    @JvmStatic
    fun `is`(call: Call): Boolean = isFunction(call) || isMacro(call)

    @JvmStatic
    fun isFunction(call: Call): Boolean = isPrivateFunction(call) || isPublicFunction(call)
    @JvmStatic
    fun isPublicFunction(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEF)
    fun isPrivateFunction(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFP)

    @JvmStatic
    fun isMacro(call: Call): Boolean = isPrivateMacro(call) || isPublicMacro(call)
    @JvmStatic
    fun isPublicMacro(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFMACRO)
    fun isPrivateMacro(call: Call): Boolean = isCallingKernelMacroOrHead(call, DEFMACROP)

    fun isPublic(call: Call): Boolean = isPublicFunction(call) || isPublicMacro(call)

    /**
     * The name and arity range of the call definition this clause belongs to.
     *
     * @param call
     * @return The name and arities of the [CallDefinition] this clause belongs.  Multiple arities occur when
     * default arguments are used, which produces an arity for each default argument that is turned on and off.
     * @see Call.resolvedFinalArityRange
     */
    @JvmStatic
    fun nameArityRange(call: Call): NameArityRange? = head(call)?.let { CallDefinitionHead.nameArityRange(it) }

    fun nameIdentifier(call: Call): PsiElement? = head(call)?.let { CallDefinitionHead.nameIdentifier(it) }

    private fun functionElementDescription(
            @Suppress("UNUSED_PARAMETER") call: Call,
            location: ElementDescriptionLocation
    ): String? =
            if (location === UsageViewTypeLocation.INSTANCE) {
                "function"
            } else {
                null
            }

    private fun macroElementDescription(location: ElementDescriptionLocation): String? =
            if (location === UsageViewTypeLocation.INSTANCE) {
                "macro"
            } else {
                null
            }

    private fun isCallingKernelMacroOrHead(call: Call, resolvedName: String): Boolean =
            call.isCallingMacro(KERNEL, resolvedName, 2) ||
                    call.isCalling(KERNEL, resolvedName, 1)
}
