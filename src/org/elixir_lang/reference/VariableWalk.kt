package org.elixir_lang.reference

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.InMatch
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.walk.Classifier
import org.elixir_lang.psi.walk.Leaves

/** The ancestors that decide whether an identifier is a variable, for [Callable.isVariable]. */
object VariableWalk {
    enum class Bucket {
        /** The identifier is bound here, so the walk ends with `true`. */
        DECLARES,
        /** Holds expressions without binding anything itself, so ask the parent. */
        TRANSPARENT,
        /** A call decides by what it calls. Every operation is a call, so this comes after them. */
        CALL,
        /** Holds expressions, but no declaration reaches through it, so the walk ends with `false`. */
        STOP,
        /** Cannot hold an expression at all, so also `false`. */
        LEAF
    }

    val classifier = Classifier(
        listOf(
            Bucket.DECLARES to listOf(
                ElixirInterpolation::class.java,
                // bound quoted variable name in `quote bind_quoted: [name: value] do ... end`
                ElixirKeywordKey::class.java,
                ElixirStabNoParenthesesSignature::class.java,
                /* if a StabOperation is encountered before ElixirStabNoParenthesesSignature or
                   ElixirStabParenthesesSignature, then must have come from body */
                ElixirStabOperation::class.java,
                ElixirStabParenthesesSignature::class.java,
                InMatch::class.java,
                Match::class.java
            ),
            Bucket.TRANSPARENT to listOf(
                ElixirAccessExpression::class.java,
                /* an anonymous function is only reached when its stab has no `->`, which is a syntax error,
                   but it can also occur during typing, so try searching above it */
                ElixirAnonymousFunction::class.java,
                ElixirAssociations::class.java,
                ElixirAssociationsBase::class.java,
                ElixirBitString::class.java,
                ElixirBlockItem::class.java,
                ElixirBlockList::class.java,
                ElixirBracketArguments::class.java,
                ElixirContainerAssociationOperation::class.java,
                ElixirDoBlock::class.java,
                ElixirEex::class.java,
                ElixirEexTag::class.java,
                ElixirKeywordPair::class.java,
                ElixirKeywords::class.java,
                ElixirList::class.java,
                ElixirMapArguments::class.java,
                ElixirMapConstructionArguments::class.java,
                ElixirMapOperation::class.java,
                ElixirMapUpdateArguments::class.java,
                /* parenthesesArguments can be used in @spec other type declarations, so may not be variable
                   until ancestor call is checked */
                ElixirMatchedParenthesesArguments::class.java,
                /* Happens when tuple is after `MyAlias.` when add qualified call above line with pre-existing
                   tuple */
                ElixirMultipleAliases::class.java,
                ElixirNoParenthesesOneArgument::class.java,
                ElixirNoParenthesesArguments::class.java,
                ElixirNoParenthesesKeywordPair::class.java,
                ElixirNoParenthesesKeywords::class.java,
                /* ElixirNoParenthesesManyStrictNoParenthesesExpression and ElixirNoParenthesesStrict indicates
                   a syntax error, but it can also occur during typing, so try searching above the syntax error
                   to resolve whether a variable */
                ElixirNoParenthesesManyStrictNoParenthesesExpression::class.java,
                ElixirNoParenthesesStrict::class.java,
                ElixirParenthesesArguments::class.java,
                ElixirParentheticalStab::class.java,
                ElixirStab::class.java,
                ElixirStabBody::class.java,
                ElixirStructOperation::class.java,
                ElixirTuple::class.java,
                ElixirVariable::class.java,
                Type::class.java
            ),
            Bucket.CALL to listOf(Call::class.java),
            Bucket.STOP to listOf(
                AtUnqualifiedBracketOperation::class.java,
                // `@1[key]` binds nothing, like the other bracket operations
                AtNumericBracketOperation::class.java,
                AtOperation::class.java,
                BracketOperation::class.java,
                QualifiedMultipleAliases::class.java,
                // an alias must expand to an atom at compile time, so its qualifier is never a variable
                QualifiedAlias::class.java,
                // the walk passed no scope that could declare the variable
                PsiFile::class.java
            ),
            Bucket.LEAF to Leaves.SHAPES
        ),
        fallback = Bucket.STOP
    )

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
