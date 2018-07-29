package org.elixir_lang.debugger

import com.ericsson.otp.erlang.OtpErlangAtom

data class InterpretedModule(val interpreted: Boolean, val module: OtpErlangAtom) : Comparable<InterpretedModule> {
    override fun compareTo(other: InterpretedModule): Int = module.atomValue().compareTo(other.module.atomValue())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InterpretedModule

        if (module != other.module) return false

        return true
    }

    override fun hashCode(): Int = module.hashCode()
}
