package org.elixir_lang

import com.intellij.openapi.diagnostic.Logger

inline fun Logger.debug(lazyMessage: () -> String) {
  if (isDebugEnabled) {
    debug(lazyMessage())
  }
}
