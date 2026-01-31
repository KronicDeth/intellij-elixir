package org.elixir_lang

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.jps.sdk_type.Erlang
import java.io.FileNotFoundException

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
        val exePath = Erlang.homePathToErlExePath(homePath)
        return exePath
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
