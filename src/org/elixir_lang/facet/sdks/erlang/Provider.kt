package org.elixir_lang.facet.sdks.erlang

import org.elixir_lang.facet.sdks.Provider

class Provider : Provider() {
    override fun createConfigurable(): Configurable = Configurable()
}
