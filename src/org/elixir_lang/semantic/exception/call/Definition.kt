package org.elixir_lang.semantic.exception.call

import org.elixir_lang.NameArityInterval
import org.elixir_lang.semantic.Exception
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.Definition
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.documentation.CallDefinition
import org.elixir_lang.semantic.exception.call.definition.Clause

class Definition(
    val exception: Exception,
    override val nameArityInterval: NameArityInterval
) : Definition {
    override val enclosingModular: Modular
        get() = TODO()
    override val time: Time = Time.RUN
    override val decompiled: List<Definition>
        get() = TODO("Not yet implemented")
    override val visibility: Visibility = Visibility.PUBLIC
    override val docs: List<CallDefinition> = emptyList()
    override val clauses: List<org.elixir_lang.semantic.call.definition.Clause> by lazy {
        listOf(Clause(this))
    }
    override val presentation: org.elixir_lang.navigation.item_presentation.NameArityInterval
        get() = TODO("Not yet implemented")
}
