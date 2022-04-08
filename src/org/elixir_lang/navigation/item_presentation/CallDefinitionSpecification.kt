package org.elixir_lang.navigation.item_presentation

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.ui.RowIcon
import org.elixir_lang.Icons
import org.elixir_lang.Icons.Time.from
import org.elixir_lang.semantic.call.definition.clause.Time
import javax.swing.Icon

class CallDefinitionSpecification(
    private val location: String?,
    private val specification: PsiElement?,
    private val callback: Boolean,
    private val time: Time
) : ItemPresentation {
    override fun getIcon(unused: Boolean): Icon? {
        var layers = 3
        if (callback) {
            layers++
        }
        val rowIcon = RowIcon(layers)
        var layer = 0
        if (callback) {
            rowIcon.setIcon(Icons.CALLBACK, layer++)
        }
        val timeIcon = from(time)
        rowIcon.setIcon(timeIcon, layer++)
        rowIcon.setIcon(Icons.Visibility.PUBLIC, layer++)
        rowIcon.setIcon(Icons.SPECIFICATION, layer)
        return rowIcon
    }

    override fun getLocationString(): String? = location
    override fun getPresentableText(): String? = specification?.text
}
