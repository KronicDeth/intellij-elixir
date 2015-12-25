package org.elixir_lang.psi;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.Parentheses;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * <expression> dotInfixOperator parenthesesArguments parenthesesArguments? doBlock?
 */
public interface DotCall extends Call, Quotable {
    Quotable getDotInfixOperator();

    List<ElixirParenthesesArguments> getParenthesesArgumentsList();

    /**
     * Unlike with a base {@link Call}, {@link Parentheses#primaryArguments} are {@code @NotNull} because the first set
     * of parentheses has to be there or it wouldn't be a {@link Parentheses}
     */
    @Contract(pure = true)
    @Override
    @NotNull
    PsiElement[] primaryArguments();
}
