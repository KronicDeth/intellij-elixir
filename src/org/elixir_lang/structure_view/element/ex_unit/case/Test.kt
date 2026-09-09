package org.elixir_lang.structure_view.element.ex_unit.case

import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.Body
import org.elixir_lang.structure_view.element.Quote
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.navigation.item_presentation.ex_unit.case.Test as TestPresentation

/**
 * `ExUnit.Case.test/3` expands to a `def` whose body is the test's block, so a `test` shows what a
 * `def` clause shows. A [Modular] so the modules it declares can be built and qualified beneath it.
 */
class Test(parent: Modular, call: Call) : Module(parent, call) {
    override fun getPresentation(): ItemPresentation = TestPresentation(location(), navigationItem)

    override fun getChildren(): Array<TreeElement> =
        Body.treeElements(this, { Quote(this, it) }, navigationItem)
}
