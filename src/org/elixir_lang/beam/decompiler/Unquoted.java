package org.elixir_lang.beam.decompiler;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.elixir_lang.beam.decompiler.Default.appendParameters;

public class Unquoted extends MacroNameArity {
    /*
     * CONSTANTS
     */

    public static final MacroNameArity INSTANCE = new Unquoted();
    private static final Predicate<String> NOT_IDENTIFIER_OR_PREFIX_OPERATOR_PREDICATE;
    private static final Predicate<String> BARE_ATOM_PREDICATE;

    static {
        Stream<String> keywordStream = Stream.of(
                "do",
                "end",
                "false",
                "nil",
                "true"
        );
        Stream<String> specialFormStream = Stream.of(
                "!",
                "%",
                "%{}",
                "&",
                ".",
                "<<>>",
                "@",
                "fn",
                "unquote",
                "unquote_splicing",
                "{}"
        );
        String fixed = Stream
                .concat(keywordStream, specialFormStream)
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        // See Elixir.flex IDENTIFIER_TOKEN_TAIL
        @Language("RegExp")
        String identifierTokenTail = "[0-9a-zA-Z_]*[?!]?";
        // See Elixir.flex ALIAS
        @Language("RegExp")
        String alias = "[A-Z]" + identifierTokenTail;
        BARE_ATOM_PREDICATE = Pattern
                .compile("^(" + fixed + "|" + alias + ")$")
                .asPredicate();
        String prefixOperators = "[@!]|~~~";
        // See Elixir.flex IDENTIFIER_TOKEN
        NOT_IDENTIFIER_OR_PREFIX_OPERATOR_PREDICATE = Pattern.compile(
                "^(" + "[a-z_]" + identifierTokenTail + "|" + prefixOperators + ")$"
        ).asPredicate().negate();
    }

    /*
     * Instance Methods
     */

    /**
     * Wehther the decompiler accepts the {@code macroNameArity}.
     *
     * @return {@code true} if {@link #append(StringBuilder, org.elixir_lang.beam.MacroNameArity)} should be called with
     *   {@code macroNameArity}.
     */
    @Override
    public boolean accept(@NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        return BARE_ATOM_PREDICATE.or(NOT_IDENTIFIER_OR_PREFIX_OPERATOR_PREDICATE).test(macroNameArity.name);
    }

    /**
     * Append the decompiled source for {@code macroNameArity} to {@code decompiled}.
     *
     * @param decompiled the decompiled source so far
     */
    @Override
    public void append(@NotNull StringBuilder decompiled, @NotNull org.elixir_lang.beam.MacroNameArity macroNameArity) {
        decompiled
                .append("  ")
                .append(macroNameArity.macro)
                .append(' ');

        appendName(decompiled, macroNameArity.name);
        appendParameters(decompiled, macroNameArity);
        appendBody(decompiled);
    }

    @Override
    public void appendName(@NotNull StringBuilder decompiled, @NotNull String name) {
        decompiled
                .append("unquote(:")
                .append(macroNameToAtomName(name))
                .append(")");
    }

    @NotNull
    private StringBuilder macroNameToAtomName(@NotNull String macroName) {
        StringBuilder atomName = new StringBuilder();

        if (BARE_ATOM_PREDICATE.test(macroName)) {
            atomName.append(macroName);
        } else {
            atomName.append('"').append(macroName).append('"');
        }

        return atomName;
    }
}
