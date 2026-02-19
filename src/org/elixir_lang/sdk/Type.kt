package org.elixir_lang.sdk

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.JavadocOrderRootType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.sdk.wsl.wslCompat
import java.nio.file.Path
import java.util.function.Consumer

object Type {
    private val LOG = Logger.getInstance(Type::class.java)

    @JvmStatic
    fun addCodePaths(sdkModificator: SdkModificator) {
        val homePath = sdkModificator.homePath

        if (homePath == null) {
            LOG.warn("homePath is null, cannot add code paths")
            return
        }

        // Clear existing CLASSES roots to prevent duplicates when refreshing SDK
        sdkModificator.removeRoots(OrderRootType.CLASSES)

        SdkEbinPaths.eachEbinPath(
            homePath
        ) { ebin: Path ->
            ebinPathChainVirtualFile(ebin) { virtualFile: VirtualFile? ->
                if (virtualFile != null) {
                    sdkModificator.addRoot(virtualFile, OrderRootType.CLASSES)
                }
            }
        }
    }

    @JvmStatic
    fun ebinPathChainVirtualFile(ebinPath: Path, virtualFileConsumer: Consumer<VirtualFile?>) {
        // LocalFileSystem.refreshAndFindFileByNioFile handles both regular paths and WSL UNC paths correctly
        // For WSL paths, ebinPath is already a Windows UNC path thanks to maybeTranslateToUnc in SdkEbinPaths.eachEbinPath
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(ebinPath)

        if (virtualFile != null) {
            virtualFileConsumer.accept(virtualFile)
        }
    }

    @JvmStatic
    fun documentationRootType(): OrderRootType? {
        val rootType = if (ProcessOutput.isSmallIde) {
            null
        } else {
            JavadocOrderRootType.getInstance()
        }

        return rootType
    }

    /**
     * Appends WSL distribution suffix to a string if the SDK home path is in WSL.
     * Used for displaying WSL information in SDK names, status widgets, etc.
     *
     * @param text The base text (e.g., SDK name or version string)
     * @param sdkHomePath The SDK home path to check for WSL
     * @return The text with " (WSL: DistributionName)" appended if WSL, otherwise unchanged
     */
    @JvmStatic
    fun appendWslSuffix(text: String, sdkHomePath: String?): String {
        if (sdkHomePath == null) return text

        return if (wslCompat.isWslUncPath(sdkHomePath)) {
            val distribution = wslCompat.getDistributionByWindowsUncPath(sdkHomePath)
            val distroName = distribution?.msId ?: "WSL"
            "$text (WSL: $distroName)"
        } else {
            text
        }
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
