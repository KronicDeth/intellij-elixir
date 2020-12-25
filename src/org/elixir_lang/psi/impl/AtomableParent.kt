package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.psi.ElixirInterpolation
import org.elixir_lang.psi.Parent
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.sdk.elixir.Type

// no matter whether the quote is a charlist or a string, it quotes as a string because atoms need binaries
class AtomableParent(val wrapped: Parent) : Parent by wrapped {
    override fun quoteBinary(metadata: OtpErlangList, argumentList: List<OtpErlangObject>): OtpErlangObject =
            QuotableImpl.quotedFunctionCall("<<>>", metadata, *argumentList.toTypedArray())

    override fun quoteInterpolation(interpolation: ElixirInterpolation): OtpErlangObject {
        val level = Type.getNonNullRelease(interpolation).level()
        val quotedChildren = QuotableImpl.quote(interpolation.children, level)
        val interpolationMetadata = QuotableImpl.metadata(interpolation)

        val quotedKernelToStringCall = QuotableImpl.quotedFunctionCall(
                Module.prependElixirPrefix(Module.KERNEL),
                "to_string",
                interpolationMetadata,
                quotedChildren
        )
        val quotedBinaryCall = QuotableImpl.quotedVariable(
                "binary",
                interpolationMetadata
        )

        return QuotableImpl.quotedFunctionCall(
                "::",
                interpolationMetadata,
                quotedKernelToStringCall,
                quotedBinaryCall
        )
    }
}
