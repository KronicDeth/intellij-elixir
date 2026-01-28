package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.sdk.HomePath
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.AccessDeniedException

object Erl {
    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.erlCommandLine]
     */
    fun commandLine(pty: Boolean, environment: Map<String, String>, workingDirectory: String?, erlangSdk: Sdk):
            GeneralCommandLine {
        val commandLine = commandLine(pty, environment, workingDirectory)
        setErl(commandLine, erlangSdk)

        return commandLine
    }

    fun prependCodePaths(generalCommandLine: GeneralCommandLine, ebinDirectories: kotlin.collections.List<String>) {
        ebinDirectories.forEach { generalCommandLine.addParameters("-pa", it) }
    }

    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.sdkPropertiesToErlExePath]
     */
    private fun exePath(erlangSdk: Sdk): String {
        val homePath = erlangSdk.homePath ?: throw FileNotFoundException("Erlang SDK home path is not set")
        val fileName = HomePath.getExecutableFileName(homePath, "erl", ".exe")
        val erlFile = File(File(homePath, "bin"), fileName)
        return exeFileToExePath(erlFile)
    }

    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.prependCodePaths]
     */
    private fun prependCodePaths(generalCommandLine: GeneralCommandLine, sdk: Sdk) {
        prependCodePaths(
            generalCommandLine,
            sdk.ebinDirectories()
        )
    }


    /**
     * Keep in-sync with [org.elixir_lang.jps.Builder.setErl]
     */
    private fun setErl(commandLine: GeneralCommandLine, erlangSdk: Sdk) {
        commandLine.exePath = exePath(erlangSdk)
        prependCodePaths(commandLine, erlangSdk)
    }
}

private fun exeFileToExePath(file: File): String {
    if (!file.exists()) {
        throw FileNotFoundException(file.absolutePath + " does not exist")
    }
    if (!file.canExecute()) {
        throw AccessDeniedException(file.absolutePath, null, " is not executable")
    }
    return file.absolutePath
}

fun Sdk.ebinDirectories(): kotlin.collections.List<String> {
    return try {
        rootProvider.getFiles(OrderRootType.CLASSES)
            .mapNotNull { virtualFile ->
                val canonicalPath = virtualFile.canonicalPath ?: return@mapNotNull null
                canonicalPath
            }
    } catch (_: AssertionError) {
        // rootProvider may be disposed, try reloading SDK from table once
        tryReloadSdkEbinDirectories()
    } catch (_: RuntimeException) {
        // rootProvider may be disposed, try reloading SDK from table once
        tryReloadSdkEbinDirectories()
    }
}

private fun Sdk.tryReloadSdkEbinDirectories(): kotlin.collections.List<String> {
    return try {
        ProjectJdkTable.getInstance().findJdk(name)?.rootProvider?.getFiles(OrderRootType.CLASSES)
            ?.mapNotNull { virtualFile ->
                val canonicalPath = virtualFile.canonicalPath ?: return@mapNotNull null
                canonicalPath
            } ?: emptyList()
    } catch (_: Exception) {
        // If reload also fails, return empty list to avoid infinite recursion
        emptyList()
    }
}
