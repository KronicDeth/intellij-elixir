package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile

interface SdkModificatorRootTypeConsumer {
    fun consume(
        sdkModificator: SdkModificator,
        configuredRoots: Array<VirtualFile?>?,
        expandedInternalRoot: VirtualFile,
        type: OrderRootType
    )
}
