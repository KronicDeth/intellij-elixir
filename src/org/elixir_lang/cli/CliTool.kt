package org.elixir_lang.cli

import com.intellij.execution.wsl.WslPath
import com.intellij.util.system.OS
import org.elixir_lang.jps.shared.cli.CliTool

fun CliTool.getExecutableFilepathWslSafe(sdkHomePath: String): String {
    if (WslPath.isWslUncPath(sdkHomePath)) {
        return getExecutableFilepath(sdkHomePath, OS.Linux)
    }
    return getExecutableFilepath(sdkHomePath, OS.CURRENT)
}
