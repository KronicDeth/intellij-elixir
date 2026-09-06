package org.elixir_lang.psi.walk

import com.intellij.psi.PsiFile
import org.elixir_lang.annotator.ParameterWalk
import org.elixir_lang.psi.*
import org.elixir_lang.psi.UnquotedVariableWalk
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.scope.TypeAscent
import org.elixir_lang.psi.scope.TypeDescent
import org.elixir_lang.psi.scope.VariableDescent
import org.elixir_lang.reference.VariableUseScopeWalk
import org.elixir_lang.reference.VariableWalk
import org.elixir_lang.beam.psi.CallDefinition as BeamCallDefinition
import org.elixir_lang.beam.psi.Module as BeamModule
import org.elixir_lang.beam.psi.TypeDefinition as BeamTypeDefinition

/**
 * Every shape the walks meet, one row each, with the bucket each walk files it in. A walk that does not name a
 * shape leaves its column null and the shape is decided by a wider row below it, so rows are ordered with the
 * narrower interface before the wider one it extends: every operation before `Call`, every argument list before
 * `QuotableArguments`. A new grammar rule is one new row; the coverage test says which walks still need a value, but
 * not which value: a row that files a real container as a stop in every column passes and silently loses what is
 * bound inside it, so a new row is reviewed against Elixir, not against the tests.
 */
object ShapeTable {
    class Row(
        val shape: Class<*>,
        val variable: VariableWalk.Bucket? = null,
        val useScope: VariableUseScopeWalk.Bucket? = null,
        val parameter: ParameterWalk.Bucket? = null,
        val unquote: UnquotedVariableWalk.Bucket? = null,
        val descent: VariableDescent.Bucket? = null,
        val typeDescent: TypeDescent.Bucket? = null,
        val typeAscent: TypeAscent.Bucket? = null
    )

