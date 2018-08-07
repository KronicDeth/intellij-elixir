package org.elixir_lang.debugger.node

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect

data class Binding(val erlangName: String, val value: OtpErlangObject) : Comparable<Binding> {
    val elixirName: String? by lazy {
        elixirNameMatchResult?.let { matchResult ->
            matchResult.groupValues[1]
        }
    }

    val index: Int? by lazy {
        elixirNameMatchResult?.let { matchResult ->
            matchResult.groupValues[2].toInt()
        }
    }

    override fun compareTo(other: Binding): Int {
        val thisElixirName = this.elixirName
        val otherElixirName = other.elixirName

        return if (thisElixirName != null && otherElixirName != null) {
            val elixirNameCompared = thisElixirName.compareTo(otherElixirName)

            if (elixirNameCompared == 0) {
                index!!.compareTo(other.index!!)
            } else {
                elixirNameCompared
            }
        } else {
            erlangName.compareTo(other.erlangName)
        }
    }

    private val elixirNameMatchResult by lazy {
        elixirNameRegex.matchEntire(erlangName)
    }

    companion object {
        // 1.6: V<name>@<binding>
        // 1.7: _<name>@<binding>
        private val elixirNameRegex = Regex("(?:V|_)(.+)@(\\d+)")

        private const val ARITY = 2

        private val LOGGER = Logger.getInstance(Binding::class.java)

        fun from(term: OtpErlangObject): Binding? =
                when (term) {
                    is OtpErlangTuple -> from(term)
                    else -> {
                        LOGGER.error("Binding ${inspect(term)} is not an OtpErlangTuple")

                        null
                    }
                }

        private fun from(tuple: OtpErlangTuple): Binding? {
            val arity = tuple.arity()

            return if (arity == ARITY) {
                name(tuple)?.let { name ->
                    val value = tuple.elementAt(1)

                    Binding(name, value)
                }
            } else {
                LOGGER.error("Binding (${inspect(tuple)}) arity ($arity) is not $ARITY")

                null
            }
        }

        private fun name(tuple: OtpErlangTuple): String? {
            val name = tuple.elementAt(0)

            return when (name) {
                is OtpErlangAtom -> name.atomValue()
                else -> {
                    LOGGER.error("name (${inspect(name)}) is not an OtpErlangAtom")

                    null
                }
            }
        }
    }
}
