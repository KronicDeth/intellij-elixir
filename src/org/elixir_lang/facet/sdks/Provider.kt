package org.elixir_lang.facet.sdks

import com.intellij.openapi.options.ConfigurableProvider
import org.elixir_lang.sdk.ProcessOutput

class Provider : ConfigurableProvider() {
    override fun canCreateConfigurable(): Boolean = ProcessOutput.isSmallIde()
    override fun createConfigurable(): Configurable = Configurable()
}
