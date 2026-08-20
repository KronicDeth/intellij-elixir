package org.elixir_lang.psi;

/**
 * A quote (CharList or String) as opposed to a sigil
 *
 * Created by kadie.enheduanna.inanna on 2/17/15.
 */
public interface Quote extends Parent {
    boolean isCharList();
}
