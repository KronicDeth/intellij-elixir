package org.elixir_lang.distillery.configuration.editor

import java.util.Locale
import java.util.Locale.getDefault

enum class CodeLoadingMode {
    EMBEDDED {
        override fun toString(): String = super.toString().lowercase(getDefault())
    },
    INTERACTIVE {
        override fun toString(): String = super.toString().lowercase(getDefault())
    }
}
