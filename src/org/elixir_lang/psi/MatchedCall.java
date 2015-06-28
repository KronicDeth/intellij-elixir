package org.elixir_lang.psi;

import org.jetbrains.annotations.Nullable;

/**
 * Matched calls don't have a doBlock
 */
public interface MatchedCall {
    @Nullable
    ElixirDoBlock getDoBlock();
}
