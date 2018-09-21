package org.elixir_lang.rebar3

import org.elixir_lang.package_manager.DepGatherer

class PackageManager : org.elixir_lang.PackageManager {
    override val fileName: String = "rebar.config"
    override fun depGatherer(): DepGatherer = org.elixir_lang.rebar3.DepGatherer()
}
