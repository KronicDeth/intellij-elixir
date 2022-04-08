package org.elixir_lang.semantic.type.definition.source.callback.call

import org.elixir_lang.NameArityInterval
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.documentation.CallDefinition
import org.elixir_lang.semantic.type.definition.source.Callback

class Definition(val callback: Callback) : org.elixir_lang.semantic.call.Definition {
    override val time: Time
        get() = callback.time
    override val decompiled: List<Definition>
        get() = TODO("Not yet implemented")
    override val visibility: Visibility = Visibility.PUBLIC
    override val nameArityInterval: NameArityInterval by lazy {
        callback.nameArity.toNameArityInterval()
    }
    override val docs: List<CallDefinition>
        get() = TODO("Not yet implemented")
    override val enclosingModular: Modular
        get() = TODO()
    override val clauses: List<Clause> by lazy {
        listOf(org.elixir_lang.semantic.type.definition.source.callback.call.definition.Clause(this))
    }
    override val presentation: org.elixir_lang.navigation.item_presentation.NameArityInterval
        get() = TODO("Not yet implemented")
}
