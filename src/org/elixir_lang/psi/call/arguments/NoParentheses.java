package org.elixir_lang.psi.call.arguments;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirMatchedParenthesesArguments;
import org.elixir_lang.psi.ElixirNoParenthesesOneArgument;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A call with no parentheses around the arguments
 *
 * {@code <...> noParenthesesOneArgument}
 */
public interface NoParentheses extends Call {
    /**
     * Unlike with a base {@link Call}, {@link NoParentheses#primaryArguments} are {@code @NotNull} because the first
     * set of arguments has to be there or it would be a {@link None}
     */
    @Contract(pure = true)
    @Override
    @NotNull
    PsiElement[] primaryArguments();

    /**
     * @return Always {@code null} because without parentheses, there is no way to separate sets of arguments.
     */
    @Contract(pure = true, value = "-> null")
    @Override
    @Nullable
    PsiElement[] secondaryArguments();
}
