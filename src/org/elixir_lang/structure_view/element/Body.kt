package org.elixir_lang.structure_view.element

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.QuoteMacro
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module

/**
 * What a body shows: the modules it declares, not how it branches.
 */
object Body {
    /** @param quote a `quote` takes its parent as a [Presentable], and a `def` clause is not a [Modular]. */
    @RequiresReadLock
    fun treeElements(modular: Modular, quote: (Call) -> Quote, call: Call): Array<TreeElement> =
        call
            .macroChildCalls()
            .mapNotNull { childCall -> treeElement(modular, quote, childCall) }
            .toTypedArray()

    private fun treeElement(modular: Modular, quote: (Call) -> Quote, childCall: Call): TreeElement? =
        when {
            org.elixir_lang.psi.Implementation.`is`(childCall) -> Implementation(modular, childCall)
            org.elixir_lang.psi.Module.`is`(childCall) -> Module(modular, childCall)
            QuoteMacro.`is`(childCall) -> quote(childCall)
            else -> null
        }
}
