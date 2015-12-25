package org.elixir_lang.psi.call.arguments;

import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.ElixirMatchedParenthesesArguments;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A call with no arguments; not even empty parentheses.
 */
public interface None extends Call {
    /**
     * @return Always {@code null} because a no argument call by definition has no arguments, not even an empty list
     *   of arguments.
     */
    @Contract(pure = true, value = "-> null")
    @Override
    @Nullable
    PsiElement[] primaryArguments();

    /**
     * @return Always {@code null} because a no argument call doesn't ever have {@link #primaryArguments}, so it can't
     *   have secondary arguments.
     */
    @Contract(pure = true, value = "-> null")
    @Override
    @Nullable
    PsiElement[] secondaryArguments();
}
