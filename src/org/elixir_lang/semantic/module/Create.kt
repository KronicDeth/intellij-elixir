package org.elixir_lang.semantic.module

import com.intellij.navigation.ItemPresentation
import com.intellij.psi.ElementDescriptionLocation
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.documentation.Module

class Create(call: org.elixir_lang.psi.call.Call) :
    org.elixir_lang.semantic.modular.Call(null, call), org.elixir_lang.semantic.Module {
    override val moduleDocs: List<Module>
        get() = TODO("Not yet implemented")
    override val callDefinitions: Definitions
        get() = TODO("Not yet implemented")
    override val decompiled: Set<Modular>
        get() = TODO("Not yet implemented")
    override val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
        get() = TODO("Not yet implemented")
    override val locationString: String
        get() = TODO("Not yet implemented")
    override val name: String?
        get() = canonicalName
    override val canonicalNameSet: Set<String>
        get() = TODO("Not yet implemented")
    override val presentation: ItemPresentation?
        get() = TODO("Not yet implemented")

    override fun elementDescription(location: ElementDescriptionLocation): String? {
        TODO("Not yet implemented")
    }
}
