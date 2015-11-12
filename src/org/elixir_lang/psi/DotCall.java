package org.elixir_lang.psi;

import org.elixir_lang.psi.call.Call;

import java.util.List;

/**
 * <expression> dotInfixOperator parenthesesArguments parenthesesArguments? doBlock?
 */
public interface DotCall extends Call, Quotable {
    Quotable getDotInfixOperator();

    List<ElixirParenthesesArguments> getParenthesesArgumentsList();
}
