package org.elixir_lang.psi;

import java.util.List;

/**
 * Created by kadie.enheduanna.inanna on 1/9/15.
 */
public interface WholeNumber extends Quotable {
    int base();
    List<Digits> digitsList();
}
