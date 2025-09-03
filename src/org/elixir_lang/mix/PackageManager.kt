package org.elixir_lang.mix

import org.elixir_lang.package_manager.DepGatherer

class PackageManager : org.elixir_lang.PackageManager {
    override val fileName: String = FILE_NAME
    override fun depGatherer(): DepGatherer = org.elixir_lang.mix.DepGatherer()

    companion object {
        const val FILE_NAME = "mix.exs"
    }
}
