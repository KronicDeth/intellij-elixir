package org.elixir_lang

class ElixirScriptFileType : ElixirFileType() {
    override fun getName(): String = "Elixir Script"
    override fun getDisplayName(): String = name
    override fun getDescription(): String = "Elixir Script file"
    override fun getDefaultExtension(): String = "exs"

    companion object {
        @JvmField
        val INSTANCE = ElixirScriptFileType()
    }
}
