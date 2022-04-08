package org.elixir_lang.semantic.quote

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.Arity
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.Quote
import org.elixir_lang.semantic.Variable
import org.elixir_lang.semantic.call.Definitions
import org.elixir_lang.semantic.modular.Call
import org.elixir_lang.semantic.variable.QuoteBound

class Call(enclosingModular: Modular?, call: org.elixir_lang.psi.call.Call) : Call(enclosingModular, call), Quote {
    val quoteBoundVariables: List<QuoteBound>
        get() = TODO()
    val declaredVariable: Variable?
        get() = TODO()
    override val canonicalName: String?
        get() = null
    override val moduleDocs: List<org.elixir_lang.semantic.documentation.Module>
        get() = TODO("Not yet implemented")
    override val exportedCallDefinitions: Definitions
        get() = TODO("Not yet implemented")
    override val structureViewTreeElement: org.elixir_lang.structure_view.element.modular.Module
        get() = TODO("Not yet implemented")
    override val locationString: String
        get() = TODO("Not yet implemented")
    override val callDefinitions: Definitions
        get() = TODO()
    override val decompiled: Set<Modular>
        get() = TODO("Not yet implemented")
    override val presentation: ItemPresentation?
        get() = TODO("Not yet implemented")

    companion object {
        // TODO change Elixir.Kernel to Elixir.Kernel.SpecialForms when resolving works
        const val MODULE = Module.KERNEL
        const val FUNCTION = Function.QUOTE
        private val ARITY_RANGE = 1..2

        fun from(
            enclosingModular: Modular?,
            call: org.elixir_lang.psi.call.Call,
            arity: Arity
        ): org.elixir_lang.semantic.quote.Call? =
            if (arity in ARITY_RANGE) {
                org.elixir_lang.semantic.quote.Call(enclosingModular, call)
            } else {
                null
            }
    }
}
