package org.elixir_lang.beam.decompiler

import org.elixir_lang.NameArity
import org.intellij.lang.annotations.Language
import java.lang.StringBuilder
import java.util.function.Predicate
import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.stream.Stream

object Unquoted : Default() {
    private val keywordStream = Stream.of(
            // When not `and/2`
            "and",
            "do",
            "end",
            "false",
            // When not `in/2`
            "in",
            "nil",
            // When not `or/2`
            "or",
            "true"
    )
    private val specialFormStream = Stream.of(
            "!",
            "%",
            "%{}",
            "&",
            ".",
            "<<>>",
            "@",
            "fn",
            "unquote",
            "unquote_splicing",
            "{}"
    )

    private val fixed = Stream
            .concat(keywordStream, specialFormStream)
            .map(Pattern::quote)
            .collect(Collectors.joining("|"))

    // See Elixir.flex IDENTIFIER_TOKEN_TAIL
    @Language("RegExp") val identifierTokenTail = "[0-9a-zA-Z_]*[?!]?"
    // See Elixir.flex ALIAS
    @Language("RegExp") val alias = "[A-Z]$identifierTokenTail"
    private val bareAtomPredicate = Pattern
            .compile("^($fixed|$alias)$")
            .asPredicate()
    private const val prefixOperators = "[@!]|~~~"
    // See Elixir.flex IDENTIFIER_TOKEN
    private val notIdentifierOrPrefixOperatorPredicate = Pattern.compile(
            "^([a-z_]$identifierTokenTail|$prefixOperators)$"
    ).asPredicate().negate()

    /**
     * Wehther the decompiler accepts the `macroNameArity`.
     *
     * @return `true` if [.append] should be called with
     * `macroNameArity`.
     */
    override fun accept(beamLanguage: String, nameArity: NameArity): Boolean {
        val name = nameArity.name

        return when (beamLanguage) {
            "elixir" ->
                bareAtomPredicate.or(notIdentifierOrPrefixOperatorPredicate).test(name)
            else -> {
                bareAtomPredicate.or(notIdentifierOrPrefixOperatorPredicate).test(name) ||
                        PrefixOperator.isPrefixOperator(name) ||
                        InfixOperator.isInfixOperator(name)
            }
        }
    }

    override fun appendName(decompiled: StringBuilder, name: String) {
        decompiled
                .append("unquote(:")
                .append(macroNameToAtomName(name))
                .append(")")
    }

    private fun macroNameToAtomName(macroName: String): StringBuilder {
        val atomName = StringBuilder()
        if (bareAtomPredicate.test(macroName)) {
            atomName.append(macroName)
        } else {
            atomName.append('"').append(macroName).append('"')
        }
        return atomName
    }
}
