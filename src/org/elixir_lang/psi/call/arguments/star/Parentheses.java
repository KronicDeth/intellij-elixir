package org.elixir_lang.psi.call.arguments.star;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirMatchedParenthesesArguments;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.arguments.Star;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A call with one or two sets of parentheses containing arguments inside a {@code matchedParenthesesArguments}
 *
 * {@code <...> matchedParenthesesArguments}
 */
public interface Parentheses extends Star {
    @Contract(pure = true)
    @NotNull
    ElixirMatchedParenthesesArguments getMatchedParenthesesArguments();

    /**
     * Unlike with a base {@link Call}, {@link Parentheses#primaryArguments} are {@code @NotNull} because the first set
     * of parentheses has to be there or it wouldn't be a {@link Parentheses}
     */
    @Contract(pure = true)
    @Override
    @NotNull
    PsiElement[] primaryArguments();
}
