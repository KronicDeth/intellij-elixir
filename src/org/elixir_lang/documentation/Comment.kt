package org.elixir_lang.documentation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.tree.IElementType
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.enclosingMacroCall
import org.elixir_lang.psi.impl.identifierName
import org.elixir_lang.psi.impl.siblingExpressions
import org.elixir_lang.psi.stub.type.call.Stub.isModular

class Comment(val moduleAttribute: AtUnqualifiedNoParenthesesCall<*>) : FakePsiElement(), PsiDocCommentBase {
    override fun getParent(): PsiElement = moduleAttribute

    override fun getTokenType(): IElementType = ElixirTypes.COMMENT

    override fun getOwner(): PsiElement? =
        when (val identifierName = moduleAttribute.atIdentifier.identifierName()) {
            "moduledoc" -> moduleAttribute.enclosingMacroCall().takeIf(::isModular)
            "doc" -> {
                moduleAttribute.siblingExpressions(forward = true, withSelf = false).firstOrNull { expression ->
                    expression is Call && CallDefinitionClause.`is`(expression)
                }
            }
            else -> {
                Logger.error(javaClass, "Don't know how to calculate owner", moduleAttribute)

                null
            }
        }

    override fun getTextRange(): TextRange = moduleAttribute.textRange
}
