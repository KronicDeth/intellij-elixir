package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangAtom

object Atom {
    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/atom.ex#L8-L22
    fun toString(atom: OtpErlangAtom): String = atom.atomValue()
}
