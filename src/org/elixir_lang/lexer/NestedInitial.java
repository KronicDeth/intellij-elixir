package org.elixir_lang.lexer;

import org.jetbrains.annotations.NotNull;

public class NestedInitial extends RuntimeException {
    public NestedInitial(@NotNull Stack stack) {
        super("""
            Cannot push YYINITIAL (state 0) onto a non-empty stack - YYINITIAL is not restartable \
            and pushing it mid-lex would corrupt highlighting.
            This is a bug in Elixir.flex: a rule is calling pushAndBegin(YYINITIAL) or \
            returning to YYINITIAL via popAndBegin() while the stack still contains frames.
            The stack is cleared automatically at the start of each document via \
            ElixirFlexLexerAdapter.start(), so this exception indicates a genuine flex rule error.""");
        this.stack = stack;
    }

    @NotNull
    public Stack getStack() {
        return stack;
    }

    @NotNull
    private final Stack stack;
}
