package org.elixir_lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import org.elixir_lang.psi.ElixirTypes;
import org.elixir_lang.psi.quoting.QuotingDialect;
import org.jetbrains.annotations.NotNull;

/**
 * Helpers the grammar calls as external rules, {@code <<name>>}.
 *
 * Must extend {@link GeneratedParserUtilBase}: GrammarKit static-imports this class into the
 * generated parser *instead of* that one, so its helpers have to stay in scope.
 */
public class ElixirParserUtil extends GeneratedParserUtilBase {
    /** Set by {@code File.doParseContents}; absent for builders created by any other route. */
    public static final Key<QuotingDialect> DIALECT = Key.create("ELIXIR_PARSE_DIALECT");

    /**
     * Whether the {@code &} just consumed is joined to what follows, making the two one capture
     * argument such as {@code &1} - see
     * {@link QuotingDialect#getRequiresAdjacentCaptureArgument()}.
     *
     * Used positively by {@code captureNumericOperation} and negated by {@code nonNumeric}, which is
     * what keeps those two rules exact complements: a spaced {@code & 1} the first rejects has to be
     * accepted by the second, or it matches neither and parses as an error.
     */
    public static boolean captureArgument(@NotNull PsiBuilder builder, int level) {
        QuotingDialect dialect = builder.getUserData(DIALECT);

        if (dialect == null) {
            dialect = QuotingDialect.getFALLBACK();
        }

        if (!dialect.getRequiresAdjacentCaptureArgument()) {
            return true;
        }

        /* Whitespace is skipped lazily, so the current lexeme may still be the space itself. Asking
           for the token type forces the skip, which is what makes rawLookup(-1) meaningful here. */
        builder.getTokenType();

        return builder.rawLookup(-1) == ElixirTypes.CAPTURE_OPERATOR;
    }
}
