package org.elixir_lang.iex.execute

import com.intellij.openapi.actionSystem.ActionPromoter
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.actions.EnterAction
import java.util.*

/**
 * Needed to make [org.elixir_lang.iex.Execute] win over other `ENTER` actions and for submitting text to work in the
 * IEx console.
 */
class Promoter : ActionPromoter {
    override fun promote(actions: List<AnAction>, context: DataContext): List<AnAction> = actions.sortedWith(
            Comparator { firstAction, secondAction ->
                val firstIsEnter = firstAction is EnterAction
                val secondIsEnter = secondAction is EnterAction

                when {
                    !firstIsEnter && secondIsEnter -> -1
                    firstIsEnter && secondIsEnter -> 0
                    !firstIsEnter && !secondIsEnter -> 0
                    firstIsEnter && !secondIsEnter -> 1
                    else -> TODO("Should not happen")
                }
            }
    )
}
