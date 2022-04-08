package org.elixir_lang.navigation.item_presentation

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import com.intellij.ui.RowIcon
import org.elixir_lang.Icons
import org.elixir_lang.Icons.Visibility.from
import org.elixir_lang.psi.ElixirMatchedWhenOperation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.semantic.type.Visibility
import javax.swing.Icon

class Type(private val location: String?, private val semantic: org.elixir_lang.semantic.type.Definition) :
    ItemPresentation {
    /*
     *
     * Instance Methods
     *
     */
    /*
     * Public Instance Methods
     */
    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    override fun getIcon(unused: Boolean): Icon {
        val icons = arrayOf(
            Icons.Time.COMPILE,
            from(semantic),
            Icons.TYPE
        )
        val rowIcon = RowIcon(icons.size)
        for (layer in icons.indices) {
            rowIcon.setIcon(icons[layer], layer)
        }
        return rowIcon
    }

    /**
     * Returns the location of the object (for example, the package of a class). The location
     * string is used by some renderers and usually displayed as grayed text next to the item name.
     *
     * @return the location description, or null if none is applicable.
     */
    override fun getLocationString(): String? {
        return location
    }

    /**
     * The type declaration.  The body (after `::` is not shown if the type is [.opaque].
     *
     * @return the type declaration.
     */
    override fun getPresentableText(): String {
        val head = semantic.head.toString()
        val guardless = if (visibility != Visibility.OPAQUE) {
            "$head :: ${semantic.body}"
        } else {
            head
        }

        val guard = semantic.guard

        return if (guard != null) {
            "$guardless when ${semantic.guard}"
        } else {
            guardless
        }
    }

    private val visibility: Visibility by lazy {
        when (semantic) {
            is org.elixir_lang.semantic.type.definition.source.ModuleAttribute -> semantic.visibility
            else -> Visibility.PUBLIC
        }
    }

    companion object {
        /*
     * Static Methods
     */
        fun head(type: Call): PsiElement? {
            var head: PsiElement? = null
            if (type is Type) {
                head = head(type as Type)
            } else if (type is ElixirMatchedWhenOperation) {
                head = head(type)
            }
            return head
        }

        private fun head(typeOperation: Type): PsiElement {
            return typeOperation.leftOperand()!!
        }

        private fun head(whenOperation: ElixirMatchedWhenOperation): PsiElement? {
            var head: PsiElement? = null
            val parameterizedType: PsiElement? = whenOperation.leftOperand()
            if (parameterizedType is Type) {
                head = head(parameterizedType)
            }
            return head
        }
    }
}
