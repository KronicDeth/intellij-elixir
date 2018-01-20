package org.elixir_lang.beam

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangLong
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import com.intellij.openapi.diagnostic.Logger
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.beam.term.unsignedIntToInt

data class NameArity(val name: String, val arity: Int) {
    companion object {
        private val logger = Logger.getInstance(NameArity::class.java)

        fun from(term: OtpErlangObject): NameArity? =
            when (term) {
                is OtpErlangTuple -> from(term)
                else -> {
                    logger.error("""
                                 nameArity (`${term.javaClass}`) is not a tuple

                                 ## term

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """)

                    null
                }
            }

        // Private Functions

        private fun arity(term: OtpErlangObject): Int? =
                when (term) {
                    is OtpErlangLong -> unsignedIntToInt(term.longValue())
                    else -> {
                        logger.error("""
                                     nameArity arity `${term.javaClass}` is not an `OtpErlangLong`

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """)

                        null
                    }
                }

        private fun from(tuple: OtpErlangTuple): NameArity? {
            val tupleArity = tuple.arity()

            return if (tupleArity == 2) {
                val name = name(tuple.elementAt(0))
                val arity = arity(tuple.elementAt(1))

                if (name != null && arity != null) {
                    NameArity(name, arity)
                } else {
                    null
                }
            } else {
                logger.error("""
                             nameArity arity ($tupleArity) is not 2

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """)

                null
            }
        }

        private fun name(term: OtpErlangObject): String? =
                when (term) {
                    is OtpErlangAtom -> term.atomValue()
                    else -> {
                        logger.error("""
                                     nameArity name `${term.javaClass}` is not an `OtpErlangAtom`

                                     ```elixir
                                     ${inspect(term)}
                                     ```
                                     """)

                        null
                    }
                }
    }
}
