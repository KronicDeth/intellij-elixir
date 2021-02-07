package org.elixir_lang.facet.sdks.erlang

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.SdkType
import org.elixir_lang.facet.sdks.Configurable
import org.elixir_lang.sdk.elixir.Type.Companion.erlangSdkType

class Configurable: Configurable() {
    override fun getDisplayName() = "Internal Erlang SDKs"
    override fun getId() = "language.elixir.sdks.erlang"
    override fun sdkType(): SdkType = erlangSdkType()
}
