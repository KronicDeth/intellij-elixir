package org.elixir_lang.beam.term

import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.chunk.XValueRenderer
import org.elixir_lang.debugger.XValuePresentation

fun inspect(term: OtpErlangObject): String =
    XValueRenderer().let { renderer ->
        XValuePresentation(term).renderValue(renderer)
        renderer.getText()
    }
