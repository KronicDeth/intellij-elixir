package org.elixir_lang.annotator

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.When
import org.elixir_lang.psi.walk.Classifier
import org.elixir_lang.psi.walk.Leaves
import org.elixir_lang.psi.walk.StringParts

/** The ancestors that decide whether an identifier is a parameter, for [Parameter.putParameterized]. */
object ParameterWalk {
    enum class Bucket {
        /** Holds the identifier without deciding, so ask the parent. */
        RECURSE,
        /** A call decides by whether it is a definition head. Every operation is a call, so this comes after them. */
        CALL,
        /** An anonymous function's head makes the identifier a variable-typed parameter. */
        ANONYMOUS_FUNCTION,
        /** Holds expressions, but none can be a parameter. */
        STOP,
        /** Cannot hold an expression at all, so also no parameter. */
        LEAF
    }

    val classifier = Classifier(
        listOf(
            Bucket.RECURSE to listOf(
                // before `Call`, which `When` also implements
                When::class.java,
                AtOperation::class.java,
                ElixirAccessExpression::class.java,
                ElixirAssociations::class.java,
                ElixirAssociationsBase::class.java,
                ElixirEex::class.java,
                ElixirEexTag::class.java,
                ElixirBitString::class.java,
                ElixirBracketArguments::class.java,
                ElixirContainerAssociationOperation::class.java,
                ElixirKeywordPair::class.java,
                ElixirKeywords::class.java,
                ElixirList::class.java,
                ElixirMapArguments::class.java,
                ElixirMapConstructionArguments::class.java,
                ElixirMapOperation::class.java,
                ElixirMatchedParenthesesArguments::class.java,
                ElixirNoParenthesesArguments::class.java,
                ElixirNoParenthesesKeywordPair::class.java,
                ElixirNoParenthesesKeywords::class.java,
                /* ElixirNoParenthesesManyStrictNoParenthesesExpression indicates a syntax error where no parentheses
                   calls are nested, so it's invalid, but try to still resolve parameters to have highlighting */
                ElixirNoParenthesesManyStrictNoParenthesesExpression::class.java,
                ElixirNoParenthesesOneArgument::class.java,
                /* handles `(conn, %{})` in `def (conn, %{})`, which can occur in def templates.
                   See https://github.com/KronicDeth/intellij-elixir/issues/367#issuecomment-244214975 */
                ElixirNoParenthesesStrict::class.java,
                ElixirParenthesesArguments::class.java,
                ElixirParentheticalStab::class.java,
                ElixirStab::class.java,
                ElixirStabNoParenthesesSignature::class.java,
                ElixirStabBody::class.java,
                ElixirStabOperation::class.java,
                ElixirStabParenthesesSignature::class.java,
                ElixirStructOperation::class.java,
                ElixirTuple::class.java
            ),
            Bucket.CALL to listOf(Call::class.java),
            Bucket.ANONYMOUS_FUNCTION to listOf(ElixirAnonymousFunction::class.java),
            Bucket.STOP to listOf(
                AtUnqualifiedBracketOperation::class.java,
                // `@1[key]` holds no parameters, like the other bracket operations
                AtNumericBracketOperation::class.java,
                BracketOperation::class.java,
                ElixirBlockItem::class.java,
                // A block list holds only block items, which stop the walk first
                ElixirBlockList::class.java,
                ElixirDoBlock::class.java,
                ElixirInterpolation::class.java,
                ElixirMapUpdateArguments::class.java,
                ElixirMultipleAliases::class.java,
                PsiFile::class.java,
                QualifiedAlias::class.java,
                QualifiedMultipleAliases::class.java
            ) +
                // a pattern cannot interpolate, so nothing in a string is a parameter
                StringParts.SHAPES,
            Bucket.LEAF to Leaves.SHAPES + listOf(
                // A keyword key is a bare atom
                ElixirKeywordKey::class.java,
                // An identifier with no call is a single token
                ElixirVariable::class.java
            )
        ),
        fallback = Bucket.STOP
    )

    @JvmStatic
    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
