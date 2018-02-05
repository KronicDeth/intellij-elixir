package org.elixir_lang.beam.chunk.keyword

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject

data class Entry(val key: OtpErlangAtom, val value: OtpErlangObject)
