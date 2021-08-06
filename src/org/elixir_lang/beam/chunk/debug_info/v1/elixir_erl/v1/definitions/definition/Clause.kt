package org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.definition

import com.ericsson.otp.erlang.OtpErlangList
import com.ericsson.otp.erlang.OtpErlangObject
import com.ericsson.otp.erlang.OtpErlangTuple
import org.elixir_lang.Macro
import org.elixir_lang.Macro.rewriteGuard
import org.elixir_lang.beam.Decompiler.Companion.decompiler
import org.elixir_lang.beam.chunk.Keyword
import org.elixir_lang.beam.chunk.debug_info.logger
import org.elixir_lang.beam.chunk.debug_info.v1.elixir_erl.v1.definitions.Definition
import org.elixir_lang.beam.term.inspect
import java.lang.StringBuilder


class Clause(
        val definition: Definition,
        metadata: OtpErlangObject,
        arguments: OtpErlangObject,
        guards: OtpErlangObject,
        val block: OtpErlangObject
) {
    val metadata: Keyword? = org.elixir_lang.beam.chunk.from(metadata)
    val arguments: OtpErlangList? = arguments as? OtpErlangList
    private val guards: OtpErlangList? = guards as? OtpErlangList
    val signature: String by lazy {
        definition.macroNameArity?.let { macroNameArity ->
            decompiler(macroNameArity)?.let { decompiler ->
                val decompiled = StringBuilder()
                val argumentStrings = this.arguments?.map { Macro.toString(it) }.orEmpty().toTypedArray()
                decompiler.appendSignature(decompiled, macroNameArity, macroNameArity.name, argumentStrings)

                decompiled.toString()
            }
        } ?: "${definition.name}(${argumentsToString(this.arguments)})${guardsToString(this.guards)}"
    }

    fun toMacroString(): String? =
        definition.macro?.let { macro ->
            val blockMacroString = try {
                Macro.toString(block)
            } catch (stackOverflowError: StackOverflowError) {
                "# Body not decompiled due to stack overflow in Macro."
            }

            "$macro $signature do\n${blockMacroString.prependIndent("  ")}\nend"
        }

    companion object {
        fun from(term: OtpErlangObject, definition: Definition): Clause? =
                if (term is OtpErlangTuple) {
                    from(term, definition)
                } else {
                    logger.error("""
                                 Clause is not a tuple

                                 ## clause

                                 ```elixir
                                 ${inspect(term)}
                                 ```
                                 """.trimIndent())

                    null
                }

        private fun argumentsToString(arguments: OtpErlangList?): String =
                arguments?.joinToString(", ") { argument -> Macro.toString(argument) } ?: ""

        const val expectedArity = 4

        private fun from(tuple: OtpErlangTuple, definition: Definition): Clause? {
            val arity = tuple.arity()

            return if (arity == expectedArity) {
                // https://github.com/elixir-lang/elixir/blob/8c05bb078d715504a9d34343ad807275909474ed/lib/elixir/lib/exception.ex#L231
                val (metadata, arguments, guards, block) = tuple

                Clause(definition, metadata, arguments, guards, block)
            } else {
                logger.error("""
                             Clause arity (${arity}) is not ${expectedArity}

                             ## clause

                             ```elixir
                             ${inspect(tuple)}
                             ```
                             """.trimIndent())

                null
            }
        }

        private fun guardsToString(guards: OtpErlangList?): String =
                if (guards != null) {
                    when (guards.arity()) {
                        0 -> ""
                        else -> {
                            guards.joinToString(" or ", " when ", transform = { guard ->
                                Macro.toString(rewriteGuard(guard))
                            })
                        }
                    }
                } else {
                    " when ?"
                }

    }
}

private operator fun OtpErlangTuple.component1(): OtpErlangObject = this.elementAt(0)
private operator fun OtpErlangTuple.component2(): OtpErlangObject = this.elementAt(1)
private operator fun OtpErlangTuple.component3(): OtpErlangObject = this.elementAt(2)
private operator fun OtpErlangTuple.component4(): OtpErlangObject = this.elementAt(3)
