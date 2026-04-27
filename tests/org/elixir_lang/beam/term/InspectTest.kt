package org.elixir_lang.beam.term

import com.ericsson.otp.erlang.OtpErlangString
import junit.framework.TestCase

class InspectTest : TestCase() {
    fun testEscapesInterpolationMarkers() {
        assertEquals("'abc\\#{value}'", "'abc#{value}'".elixirEscape())
    }

    fun testPreservesEscapedInterpolationMarkers() {
        assertEquals("'abc\\#{value}'", "'abc\\#{value}'".elixirEscape())
    }

    fun testEscapesInterpolationAfterEscapedNewline() {
        assertEquals("'line\\n\\#{value}'", "'line\n#{value}'".elixirEscape())
    }

    // Backslash in Erlang charlist / string literal must be escaped so that
    // the decompiled output is valid Elixir.  A single `\` in an OtpErlangString
    // previously rendered as `'\'` (unterminated), now must render as `'\\'`.
    fun testEscapesBackslashInCharlist() {
        // OtpErlangString("\\") is a string containing one backslash.
        // inspect() must render it as '\\' (4 chars: ' \ \ ')
        assertEquals("'\\\\'", inspect(OtpErlangString("\\")))
    }

    fun testEscapesSingleQuoteInCharlist() {
        // OtpErlangString("'") is a string containing one single quote.
        // inspect() must render it as '\'' (4 chars: ' \ ' ')
        assertEquals("'\\''", inspect(OtpErlangString("'")))
    }
}
