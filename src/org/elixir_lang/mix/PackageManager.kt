package org.elixir_lang.mix

import org.elixir_lang.package_manager.DepGatherer

class PackageManager : org.elixir_lang.PackageManager {
    override val fileName: String = Project.MIX_EXS
    override fun depGatherer(): DepGatherer = org.elixir_lang.mix.DepGatherer()
}