    val ROWS: List<Row> = listOf(
        // shapes that bind, and the clauses they bind for
        Row(
            ElixirStabNoParenthesesSignature::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STAB_NO_PARENTHESES_SIGNATURE,
            typeDescent = TypeDescent.Bucket.STAB_NO_PARENTHESES_SIGNATURE,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirStabOperation::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.SELF,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STAB_OPERATION,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirStabParenthesesSignature::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STAB_PARENTHESES_SIGNATURE,
            typeDescent = TypeDescent.Bucket.STAB_PARENTHESES_SIGNATURE,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            InMatch::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            descent = VariableDescent.Bucket.IN_MATCH,
        ),
        Row(
            Match::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.MATCH,
            unquote = UnquotedVariableWalk.Bucket.MATCH,
            descent = VariableDescent.Bucket.MATCH,
        ),
        // reached only while typing, before its `->`; a body is not visible outside it
        Row(
            ElixirAnonymousFunction::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.ANONYMOUS_FUNCTION,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            Type::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            descent = VariableDescent.Bucket.TYPE,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(When::class.java, parameter = ParameterWalk.Bucket.RECURSE),
        Row(ElixirMatchedWhenOperation::class.java, descent = VariableDescent.Bucket.WHEN),
        Row(In::class.java, descent = VariableDescent.Bucket.IN),

        // operations and lookups
        Row(
            ElixirBracketArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        // a match inside `m[...]` binds afterwards, so the brackets are read; the receiver is a value
        Row(
            BracketOperation::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.BRACKET,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            AtUnqualifiedBracketOperation::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.AT_BRACKET,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        // `@1[key]`, a lookup on a number, binds nothing
        Row(
            AtNumericBracketOperation::class.java,
            variable = VariableWalk.Bucket.STOP,
            useScope = VariableUseScopeWalk.Bucket.EMPTY,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            AtOperation::class.java,
            variable = VariableWalk.Bucket.STOP,
            useScope = VariableUseScopeWalk.Bucket.EMPTY,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.AT_OPERATION,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(Addition::class.java, descent = VariableDescent.Bucket.NON_DECLARING_INFIX),
        Row(And::class.java, descent = VariableDescent.Bucket.NON_DECLARING_INFIX),
        Row(Pipe::class.java, descent = VariableDescent.Bucket.INFIX, typeAscent = TypeAscent.Bucket.PARENT),
        Row(Ternary::class.java, descent = VariableDescent.Bucket.INFIX),
        Row(Two::class.java, descent = VariableDescent.Bucket.INFIX),
        Row(UnaryOperation::class.java, descent = VariableDescent.Bucket.UNARY),

        // calls
        Row(
            UnqualifiedNoArgumentsCall::class.java,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            descent = VariableDescent.Bucket.MAYBE_VARIABLE,
        ),
        Row(BeamModule::class.java, typeDescent = TypeDescent.Bucket.BEAM_MODULE),
        Row(BeamTypeDefinition::class.java, typeDescent = TypeDescent.Bucket.BEAM_TYPE_DEFINITION),
        Row(BeamCallDefinition::class.java, typeDescent = TypeDescent.Bucket.BEAM_CALL_DEFINITION),
        Row(AtUnqualifiedNoParenthesesCall::class.java, typeAscent = TypeAscent.Bucket.SPEC),
        Row(
            Call::class.java,
            variable = VariableWalk.Bucket.CALL,
            useScope = VariableUseScopeWalk.Bucket.CALL,
            parameter = ParameterWalk.Bucket.CALL,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CALL,
            typeDescent = TypeDescent.Bucket.CALL,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),

        // containers, arguments and blocks
        // every bare identifier inside `#{}` is a variable; a match inside is not scoped yet
        Row(
            ElixirInterpolation::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.EMPTY,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirAccessExpression::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.CHILDREN,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirAssociations::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirAssociationsBase::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirBitString::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirBlockItem::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirBlockList::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirContainerAssociationOperation::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CONTAINER_ASSOCIATION,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirDoBlock::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirEex::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        // a tag's variables are not searched above the tag
        Row(
            ElixirEexTag::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.EMPTY,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        // `do: block` binds nothing further up for an unquote; read through its list by the descents
        Row(
            ElixirKeywordPair::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirKeywords::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirList::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.HALT,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirMapArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.MAP_ARGUMENTS,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirMapConstructionArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirMapOperation::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.MAP_OPERATION,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        // an update is a value, read through its map arguments; a match inside binds afterwards
        Row(
            ElixirMapUpdateArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.NONE,
        ),
        Row(
            ElixirMatchedParenthesesArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirNoParenthesesOneArgument::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.CHILDREN,
        ),
        Row(
            ElixirNoParenthesesArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirNoParenthesesKeywordPair::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirNoParenthesesKeywords::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirNoParenthesesManyStrictNoParenthesesExpression::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        // syntax errors met while typing; the ascents look above them, the descents do not enter
        Row(
            ElixirNoParenthesesStrict::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PASS,
        ),
        Row(
            ElixirParenthesesArguments::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
        ),
        Row(
            ElixirParentheticalStab::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.HALT,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirStab::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirStabBody::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirStructOperation::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.STRUCT_OPERATION,
            typeDescent = TypeDescent.Bucket.HALT,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(
            ElixirTuple::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.RECURSE,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.HALT,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        Row(QuotableArguments::class.java, unquote = UnquotedVariableWalk.Bucket.RECURSE),
        Row(
            QuotableKeywordList::class.java,
            unquote = UnquotedVariableWalk.Bucket.RECURSE,
            descent = VariableDescent.Bucket.KEYWORD_LIST,
        ),
        Row(ElixirLine::class.java, typeDescent = TypeDescent.Bucket.HALT),
        Row(Arguments::class.java, typeAscent = TypeAscent.Bucket.PARENT),

        // aliases
        // the tuple after `Qualifier.` while typing
        Row(
            ElixirMultipleAliases::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.UNFOLLOWED,
            descent = VariableDescent.Bucket.CHILDREN,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        // `Qualifier.` typed above a tuple pattern; the resolver declares through it
        Row(
            QualifiedMultipleAliases::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.PARENT,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.QUALIFIED_MULTIPLE_ALIASES,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.PARENT,
        ),
        // an alias must expand to an atom at compile time, so its qualifier is never a variable
        Row(
            QualifiedAlias::class.java,
            variable = VariableWalk.Bucket.STOP,
            useScope = VariableUseScopeWalk.Bucket.EMPTY,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.STOP,
        ),
        leaf(ElixirAlias::class.java, typeDescent = null, typeAscent = null),
        Row(QualifiableAlias::class.java, typeDescent = TypeDescent.Bucket.HALT, typeAscent = TypeAscent.Bucket.NONE),

        // token-only shapes, met only as the element a walk starts from or never at all
        // the key `isVariable` starts from for `bind_quoted`; a bare atom to every other walk
        Row(
            ElixirKeywordKey::class.java,
            variable = VariableWalk.Bucket.DECLARES,
            useScope = VariableUseScopeWalk.Bucket.LEAF,
            parameter = ParameterWalk.Bucket.LEAF,
            unquote = UnquotedVariableWalk.Bucket.LEAF,
            descent = VariableDescent.Bucket.STOP,
            typeDescent = TypeDescent.Bucket.PARAMETER,
            typeAscent = TypeAscent.Bucket.LEAF,
        ),
        // the token `isVariable` starts from; never an ancestor
        Row(
            ElixirVariable::class.java,
            variable = VariableWalk.Bucket.TRANSPARENT,
            useScope = VariableUseScopeWalk.Bucket.LEAF,
            parameter = ParameterWalk.Bucket.LEAF,
            unquote = UnquotedVariableWalk.Bucket.LEAF,
            descent = VariableDescent.Bucket.VARIABLE,
            typeDescent = TypeDescent.Bucket.PASS,
            typeAscent = TypeAscent.Bucket.LEAF,
        ),
        leaf(Operator::class.java),
        leaf(Digits::class.java),
        leaf(WholeNumber::class.java, typeDescent = TypeDescent.Bucket.HALT),
        leaf(ElixirDecimalFloat::class.java),
        leaf(ElixirDecimalFloatIntegral::class.java),
        leaf(ElixirDecimalFloatFractional::class.java),
        leaf(ElixirDecimalFloatExponent::class.java),
        leaf(ElixirDecimalFloatExponentSign::class.java),
        leaf(EscapeSequence::class.java),
        leaf(ElixirHexadecimalEscapePrefix::class.java),
        leaf(ElixirEscapedHeredocTerminator::class.java),
        leaf(ElixirEscapedLineTerminator::class.java),
        leaf(Body::class.java),
        leaf(Line::class.java),
        leaf(HeredocLineable::class.java),
        leaf(HeredocLiteral::class.java),
        leaf(ElixirHeredocPrefix::class.java),
        leaf(ElixirHeredocLinePrefix::class.java),
        leaf(ElixirSigilModifiers::class.java),
        leaf(ElixirCharToken::class.java),
        leaf(ElixirAtom::class.java, typeDescent = TypeDescent.Bucket.HALT),
        leaf(ElixirAtomKeyword::class.java),
        leaf(ElixirAtIdentifier::class.java),
        leaf(ElixirIdentifier::class.java),
        leaf(ElixirRelativeIdentifier::class.java),
        leaf(ElixirBlockIdentifier::class.java),
        leaf(ElixirEmptyParentheses::class.java),
        leaf(ElixirEndOfExpression::class.java),

        // the file
        Row(
            PsiFile::class.java,
            variable = VariableWalk.Bucket.STOP,
            useScope = VariableUseScopeWalk.Bucket.FOLLOWING,
            parameter = ParameterWalk.Bucket.STOP,
            unquote = UnquotedVariableWalk.Bucket.STOP,
            descent = VariableDescent.Bucket.FILE,
        ),
        Row(ElixirFile::class.java, typeDescent = TypeDescent.Bucket.HALT, typeAscent = TypeAscent.Bucket.NONE)
    )

    /** A token-only shape: a leaf to every walk unless the type walks say otherwise. */
    private fun leaf(
        shape: Class<*>,
        typeDescent: TypeDescent.Bucket? = TypeDescent.Bucket.LEAF,
        typeAscent: TypeAscent.Bucket? = TypeAscent.Bucket.LEAF
    ) = Row(
        shape,
        variable = VariableWalk.Bucket.LEAF,
        useScope = VariableUseScopeWalk.Bucket.LEAF,
        parameter = ParameterWalk.Bucket.LEAF,
        unquote = UnquotedVariableWalk.Bucket.LEAF,
        descent = VariableDescent.Bucket.LEAF,
        typeDescent = typeDescent,
        typeAscent = typeAscent
    )

    /** A walk's classifier: the rows that name a bucket in its column, in table order. */
    fun <B : Enum<B>> column(fallback: B, pick: (Row) -> B?): Classifier<B> =
        Classifier(ROWS.mapNotNull { row -> pick(row)?.let { Classifier.Entry(row.shape, it) } }, fallback)
}
