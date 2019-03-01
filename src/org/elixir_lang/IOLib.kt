package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangLong

// :io_lib from Erlang
object IOLib {
    fun printableList(list: OtpErlangList): Boolean = printableListUnicode(list)

    fun printableListToString(list: OtpErlangList): String =
            list
                    .map { it as OtpErlangLong }
                    .map { it.intValue() }
                    .flatMap { Character.toChars(it).asList() }
                    .joinToString("")

    private fun printable(character: Long) =
            when (character) {
                in 8..13, 27L, in 32..126, in 0xA0..0xD7FF, in 0xE000..0xFFFD, in 0x10000..0x10FFFF -> true
                else -> false
            }

    private fun printable(character: OtpErlangLong) = printable(character.longValue())

    // https://github.com/erlang/otp/blob/OTP-20.0.2/lib/stdlib/src/io_lib.erl#L651-L666
    private fun printableListUnicode(list: OtpErlangList): Boolean =
        list.all { element ->
            if (element is OtpErlangLong) {
                printable(element)
            } else {
                false
            }
        }
}
