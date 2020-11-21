@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package org.elixir_lang.psi.impl

import com.ericsson.otp.erlang.*
import com.intellij.lang.ASTNode
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.util.Computable
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.Factory
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.TokenSet
import org.elixir_lang.ElixirLanguage
import org.elixir_lang.Level
import org.elixir_lang.Level.V_1_3
import org.elixir_lang.Level.V_1_6
import org.elixir_lang.Macro
import org.elixir_lang.mix.project.computeReadAction
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.prependElixirPrefix
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.IDENTIFIER_TOKEN_SET
import org.elixir_lang.psi.impl.ParentImpl.addChildTextCodePoints
import org.elixir_lang.psi.impl.ParentImpl.elixirCharList
import org.elixir_lang.psi.impl.ParentImpl.elixirString
import org.elixir_lang.psi.operation.In
import org.elixir_lang.psi.operation.Infix
import org.elixir_lang.psi.operation.NotIn
import org.elixir_lang.psi.operation.Prefix
import org.elixir_lang.sdk.elixir.Type.getNonNullRelease
import org.jetbrains.annotations.Contract
import java.lang.Double
import java.lang.Long
import java.math.BigInteger
import java.util.*

val UNQUOTED_TYPES = arrayOf<Class<*>>(ElixirEndOfExpression::class.java, PsiComment::class.java, PsiWhiteSpace::class.java)

/**
 * @return {@code true} if {@code element} should not have {@code quote} called on it because Elixir natively
 *   ignores such tokens.  {@code false} if {@code element} should have {@code quote} called on it.
 */
fun PsiElement.isUnquoted(): Boolean {
    var unquoted = false

    for (unquotedType in UNQUOTED_TYPES) {
        if (unquotedType.isInstance(this)) {
            unquoted = true
            break
        }
    }

    return unquoted
}

fun ElixirStab.quote(metadata: OtpErlangList): OtpErlangObject =
        stabBody?.quote(metadata) ?:
        stabOperationList.map(Quotable::quote).toTypedArray().let(::OtpErlangList)

fun ElixirStabBody.quote(metadata: OtpErlangList): OtpErlangObject =
        children
                .asSequence()
                .filterNot {
                    // skip endOfExpression
                    it.isUnquoted()
                }
                .map { it as Quotable }
                .map { it.quote() }
                .let { QuotableImpl.buildBlock(it.toList(), metadata) }

object QuotableImpl {
    private val AMBIGUOUS_OP = OtpErlangAtom("ambiguous_op")
    @JvmField
    val NIL = OtpErlangAtom("nil")
    private val AMBIGUOUS_OP_KEYWORD_PAIR = OtpErlangTuple(
            arrayOf<OtpErlangObject>(AMBIGUOUS_OP, NIL)
    )
    private val ALIASES = OtpErlangAtom("__aliases__")
    internal val BLOCK = OtpErlangAtom("__block__")
    private val FN = OtpErlangAtom("fn")
    private val EXCLAMATION_POINT = OtpErlangAtom("!")
    private val MINUS = OtpErlangAtom("-")
    private val MULTIPLE_ALIASES = OtpErlangAtom("{}")
    private val NOT = OtpErlangAtom("not")
    private val PLUS = OtpErlangAtom("+")
    private val REARRANGED_UNARY_OPERATORS = arrayOf(EXCLAMATION_POINT, NOT)
    private val UNQUOTE_SPLICING = OtpErlangAtom("unquote_splicing")
    private val WHEN = OtpErlangAtom("when")

