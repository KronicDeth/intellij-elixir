package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.sdk.SdkEbinPaths
import org.elixir_lang.sdk.Type.ebinPathChainVirtualFile
import java.nio.file.Path
import java.nio.file.Paths

object ElixirErlangClasspath {
    fun addNewCodePathsFromInternErlangSdk(
        elixirSdk: Sdk,
        internalErlangSdk: Sdk,
        elixirSdkModificator: SdkModificator,
    ) {
        codePathsFromInternalErlangSdk(
            elixirSdk,
            internalErlangSdk,
            elixirSdkModificator
        ) { sdkModificator, configuredRoots, expandedInternalRoot, type ->
            if (expandedInternalRoot !in configuredRoots) {
                sdkModificator.addRoot(expandedInternalRoot, type)
            }
        }
    }

    fun removeCodePathsFromInternalErlangSdk(
        elixirSdk: Sdk,
        internalErlangSdk: Sdk,
        elixirSdkModificator: SdkModificator,
    ) {
        codePathsFromInternalErlangSdk(
            elixirSdk,
            internalErlangSdk,
            elixirSdkModificator
        ) { sdkModificator, _, expandedInternalRoot, type ->
            sdkModificator.removeRoot(expandedInternalRoot, type)
        }
    }

    private fun codePathsFromInternalErlangSdk(
        elixirSdk: Sdk,
        internalErlangSdk: Sdk,
        elixirSdkModificator: SdkModificator,
        sdkModificatorRootTypeConsumer: (SdkModificator, Array<VirtualFile>, VirtualFile, OrderRootType) -> Unit,
    ) {
        val internalSdkType = internalErlangSdk.sdkType as SdkType
        val elixirSdkType = elixirSdk.sdkType as SdkType
        for (type in OrderRootType.getAllTypes()) {
            if (internalSdkType.isRootTypeApplicable(type) && elixirSdkType.isRootTypeApplicable(type)) {
                val internalRoots = internalErlangSdk.sdkModificator.getRoots(type)
                val configuredRoots = elixirSdkModificator.getRoots(type)
                for (internalRoot in internalRoots) {
                    for (expandedInternalRoot in expandInternalErlangSdkRoot(internalRoot, type)) {
                        sdkModificatorRootTypeConsumer(
                            elixirSdkModificator,
                            configuredRoots,
                            expandedInternalRoot,
                            type
                        )
                    }
                }
            }
        }
    }

    private fun expandInternalErlangSdkRoot(
        internalRoot: VirtualFile,
        type: OrderRootType,
    ): Iterable<VirtualFile> {
        val expandedInternalRootList: List<VirtualFile>
        if (type === OrderRootType.CLASSES) {
            val path = internalRoot.path

            /* Erlang SDK from intellij-erlang uses lib/erlang/lib as class path, but intellij-elixir needs the ebin
               directories under lib/erlang/lib/APP-VERSION/ebin that works as a code path used by `-pa` argument to
               `erl.exe`

               For ASDF the path ends in erlang/VERSION/lib, in both cases, going to the parent directory will get the
               right ebin paths from `eachEbinPath` */
            if (path.endsWith("lib")) {
                expandedInternalRootList = ArrayList()
                val parentPath = Paths.get(path).parent.toString()
                SdkEbinPaths.eachEbinPath(parentPath) { ebinPath: Path ->
                    ebinPathChainVirtualFile(ebinPath) { virtualFile: VirtualFile? ->
                        virtualFile?.let { expandedInternalRootList.add(it) }
                    }
                }
            } else {
                expandedInternalRootList = listOf(internalRoot)
            }
        } else {
            expandedInternalRootList = listOf(internalRoot)
        }
        return expandedInternalRootList
    }
}
