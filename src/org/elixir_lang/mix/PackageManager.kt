package org.elixir_lang.mix

import org.elixir_lang.package_manager.DepGatherer

object PackageManager : org.elixir_lang.PackageManager {
    override val fileName: String = "mix.exs"
    override fun depGatherer(): DepGatherer = org.elixir_lang.mix.DepGatherer()
}