    @Contract(pure = true)
    @JvmStatic
    fun quote(associationOperation: AssociationOperation): OtpErlangObject {
        val children = associationOperation.children

        // associationInfixOperator is private so not a PsiElement
        assert(children.size == 2)

        return OtpErlangTuple(children.map { it as Quotable }.map(Quotable::quote).toTypedArray())
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(infix: Infix): OtpErlangObject {
        val quotedLeftOperand = infix.leftOperand()!!.quote()

        val operator = infix.operator()
        val quotedOperator = operator.quote()

        val quotedRightOperand = infix.rightOperand()?.quote() ?:
        // this is not valid Elixir quoting, but something needs to be there for quoting to work
        NIL

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedLeftOperand,
                quotedRightOperand
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(notIn: NotIn): OtpErlangObject {
        val quotedLeftOperand = notIn.leftOperand()!!.quote()

        val notOperator = notIn.notInfixOperator
        val quotedNotOperator = notOperator.quote()

        val inOperator = notIn.inInfixOperator
        val quotedInOperator = inOperator.quote()

        val quotedRightOperand = notIn.rightOperand()?.quote() ?:
        // this is not valid Elixir quoting, but something needs to be there for quoting to work
        NIL

        return quotedFunctionCall(
                quotedNotOperator,
                metadata(notOperator),
                quotedFunctionCall(
                        quotedInOperator,
                        metadata(inOperator),
                        quotedLeftOperand,
                        quotedRightOperand
                )
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(bitString: ElixirBitString): OtpErlangObject {
        val openingBits = bitString.node.getChildren(TokenSet.create(ElixirTypes.OPENING_BIT))

        assert(openingBits.size == 1)

        val openingBit = openingBits[0]

        return quotedFunctionCall(
                "<<>>",
                metadata(openingBit),
                *bitString.children.map { it as Quotable }.map { it.quote() }.toTypedArray()
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(blockIdentifier: ElixirBlockIdentifier): OtpErlangObject = OtpErlangAtom(blockIdentifier.node.text)

    @Contract(pure = true)
    @JvmStatic
    fun quote(blockItem: ElixirBlockItem): OtpErlangObject {
        val blockIdentifier = blockItem.blockIdentifier
        val quotedValue = blockItem.stab?.quote() ?:
                getNonNullRelease(blockItem).level().let { level -> emptyBlock(level) }

        return OtpErlangTuple(
                arrayOf(blockIdentifier.quote(), quotedValue)
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(bracketArguments: ElixirBracketArguments): OtpErlangObject {
        val children = bracketArguments.children

        assert(children.size == 1)

        return (children[0] as Quotable).quote()
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(digits: Digits): OtpErlangObject {
        val text = digits.text
        val base = digits.base()

        return try {
            val value = Long.parseLong(text, base)
            OtpErlangLong(value)
        } catch (exception: NumberFormatException) {
            val value = BigInteger(text, base)
            OtpErlangLong(value)
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(accessExpression: ElixirAccessExpression): OtpErlangObject {
        val children = accessExpression.children

        if (children.size != 1) {
            throw TODO("Expecting 1 child in accessExpression")
        }

        return (children[0] as Quotable).quote()
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(alias: ElixirAlias): OtpErlangObject =
            quotedFunctionCall(
                    ALIASES,
                    metadata(alias, 0),
                    OtpErlangAtom(alias.text)
            )

    @Contract(pure = true)
    @JvmStatic
    fun quote(anonymousFunction: ElixirAnonymousFunction): OtpErlangObject {
        val metadata = metadata(anonymousFunction)
        val quotedStab = anonymousFunction.stab.quote()

        return OtpErlangTuple(
                arrayOf(FN, metadata, quotedStab)
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(associations: ElixirAssociations): OtpErlangObject = associations.associationsBase.quote()

    @Contract(pure = true)
    @JvmStatic
    fun quote(associationsBase: ElixirAssociationsBase): OtpErlangObject {
        return OtpErlangList(associationsBase.children.map { it as Quotable }.map(Quotable::quote).toTypedArray())
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(atom: ElixirAtom): OtpErlangObject =
            atom.charListLine?.quoteAsAtom() ?: atom.stringLine?.quoteAsAtom() ?: atom.node.let { atomNode ->
                val atomFragmentNode = atomNode.lastChildNode

                assert(atomFragmentNode.elementType === ElixirTypes.ATOM_FRAGMENT)

                OtpErlangAtom(atomFragmentNode.text)
            }

    @Contract(pure = true)
    @JvmStatic
    fun quote(atomKeyword: ElixirAtomKeyword): OtpErlangObject = OtpErlangAtom(atomKeyword.text)

    @Contract(pure = true)
    @JvmStatic
    fun quote(charListLine: ElixirCharListLine): OtpErlangObject =
            quotedChildNodes(charListLine, *childNodes(charListLine.quoteCharListBody!!))

    @Contract(pure = true)
    @JvmStatic
    fun quote(charToken: ElixirCharToken): OtpErlangObject {
        val children = charToken.node.getChildren(null)

        if (children.size != 2) {
            throw TODO("CharToken expected to be ?(<character>|<escape sequence>)")
        }

        val tokenized = children[1]
        val tokenizedElementType = tokenized.elementType

        val codePoint = if (tokenizedElementType === ElixirTypes.CHAR_LIST_FRAGMENT) {
            if (tokenized.textLength != 1) {
                throw TODO("Tokenized character expected to only be one character long")
            }

            tokenized.text.codePointAt(0)
        } else {
            (tokenized.psi as EscapeSequence).codePoint()
        }

        return OtpErlangLong(codePoint.toLong())
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(
            containerAssociationOperation: ElixirContainerAssociationOperation
    ): OtpErlangObject {
        val children = containerAssociationOperation.children

        // associationInfixOperator is private so not a PsiElement
        assert(children.size == 2)

        return OtpErlangTuple(children.map { it as Quotable }.map(Quotable::quote).toTypedArray())
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(atUnqualifiedBracketOperation: AtNumericBracketOperation): OtpErlangObject {
        val quotedOperator = atUnqualifiedBracketOperation.atPrefixOperator.quote()

        val children = atUnqualifiedBracketOperation.children

        assert(children.size == 3)

        val quotedOperand = (children[1] as Quotable).quote()
        val metadata = metadata(atUnqualifiedBracketOperation)

        val quotedContainer = quotedFunctionCall(quotedOperator, metadata, quotedOperand)

        val bracketArguments = atUnqualifiedBracketOperation.bracketArguments
        val quotedBracketArguments = bracketArguments.quote()

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(noParenthesesManyStrictNoParenthesesExpression: ElixirNoParenthesesManyStrictNoParenthesesExpression): OtpErlangObject {
        val children = noParenthesesManyStrictNoParenthesesExpression.children

        assert(children.size == 1)

        return (children[0] as Quotable).quote()
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(multipleAliases: ElixirMultipleAliases): OtpErlangObject =
            quotedFunctionArguments(*multipleAliases.children.map { it as Quotable }.map { it.quote() }.toTypedArray())

    @Contract(pure = true)
    @JvmStatic
    fun quote(mapUpdateArguments: ElixirMapUpdateArguments): OtpErlangObject {
        val children = mapUpdateArguments.children

        assert(children.size >= 3)

        val quotedCurrentMap = (children[0] as Quotable).quote()

        val pipeOperator = children[1] as Operator
        val quotedPipeOperator = pipeOperator.quote()

        val quotedMapUpdates = (2 until children.size)
                .asSequence()
                .map { children[it] as Quotable }
                .map { it.quote() }
                .flatMap {
                    if (it is OtpErlangList) {
                        it.asSequence()
                    } else {
                        sequenceOf(it)
                    }
                }
                .toList()
                .toTypedArray()
                .let(::OtpErlangList)

        return quotedFunctionCall(
                quotedPipeOperator,
                metadata(pipeOperator),
                quotedCurrentMap,
                quotedMapUpdates
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(mapOperation: ElixirMapOperation): OtpErlangObject = mapOperation.mapArguments.quote()

    @Contract(pure = true)
    @JvmStatic
    fun quote(mapArguments: ElixirMapArguments): OtpErlangObject {
        val node = mapArguments.node
        val openingCurlies = node.getChildren(TokenSet.create(ElixirTypes.OPENING_CURLY))

        assert(openingCurlies.size == 1)

        val openingCurly = openingCurlies[0]

        val quotedArguments =
                mapArguments.mapUpdateArguments?.quote()?.let { arrayOf(it) }
                        ?: mapArguments.mapConstructionArguments?.quoteArguments() ?: emptyArray()

        return quotedFunctionCall(
                "%{}",
                metadata(openingCurly),
                *quotedArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(list: ElixirList): OtpErlangObject {
        val listArguments = list.children
        val quotedListArgumentList = mutableListOf<OtpErlangObject>()

        if (listArguments.isNotEmpty()) {
            (0 until listArguments.size - 1)
                    .asSequence()
                    .map { listArguments[it] as Quotable }
                    .mapTo(quotedListArgumentList) { it.quote() }

            val lastListArgument = listArguments[listArguments.size - 1]

            if (lastListArgument is ElixirKeywords) {
                (lastListArgument as QuotableKeywordList)
                        .quotableKeywordPairList()
                        .mapTo(quotedListArgumentList) { it.quote() }
            } else {
                val quotable = lastListArgument as Quotable
                quotedListArgumentList.add(quotable.quote())
            }
        }

        return elixirCharList(OtpErlangList(quotedListArgumentList.toTypedArray()))
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(keywordKey: ElixirKeywordKey): OtpErlangObject =
            keywordKey.charListLine?.quoteAsAtom() ?: keywordKey.stringLine?.quoteAsAtom()
            ?: OtpErlangAtom(computeReadAction<String>(Computable { keywordKey.text }))

    @Contract(pure = true)
    @JvmStatic
    fun quote(quotableKeywordPair: QuotableKeywordPair): OtpErlangObject {
        val quotedKeywordKey = quotableKeywordPair.keywordKey.quote()
        val quotedKeywordValue = quotableKeywordPair.keywordValue.quote()

        val elements = arrayOf(quotedKeywordKey, quotedKeywordValue)

        return OtpErlangTuple(elements)
    }


    @Contract(pure = true)
    @JvmStatic
    fun quote(atUnqualifiedBracketOperation: AtUnqualifiedBracketOperation): OtpErlangObject {
        val operator = atUnqualifiedBracketOperation.atPrefixOperator
        val quotedOperator = operator.quote()

        val node = atUnqualifiedBracketOperation.node
        val identifierNodes = node.getChildren(IDENTIFIER_TOKEN_SET)

        assert(identifierNodes.size == 1)

        val identifierNode = identifierNodes[0]
        val identifier = identifierNode.text
        val metadata = metadata(atUnqualifiedBracketOperation)

        val quotedOperand = quotedVariable(identifier, metadata)
        val quotedContainer = quotedFunctionCall(quotedOperator, metadata, quotedOperand)

        val bracketArguments = atUnqualifiedBracketOperation.bracketArguments
        val quotedBracketArguments = bracketArguments.quote()

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): OtpErlangObject {
        val operator = atUnqualifiedNoParenthesesCall.atIdentifier.atPrefixOperator
        val quotedOperator = operator.quote()

        val identifierNodes = atUnqualifiedNoParenthesesCall.atIdentifier.node.getChildren(IDENTIFIER_TOKEN_SET)

        assert(identifierNodes.size == 1)

        val identifierNode = identifierNodes[0]
        val identifier = identifierNode.text
        val quotedIdentifier = OtpErlangAtom(identifier)

        val quotedArguments = atUnqualifiedNoParenthesesCall.noParenthesesOneArgument.quoteArguments()
        val doBlock = atUnqualifiedNoParenthesesCall.doBlock

        val quotedOperand = quotedBlockCall(
                quotedIdentifier,
                metadata(operator),
                quotedArguments,
                doBlock
        )

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedOperand
        )
    }

    @Contract(pure = true)
    @JvmStatic
    private fun quotedBlockCall(
            quotedIdentifier: OtpErlangObject,
            callMetadata: OtpErlangList,
            quotedArguments: Array<OtpErlangObject>,
            doBlock: ElixirDoBlock?): OtpErlangObject {
        val quotedCombinedArguments: Array<OtpErlangObject> =
                if (doBlock != null) {
                    quotedArguments + doBlock.quoteArguments()
                } else {
                    quotedArguments
                }

        return quotedFunctionCall(
                quotedIdentifier,
                callMetadata,
                *quotedCombinedArguments
        )
    }

    @JvmStatic
    fun quote(bracketOperation: BracketOperation): OtpErlangObject {
        val children = bracketOperation.children

        assert(children.size == 2)

        val quotedMatchedExpression = (children[0] as Quotable).quote()
        val bracketArguments = children[1] as Quotable
        val quotedBracketArguments = bracketArguments.quote()

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedMatchedExpression,
                quotedBracketArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(dotCall: DotCall<*>): OtpErlangObject {
        val leftOperand = dotCall.firstChild as Quotable
        val quotedLeftOperand = leftOperand.quote()

        val operator = dotCall.dotInfixOperator
        val quotedOperator = operator.quote()
        val operatorMetadata = metadata(operator)

        val quotedIdentifier = quotedFunctionCall(
                quotedOperator,
                operatorMetadata,
                quotedLeftOperand
        )

        val parenthesesArgumentsList = dotCall.parenthesesArgumentsList
        val doBlock = dotCall.doBlock

        return quotedParenthesesCall(
                quotedIdentifier,
                operatorMetadata,
                parenthesesArgumentsList,
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    private fun quotedParenthesesCall(
            quotedIdentifier: OtpErlangObject,
            identifierMetadata: OtpErlangList,
            parenthesesArgumentsList: List<ElixirParenthesesArguments>,
            doBlock: ElixirDoBlock?): OtpErlangObject {
        val parenthesesArgumentsListSize = parenthesesArgumentsList.size

        assert(parenthesesArgumentsListSize > 0)

        val lastIndex = parenthesesArgumentsListSize - 1

        return parenthesesArgumentsList.foldIndexed(
                quotedIdentifier
        ) { index, quotedIdentifierForCall, parenthesesArguments ->
            val quotedParenthesesArguments = parenthesesArguments.quoteArguments()

            if (index == lastIndex) {
                quotedBlockCall(
                        quotedIdentifierForCall,
                        identifierMetadata,
                        quotedParenthesesArguments,
                        doBlock
                )
            } else {
                quotedFunctionCall(
                        quotedIdentifierForCall,
                        identifierMetadata,
                        *quotedParenthesesArguments
                )
            }
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(`in`: In): OtpErlangObject {
        val children = `in`.children

        if (children.size != 3) {
            throw TODO("BinaryOperation expected to have 3 children (left operand, operator, right operand")
        }

        val quotedLeftOperand = (children[0] as Quotable).quote()

        val operator = children[1] as Quotable
        val quotedOperator = operator.quote()

        val quotedRightOperand = (children[2] as Quotable).quote()

        var quoted: OtpErlangObject? = null

        // @see https://github.com/elixir-lang/elixir/blob/6c288be7300509ff7b809002a3563c6a02dc13fa/lib/elixir/src/elixir_parser.yrl#L596-L597
        // @see https://github.com/elixir-lang/elixir/blob/6c288be7300509ff7b809002a3563c6a02dc13fa/lib/elixir/src/elixir_parser.yrl#L583
        if (Macro.isExpression(quotedLeftOperand)) {
            val leftExpression = quotedLeftOperand as OtpErlangTuple
            val leftOperator = leftExpression.elementAt(0)

            for (rearrangedUnaryOperator in REARRANGED_UNARY_OPERATORS) {
                /* build_op({_Kind, Line, 'in'}, {UOp, _, [Left]}, Right) when ?rearrange_uop(UOp) ->
                     {UOp, meta(Line), [{'in', meta(Line), [Left, Right]}]}; */
                if (leftOperator == rearrangedUnaryOperator) {
                    val unaryOperatorArguments = leftExpression.elementAt(2)
                    val originalUnaryOperand = when (unaryOperatorArguments) {
                        is OtpErlangString -> {
                            OtpErlangLong(unaryOperatorArguments.stringValue().codePointAt(0).toLong())
                        }
                        is OtpErlangList -> unaryOperatorArguments.elementAt(0)
                        else -> throw TODO("Expected REARRANGED_UNARY_OPERATORS operand to be quoted as an OtpErlangString or OtpErlangList")
                    }

                    val operatorMetadata = metadata(operator)

                    quoted = quotedFunctionCall(
                            leftOperator,
                            operatorMetadata,
                            quotedFunctionCall(
                                    quotedOperator,
                                    operatorMetadata,
                                    originalUnaryOperand,
                                    quotedRightOperand
                            )
                    )
                }
            }
        }

        if (quoted == null) {
            quoted = quotedFunctionCall(
                    quotedOperator,
                    metadata(operator),
                    quotedLeftOperand,
                    quotedRightOperand
            )
        }

        return quoted
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedAlias: QualifiedAlias): OtpErlangObject {
        val children = qualifiedAlias.children

        assert(children.size == 3)

        val quotedAlias = (children[2] as Quotable).quote() as OtpErlangTuple
        val quotedMatchedExpression = (children[0] as Quotable).quote()

        /*
         * Use line from last alias, but drop `counter: 0`
         */
        val aliasMetadata = Macro.metadata(quotedAlias)
        val lineTuple = org.elixir_lang.List.keyfind(
                aliasMetadata,
                OtpErlangAtom("line"),
                0
        ) as OtpErlangTuple
        val qualifiedAliasMetadata = OtpErlangList(
                arrayOf<OtpErlangObject>(lineTuple)
        )

        val lastAliases = Macro.callArguments(quotedAlias).elements()

        /* if both aliases, then the counter: 0 needs to be removed from the metadata data and the arguments for
           each __aliases__ need to be combined */
        val mergedArguments = if (Macro.isAliases(quotedMatchedExpression)) {
            val firstAliasList = Macro.callArguments(quotedMatchedExpression as OtpErlangTuple)
            firstAliasList.elements() + lastAliases
        } else {
            arrayOf(quotedMatchedExpression) + lastAliases
        }

        return quotedFunctionCall(
                ALIASES,
                qualifiedAliasMetadata,
                *mergedArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedMultipleAliases: QualifiedMultipleAliases): OtpErlangObject {
        val children = qualifiedMultipleAliases.children

        assert(children.size == 3)

        val quotedMultipleAliases = (children[2] as Quotable).quote()
        val quotedExpression = (children[0] as Quotable).quote()
        val metadata = metadata(children[1] as Operator)

        // See https://github.com/lexmag/elixir/blob/8c57c9110301c1ee02d84b574c59feff00e14ba3/lib/elixir/src/elixir_parser.yrl#L644
        val head = quotedFunctionCall(
                ".",
                metadata,
                quotedExpression,
                MULTIPLE_ALIASES
        )

        return OtpErlangTuple(
                arrayOf(head, metadata, quotedMultipleAliases)
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedBracketOperation: QualifiedBracketOperation): OtpErlangObject {
        var quotedIdentifier = (qualifiedBracketOperation.firstChild as Quotable).quote()

        val relativeIdentifier = qualifiedBracketOperation.relativeIdentifier
        val quotedRelativeIdentifier = relativeIdentifier.quote()

        quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedIdentifier,
                quotedRelativeIdentifier
        )

        val callMetadata = Macro.metadata(quotedIdentifier)

        val quotedContainer = quotedFunctionCall(
                quotedIdentifier,
                callMetadata
        )

        val bracketArguments = qualifiedBracketOperation.bracketArguments
        val quotedBracketArguments = bracketArguments.quote()

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedNoArgumentsCall: QualifiedNoArgumentsCall<*>): OtpErlangObject {
        val quotedQualifier = (qualifiedNoArgumentsCall.firstChild as Quotable).quote()

        val relativeIdentifier = qualifiedNoArgumentsCall.relativeIdentifier
        val quotedRelativeIdentifier = relativeIdentifier.quote()

        val quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedQualifier,
                quotedRelativeIdentifier
        )

        val doBlock = qualifiedNoArgumentsCall.doBlock

        return quotedBlockCall(
                quotedIdentifier,
                metadata(relativeIdentifier),
                emptyArray(),
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedNoParenthesesCall: QualifiedNoParenthesesCall<*>): OtpErlangObject {
        val quotedQualifier = (qualifiedNoParenthesesCall.firstChild as Quotable).quote()

        val relativeIdentifier = qualifiedNoParenthesesCall.relativeIdentifier
        val quotedRelativeIdentifier = relativeIdentifier.quote()

        val quotedIdentifier = quotedFunctionCall(
                ".",
                metadata(relativeIdentifier),
                quotedQualifier,
                quotedRelativeIdentifier
        )

        val quotedArguments = qualifiedNoParenthesesCall.noParenthesesOneArgument.quoteArguments()
        val doBlock = qualifiedNoParenthesesCall.doBlock

        return quotedBlockCall(
                quotedIdentifier,
                metadata(relativeIdentifier),
                quotedArguments,
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(qualifiedParenthesesCall: QualifiedParenthesesCall<*>): OtpErlangObject {
        val quotedQualifier = (qualifiedParenthesesCall.firstChild as Quotable).quote()

        val relativeIdentifier = qualifiedParenthesesCall.relativeIdentifier
        val quotedRelativeIdentifier = relativeIdentifier.quote()

        val metadata = metadata(relativeIdentifier)
        val quotedIdentifier = quotedFunctionCall(
                ".",
                metadata,
                quotedQualifier,
                quotedRelativeIdentifier
        )

        val parenthesesArgumentsList = qualifiedParenthesesCall.matchedParenthesesArguments.parenthesesArgumentsList
        val doBlock = qualifiedParenthesesCall.doBlock

        return quotedParenthesesCall(
                quotedIdentifier,
                metadata,
                parenthesesArgumentsList,
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(unqualifiedBracketOperation: UnqualifiedBracketOperation): OtpErlangObject {
        val quotedIdentifier = OtpErlangAtom(unqualifiedBracketOperation.node.firstChildNode.text)
        val quotedContainer = quotedVariable(quotedIdentifier, metadata(unqualifiedBracketOperation))

        val bracketArguments = unqualifiedBracketOperation.bracketArguments
        val quotedBracketArguments = bracketArguments.quote()

        return quotedFunctionCall(
                "Elixir.Access",
                "get",
                metadata(bracketArguments),
                quotedContainer,
                quotedBracketArguments
        )
    }

    /* Replaces `nil` argument in variables with the quoted ElixirMatchedNotParenthesesArguments.
     *
     */
    @Contract(pure = true)
    @JvmStatic
    fun quote(unqualifiedNoParenthesesCall: UnqualifiedNoParenthesesCall<*>): OtpErlangObject {
        val quotedIdentifier = OtpErlangAtom(unqualifiedNoParenthesesCall.functionName())
        val quotedArguments = unqualifiedNoParenthesesCall.noParenthesesOneArgument.quoteArguments()

        var blockCallMetadata = metadata(unqualifiedNoParenthesesCall)

        // see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b//lib/elixir/src/elixir_parser.yrl#L627-L628
        if (quotedArguments.size == 1) {
            val quotedArgument = quotedArguments[0]

            if (Macro.isExpression(quotedArgument)) {
                val expression = quotedArgument as OtpErlangTuple
                val receiver = expression.elementAt(0)

                if (receiver == MINUS || receiver == PLUS) {
                    val dualCallArguments = Macro.callArguments(expression)

                    // [Arg]
                    if (dualCallArguments.arity() == 1) {
                        /* @note getChildren[0] is NOT the same as getFirstChild().  getFirstChild() will get the
                             leaf node for identifier instead of the first compound, rule node for the argument. */
                        val argument = unqualifiedNoParenthesesCall.children[1].firstChild

                        if (!(argument is ElixirAccessExpression && argument.getFirstChild() is ElixirParentheticalStab)) {
                            blockCallMetadata = OtpErlangList(
                                    arrayOf(AMBIGUOUS_OP_KEYWORD_PAIR, blockCallMetadata.elementAt(0))
                            )
                        }
                    }
                }
            }
        }

        val doBlock = unqualifiedNoParenthesesCall.doBlock

        return quotedBlockCall(
                quotedIdentifier,
                blockCallMetadata,
                quotedArguments,
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>): OtpErlangObject {
        val doBlock = unqualifiedNoArgumentsCall.doBlock
        val quoted: OtpErlangObject
        val identifier = unqualifiedNoArgumentsCall.identifier

        val identifierText = runReadAction {
            identifier.text
        }
        val callMetadata = metadata(identifier)

        // if a variable has a `do` block is no longer a variable because the do block acts as keyword arguments.
        if (doBlock != null) {
            val quotedBlockArguments = doBlock.quoteArguments()

            quoted = quotedFunctionCall(
                    identifierText,
                    callMetadata,
                    *quotedBlockArguments
            )
        } else {
            /* @note quotedFunctionCall cannot be used here because in the 3-tuple for function calls, the elements are
              {name, metadata, arguments}, while for an ambiguous call or variable, the elements are
              {name, metadata, context}.  Importantly, context is nil when there is no context while arguments are []
              when there are no arguments. */
            quoted = quotedVariable(
                    identifierText,
                    callMetadata
            )
        }

        return quoted
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(unqualifiedParenthesesCall: UnqualifiedParenthesesCall<*>): OtpErlangObject {
        val metadata = metadata(unqualifiedParenthesesCall)
        val quotedIdentifier = OtpErlangAtom(unqualifiedParenthesesCall.node.firstChildNode.text)
        val parenthesesArgumentsList = unqualifiedParenthesesCall.matchedParenthesesArguments.parenthesesArgumentsList
        val doBlock = unqualifiedParenthesesCall.doBlock

        return quotedParenthesesCall(
                quotedIdentifier,
                metadata,
                parenthesesArgumentsList,
                doBlock
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(parentheticalStab: ElixirParentheticalStab): OtpErlangObject =
        parentheticalStab.stab?.quote(emptyMetadata(parentheticalStab)) ?:
        // @note CANNOT use quotedFunctionCall because it requires metadata and gives nil instead of [] when no
        //   arguments are given while empty block is quoted as `{__block__, [], []}`
        OtpErlangTuple(
                arrayOf(BLOCK, emptyMetadata(parentheticalStab), OtpErlangList())
        )

    private fun emptyMetadata(parentheticalStab: ElixirParentheticalStab): OtpErlangList {
        val level = getNonNullRelease(parentheticalStab).level()

        return if (level < V_1_6) {
            OtpErlangList()
        } else {
            metadata(parentheticalStab)
        }
    }

    /* @note quotedFunctionCall cannot be used here because in the 3-tuple for function calls, the elements are
       {name, metadata, arguments}, while for an ambiguous call or variable, the elements are
       {name, metadata, context}.  Importantly, context is nil when there is no context while arguments are [] when
       there are no arguments. */
    @Contract(pure = true)
    @JvmStatic
    fun quote(variable: ElixirVariable): OtpErlangObject = quotedVariable(variable)

    @Contract(pure = true)
    @JvmStatic
    fun quote(operator: Operator): OtpErlangObject = OtpErlangAtom(operator.operatorTokenNode().text)

    @Contract(pure = true)
    @JvmStatic
    fun quote(prefix: Prefix): OtpErlangObject {
        val children = prefix.children

        if (children.size != 2) {
            throw TODO("Prefix expected to have 2 children (operator and operand")
        }

        val quotedOperator = (children[0] as Quotable).quote()
        val quotedOperand = (children[1] as Quotable).quote()

        return quotedFunctionCall(
                quotedOperator,
                metadata(prefix),
                quotedOperand
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(children: Array<PsiElement>, level: Level): OtpErlangObject =
        children.asSequence().filter { it !is Unquoted }.map {
            it as? Quotable ?: throw TODO("Child, $it, must be Quotable or Unquoted")
        }.toList().toTypedArray().let { quote(it, level) }

    @Contract(pure = true)
    @JvmStatic
    fun quote(file: PsiFile): OtpErlangObject =
            (file.viewProvider.getPsi(ElixirLanguage) as ElixirFile).let { root ->
                ElixirPsiImplUtil.quote(root)
            }

    @Contract(pure = true)
    @JvmStatic
    fun quote(unqualifiedNoParenthesesManyArgumentsCall: ElixirUnqualifiedNoParenthesesManyArgumentsCall): OtpErlangObject {
        val quotedIdentifier = unqualifiedNoParenthesesManyArgumentsCall.identifier.quote()
        val quotedArguments = ElixirPsiImplUtil.quoteArguments(unqualifiedNoParenthesesManyArgumentsCall)

        return anchoredQuotedFunctionCall(unqualifiedNoParenthesesManyArgumentsCall, quotedIdentifier, *quotedArguments)
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(sigilHeredoc: SigilHeredoc): OtpErlangObject {
        val quotedHeredoc = quote(sigilHeredoc as Heredoc)

        return quote(sigilHeredoc, quotedHeredoc)
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(sigilLine: SigilLine): OtpErlangObject {
        val bodyChildNodes = childNodes(sigilLine.body)
        val quotedBody = quotedChildNodes(sigilLine, *bodyChildNodes)

        return quote(sigilLine, quotedBody)
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(quotableKeywordList: QuotableKeywordList): OtpErlangObject =
            OtpErlangList(quotableKeywordList.quotableKeywordPairList().map { it.quote() }.toTypedArray())

    @Contract(pure = true)
    @JvmStatic
    fun quote(heredocLine: HeredocLine, heredoc: Heredoc, prefixLength: Int): OtpErlangObject {
        val excessWhitespace = heredocLine.heredocLinePrefix.excessWhitespace(heredoc.fragmentType, prefixLength)
        val directChildNodes = childNodes(heredocLine.body)

        val accumulatedChildNodes = if (excessWhitespace != null) {
            arrayOf(excessWhitespace) + directChildNodes
        } else {
            directChildNodes
        }

        return quotedChildNodes(
                heredoc,
                metadata(heredocLine),
                *accumulatedChildNodes
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(heredoc: Heredoc): OtpErlangObject {
        val prefixLength = heredoc.heredocPrefix.textLength
        val alignedNodeQueue = LinkedList<ASTNode>()
        val heredocLineList = heredoc.heredocLineList
        val fragmentType = heredoc.fragmentType

        for (line in heredocLineList) {
            queueChildNodes(line, fragmentType, prefixLength, alignedNodeQueue)
        }

        val mergedNodeQueue = mergeFragments(
                alignedNodeQueue,
                heredoc.fragmentType,
                heredoc.manager
        )

        return quotedChildNodes(heredoc, *mergedNodeQueue.toTypedArray())
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(tuple: ElixirTuple): OtpErlangObject {
        val openingCurlies = tuple.node.getChildren(TokenSet.create(ElixirTypes.OPENING_CURLY))

        assert(openingCurlies.size == 1)

        val openingCurly = openingCurlies[0]
        val quotedChildren =
                tuple.children.asSequence().map { it as Quotable }.map(Quotable::quote).toList().toTypedArray()

        // 2-tuples are literals in quoted form
        return if (quotedChildren.size == 2) {
            OtpErlangTuple(quotedChildren)
        } else {
            quotedFunctionCall(
                    "{}",
                    metadata(openingCurly),
                    *quotedChildren
            )
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(structOperation: ElixirStructOperation): OtpErlangObject {
        val children = structOperation.children

        assert(children.size == 3)

        val operator = children[0] as Operator
        val quotedOperator = operator.quote()

        val quotedName = (children[1] as Quotable).quote()
        val quotedMapArguments = (children[2] as Quotable).quote()

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedName,
                quotedMapArguments
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(stringLine: ElixirStringLine): OtpErlangObject =
            quotedChildNodes(stringLine, *childNodes(stringLine.quoteStringBody!!))

    @Contract(pure = true)
    @JvmStatic
    fun quote(stabParenthesesSignature: ElixirStabParenthesesSignature): OtpErlangObject {
        val children = stabParenthesesSignature.children

        assert(children.isNotEmpty())

        // stabParenthesesManyArguments ...
        val quotedArguments = (children[0] as QuotableArguments).quoteArguments()

        // stabParenthesesManyArguments WHEN_OPERATOR expression
        val quotedListElements = if (children.size > 1) {
            assert(children.size == 3)

            val operator = children[1] as Operator
            val quotedOperator = operator.quote()

            val quotedGuard = (children[2] as Quotable).quote()
            val quotedWhenArguments = quotedArguments + arrayOf(quotedGuard)

            val quotedWhenOperation = quotedFunctionCall(
                    quotedOperator,
                    metadata(operator),
                    *quotedWhenArguments
            )

            arrayOf(quotedWhenOperation)
        } else {
            quotedArguments
        }

        return OtpErlangList(quotedListElements)
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(stabOperation: ElixirStabOperation): OtpErlangObject {
        val operator = stabOperation.operator()
        val quotedOperator = operator.quote()

        return quotedFunctionCall(
                quotedOperator,
                metadata(operator),
                quotedLeftOperand(stabOperation),
                quotedRightOperand(stabOperation)
        )
    }

    @Contract(pure = true)
    fun quotedRightOperand(stabOperation: ElixirStabOperation): OtpErlangObject {
        val rightOperand = stabOperation.rightOperand()
        val quotedRightOperand: OtpErlangObject

        if (rightOperand != null) {
            val quotableRightOperand = rightOperand as Quotable?
            quotedRightOperand = quotableRightOperand!!.quote()
        } else {
            quotedRightOperand = NIL
        }

        return quotedRightOperand
    }

    // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L277
    @Contract(pure = true)
    @JvmStatic
    fun quote(stabNoParenthesesSignature: ElixirStabNoParenthesesSignature): OtpErlangObject =
        stabNoParenthesesSignature.noParenthesesArguments.quoteArguments()
                .let { unwrapWhen(it) }
                .let(::OtpErlangList)
                .let { elixirCharList(it) }

    @Contract(pure = true)
    @JvmStatic
    fun quote(stabBody: ElixirStabBody): OtpErlangObject = stabBody.quote(OtpErlangList())

    @Contract(pure = true)
    @JvmStatic
    fun quote(stab: ElixirStab): OtpErlangObject = stab.quote(OtpErlangList())

    @Contract(pure = true)
    @JvmStatic
    fun quote(sigilModifiers: ElixirSigilModifiers): OtpErlangObject {
        val codePoints = sigilModifiers.text.codePoints().toArray()

        return if (codePoints.isEmpty()) {
            OtpErlangList()
        } else {
            elixirCharList(codePoints.toList())
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(relativeIdentifier: ElixirRelativeIdentifier): OtpErlangObject {
        val children = relativeIdentifier.children

        // Only tokens
        return if (children.isEmpty()) {
            // take first node to avoid SIGNIFICANT_WHITE_SPACE after DUAL_OPERATOR
            relativeIdentifier.node.firstChildNode.text.let(::OtpErlangAtom)
        } else {
            assert(children.size == 1)

            val child = children[0]

            if (child is Atomable) {
                child.quoteAsAtom()
            } else {
                (child as Quotable).quote()
            }
        }
    }

    /* "#{a}" is transformed to "<<Kernel.to_string(a) :: binary>>" in
     * `"\"\#{a}\"" |> Code.string_to_quoted |> Macro.to_string`, so interpolation has to be represented as a type call
     * (`:::`) to binary of a call of `Kernel.to_string`
     */
    @Contract(pure = true)
    @JvmStatic
    fun quote(interpolation: ElixirInterpolation): OtpErlangObject {
        val level = getNonNullRelease(interpolation).level()
        val quotedChildren = quote(interpolation.children, level)
        val interpolationMetadata = metadata(interpolation)

        val quotedKernelToStringCall = quotedFunctionCall(
                prependElixirPrefix(KERNEL),
                "to_string",
                interpolationMetadata,
                quotedChildren
        )
        val quotedBinaryCall = quotedVariable(
                "binary",
                interpolationMetadata
        )

        return quotedFunctionCall(
                "::",
                interpolationMetadata,
                quotedKernelToStringCall,
                quotedBinaryCall
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(identifier: ElixirIdentifier): OtpErlangObject = OtpErlangAtom(identifier.text)

    @JvmStatic
    fun quote(decimalFloat: ElixirDecimalFloat): OtpErlangObject {
        val integralDigitsList = decimalFloat.decimalFloatIntegral.decimalWholeNumber.digitsList()
        val integralString = compactDigits(integralDigitsList)

        val fractionalDigitsList = decimalFloat.decimalFloatFractional.decimalWholeNumber.digitsList()
        val fractionalString = compactDigits(fractionalDigitsList)

        val decimalFloatExponent = decimalFloat.decimalFloatExponent

        return if (decimalFloatExponent != null) {
            val exponentDigitsList = decimalFloatExponent.decimalWholeNumber.digitsList()
            val exponentSignString = decimalFloatExponent.decimalFloatExponentSign.text
            val exponentString = compactDigits(exponentDigitsList)

            val floatString = String.format(
                    "%s.%se%s%s",
                    integralString,
                    fractionalString,
                    exponentSignString,
                    exponentString
            )

            if (integralDigitsList.inBase() && fractionalDigitsList.inBase() && exponentDigitsList.inBase()) {
                Double.parseDouble(floatString).let(::OtpErlangDouble)
            } else {
                // Convert parser error to runtime ArgumentError
                quotedFunctionCall(
                        "String",
                        "to_float",
                        metadata(decimalFloat),
                        elixirString(floatString)
                )
            }
        } else {
            val floatString = String.format(
                    "%s.%s",
                    integralString,
                    fractionalString
            )

            if (integralDigitsList.inBase() && fractionalDigitsList.inBase()) {
                java.lang.Double.parseDouble(floatString).let(::OtpErlangDouble)
            } else {
                // Convert parser error to runtime ArgumentError
                quotedFunctionCall(
                        "String",
                        "to_float",
                        metadata(decimalFloat),
                        elixirString(floatString)
                )
            }
        }
    }

    @JvmStatic
    fun quote(@Suppress("UNUSED_PARAMETER") emptyParentheses: ElixirEmptyParentheses): OtpErlangObject =
        getNonNullRelease(emptyParentheses)
                .level()
                .let { emptyBlock(it) }

    @JvmStatic
    fun quote(file: ElixirFile): OtpErlangObject {
        val quotedChildren = LinkedList<OtpErlangObject>()

        file.acceptChildren(
                object : PsiElementVisitor() {
                    override fun visitElement(element: PsiElement) {
                        if (element is Quotable) {
                            visitQuotable((element as Quotable?)!!)
                        } else if (!element.isUnquoted()) {
                            throw TODO("Don't know how to visit $element")
                        }

                        super.visitElement(element)
                    }

                    internal fun visitQuotable(child: Quotable) {
                        val quotedChild = child.quote()
                        quotedChildren.add(quotedChild)
                    }
                }
        )

        val level = getNonNullRelease(file).level()

        // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L76-L79
        return toBlock(quotedChildren, level)
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(sigil: Sigil, quotedContent: OtpErlangObject): OtpErlangObject {
        val sigilName = sigil.sigilName()
        val sigilMetadata = metadata(sigil)
        val sigilBinary = quotedContent as? OtpErlangTuple ?: quotedFunctionCall("<<>>", sigilMetadata, quotedContent)
        val quotedModifiers = sigil.sigilModifiers.quote()

        return quotedFunctionCall(
                "sigil_" + sigilName,
                sigilMetadata,
                sigilBinary,
                quotedModifiers
        )
    }

    @Contract(pure = true)
    @JvmStatic
    fun quote(wholeNumber: WholeNumber): OtpErlangObject {
        val digitsList = wholeNumber.digitsList()
        val digitsListString = digitsList.textToString()
        val base = wholeNumber.base()

        return if (digitsList.inBase()) {
            quoteInBase(digitsListString, base)
        } else {
            /* 0 elements is invalid in native Elixir and can be emulated as `String.to_integer("", base)` while
               2 elements implies at least one element is invalidDigitsElementType which is invalid in native Elixir and
               can be emulated as String.to_integer(<all-digits>, base) so that it raises an ArgumentError on the invalid
               digits */
            quotedFunctionCall(
                    "String",
                    "to_integer",
                    metadata(wholeNumber),
                    elixirString(digitsListString),
                    OtpErlangLong(base.toLong())
            )
        }
    }

    // Private Functions

    private fun anchoredQuotedFunctionCall(
            anchor: PsiElement,
            quotedIdentifier: OtpErlangObject,
            vararg quotedArguments: OtpErlangObject
    ): OtpErlangObject {
        val metadata = if (Macro.isExpression(quotedIdentifier)) {
            val expression = quotedIdentifier as OtpErlangTuple
            /* Grab metadata from quotedIdentifier so line of quotedFunctionCall is line of identifier or `.` in
               identifier, which can differ from the line of quotable call when there are newlines on either side of
               `.`. */
            Macro.metadata(expression)
        } else {
            metadata(anchor)
        }

        return quotedFunctionCall(
                quotedIdentifier,
                metadata,
                *quotedArguments
        )
    }

    private fun keywordTuple(key: String, value: Int): OtpErlangTuple {
        val keyAtom = OtpErlangAtom(key)
        val valueInt = OtpErlangInt(value)

        return OtpErlangTuple(arrayOf(keyAtom, valueInt))
    }

    /* Returns the 0-indexed line number for the element */
    private fun lineNumber(node: ASTNode): Int = node.psi.document()!!.getLineNumber(node.startOffset)

    private fun lineNumberKeywordTuple(node: ASTNode): OtpErlangTuple =
            keywordTuple(
                    "line",
                    lineNumber(node) + 1
            )

    @Contract(pure = true)
    private fun quote(children: Array<Quotable>, level: Level) =
        // Uses toBlock because this is for inside interpolation, which functions the same as an embedded file
        children.map(Quotable::quote).let { toBlock(it, level) }

    @JvmStatic
    fun quotedFunctionCall(
            quotedQualifiedIdentifier: OtpErlangObject,
            metadata: OtpErlangList,
            vararg arguments: OtpErlangObject
    ): OtpErlangTuple = OtpErlangTuple(
            arrayOf(quotedQualifiedIdentifier, metadata, quotedFunctionArguments(*arguments))
    )

    @JvmStatic
    fun metadata(node: ASTNode): OtpErlangList = OtpErlangList(arrayOf<OtpErlangObject>(lineNumberKeywordTuple(node)))
    @JvmStatic
    fun metadata(operator: Operator): OtpErlangList = metadata(operator.operatorTokenNode())
    @JvmStatic
    fun metadata(element: PsiElement): OtpErlangList = metadata(element.node)

    @JvmStatic
    fun metadata(element: PsiElement, counter: Int): OtpErlangList {
        val level = getNonNullRelease(element).level()
        val lineNumberKeywordTuple = lineNumberKeywordTuple(element.node)

        return if (level < V_1_6) {
            /* QuotableKeywordList should be compared by sorting keys, but Elixir does counter first, so it's simpler to just use
               same order than detect a OtpErlangList is a QuotableKeywordList */
            arrayOf<OtpErlangObject>(keywordTuple("counter", counter), lineNumberKeywordTuple)
        } else {
            arrayOf<OtpErlangObject>(lineNumberKeywordTuple)
        }.let(::OtpErlangList)
    }

    @JvmStatic
    fun quotedFunctionCall(
            identifier: String,
            metadata: OtpErlangList,
            vararg arguments: OtpErlangObject
    ): OtpErlangTuple =
            quotedFunctionCall(
                    OtpErlangAtom(identifier),
                    metadata,
                    *arguments
            )

    @JvmStatic
    fun quotedFunctionCall(
            module: String,
            identifier: String,
            metadata: OtpErlangList,
            vararg arguments: OtpErlangObject
    ): OtpErlangTuple {
        val quotedQualifiedIdentifier = quotedFunctionCall(
                ".",
                metadata,
                OtpErlangAtom(module),
                OtpErlangAtom(identifier)
        )

        return quotedFunctionCall(
                quotedQualifiedIdentifier,
                metadata,
                *arguments
        )
    }

    /*
     * Erlang will automatically stringify a list that is just a list of LATIN-1 printable code
     * points.
     * OtpErlangString and OtpErlangList are not equal when they have the same content, so to check against
     * Elixir.Code.string_to_quoted, this code must determine if Erlang would return an OtpErlangString instead
     * of OtpErlangList and do the same.
     */
    private fun quotedFunctionArguments(vararg arguments: OtpErlangObject): OtpErlangObject =
            elixirCharList(OtpErlangList(arguments))

    @Contract(pure = true)
    private fun quotedLeftOperand(stabOperation: ElixirStabOperation): OtpErlangObject =
        stabOperation.leftOperand()?.quote() ?:
        // when there is not signature before `->`.
        OtpErlangList()

    @JvmStatic
    fun childNodes(parentElement: PsiElement): Array<ASTNode> = parentElement.node.getChildren(null)

    private fun quotedChildNodes(parent: Parent, vararg children: ASTNode): OtpErlangObject =
            quotedChildNodes(parent, metadata(parent), *children)

    private fun quotedChildNodes(parent: Parent, metadata: OtpErlangList, vararg children: ASTNode): OtpErlangObject {
        val quoted: OtpErlangObject

        val childCount = children.size

        if (childCount == 0) {
            quoted = parent.quoteEmpty()
        } else {
            val quotedParentList = LinkedList<OtpErlangObject>()
            var codePointList: MutableList<Int>? = null

            for (child in children) {
                val elementType = child.elementType

                if (elementType === parent.fragmentType) {
                    codePointList = parent.addFragmentCodePoints(codePointList, child)
                } else if (elementType === ElixirTypes.ESCAPED_CHARACTER) {
                    codePointList = parent.addEscapedCharacterCodePoints(codePointList, child)
                } else if (elementType === ElixirTypes.ESCAPED_EOL) {
                    codePointList = parent.addEscapedEOL(codePointList, child)
                } else if (elementType === ElixirTypes.HEXADECIMAL_ESCAPE_PREFIX) {
                    codePointList = addChildTextCodePoints(codePointList, child)
                } else if (elementType === ElixirTypes.INTERPOLATION) {
                    if (codePointList != null) {
                        quotedParentList.add(elixirString(codePointList))
                        codePointList = null
                    }

                    val childElement = child.psi as ElixirInterpolation
                    quotedParentList.add(childElement.quote())
                } else if (elementType === ElixirTypes.QUOTE_HEXADECIMAL_ESCAPE_SEQUENCE || elementType === ElixirTypes.SIGIL_HEXADECIMAL_ESCAPE_SEQUENCE) {
                    codePointList = parent.addHexadecimalEscapeSequenceCodePoints(codePointList, child)
                } else {
                    throw TODO("Can't quote " + child)
                }
            }

            quoted = if (codePointList != null && quotedParentList.isEmpty()) {
                parent.quoteLiteral(codePointList)
            } else {
                if (codePointList != null) {
                    quotedParentList.add(elixirString(codePointList))
                }

                val binaryConstruction = quotedFunctionCall("<<>>", metadata, *quotedParentList.toTypedArray())
                parent.quoteBinary(binaryConstruction)
            }
        }

        return quoted
    }

    @Contract(pure = true)
    private fun quotedVariable(variable: PsiElement): OtpErlangObject =
            quotedVariable(
                    variable.text,
                    metadata(variable)
            )

    @Contract(pure = true)
    private fun quotedVariable(identifier: String, metadata: OtpErlangList): OtpErlangObject =
            quotedVariable(
                    OtpErlangAtom(identifier),
                    metadata
            )

    @Contract(pure = true)
    private fun quotedVariable(
            quotedIdentifier: OtpErlangObject,
            metadata: OtpErlangList,
            context: OtpErlangObject = NIL
    ): OtpErlangObject =
            OtpErlangTuple(
                    arrayOf(quotedIdentifier, metadata, context)
            )

    private fun queueChildNodes(
            line: HeredocLine,
            fragmentType: IElementType,
            prefixLength: Int,
            heredocDescendantNodes: Queue<ASTNode>
    ) {
        val excessWhitespace = line.heredocLinePrefix.excessWhitespace(fragmentType, prefixLength)

        if (excessWhitespace != null) {
            heredocDescendantNodes.add(excessWhitespace)
        }

        val level = getNonNullRelease(line).level()
        val childNodes = childNodes(line.body)

        if (level < V_1_3 &&
                childNodes.isNotEmpty() &&
                childNodes[childNodes.size - 1].elementType == ElixirTypes.ESCAPED_EOL) {
            heredocDescendantNodes.addAll(Arrays.asList(*childNodes).subList(0, childNodes.size - 1))
        } else {
            Collections.addAll(heredocDescendantNodes, *childNodes)
            val eolNode = Factory.createSingleLeafElement(
                    fragmentType,
                    "\n",
                    0,
                    1, null,
                    line.manager
            )
            heredocDescendantNodes.add(eolNode)
        }
    }

    @Contract(pure = true)
    private fun mergeFragments(
            unmergedNodes: Deque<ASTNode>,
            fragmentType: IElementType,
            manager: PsiManager
    ): Queue<ASTNode> {
        val mergedNodes = LinkedList<ASTNode>()
        var fragmentStringBuilder: StringBuilder? = null

        for (unmergedNode in unmergedNodes) {
            if (unmergedNode.elementType === fragmentType) {
                if (fragmentStringBuilder == null) {
                    fragmentStringBuilder = StringBuilder()
                }

                val fragment = unmergedNode.text
                fragmentStringBuilder.append(fragment)
            } else {
                addMergedFragments(mergedNodes, fragmentType, fragmentStringBuilder, manager)
                fragmentStringBuilder = null
                mergedNodes.add(unmergedNode)
            }
        }

        addMergedFragments(mergedNodes, fragmentType, fragmentStringBuilder, manager)

        return mergedNodes
    }

    private fun addMergedFragments(
            mergedNodes: Queue<ASTNode>,
            fragmentType: IElementType,
            fragmentStringBuilder: StringBuilder?,
            manager: PsiManager
    ) {
        if (fragmentStringBuilder != null) {
            val charListFragment = Factory.createSingleLeafElement(
                    fragmentType,
                    fragmentStringBuilder.toString(),
                    0,
                    fragmentStringBuilder.length, null,
                    manager
            )
            mergedNodes.add(charListFragment)
        }
    }

    /**
     * Unwraps `when` from left end of noParenthesesArguments in stabNoParenthesesSignature so that the when acts as a guard for the signature instead of a guard on the last positional argument.
     *
     * @see [`unwrap_when` in `stab_expr`](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl.L276-L277)
     *
     * @see [`unwrap_when`](https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl.L716-L722)
     */
    private fun unwrapWhen(quotedArguments: Array<OtpErlangObject>): Array<OtpErlangObject> {
        // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L717
        val last = quotedArguments[quotedArguments.size - 1]
        // https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L720-L721
        var unwrapped = quotedArguments

        // { _, _, _}
        if (Macro.isExpression(last)) {
            val expression = last as OtpErlangTuple
            val receiver = expression.elementAt(0)

            // {'when', _, _ }
            if (receiver == WHEN) {
                val operands = expression.elementAt(2)

                // is_list(End)
                if (operands is OtpErlangList) {

                    // Have to check for two element so that unwrap_when doesn't happen recursively as the unwrapped version of when will have more than 2 arguments, which is only seen in stabSignatures.
                    // [_, _] = End
                    if (operands.arity() == 2) {
                        val unwrappedArguments =
                                quotedArguments.slice(0 until quotedArguments.size - 1).toTypedArray() +
                                        operands.elements()

                        unwrapped = arrayOf(
                                quotedFunctionCall(
                                        receiver,
                                        Macro.metadata(expression),
                                        *unwrappedArguments
                                )
                        )
                    }
                }
            }
        }

        return unwrapped
    }

    private fun compactDigits(digitsList: List<Digits>): String {
        val builtString = StringBuilder()

        for (digits in digitsList) {
            builtString.append(digits.text)
        }

        return builtString.toString()
    }

    private fun quoteInBase(string: String, base: Int): OtpErlangObject =
        BigInteger(string, base).let(::OtpErlangLong)

    /**
     * Converts group of separated expressions into a block or returns the single expression.
     *
     * See https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L724-L725
     */
    @Contract(pure = true)
    private fun toBlock(quotedChildren: List<OtpErlangObject>, level: Level): OtpErlangObject =
            when (quotedChildren.size) {
                0 -> emptyBlock(level)
                1 ->  if (level < V_1_6) {
                    quotedChildren.first()
                } else {
                    buildBlock(quotedChildren, OtpErlangList())
                }
                else -> blockFunctionCall(quotedChildren, OtpErlangList())
            }

    private fun emptyBlock(level: Level) =
            if (level < V_1_6)  {
                NIL
            } else {
                OtpErlangTuple(
                        arrayOf(BLOCK, OtpErlangList(), OtpErlangList())
                )
            }

    @Contract(pure = true)
    private fun blockFunctionCall(quotedChildren: List<OtpErlangObject>, metadata: OtpErlangList): OtpErlangTuple =
            quotedFunctionCall(
                    BLOCK,
                    metadata,
                    *quotedChildren.toTypedArray()
            )

    /**
     * Builds a block for stab bodies.  Unlike `toBlock`, handles rearranging unary operations `not` and `!` and putting
     * solitary `unquote_splicing` calls in blocks.
     *
     * @param quotedChildren
     */
    @Contract(pure = true)
    internal fun buildBlock(quotedChildren: List<OtpErlangObject>, metadata: OtpErlangList): OtpErlangObject =
            when (quotedChildren.size) {
                0 -> NIL
                1 -> {
                    val quotedChild = quotedChildren.first()

                    // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L588
                    if (Macro.isLocalCall(quotedChild)) {
                        // @see https://github.com/elixir-lang/elixir/blob/de39bbaca277002797e52ffbde617ace06233a2b/lib/elixir/src/elixir_parser.yrl#L547
                        when ((quotedChild as OtpErlangTuple).elementAt(0)) {
                            EXCLAMATION_POINT, NOT, UNQUOTE_SPLICING ->
                                QuotableImpl.blockFunctionCall(quotedChildren, metadata)
                            else ->
                                quotedChild
                        }
                    } else {
                        quotedChild
                    }
                }
                else -> QuotableImpl.blockFunctionCall(quotedChildren, metadata)
            }
}
