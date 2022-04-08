package org.elixir_lang.navigation.item_presentation

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.NameArityInterval
import org.elixir_lang.name_arity.PresentationData
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import javax.swing.Icon

/**
 * The name/arity of a [Definition] or
 * [org.elixir_lang.structure_view.element.CallReference].
 */
class NameArityInterval
    (
    private val location: String?,
    private val callback: Boolean,
    private val time: Time,
    private val visibility: Visibility?,
    // is allowed to be overridden by an override function
    private val overridable: Boolean,
    // overrides an overridable function
    private val override: Boolean,
    private val nameArityInterval: NameArityInterval
) : ItemPresentation {
    /**
     * The name/arity interval of this function.
     *
     * @return `name`/`arity interval`
     */
    override fun getPresentableText(): String = nameArityInterval.toString()

    /**
     * Returns the module name where the function is defined
     *
     * @return the module name or `null` for `quote`.
     */
    override fun getLocationString(): String? {
        return location
    }

    /**
     * Returns the icon representing the object.
     *
     * @param unused Used to mean if open/close icons for tree renderer. No longer in use. The parameter is only there for API compatibility reason.
     */
    override fun getIcon(unused: Boolean): Icon {
        return PresentationData.icon(callback, overridable, override, time, visibility)
    }

    fun time(): Time {
        return time
    }
}
