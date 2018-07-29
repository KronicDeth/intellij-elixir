package org.elixir_lang.mail_box

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.beam.term.inspect
import java.io.IOException

val BADRPC = OtpErlangAtom("badrpc")

class BadRPC(val reason: OtpErlangObject) : IOException(inspect(reason))
