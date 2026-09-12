package org.elixir_lang.intellij_elixir

import com.ericsson.otp.erlang.OtpErlangBinary
import com.ericsson.otp.erlang.OtpErlangTuple
import org.junit.Assert.assertEquals
import org.junit.Test

class QuoterTest {
    @Test
    fun `a binary error message is the whole message`() {
        assertEquals(
            "syntax error before: ",
            Quoter.errorMessage(binary("syntax error before: "), "end")
        )
    }

    /** Elixir 1.20.4's answer for a string holding U+202A, a bidirectional formatting character. */
    @Test
    fun `a prefix and suffix error message surrounds its token`() {
        val message = OtpErlangTuple(
            arrayOf(
                binary("invalid bidirectional formatting character in string: "),
                binary(". If you want to use such character, use it in its escaped \\u202A form instead")
            )
        )

        assertEquals(
            "invalid bidirectional formatting character in string: \\u202A. If you want to use such character, " +
                    "use it in its escaped \\u202A form instead",
            Quoter.errorMessage(message, "\\u202A")
        )
    }

    private fun binary(text: String) = OtpErlangBinary(text.toByteArray(Charsets.UTF_8))
}
