package org.elixir_lang.distillery.configuration.editor

enum class CodeLoadingMode {
    EMBEDDED {
        override fun toString(): String = super.toString().toLowerCase()
    },
    INTERACTIVE {
        override fun toString(): String = super.toString().toLowerCase()
    }
}
