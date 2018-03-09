package org.elixir_lang.psi;

import java.util.List;

/**
 * Created by luke.imhoff on 1/9/15.
 */
public interface WholeNumber extends Quotable {
    int base();
    List<Digits> digitsList();
}
