package org.elixir_lang.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.walk.Classifier
import org.elixir_lang.psi.walk.Leaves

/** The parents of an unquoted variable that lead to the value it carries, for [Unquote]. */
object UnquotedVariableWalk {
    enum class Bucket {
        /** `variable = value` binds the variable, so follow the value. A match is a call, so this comes first. */
        MATCH,
        /** Nothing to follow. Parentheses arguments are `QuotableArguments`, so this comes before `RECURSE`. */
        STOP,
        /** A container a variable can be declared through, which this walk does not enter yet. Answers as `STOP`. */
        UNFOLLOWED,
        /** A wrapper the value passes through, so ask the parent. */
        RECURSE,
        /** Cannot hold an expression at all, so nothing is bound through it. */
        LEAF
    }

    val classifier = Classifier(
        listOf(
            Bucket.MATCH to listOf(Match::class.java),
            Bucket.STOP to listOf(
                // `...: variable`, such as `do: block` in macro parameters
                ElixirKeywordPair::class.java,
                // `(..., parameter)`
                ElixirParenthesesArguments::class.java,
                ElixirInterpolation::class.java,
                // nothing above the file binds
                PsiFile::class.java,
                // no variable is declared through a lookup, module attribute or alias list
                AtOperation::class.java,
                AtUnqualifiedBracketOperation::class.java,
                AtNumericBracketOperation::class.java,
                BracketOperation::class.java,
                QualifiedMultipleAliases::class.java,
                QualifiedAlias::class.java
            ),
            Bucket.UNFOLLOWED to listOf(
                ElixirAnonymousFunction::class.java,
                ElixirAssociations::class.java,
                ElixirAssociationsBase::class.java,
                ElixirBitString::class.java,
                ElixirBlockItem::class.java,
                ElixirBracketArguments::class.java,
                ElixirContainerAssociationOperation::class.java,
                ElixirEex::class.java,
                ElixirEexTag::class.java,
                ElixirList::class.java,
                ElixirMapArguments::class.java,
                ElixirMapOperation::class.java,
                ElixirMapUpdateArguments::class.java,
                ElixirMatchedParenthesesArguments::class.java,
                ElixirMultipleAliases::class.java,
                ElixirNoParenthesesKeywordPair::class.java,
                ElixirNoParenthesesManyStrictNoParenthesesExpression::class.java,
                ElixirParentheticalStab::class.java,
                ElixirStabNoParenthesesSignature::class.java,
                ElixirStabOperation::class.java,
                ElixirStabParenthesesSignature::class.java,
                ElixirStructOperation::class.java
            ),
            Bucket.RECURSE to listOf(
                ElixirTuple::class.java,
                ElixirAccessExpression::class.java,
                ElixirStabBody::class.java,
                ElixirStab::class.java,
                QuotableArguments::class.java,
                QuotableKeywordList::class.java,
                Call::class.java
            ),
            Bucket.LEAF to Leaves.SHAPES + listOf(
                // A keyword key is a bare atom
                ElixirKeywordKey::class.java,
                // An identifier with no call is a single token
                ElixirVariable::class.java
            )
        ),
        fallback = Bucket.STOP
    )

    fun classify(parent: PsiElement): Bucket = classifier.classify(parent)
}
