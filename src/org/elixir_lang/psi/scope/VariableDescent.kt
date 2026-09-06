package org.elixir_lang.psi.scope

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.walk.ShapeTable

/**
 * The shapes the variable resolver descends into looking for declarations, for [Variable.execute]. Each bucket names
 * the overload that reads the shape and, in [Bucket.reads], the type that overload casts to, `PsiElement` where it
 * does not cast, so a test can check that every shape in a bucket is one its arm accepts.
 */
object VariableDescent {
    enum class Bucket(val reads: Class<*>) {
        /** An infix operation whose operands can never declare, so the descent continues with declaring off. */
        NON_DECLARING_INFIX(Infix::class.java),
        /** A container whose children are read in turn. */
        CHILDREN(PsiElement::class.java),
        /** Bracket access: the arguments are read, the receiver is a value. */
        BRACKET(BracketOperation::class.java),
        AT_BRACKET(AtUnqualifiedBracketOperation::class.java),
        /** `key => value`: only the value can declare. */
        CONTAINER_ASSOCIATION(ElixirContainerAssociationOperation::class.java),
        /** Map arguments: construction may be a pattern, an update is a value. */
        MAP_ARGUMENTS(ElixirMapArguments::class.java),
        MAP_OPERATION(ElixirMapOperation::class.java),
        /** `pattern when guard`: only the pattern declares. */
        WHEN(ElixirMatchedWhenOperation::class.java),
        STAB_OPERATION(ElixirStabOperation::class.java),
        STAB_NO_PARENTHESES_SIGNATURE(ElixirStabNoParenthesesSignature::class.java),
        STAB_PARENTHESES_SIGNATURE(ElixirStabParenthesesSignature::class.java),
        STRUCT_OPERATION(ElixirStructOperation::class.java),
        /** The token that is itself a variable. */
        VARIABLE(ElixirVariable::class.java),
        /** `rescue e in Error`: the left operand declares. */
        IN(In::class.java),
        /** `\\` and `<-`. Every operation is a call, so this and the others below come before `CALL`. */
        IN_MATCH(InMatch::class.java),
        MATCH(Match::class.java),
        /** An infix operation either side of which can declare in a match. */
        INFIX(Infix::class.java),
        TYPE(Type::class.java),
        /** A pin declares nothing; any other prefix operation is read as a call. */
        UNARY(UnaryOperation::class.java),
        /** A no-arguments call is a variable unless it is really a piped arity-1 call. */
        MAYBE_VARIABLE(UnqualifiedNoArgumentsCall::class.java),
        /** A call decides by what it is: a definition head, `quote`, a macro with a `do` block, `use`, Ecto, ExUnit. */
        CALL(Call::class.java),
        /** `Qualifier.` typed above a tuple pattern: the tuple is read as the pattern. */
        QUALIFIED_MULTIPLE_ALIASES(QualifiedMultipleAliases::class.java),
        /** A keyword list: each pair's value is read. */
        KEYWORD_LIST(QuotableKeywordList::class.java),
        /** The file: nothing above it to search. */
        FILE(PsiFile::class.java),
        /** Holds expressions, but a declaration inside is not visible from outside, so the descent passes over it. */
        STOP(PsiElement::class.java),
        /** Cannot hold an expression at all, so also passed over. */
        LEAF(PsiElement::class.java)
    }

    val classifier = ShapeTable.column(Bucket.STOP) { it.descent }

    fun classify(element: PsiElement): Bucket = classifier.classify(element)
}
