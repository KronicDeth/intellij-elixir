package org.elixir_lang.heex.lexer.look_ahead;

import org.elixir_lang.heex.lexer.Flex;
import org.elixir_lang.heex.psi.Types;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * HEEx-only: {@code <script>}/{@code <style>} enter a state where `{` is inert (only YYINITIAL
 * treats `{` as HEEx brace interpolation - see HEEx.flex), and an embedded `<% %>`/`<%= %>` tag
 * restores that state on exit via {@code tagReturnState} - closing the tag returns to SCRIPT_TAG/
 * STYLE_TAG rather than falling back to YYINITIAL, so `{` stays inert and `</script>`/`</style>`
 * still closes the tag afterward.
 *
 * `<Script.component>`/`<Style.thing>` must NOT enter script/style
 * mode - START_SCRIPT_TAG/START_STYLE_TAG require the character after "script"/"style" to be
 * whitespace or `>`, so a `.` falls through to plain DATA - and a self-closing `<script .../>`/
 * `<style .../>`, which has no body, must likewise never enter script/style mode.
 */
@RunWith(Parameterized.class)
public class ScriptStyleTest extends Test {
    private static final Lex[][] SEQUENCES = new Lex[][]{
            // <script> enters SCRIPT_TAG; the OPENING token type differs from DATA so it is not
            // merged away, making the entry state directly observable.
            {
                    new Lex("<script>", Types.DATA, Flex.SCRIPT_TAG),
                    new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE),
                    new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR),
                    new Lex("x", Types.ELIXIR, Flex.ELIXIR),
                    // Closing the embedded tag returns to SCRIPT_TAG, not YYINITIAL - script mode is
                    // preserved, so ";" and "</script>" both lex (and merge, both being DATA) as
                    // script content ending in the real </script> close, not as two separate DATA
                    // runs split by a spurious YYINITIAL re-entry.
                    new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE),
                    new Lex(";</script>", Types.DATA, Flex.YYINITIAL)
            },
            // <style> is the same shape, entering STYLE_TAG instead.
            {
                    new Lex("<style>", Types.DATA, Flex.STYLE_TAG),
                    new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE),
                    new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR),
                    new Lex("x", Types.ELIXIR, Flex.ELIXIR),
                    new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE),
                    new Lex(";</style>", Types.DATA, Flex.YYINITIAL)
            },
            // <Script.component> - the "." after "Script" means START_SCRIPT_TAG does not match,
            // so the whole tag is plain DATA and YYINITIAL is never left.
            {
                    new Lex("<Script.component></Script.component>", Types.DATA, Flex.YYINITIAL)
            },
            // <Style.thing> - same non-entry behaviour for style.
            {
                    new Lex("<Style.thing></Style.thing>", Types.DATA, Flex.YYINITIAL)
            },
            // <script src="x.js" /><div> - self-closing, so it never enters SCRIPT_TAG; the whole
            // thing (including the following "<div>") stays one merged DATA run in YYINITIAL. If it
            // wrongly entered SCRIPT_TAG, the un-terminated body would run past "<div>" looking for
            // "</script>" instead.
            {
                    new Lex("<script src=\"x.js\" /><div>", Types.DATA, Flex.YYINITIAL)
            },
            // <style media="screen" /><div> - same non-entry behaviour for style.
            {
                    new Lex("<style media=\"screen\" /><div>", Types.DATA, Flex.YYINITIAL)
            },
            // <script>{a}<%= x %>{b}</script> - `{` stays inert both before AND after the embedded
            // tag, so `{b}` is literal script text rather than brace interpolation.
            // "<script>" and "{a}" merge (both DATA, uninterrupted by any other token type), and
            // "{b}" merges with the real "</script>" close that follows it for the same reason.
            {
                    new Lex("<script>{a}", Types.DATA, Flex.SCRIPT_TAG),
                    new Lex("<%", Types.OPENING, Flex.MARKER_MAYBE),
                    new Lex("=", Types.EQUALS_MARKER, Flex.ELIXIR),
                    new Lex(" x ", Types.ELIXIR, Flex.ELIXIR),
                    new Lex("%>", Types.CLOSING, Flex.WHITESPACE_MAYBE),
                    new Lex("{b}</script>", Types.DATA, Flex.YYINITIAL)
            }
    };

    public ScriptStyleTest(@NotNull Sequence sequence) {
        super(sequence);
    }

    @Contract(pure = true)
    @NotNull
    @Parameterized.Parameters(name = "#{index} {0}")
    public static Iterable<Object> parameters() {
        return Arrays.stream(SEQUENCES).<Object>map(Sequence::new)::iterator;
    }

    @org.junit.Test
    public void scriptStyle() {
        assertSequence();
    }
}
