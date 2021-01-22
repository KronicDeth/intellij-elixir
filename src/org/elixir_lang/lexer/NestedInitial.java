package org.elixir_lang.lexer;

import org.jetbrains.annotations.NotNull;

public class NestedInitial extends RuntimeException {
    public NestedInitial(@NotNull Stack stack) {
        super("Cannot return to initial state when stack is not empty - it will break highlighting as initial state assumes restartable.\n" +
                "Add `stack.clear();` to `ElixirFlexLexer#reset`, the JFlex generator cannot be customized to do this automatically when regenerating.\n" +
                "If `stack.clear();` is there, then there is a bug in `Elixir.flex` where a state enter YYINITIAL more than once.");
        this.stack = stack;
    }

    @NotNull
    public Stack getStack() {
        return stack;
    }

    @NotNull
    private final Stack stack;
}
