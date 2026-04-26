package org.elixir_lang

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple

inline fun <R> ifTaggedTuple(
    term: OtpErlangObject?,
    arity: Int,
    crossinline predicate: (String) -> Boolean,
    crossinline ifTrue: (OtpErlangTuple) -> R?
): R? =
    (term as? OtpErlangTuple)?.let { tuple ->
        ifTaggedTuple(tuple, arity, predicate, ifTrue)
    }

inline fun <R> ifTaggedTuple(
    tuple: OtpErlangTuple,
    arity: Int,
    crossinline predicate: (String) -> Boolean,
    crossinline ifTrue: (OtpErlangTuple) -> R?
): R? {
    if (tuple.arity() != arity) {
        return null
    }

    val tag = (tuple.elementAt(0) as? OtpErlangAtom)?.atomValue() ?: return null

    return if (predicate(tag)) {
        ifTrue(tuple)
    } else {
        null
    }
}
