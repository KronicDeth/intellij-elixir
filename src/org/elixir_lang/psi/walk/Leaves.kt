package org.elixir_lang.psi.walk

import org.elixir_lang.psi.*

/**
 * Generated shapes that cannot hold an expression, so no walk meets one as an ancestor. Listed apart from each walk's
 * own buckets so the coverage test can tell "cannot declare" from "never considered".
 */
object Leaves {
    val SHAPES: List<Class<*>> = listOf(
        // An operator node holds only its tokens; the operands are its siblings.
        Operator::class.java,
        // Number literals: digit runs, whole numbers in every base, and the parts of a decimal float.
        Digits::class.java,
        WholeNumber::class.java,
        ElixirDecimalFloat::class.java,
        ElixirDecimalFloatIntegral::class.java,
        ElixirDecimalFloatFractional::class.java,
        ElixirDecimalFloatExponent::class.java,
        ElixirDecimalFloatExponentSign::class.java,
        // Escape sequences and their prefixes and terminators are token runs inside a string, char list or sigil.
        EscapeSequence::class.java,
        ElixirHexadecimalEscapePrefix::class.java,
        ElixirEscapedHeredocTerminator::class.java,
        ElixirEscapedLineTerminator::class.java,
        // The token-only parts of a heredoc or sigil; the parts that can hold an interpolation are in StringParts.
        ElixirHeredocPrefix::class.java,
        ElixirHeredocLinePrefix::class.java,
        ElixirSigilModifiers::class.java,
        ElixirCharToken::class.java,
        // Names and atoms are single tokens.
        ElixirAlias::class.java,
        ElixirAtom::class.java,
        ElixirAtomKeyword::class.java,
        ElixirAtIdentifier::class.java,
        ElixirIdentifier::class.java,
        ElixirRelativeIdentifier::class.java,
        ElixirBlockIdentifier::class.java,
        // Punctuation-only rules.
        ElixirEmptyParentheses::class.java,
        ElixirEndOfExpression::class.java
    )
}
