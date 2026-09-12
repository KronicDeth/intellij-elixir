package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.quoting.QuotingDialect;
import org.jetbrains.annotations.NotNull;

/**
 * Helpers the grammar calls as external rules, {@code <<name>>}.
 * <p>
 * Must extend {@link GeneratedParserUtilBase}: GrammarKit static-imports this class into the
 * generated parser *instead of* that one, so its helpers have to stay in scope.
 */
// GrammarKit calls every external rule with the recursion level.
@SuppressWarnings("unused")
public class ElixirParserUtil extends GeneratedParserUtilBase {
    /** Set by {@code File.doParseContents}; absent for builders created by any other route. */
    public static final Key<QuotingDialect> DIALECT = Key.create("ELIXIR_PARSE_DIALECT");

    /**
     * Whether the {@code &} just consumed is joined to what follows, making the two one capture
     * argument such as {@code &1} - see
     * {@link QuotingDialect#getRequiresAdjacentCaptureArgument()}.
     * <p>
     * Used positively by {@code captureNumericOperation} and negated by {@code nonNumeric}, which is
     * what keeps those two rules exact complements: a spaced {@code & 1} the first rejects has to be
     * accepted by the second, or it matches neither and parses as an error.
     */
    public static boolean captureArgument(@NotNull PsiBuilder builder, int level) {
        if (!dialect(builder).getRequiresAdjacentCaptureArgument()) {
            return true;
        }

        /* Whitespace is skipped lazily, so the current lexeme may still be the space itself. Asking
           for the token type forces the skip, which is what makes rawLookup(-1) meaningful here. */
        builder.getTokenType();

        return builder.rawLookup(-1) == ElixirTypes.CAPTURE_OPERATOR;
    }

    /** Whether {@code //} is the step operator - see {@link QuotingDialect#getHasStepOperator()}. */
    public static boolean stepOperator(@NotNull PsiBuilder builder, int level) {
        return dialect(builder).getHasStepOperator();
    }

    /** {@code ...}, which the lexer returns as an identifier. */
    public static boolean ellipsis(@NotNull PsiBuilder builder, int level) {
        return builder.getTokenType() == ElixirTypes.IDENTIFIER_TOKEN && "...".equals(builder.getTokenText());
    }

    /**
     * Whether the {@code +} or {@code -} here takes the other reading from the one the lexer gave it, because the
     * dialect does not count an escaped newline as space - see
     * {@link QuotingDialect#getCountsEscapedNewlineAsSpace()}. The lexer follows the newer reading, so this is true
     * for a binary sign in {@code f -\}+newline+{@code var} and a unary one in {@code f \}+newline+{@code -var}.
     */
    public static boolean escapedNewlineSwapsDualOperator(@NotNull PsiBuilder builder, int level) {
        IElementType tokenType = builder.getTokenType();

        if (tokenType == ElixirTypes.ADDITION_OPERATOR || tokenType == ElixirTypes.SUBTRACTION_OPERATOR) {
            return !dialect(builder).getCountsEscapedNewlineAsSpace() &&
                    rawTokenStartsWith(builder, 1, '\\') &&
                    rawTokenStartsWithHorizontalSpace(builder, -1) &&
                    builder.rawLookup(-2) == ElixirTypes.IDENTIFIER_TOKEN;
        }

        if (tokenType == ElixirTypes.NEGATE_OPERATOR || tokenType == ElixirTypes.NUMBER_OR_BADARITH_OPERATOR) {
            if (dialect(builder).getCountsEscapedNewlineAsSpace()) {
                return false;
            }

            int steps = -1;
            boolean escapedNewline = false;

            while (builder.rawLookup(steps) == TokenType.WHITE_SPACE) {
                escapedNewline |= rawTokenStartsWith(builder, steps, '\\');
                steps--;
            }

            return escapedNewline &&
                    steps < -1 &&
                    rawTokenStartsWithHorizontalSpace(builder, steps + 1) &&
                    builder.rawLookup(steps) == ElixirTypes.IDENTIFIER_TOKEN;
        }

        return false;
    }

    private static QuotingDialect dialect(@NotNull PsiBuilder builder) {
        QuotingDialect dialect = builder.getUserData(DIALECT);

        return dialect != null ? dialect : QuotingDialect.getFALLBACK();
    }

    private static boolean rawTokenStartsWithHorizontalSpace(@NotNull PsiBuilder builder, int steps) {
        return rawTokenStartsWith(builder, steps, ' ') || rawTokenStartsWith(builder, steps, '\t');
    }

    private static boolean rawTokenStartsWith(@NotNull PsiBuilder builder, int steps, char character) {
        if (builder.rawLookup(steps) != TokenType.WHITE_SPACE) {
            return false;
        }

        int start = builder.rawTokenTypeStart(steps);
        CharSequence text = builder.getOriginalText();

        return start < text.length() && text.charAt(start) == character;
    }
}
