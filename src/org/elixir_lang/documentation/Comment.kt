package org.elixir_lang.documentation

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocCommentBase
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement
import com.intellij.psi.tree.IElementType
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirTypes

class Comment(val moduleAttribute: AtUnqualifiedNoParenthesesCall<*>) : FakePsiElement(), PsiDocCommentBase {
    override fun getParent(): PsiElement = moduleAttribute

    override fun getTokenType(): IElementType = ElixirTypes.COMMENT

    override fun getOwner(): PsiElement? {
        Logger.error(javaClass, "Don't know how to calculate owner", moduleAttribute)

        return null
    }

    override fun getTextRange(): TextRange = moduleAttribute.textRange
}
