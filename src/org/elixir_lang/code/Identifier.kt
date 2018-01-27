package org.elixir_lang.code

import com.ericsson.otp.erlang.OtpErlangAtom
import com.ericsson.otp.erlang.OtpErlangObject
import org.elixir_lang.Atom

typealias Precedence = Int

// https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/code/identifier.ex
object Identifier {
    enum class Associativity {
        LEFT,
        NON_ASSOCIATIVE,
        RIGHT
    }


    data class AssociativityPrecedence(val associativity: Associativity, val precedence: Precedence)

    enum class Classification {
        ALIAS,
        CALLABLE_LOCAL,
        CALLABLE_OPERATOR,
        NOT_CALLABLE,
        OTHER
    }

    private val aliasRegex = Regex("Elixir(.[A-Z][a-zA-Z0-9_]*)+")
    // Not Unicode Identifier, based on Elixir.flex IDENTIFIER_TOKEN
    private val identifierRegex = Regex("[a-z_][a-zA-Z0-9_]*[?!]?")
    private val notCallableAtomValues = arrayOf("%", "%{}", "{}", "<<>>", "...", "..", ".", "->")

    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/code/identifier.ex#L23-L53
    fun binaryOperator(atom: OtpErlangAtom) = binaryOperator(atom.atomValue())

    fun binaryOperator(term: OtpErlangObject) =
        when (term) {
            is OtpErlangAtom -> binaryOperator(term)
            else -> null
        }

    fun binaryOperator(atomValue: String): AssociativityPrecedence? =
        when(atomValue) {
            "<-", "\\\\" -> AssociativityPrecedence(Associativity.LEFT, 40)
            "when" -> AssociativityPrecedence(Associativity.RIGHT, 50)
            "::" -> AssociativityPrecedence(Associativity.RIGHT, 60)
            "|" -> AssociativityPrecedence(Associativity.RIGHT, 70)
            "=" -> AssociativityPrecedence(Associativity.RIGHT, 100)
            "||", "|||", "or" -> AssociativityPrecedence(Associativity.LEFT, 130)
            "&&", "&&&", "and" -> AssociativityPrecedence(Associativity.LEFT, 140)
            "==", "!=", "=~", "===", "!==" -> AssociativityPrecedence(Associativity.LEFT, 150)
            "<", "<=", ">=", ">" -> AssociativityPrecedence(Associativity.LEFT, 160)
            "|>", "<<<", ">>>", "<~", "~>", "<<~", "~>>", "<~>", "<|>" -> AssociativityPrecedence(Associativity.LEFT, 170)
            "in" -> AssociativityPrecedence(Associativity.LEFT, 180)
            "^^^" -> AssociativityPrecedence(Associativity.LEFT, 190)
            "++", "--", "..", "<>" -> AssociativityPrecedence(Associativity.RIGHT, 200)
            "+", "-" -> AssociativityPrecedence(Associativity.LEFT, 210)
            "*", "/" -> AssociativityPrecedence(Associativity.LEFT, 220)
            "." -> AssociativityPrecedence(Associativity.LEFT, 310)
            else -> null
        }

    fun classify(atom: OtpErlangAtom): Classification = classify(atom.atomValue())

    fun classify(atomValue: String): Classification =
            when {
                atomValue in notCallableAtomValues ->
                    Classification.NOT_CALLABLE
                (unaryOperator(atomValue) ?: binaryOperator(atomValue)) != null ->
                    Classification.CALLABLE_OPERATOR
                isValidAlias(atomValue) ->
                    Classification.ALIAS
                isIdentifier(atomValue) ->
                    Classification.CALLABLE_LOCAL
                "@" !in atomValue ->
                    Classification.NOT_CALLABLE
                else ->
                    Classification.OTHER
            }

    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/code/identifier.ex#L168-L188
    fun inspectAsFunction(atom: OtpErlangAtom): String = inspectAsFunction(atom.atomValue())

    fun inspectAsFunction(string: String): String {
        val classification = classify(string)

        return if (classification in arrayOf(Classification.CALLABLE_LOCAL, Classification.CALLABLE_OPERATOR)) {
            string
        } else {
           val escaped = if (classification in arrayOf(Classification.ALIAS, Classification.NOT_CALLABLE)) {
               string
           } else {
               string.replace("\"", "\\\"")
           }

           "\"$escaped\""
        }
    }

    fun inspectAsKey(atom: OtpErlangAtom): String {
        val string = Atom.toString(atom)
        val classification = classify(atom)

        return if (classify(atom) in arrayOf(Classification.CALLABLE_LOCAL, Classification.CALLABLE_OPERATOR, Classification.NOT_CALLABLE)) {
            "$string:"
        } else {
            val escaped = string.replace("\"", "\\\"")

            "\"$escaped\":"
        }
    }

    fun isIdentifier(atomValue: String): Boolean = identifierRegex.matches(atomValue)

    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/code/identifier.ex#L104-L105
    fun isValidAlias(atomValue: String): Boolean = aliasRegex.matches(atomValue)

    // https://github.com/elixir-lang/elixir/blob/v1.6.0-rc.1/lib/elixir/lib/code/identifier.ex#L4-L21
    fun unaryOperator(atom: OtpErlangAtom) = unaryOperator(atom.atomValue())

    fun unaryOperator(term: OtpErlangObject) =
            when (term) {
                is OtpErlangAtom -> unaryOperator(term)
                else -> null
            }

    fun unaryOperator(atomValue: String): AssociativityPrecedence? =
        when (atomValue) {
            "&" -> AssociativityPrecedence(Associativity.NON_ASSOCIATIVE, 90)
            "!", "^", "not", "+", "-", "~~~" -> AssociativityPrecedence(Associativity.NON_ASSOCIATIVE, 300)
            "@" -> AssociativityPrecedence(Associativity.NON_ASSOCIATIVE, 320)
            else -> null
        }
}
