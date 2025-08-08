package org.elixir_lang.beam.term

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.XValueRenderer
import org.elixir_lang.debugger.stack_frame.value.Presentation
import java.util.stream.IntStream

fun inspect(term: OtpErlangObject): String =
    XValueRenderer().let { renderer ->
        Presentation(term).renderValue(renderer)
        renderer.getText().elixirEscape()
    }

fun String.elixirEscape(): String {
    val elixirEscapedCodePoints = this.codePoints().flatMap {
        it.elixirEscape()
    }

    val elixirEscapedBuilder = StringBuilder()

    elixirEscapedCodePoints.forEachOrdered { elixirEscapedCodePoint ->
        elixirEscapedBuilder.appendCodePoint(elixirEscapedCodePoint)
    }

    return elixirEscapedBuilder.toString()
}

private fun Int.elixirEscape(): IntStream =
    when (this) {
        '\n'.code -> "\\n".codePoints()
        '\r'.code -> "\\r".codePoints()
        27 -> "\\e".codePoints()
        else -> IntStream.of(this)
    }
