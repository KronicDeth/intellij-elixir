package org.elixir_lang.semantic

import com.intellij.psi.ResolveState

interface ScopeProcessor {
    fun execute(semantic: Semantic, state: ResolveState): Boolean
}
