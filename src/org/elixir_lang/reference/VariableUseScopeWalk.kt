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

/** The ancestors that decide a variable's use scope, for [Callable.variableUseScope]. */
object VariableUseScopeWalk {
    enum class Bucket {
        /** Holds the variable without scoping it, so ask the parent. */
        PARENT,
        /** The scope is this ancestor itself. */
        SELF,
        /** A match scopes by where it sits. Every operation is a call, so this comes before `CALL`. */
        MATCH,
        /** A call scopes by what it calls. */
        CALL,
        /** No variable can be declared inside these, so the use has no scope. */
        EMPTY,
        /** Cannot hold an expression at all, so also no scope. */
        LEAF
    }

    val classifier = Classifier(
        listOf(
            Bucket.PARENT to listOf(
                ElixirAccessExpression::class.java,
                ElixirAssociations::class.java,
                ElixirAssociationsBase::class.java,
                ElixirBitString::class.java,
                ElixirBlockItem::class.java,
                ElixirBlockList::class.java,
                ElixirContainerAssociationOperation::class.java,
                ElixirDoBlock::class.java,
                ElixirKeywordPair::class.java,
                ElixirKeywords::class.java,
                ElixirList::class.java,
                ElixirMapArguments::class.java,
                ElixirMapConstructionArguments::class.java,
                ElixirMapOperation::class.java,
                ElixirMatchedParenthesesArguments::class.java,
                ElixirNoParenthesesOneArgument::class.java,
                ElixirNoParenthesesArguments::class.java,
                ElixirNoParenthesesKeywordPair::class.java,
                ElixirNoParenthesesKeywords::class.java,
                ElixirParenthesesArguments::class.java,
                ElixirParentheticalStab::class.java,
                ElixirStab::class.java,
                ElixirStabBody::class.java,
                ElixirStabNoParenthesesSignature::class.java,
                ElixirStabParenthesesSignature::class.java,
                ElixirStructOperation::class.java,
                ElixirTuple::class.java,
                InMatch::class.java,
                Type::class.java,
                UnqualifiedNoArgumentsCall::class.java
            ),
            Bucket.SELF to listOf(
                ElixirStabOperation::class.java,
                QualifiedAlias::class.java
            ),
            Bucket.MATCH to listOf(Match::class.java),
            Bucket.CALL to listOf(Call::class.java),
            Bucket.EMPTY to listOf(
                ElixirMapUpdateArguments::class.java,
                ElixirEexTag::class.java,
                ElixirInterpolation::class.java,
                // the walk passed no scope that could declare the variable, as for a match outside any block
                PsiFile::class.java,
                // A lookup, module attribute or alias list binds nothing
                AtOperation::class.java,
                AtUnqualifiedBracketOperation::class.java,
                AtNumericBracketOperation::class.java,
                BracketOperation::class.java,
                QualifiedMultipleAliases::class.java,
                ElixirBracketArguments::class.java,
                ElixirMultipleAliases::class.java,
                // Syntax errors met while typing: `fn` without `->`, nested no-parentheses calls, `def (a, b)`
                ElixirAnonymousFunction::class.java,
                ElixirNoParenthesesManyStrictNoParenthesesExpression::class.java,
                ElixirNoParenthesesStrict::class.java,
                // An EEx template is scoped by its tags, answered above
                ElixirEex::class.java
            ),
            Bucket.LEAF to Leaves.SHAPES + listOf(
                // A keyword key is a bare atom
                ElixirKeywordKey::class.java,
                // An identifier with no call is a single token
                ElixirVariable::class.java
            )
        ),
        fallback = Bucket.EMPTY
    )

    fun classify(ancestor: PsiElement): Bucket = classifier.classify(ancestor)
}
