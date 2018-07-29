package org.elixir_lang.generic_server

import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.GenericServer.GEN_CALL
import org.elixir_lang.GenericServer.GEN_CAST

interface Behaviour {
    fun handleCall(from: OtpErlangTuple, request: OtpErlangObject): OtpErlangObject
    fun handleCast(request: OtpErlangObject): OtpErlangObject
}

fun Behaviour.handleMessage(message: OtpErlangObject): OtpErlangObject? =
    when (message) {
        is OtpErlangTuple -> handleMessage(message)
        else -> null
    }

fun Behaviour.handleMessage(message: OtpErlangTuple): OtpErlangObject? =
    when (message.arity()) {
        2 -> handleMessage(message.elementAt(0), message.elementAt(1))
        3 -> handleMessage(message.elementAt(0), message.elementAt(2), message.elementAt(3))
        else -> null
    }

private fun Behaviour.handleMessage(label: OtpErlangObject, request: OtpErlangObject): OtpErlangObject? =
        when (label) {
            GEN_CAST -> handleCast(request)
            else -> null
        }

private fun Behaviour.handleMessage(label: OtpErlangObject, from: OtpErlangObject, request: OtpErlangObject): OtpErlangObject? =
        when (label) {
            GEN_CALL -> handleCall(from as OtpErlangTuple, request)
            else -> null
        }
