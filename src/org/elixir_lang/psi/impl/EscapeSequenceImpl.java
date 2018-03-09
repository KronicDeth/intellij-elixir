package org.elixir_lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.elixir_lang.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class EscapeSequenceImpl {
    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirEscapedCharacter escapedCharacter) {
        ASTNode[] escapedCharacterTokens = escapedCharacter
                .getNode()
                .getChildren(TokenSet.create(ElixirTypes.ESCAPED_CHARACTER_TOKEN));
        int parsedCodePoint = -1;

        if (escapedCharacterTokens.length == 1) {
            ASTNode escapedCharacterToken = escapedCharacterTokens[0];
            String formattedEscapedCharacter = escapedCharacterToken.getText();
            int formattedCodePoint = formattedEscapedCharacter.codePointAt(0);

            // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_interpolation.erl#L130-L142
            switch (formattedCodePoint) {
                case '0':
                    parsedCodePoint = 0;
                    break;
                case 'a':
                    parsedCodePoint = 7;
                    break;
                case 'b':
                    parsedCodePoint = 8;
                    break;
                case 'd':
                    parsedCodePoint = 127;
                    break;
                case 'e':
                    parsedCodePoint = 27;
                    break;
                case 'f':
                    parsedCodePoint = 12;
                    break;
                case 'n':
                    parsedCodePoint = 10;
                    break;
                case 'r':
                    parsedCodePoint = 13;
                    break;
                case 's':
                    parsedCodePoint = 32;
                    break;
                case 't':
                    parsedCodePoint = 9;
                    break;
                case 'v':
                    parsedCodePoint = 11;
                    break;
                default:
                    parsedCodePoint = formattedCodePoint;
            }
        }

        return parsedCodePoint;
    }

    @Contract(pure = true)
    public static int codePoint(@SuppressWarnings("unused") ElixirEscapedEOL escapedEOL) {
        return 10;
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull EscapedHexadecimalDigits hexadecimalEscapeSequence) {
        ASTNode[] validHexadecimalDigitsArray = hexadecimalEscapeSequence
                .getNode()
                .getChildren(
                        TokenSet.create(ElixirTypes.VALID_HEXADECIMAL_DIGITS)
                );
        int parsedCodePoint = -1;

        if (validHexadecimalDigitsArray.length == 1) {
            ASTNode validHexadecimalDigits = validHexadecimalDigitsArray[0];
            String formattedHexadecimalDigits = validHexadecimalDigits.getText();
            parsedCodePoint = Integer.parseInt(formattedHexadecimalDigits, 16);
        }

        return parsedCodePoint;
    }

    // @return -1 if codePoint cannot be parsed.
    public static int codePoint(@NotNull ElixirQuoteHexadecimalEscapeSequence quoteHexadecimalEscapeSequence) {
        EscapeSequence escapeSequence = quoteHexadecimalEscapeSequence.getEnclosedHexadecimalEscapeSequence();

        if (escapeSequence == null) {
            escapeSequence = quoteHexadecimalEscapeSequence.getOpenHexadecimalEscapeSequence();
        }

        int parsedCodePoint = -1;

        if (escapeSequence != null) {
            parsedCodePoint = escapeSequence.codePoint();
        }

        return parsedCodePoint;
    }

    public static int codePoint(@NotNull ElixirSigilHexadecimalEscapeSequence sigilHexadecimalEscapeSequence) {
        EscapeSequence escapeSequence = sigilHexadecimalEscapeSequence.getEnclosedHexadecimalEscapeSequence();

        if (escapeSequence == null) {
            escapeSequence = sigilHexadecimalEscapeSequence.getOpenHexadecimalEscapeSequence();
        }

        assert escapeSequence != null;

        return escapeSequence.codePoint();
    }
}
