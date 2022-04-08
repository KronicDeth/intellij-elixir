package org.elixir_lang.semantic.call

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.NameArityInterval
import org.elixir_lang.Presentable
import org.elixir_lang.Timed
import org.elixir_lang.psi.ArityInterval
import org.elixir_lang.semantic.Modular
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.documentation.CallDefinition
import org.elixir_lang.semantic.modular.Enclosed

class Definition(
    override val enclosingModular: Modular,
    override val time: Time,
    val visibility: Visibility,
    val nameArityInterval: NameArityInterval
) : Enclosed, Timed,
    Presentable {
    val name: String
        get() = nameArityInterval.name

    fun expandArityInterval(arityInterval: ArityInterval): Definition {
        TODO()

        return this
    }

    val docs: List<CallDefinition>
        get() = TODO()
    val clauses: List<Clause>
        get() = TODO()

    override val presentation: org.elixir_lang.navigation.item_presentation.NameArityInterval
        get() = TODO()

    fun elementDescription(location: ElementDescriptionLocation): String? =
        when (location) {
            UsageViewTypeLocation.INSTANCE -> usageViewType(time)
            else -> null
        }

    companion object {
        fun usageViewType(time: Time): String =
            when (time) {
                Time.COMPILE -> "macro"
                Time.GUARD -> "guard"
                Time.RUN -> "function"
            }
    }
}
