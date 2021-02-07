package org.elixir_lang.facet.sdks.elixir

import com.intellij.openapi.projectRoots.SdkType
import org.elixir_lang.facet.sdks.Configurable
import org.elixir_lang.sdk.elixir.Type

class Configurable: Configurable() {
    override fun getDisplayName() = "SDKs"
    override fun getId() = "language.elixir.sdks.elixir"
    override fun sdkType(): SdkType = Type.instance
}
