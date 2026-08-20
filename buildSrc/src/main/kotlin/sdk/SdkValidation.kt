package sdk

import java.io.File

/**
 * Basic Erlang home validation based on expected bin layout.
 */
fun isValidErlangHome(home: File): Boolean {
    val erl = File(home, "bin${File.separator}${erlangExecutableName()}")
    return home.isDirectory && erl.isFile
}

/**
 * Basic Elixir home validation based on expected bin and lib layout.
 */
fun isValidElixirHome(home: File): Boolean {
    val elixir = File(home, "bin${File.separator}${elixirExecutableName()}")
    val ebin = File(home, "lib${File.separator}elixir${File.separator}ebin")
    val app = File(ebin, "elixir.app")
    return home.isDirectory && elixir.isFile && ebin.isDirectory && app.isFile
}
