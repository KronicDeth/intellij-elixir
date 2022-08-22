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
import org.elixir_lang.beam.decompiler.Options
import org.elixir_lang.beam.term.inspect
import org.elixir_lang.toOtpErlangList
import java.lang.StringBuilder


class Clause(
        val definition: Definition,
        metadata: OtpErlangObject,
        arguments: OtpErlangObject,
        guards: OtpErlangObject,
        val block: OtpErlangObject
) {
    val metadata: Keyword? = org.elixir_lang.beam.chunk.from(metadata)
    val arguments: OtpErlangList = arguments.toOtpErlangList()
    private val guards: OtpErlangList? = guards as? OtpErlangList
    val signature: String by lazy {
        val guardsString = guardsToString(this.guards)

        definition.macroNameArity?.let { macroNameArity ->
            decompiler("elixir", macroNameArity.toNameArity())?.let { decompiler ->
                val decompiled = StringBuilder()
                val argumentStrings = this.arguments.map { Macro.toString(it) }.toTypedArray()
                decompiler.appendSignature(decompiled, macroNameArity, macroNameArity.name, argumentStrings)
                decompiled.append(guardsString)

                decompiled.toString()
            }
        } ?: "${definition.name}(${argumentsToString(this.arguments)})${guardsString}"
    }

    fun toMacroString(options: Options): String? =
        definition.macro?.let { macro ->
            val prefix = "$macro $signature"

            if (options.decompileBodies) {
                try {
                    val blockMacroToString = Macro.toString(block)

                    if (options.truncateDecompiledBody(blockMacroToString)) {
                        "$prefix, do: ..."
                    } else {
                        "$prefix do\n${blockMacroToString.prependIndent("  ")}\nend"
                    }
                } catch (stackOverflowError: StackOverflowError) {
                    "$prefix, do: ..."
                }
            } else {
                "$prefix, do: ..."
            }
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
