package org.elixir_lang.semantic.alias

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.Presentable
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.PresentationImpl
import org.elixir_lang.semantic.Alias
import org.elixir_lang.semantic.Modular

/**
 * `__MODULE__`
 */
class Module(val enclosingModular: Modular?, override val psiElement: Call) : Alias, Presentable {
    override val name: String = "__MODULE__"
    override val modulars: Set<Modular>
        get() = TODO("Not yet implemented")
    override val suffix: Alias = this
    override val nested: Set<Alias>
        get() = TODO("Not yet implemented")
    override val presentation: ItemPresentation
        get() = PresentationImpl.getPresentation(psiElement)

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
