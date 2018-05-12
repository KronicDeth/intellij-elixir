package org.elixir_lang.psi.impl.qualifiable_alias

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.locationString
import javax.swing.Icon

class ItemPresentation(private val qualifiableAlias: QualifiableAlias) : ItemPresentation {
    override fun getLocationString(): String =
        qualifiableAlias.containingFile.locationString(qualifiableAlias.project)

    override fun getIcon(unused: Boolean): Icon? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPresentableText(): String? =
            qualifiableAlias.parent.let { parent ->
                when (parent) {
                    is ElixirAccessExpression -> getPresentableText(parent)
                    else -> qualifiableAlias.fullyQualifiedName()
                }
            }

    private tailrec fun getPresentableText(ancestor: PsiElement): String? =
            when (ancestor) {
                is Call ->
                        if (ancestor.isCalling(KERNEL, ALIAS)) {
                            "alias ${qualifiableAlias.fullyQualifiedName()}"
                        } else {
                            qualifiableAlias.fullyQualifiedName()
                        }
                is ElixirAccessExpression, is ElixirNoParenthesesOneArgument -> getPresentableText(ancestor.parent)
                else -> qualifiableAlias.fullyQualifiedName()
            }
}
