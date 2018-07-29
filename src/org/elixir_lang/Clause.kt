package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.clause.Match

/**
 * Emulates a function clause
 */
interface Clause {
    fun match(arguments: OtpErlangList): Match?
    fun run(match: Match): OtpErlangObject
}

fun Iterable<Clause>.run(arguments: OtpErlangList): OtpErlangObject =
    asSequence()
            .mapNotNull { clause ->
                clause.match(arguments)?.let { match ->
                    clause.run(match)
                }
            }
            .first()
