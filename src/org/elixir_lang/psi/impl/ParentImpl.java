package org.elixir_lang.psi.impl;

import com.ericsson.otp.erlang.*;
import com.intellij.lang.ASTNode;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.Level;
import org.elixir_lang.psi.*;
import org.elixir_lang.sdk.elixir.Release;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.elixir_lang.Level.V_1_3;
import static org.elixir_lang.psi.impl.QuotableImpl.metadata;
import static org.elixir_lang.psi.impl.QuotableImpl.quotedFunctionCall;
import static org.elixir_lang.sdk.elixir.Type.getNonNullRelease;

public class ParentImpl {
    @NotNull
    public static List<Integer> addChildTextCodePoints(@Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        return addStringCodePoints(codePointList, child.getText());
    }

    @NotNull
    static OtpErlangObject elixirCharList(@NotNull final List<Integer> codePointList) {
        OtpErlangList elixirCodePointList = elixirCodePointList(codePointList);

        return elixirCharList(elixirCodePointList);
    }

    /**
     * Erlang will automatically stringify a list that is just a list of LATIN-1 printable code
     * points.
     * OtpErlangString and OtpErlangList are not equal when they have the same content, so to check against
     * Elixir.Code.string_to_quoted, this code must determine if Erlang would return an OtpErlangString instead
     * of OtpErlangList and do the same.
     */
    @NotNull
    static OtpErlangObject elixirCharList(@NotNull final OtpErlangList erlangList) {
        OtpErlangObject charList;

        /* JInterface will return an OtpErlangString in some case and an OtpErlangList in other.  Right now, I'm
           assuming it works similar to the printing in `iex` and is based on whether the codePoint is printable, but
           ASCII printable instead of Unicode printable since Erlang is ASCII/LATIN-1 based */
        if (isErlangPrintable(erlangList))  {
            try {
                charList = new OtpErlangString(erlangList);
            } catch (OtpErlangException e) {
                throw new NotImplementedException(e);
            }
        } else {
            charList = erlangList;
        }

        return charList;
    }

    @NotNull
    static OtpErlangBinary elixirString(@NotNull final List<Integer> codePointList) {
        StringBuilder stringAccumulator = new StringBuilder();

        for (int codePoint : codePointList) {
            stringAccumulator.appendCodePoint(codePoint);
        }

        return elixirString(stringAccumulator.toString());
    }

    @NotNull
    public static OtpErlangBinary elixirString(@NotNull String javaString) {
        final byte[] bytes = javaString.getBytes(Charset.forName("UTF-8"));
        return new OtpErlangBinary(bytes);
    }

    // Parent methods

    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull @SuppressWarnings("unused") Quote parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        codePointList = ensureCodePointList(codePointList);

        ElixirEscapedCharacter escapedCharacter = (ElixirEscapedCharacter) child.getPsi();

        codePointList.add(
                escapedCharacter.codePoint()
        );

