package org.elixir_lang.beam.chunk

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.beam.binaryToTerm
import org.elixir_lang.beam.chunk.keyword.Entry

typealias Keyword = MutableList<Entry>

fun keyword(): Keyword = mutableListOf()

/**
 * Keyword is a keyword list, but from Erlang
 *
 *     -spec module(module_code(), [{binary(), binary()}], [{atom(),term()}], [compile:option()]) ->
 *       {'ok',binary()}.
 *
 *     module(Code, ExtraChunks, Keyword, CompilerOpts)
 *
 * https://github.com/erlang/otp/blob/0052a4641d6f904fe632a9c056b9eed7ffc18b54/lib/compiler/src/beam_asm.erl#L58-L61
 *
 * Keyword is transformed
 *
 *     {Attributes,Compile} = build_attributes(Attr, Keyword, MD5),
 *
 * https://github.com/erlang/otp/blob/0052a4641d6f904fe632a9c056b9eed7ffc18b54/lib/compiler/src/beam_asm.erl#L185
 *
 * build_attributes uses term_to_binary to add some versioning before Keyword's term_to_binary
 *
 *     build_attributes(Attr, Compile, MD5) ->
 *       AttrBinary = term_to_binary(set_vsn_attribute(Attr, MD5)),
 *       CompileBinary = term_to_binary([{version,?COMPILER_VSN}|Compile]),
 *       {AttrBinary,CompileBinary}.
 *
 * https://github.com/erlang/otp/blob/0052a4641d6f904fe632a9c056b9eed7ffc18b54/lib/compiler/src/beam_asm.erl#L265-L268
 *
 * Before being put in the chunk
 *
 *     CompileChunk = chunk(<<"CInf">>, Compile),
 *
 * https://github.com/erlang/otp/blob/0052a4641d6f904fe632a9c056b9eed7ffc18b54/lib/compiler/src/beam_asm.erl#L187
 */
fun from(chunk: Chunk): Keyword? {
    // Reverse `CompileBinary = term_to_binary([{version,?COMPILER_VSN}|Compile]),`
    val (compileTerm, _) = binaryToTerm(chunk.data, offset = 0)

    return from(compileTerm)
}

fun from(list: OtpErlangList): Keyword? {
    val keyword = keyword()
    var valid = true

    for (element in list.elements()) {
        if (element is OtpErlangTuple && element.arity() == 2) {
            val (key, value) = element

            if (key is OtpErlangAtom) {
                keyword.add(Entry(key, value))
            } else {
                valid = false
                break
            }
        } else {
            valid = false
            break
        }
    }

    return if (valid) {
        keyword
    } else {
        null
    }
}

fun from(term: OtpErlangObject): Keyword? =
        when (term) {
            is OtpErlangList -> from(term)
            else -> null
        }

operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
