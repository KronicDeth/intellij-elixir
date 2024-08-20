package org.elixir_lang.sdk

import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.JavadocOrderRootType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.jps.HomePath
import java.nio.file.Path
import java.util.function.Consumer

object Type {
    fun addCodePaths(sdkModificator: SdkModificator) {
        HomePath.eachEbinPath(
            sdkModificator.homePath
        ) { ebin: Path ->
            ebinPathChainVirtualFile(
                ebin
            ) { virtualFile: VirtualFile? ->
                sdkModificator.addRoot(
                    virtualFile!!, OrderRootType.CLASSES
                )
            }
        }
    }

    fun ebinPathChainVirtualFile(ebinPath: Path, virtualFileConsumer: Consumer<VirtualFile?>) {
        val virtualFile = LocalFileSystem
            .getInstance()
            .findFileByIoFile(ebinPath.toFile())

        if (virtualFile != null) {
            virtualFileConsumer.accept(virtualFile)
        }
    }

    fun documentationRootType(): OrderRootType? {
        val rootType = if (ProcessOutput.isSmallIde) {
            null
        } else {
            JavadocOrderRootType.getInstance()
        }

        return rootType
    }
}
