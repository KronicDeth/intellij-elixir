package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VfsUtil
import org.elixir_lang.sdk.SdkEbinPaths
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

object ElixirSdkPathConfigurator {
    private val LOG = Logger.getInstance(ElixirSdkPathConfigurator::class.java)

    fun configure(sdk: Sdk) {
        LOG.info("Configuring SDK paths for ${sdk.name}")
        val sdkModificator = sdk.sdkModificator

        // Configure base paths
        org.elixir_lang.sdk.Type.addCodePaths(sdkModificator)
        sdkModificator.removeRoots(OrderRootType.SOURCES)
        org.elixir_lang.sdk.Type.documentationRootType()?.let { sdkModificator.removeRoots(it) }
        // addDocumentationPaths removed: VirtualFileManager.findFileByUrl returns null for HTTP URLs,
        // the hostname was wrong (hexdoc.pm vs hexdocs.pm), and no ExternalDocumentationProvider
        // is registered to consume JavadocOrderRootType roots.
        addSourcePaths(sdkModificator)

        // Configure internal Erlang SDK - this will now create and fully setup the Erlang SDK synchronously
        val erlangSdk = Type.configureInternalErlangSdk(sdk, sdkModificator)

        // Commit changes - check if we're already in a write action to avoid deadlock
        val app = ApplicationManager.getApplication()
        if (app.isWriteAccessAllowed) {
            LOG.debug { "Committing SDK changes for ${sdk.name} (already in write action)" }
            sdkModificator.commitChanges()
        } else {
            val runnable = Runnable {
                app.runWriteAction {
                    LOG.debug { "Committing SDK changes for ${sdk.name}" }
                    sdkModificator.commitChanges()
                    LOG.debug { "Committed SDK changes for ${sdk.name}" }
                }
            }
            if (app.isDispatchThread) {
                runnable.run()
            } else {
                app.invokeAndWait(runnable)
            }
        }
        LOG.info("SDK paths configured for ${sdk.name} (Erlang SDK: ${erlangSdk?.name ?: "none"})")
    }

    private fun addSourcePaths(sdkModificator: SdkModificator) {
        val homePath = sdkModificator.homePath ?: return
        SdkEbinPaths.eachEbinPath(
            homePath,
        ) { ebinPath: Path -> addSourcePath(sdkModificator, ebinPath) }
    }

    private fun addSourcePath(
        sdkModificator: SdkModificator,
        libFile: File,
    ) {
        val sourcePath = VfsUtil.findFileByIoFile(libFile, true)
        if (sourcePath != null) {
            sdkModificator.addRoot(sourcePath, OrderRootType.SOURCES)
        }
    }

    private fun addSourcePath(
        sdkModificator: SdkModificator,
        ebinPath: Path,
    ) {
        val parentPath = ebinPath.parent
        val libPath = Paths.get(parentPath.toString(), "lib")
        val libFile = libPath.toFile()
        if (libFile.exists()) {
            addSourcePath(sdkModificator, libFile)
        }
    }
}
