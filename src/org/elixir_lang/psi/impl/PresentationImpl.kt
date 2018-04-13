package org.elixir_lang.psi.impl

import com.intellij.navigation.ItemPresentation
import com.intellij.usageView.UsageViewUtil
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.CallDefinitionClause
import javax.swing.Icon

object PresentationImpl {
    @JvmStatic
    fun getPresentation(call: Call): ItemPresentation =
            if (CallDefinitionClause.`is`(call)) {
                CallDefinitionClause.fromCall(call)?.presentation
            } else {
                null
            } ?: getDefaultPresentation(call)

    @JvmStatic
    fun getPresentation(identifier: ElixirIdentifier): ItemPresentation? {
        val parameterizedParameter = Parameter(identifier).let { Parameter.putParameterized(it) }

        return if ((parameterizedParameter.type == Parameter.Type.FUNCTION_NAME ||
                        parameterizedParameter.type == Parameter.Type.MACRO_NAME) &&
                parameterizedParameter.parameterized != null) {
            (parameterizedParameter.parameterized as? Call)?.let {
                CallDefinitionClause.fromCall(it)?.presentation
            }
        } else {
            null
        }
    }

    private fun getDefaultPresentation(call: Call): ItemPresentation {
        val text = UsageViewUtil.createNodeText(call)

        return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return text
            }

            override fun getLocationString(): String? {
                return call.containingFile.name
            }

            override fun getIcon(b: Boolean): Icon? {
                return call.getIcon(0)
            }
        }
    }
}
