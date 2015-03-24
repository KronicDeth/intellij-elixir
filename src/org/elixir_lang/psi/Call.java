package org.elixir_lang.psi;

/*
 * A function call with a (potentially qualified) identifier and arguments.
 */
public interface Call extends Quotable {
    public QuotableArguments getArguments();
    public Quotable getIdentifier();
}