        return codePointList;
    }


    @NotNull
    public static List<Integer> addEscapedCharacterCodePoints(@NotNull @SuppressWarnings("unused") Sigil parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        String childText = child.getText();

        // Not sure, why, but \ gets stripped in front of # when quoting using Quoter.
        if (childText.equals("\\#")) {
            childText = "#";
        } else if (parent instanceof SigilLine) {
            SigilLine sigilLine = (SigilLine) parent;

            char terminator = sigilLine.terminator();

            if (childText.equals("\\" + terminator)) {
                childText = new String(
                        new char[] {
                                terminator
                        }
                );
            }
        }

        return addStringCodePoints(codePointList, childText);
    }

    @NotNull
    public static List<Integer> addEscapedEOL(@NotNull Parent parent,
                                              @Nullable List<Integer> maybeCodePointList,
                                              @NotNull @SuppressWarnings("unused") ASTNode child) {
        List<Integer> codePointList = ensureCodePointList(maybeCodePointList);

        Level level = getNonNullRelease(parent).level();

        if (level.compareTo(V_1_3) >= 0) {
            if (parent instanceof LiteralSigilHeredoc) {
                codePointList = addStringCodePoints(codePointList, "\\");
            } else if (parent instanceof LiteralSigilLine) {
                for (Integer codePoint : codePoints("\\\n")) {
                    codePointList.add(codePoint);
                }
            }
        }

        return codePointList;
    }

    @NotNull
    public static List<Integer> addFragmentCodePoints(@NotNull @SuppressWarnings("unused") Parent parent,
                                                      @Nullable List<Integer> codePointList,
                                                      @NotNull ASTNode child) {
        return addChildTextCodePoints(codePointList, child);
    }
    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull @SuppressWarnings("unused") Quote parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        codePointList = ensureCodePointList(codePointList);

        ElixirQuoteHexadecimalEscapeSequence hexadecimalEscapeSequence = (ElixirQuoteHexadecimalEscapeSequence) child.getPsi();

        codePointList.add(hexadecimalEscapeSequence.codePoint());

        return codePointList;
    }

    @NotNull
    public static List<Integer> addHexadecimalEscapeSequenceCodePoints(@NotNull @SuppressWarnings("unused") Sigil parent, @Nullable List<Integer> codePointList, @NotNull ASTNode child) {
        return addChildTextCodePoints(codePointList, child);
    }


    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(InterpolatedCharList interpolatedCharList, OtpErlangTuple binary) {
        return quotedFunctionCall(
                "Elixir.String",
                quoteBinaryFunctionIdentifier(interpolatedCharList),
                metadata(interpolatedCharList),
                binary
        );
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(@SuppressWarnings("unused") InterpolatedString interpolatedString, OtpErlangTuple binary) {
        return binary;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteBinary(@SuppressWarnings("unused") Sigil sigil, OtpErlangTuple binary) {
        return binary;
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") InterpolatedCharList interpolatedCharList) {
        return new OtpErlangList();
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") InterpolatedString interpolatedString) {
        return elixirString("");
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteEmpty(@SuppressWarnings("unused") Sigil sigil) {
        return elixirString("");
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") InterpolatedCharList interpolatedCharList, List<Integer> codePointList) {
        return elixirCharList(codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") InterpolatedString interpolatedString, List<Integer> codePointList) {
        return elixirString(codePointList);
    }

    @Contract(pure = true)
    @NotNull
    public static OtpErlangObject quoteLiteral(@SuppressWarnings("unused") Sigil sigil, List<Integer> codePointList) {
        return elixirString(codePointList);
    }

    // Private Methods

    @NotNull
    private static List<Integer> addStringCodePoints(@Nullable List<Integer> codePointList, @NotNull String string) {
        codePointList = ensureCodePointList(codePointList);
        String filteredString = filterEscapedEOL(string);

        for (Integer codePoint : codePoints(filteredString)) {
            codePointList.add(codePoint);
        }

        return codePointList;
    }

    /*
     * @todo use String.codePoints in Java 8 when IntelliJ is using it
     * @see https://stackoverflow.com/questions/1527856/how-can-i-iterate-through-the-unicode-codepoints-of-a-java-string/21791059#21791059
     */
    private static Iterable<Integer> codePoints(final String string) {
        return () -> new Iterator<Integer>() {
            int nextIndex = 0;

            public boolean hasNext() {
                return nextIndex < string.length();
            }

            public Integer next() {
                int result = string.codePointAt(nextIndex);
                nextIndex += Character.charCount(result);
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }


    @NotNull
    private static OtpErlangList elixirCodePointList(@NotNull final List<Integer> codePointList) {
        OtpErlangLong[] erlangCodePoints = new OtpErlangLong[codePointList.size()];

        int i = 0;
        for (int codePoint : codePointList) {
            erlangCodePoints[i++] = new OtpErlangLong(codePoint);
        }

        return new OtpErlangList(erlangCodePoints);
    }

    @NotNull
    private static List<Integer> ensureCodePointList(@Nullable List<Integer> codePointList) {
        if (codePointList == null) {
            codePointList = new LinkedList<>();
        }

        return codePointList;
    }

    @NotNull
    private static String filterEscapedEOL(String unfiltered) {
        return unfiltered.replace("\\\n", "");
    }

    private static boolean isErlangPrintable(@NotNull final OtpErlangList erlangList) {
        boolean isErlangPrintable = true;

        for (OtpErlangObject erlangObject : erlangList) {

            if (erlangObject instanceof OtpErlangLong) {
                OtpErlangLong erlangLong = (OtpErlangLong) erlangObject;

                final int codePoint;

                try {
                    codePoint = erlangLong.intValue();
                } catch (OtpErlangRangeException e) {
                    isErlangPrintable = false;
                    break;
                }

                if (!isErlangPrintable(codePoint)) {
                    isErlangPrintable = false;
                    break;
                }
            } else {
                isErlangPrintable = false;
                break;
            }
        }

        if (erlangList.arity() == 0) {
            isErlangPrintable = false;
        }

        return isErlangPrintable;
    }

    @Contract(pure = true)
    private static boolean isErlangPrintable(int codePoint) {
        return (codePoint >= 0 && codePoint <= 255);
    }

    /**
     * Elixir 1.3 changed from `to_char_list` to `to_charlist`
     * (https://github.com/elixir-lang/elixir/blob/v1.3/CHANGELOG.md)
     *
     * @return {@code "to_charlist} by default;  {@code "to_char_list"}
     */
    @Contract(pure = true)
    @NotNull
    @SuppressWarnings("SpellCheckingInspection")
    private static String quoteBinaryFunctionIdentifier(@NotNull final InterpolatedCharList interpolatedCharList) {
        Release release = getNonNullRelease(interpolatedCharList);

        return release.level().quoteBinaryFunctionIdentifier;
    }
}
