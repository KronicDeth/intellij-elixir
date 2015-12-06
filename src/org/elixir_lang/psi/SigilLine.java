package org.elixir_lang.psi;

/**
 * Created by luke.imhoff on 2/27/15.
 */
public interface SigilLine extends Bodied, Line, Sigil {
    char terminator();
}
