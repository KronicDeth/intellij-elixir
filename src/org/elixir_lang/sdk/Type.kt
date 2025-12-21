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

    /**
     * Creates a home chooser descriptor for SDK configuration with validation.
     *
     * @param presentableName The presentable name of the SDK type (from SdkType.getPresentableName())
     * @param validateSdkHomePath Function to validate the selected path, should throw an exception if invalid
     * @return FileChooserDescriptor configured for SDK home selection
     */
    @JvmStatic
    fun createHomeChooserDescriptor(
        presentableName: String,
        validateSdkHomePath: (VirtualFile) -> Unit
    ): FileChooserDescriptor {
        val descriptor: FileChooserDescriptor =
            object : FileChooserDescriptor(false, true, false, false, false, false) {
                override fun validateSelectedFiles(files: Array<VirtualFile>) {
                    files.firstOrNull()?.let {
                        validateSdkHomePath(it)
                    }
                }
            }
        descriptor.title = ProjectBundle.message("sdk.configure.home.title", presentableName)
        return descriptor
    }
}
