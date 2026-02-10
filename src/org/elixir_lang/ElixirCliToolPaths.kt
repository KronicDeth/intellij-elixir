package org.elixir_lang

import java.io.File
import java.io.FileNotFoundException

internal object ElixirCliToolPaths {
    // mix is passed as an erl argument; keep the script path without a .bat wrapper.
    fun mixPath(homePath: String?): String {
        val sdkHome = homePath ?: throw FileNotFoundException("Elixir SDK home path is not set")
        return File(File(sdkHome, "bin"), "mix").path
    }
}
