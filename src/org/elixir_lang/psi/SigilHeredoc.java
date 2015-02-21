package org.elixir_lang.psi;

/**
 * Created by luke.imhoff on 2/16/15.
 */
public interface SigilHeredoc extends Heredoc, Sigil {
    public ElixirSigilModifiers getSigilModifiers();
}
