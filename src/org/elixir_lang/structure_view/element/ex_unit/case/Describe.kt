package org.elixir_lang.structure_view.element.ex_unit.case

import com.intellij.navigation.ItemPresentation
import org.elixir_lang.psi.call.Call
import org.elixir_lang.structure_view.element.modular.Modular
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.navigation.item_presentation.ex_unit.case.Describe as DescribePresentation

/**
 * `ExUnit.Case.describe/2` evaluates its block in the module body, so what a `describe` declares -
 * a `defdelegate`, a `setup` - is compiled onto the case module. It shows what a `defmodule` shows.
 */
class Describe(parent: Modular, call: Call) : Module(parent, call) {
    override fun getPresentation(): ItemPresentation = DescribePresentation(location(), navigationItem)
}
