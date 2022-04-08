package org.elixir_lang.semantic.call.definition.eex

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.psi.ElixirAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.semantic.Modular

class FunctionFromFile(enclosingModular: Modular, call: Call) : FunctionFrom(enclosingModular, call) {
    val usesEExFile: Boolean by lazy {
        call.finalArguments()?.let { arguments ->
            fileArgumentUsesEExFile(arguments[2])
        } ?: false
    }
    override val name: String?
        get() = TODO("Not yet implemented")
    override val presentation: NameArityInterval
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }

    companion object {
        const val FUNCTION = "function_from_file"

        private fun fileArgumentUsesEExFile(argument: PsiElement): Boolean =
            when (argument) {
                is Qualified -> {
                    argument.qualifier().let { it as? ElixirAlias }?.name == "Path" &&
                            argument.functionName() == "expand" &&
                            argument.resolvedFinalArity() == 2 &&
                            pathExpandUsesEExFile(argument)
                }
                else -> {
                    false
                }
            }

        private fun pathExpandUsesEExFile(pathExpand: Call): Boolean =
            pathExpand.finalArguments()?.let { arguments ->
                arguments[0].text == "\"${pathExpand.containingFile?.virtualFile?.name}\"" &&
                        arguments[1].text == "__DIR__"
            } ?: false
    }
}
