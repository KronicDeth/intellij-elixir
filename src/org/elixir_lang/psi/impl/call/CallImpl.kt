package org.elixir_lang.psi.impl.call

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.mix.project.computeReadAction
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.call.arguments.None
import org.elixir_lang.psi.call.arguments.star.NoParentheses
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument
import org.elixir_lang.psi.call.arguments.star.Parentheses
import org.elixir_lang.psi.call.name.Function.__MODULE__
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.stripElixirPrefix
import org.elixir_lang.psi.impl.*
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.*
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.qualification.Qualified
import org.elixir_lang.psi.qualification.Unqualified
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.psi.scope.isTypeSpecPseudoFunction
import org.elixir_lang.psi.stub.call.Stub
import org.elixir_lang.reference.Callable
import org.elixir_lang.reference.Callable.Companion.isBitStreamSegmentOption
import org.jetbrains.annotations.Contract
import java.util.*
import org.elixir_lang.psi.impl.macroChildCallList as psiElementToMacroChildCallList

fun Call.computeReference(): PsiReference? =
    /* if the call is just the identifier for a module attribute reference, then don't return a Callable reference,
           and instead let {@link #getReference(AtNonNumericOperation) handle it */
    if (!this.isModuleAttributeNameElement() &&
        // if a bitstring segment option then the option is a pseudo-function
        !isBitStreamSegmentOption(this) &&
        !this.isSlashInCaptureNameSlashArity() &&
        !org.elixir_lang.ecto.Query.isAssoc(this)
    ) {
        val parent = parent

        when {
            parent is Type -> {
                parent.leftOperand()?.let { parentLeftOperand ->
                    if (parentLeftOperand.isEquivalentTo(this)) {
                        val grandParent = parent.parent

                        val maybeArgument = if (grandParent is When) {
                            grandParent.parent
                        } else {
                            grandParent
                        }

                        (maybeArgument as? ElixirNoParenthesesOneArgument)?.let { argument ->
                            (argument.parent as? AtUnqualifiedNoParenthesesCall<*>)?.let { moduleAttribute ->
                                val name = moduleAttributeName(moduleAttribute)

                                if (name == "@spec") {
                                    org.elixir_lang.reference.CallDefinitionClause(this, moduleAttribute)
                                } else {
                                    null
                                }
                            }
                        }
                    } else {
                        null
                    }
                }
                    ?: computeCallableReference()
            }

            parent.isSlashInCaptureNameSlashArity() -> null
            else -> computeCallableReference()
        }
    } else {
        null
    }

private fun PsiElement.isCaptureNonNumericOperation(): Boolean =
    when (this) {
        is ElixirMatchedCaptureNonNumericOperation,
        is ElixirUnmatchedCaptureNonNumericOperation ->
            true

        else ->
            false
    }

private fun PsiElement.isSlashInCaptureNameSlashArity(): Boolean =
    if (this is Infix &&
        (this is ElixirMatchedMultiplicationOperation || this is ElixirUnmatchedMultiplicationOperation)
    ) {
        val operator = org.elixir_lang.psi.operation.Normalized.operator(this)
        val divisionOperatorChildren = operator.node.getChildren(TokenSet.create(ElixirTypes.DIVISION_OPERATOR))

        if (divisionOperatorChildren.isNotEmpty()) {
            val rightOperand =
                org.elixir_lang.psi.operation.infix.Normalized.rightOperand(this)?.stripAccessExpression()

            if (rightOperand is ElixirDecimalWholeNumber) {
                val parent = this.parent

                parent.isCaptureNonNumericOperation()
            } else {
                false
            }
        } else {
            false
        }
    } else {
        false
    }


private fun Call.computeCallableReference(): PsiReference? =
    if (Callable.isDefiner(this)) {
        Callable.definer(this)
    } else if (isCalling(KERNEL, __MODULE__, 0)) {
        org.elixir_lang.psi.__MODULE__.reference(this)
    } else {
        val ancestorTypeSpec = this.ancestorTypeSpec()

        if (ancestorTypeSpec != null && !Unquote.`is`(this)) {
            if (this.isTypeSpecPseudoFunction()) {
                null
            } else {
                org.elixir_lang.reference.Type(ancestorTypeSpec, this)
            }
        } else {
            Callable(this)
        }
    }

/**
 * The outer most arguments
 *
 * @return [Call.primaryArguments]
 */
fun Call.finalArguments(): Array<PsiElement>? = try {
    (secondaryArguments() ?: primaryArguments())?.map { it!! }?.toTypedArray()
} catch (e: NullPointerException) {
    Logger.error(this.javaClass, "NullPointerException getting Call.finalArguments()", this);
    null
}

fun Call.getReference(): PsiReference? =
    CachedValuesManager.getCachedValue(this) {
        CachedValueProvider.Result.create(computeReference(), this)
    }

/**
 * The value of the keyword argument with the given keywordKeyText.
 *
 * @param this@keywordArgument call to seach for the keyword argument.
 * @param keywordKeyText the text of the key, such as `"do"`
 * @return the keyword value `PsiElement` if `call` has [ElixirPsiImplUtil.keywordArguments]
 * and there is a [] for `keywordKeyText`.
 */
fun Call.keywordArgument(keywordKeyText: String): PsiElement? = keywordArguments()?.keywordValue(keywordKeyText)

/**
 * The keyword arguments for `call`.
 * @param this@keywordArguments call to search for keyword arguments.
 * @return the final element of the [ElixirPsiImplUtil.finalArguments] of `` if they are a
 * [QuotableKeywordList]; otherwise, `null`.
 */
fun Call.keywordArguments(): QuotableKeywordList? {
    val finalArguments = this.finalArguments()
    var keywordArguments: QuotableKeywordList? = null

    if (finalArguments != null) {
        val finalArgumentCount = finalArguments.size

        if (finalArgumentCount > 0) {
            val potentialKeywords = finalArguments[finalArgumentCount - 1]

            if (potentialKeywords is QuotableKeywordList) {
                keywordArguments = potentialKeywords
            } else if (potentialKeywords is ElixirAccessExpression) {
                val accessExpressionChild = potentialKeywords.stripAccessExpression()

                if (accessExpressionChild is ElixirList) {
                    val listChildren = accessExpressionChild.children

                    if (listChildren.size == 1) {
                        val listChild = listChildren[0]

                        if (listChild is QuotableKeywordList) {
                            keywordArguments = listChild
                        }
                    }
                }
            }
        }
    }

    return keywordArguments
}

fun Call.macroChildCalls(): Array<Call> {
    val childCallList = macroChildCallList()

    return childCallList.toTypedArray()
}

fun <R> Call.foldChildrenWhile(
    initial: R,
    operation: (PsiElement, acc: R) -> AccumulatorContinue<R>
): AccumulatorContinue<R> {
    val doBlock = doBlock

    return if (doBlock != null) {
        doBlock.stab?.let { stab ->
            val stabChildren = stab.children

            if (stabChildren.size == 1) {
                val stabChild = stabChildren[0]

                if (stabChild is ElixirStabBody) {
                    stabChild.foldChildrenWhile(initial, operation)
                } else {
                    null
                }
            } else {
                null
            }
        }
    } else { // one liner version with `do:` keyword argument
        val finalArguments = finalArguments()!!

        assert(finalArguments.isNotEmpty())

        val potentialKeywords = finalArguments[finalArguments.size - 1]

        if (potentialKeywords is QuotableKeywordList) {
            val quotableKeywordPairList = potentialKeywords.quotableKeywordPairList()
            val firstQuotableKeywordPair = quotableKeywordPairList[0]
            val keywordKey = firstQuotableKeywordPair.keywordKey

            if (keywordKey.text == "do") {
                firstQuotableKeywordPair.keywordValue.foldChildrenWhile(initial, operation)
            } else {
                null
            }
        } else {
            null
        }
    } ?: AccumulatorContinue(initial, true)
}

fun Call.macroChildCallList(): List<Call> {
    var childCallList: List<Call>? = null
    val doBlock = doBlock

    if (doBlock != null) {
        val stab = doBlock.stab

        if (stab != null) {
            val stabChildren = stab.children

            if (stabChildren.size == 1) {
                val stabChild = stabChildren[0]

                if (stabChild is ElixirStabBody) {
                    childCallList = stabChild.psiElementToMacroChildCallList()
                }
            }
        }
    } else { // one liner version with `do:` keyword argument
        val finalArguments = finalArguments()!!

        assert(finalArguments.isNotEmpty())

        val potentialKeywords = finalArguments[finalArguments.size - 1]

        if (potentialKeywords is QuotableKeywordList) {
            val quotableKeywordPairList = potentialKeywords.quotableKeywordPairList()
            val firstQuotableKeywordPair = quotableKeywordPairList[0]
            val keywordKey = firstQuotableKeywordPair.keywordKey

            if (keywordKey.text == "do") {
                val keywordValue = firstQuotableKeywordPair.keywordValue

                if (keywordValue is Call) {
                    val childCall = keywordValue as Call
                    childCallList = listOf(childCall)
                }
            }
        }
    }

    if (childCallList == null) {
        childCallList = emptyList()
    }

    return childCallList
}

fun Call.macroChildCallSequence(): Sequence<Call> = this.macroChildCallList().asSequence()

@Contract(pure = true)
fun Call.macroDefinitionClauseForArgument(): Call? {
    var macroDefinitionClause: Call? = null
    val parent = parent

    if (parent is ElixirMatchedWhenOperation) {
        val grandParent = parent.getParent()

        if (grandParent is ElixirNoParenthesesOneArgument) {
            val greatGrandParent = grandParent.getParent()

            if (greatGrandParent is Call) {
                if (CallDefinitionClause.isMacro(greatGrandParent)) {
                    macroDefinitionClause = greatGrandParent
                }
            }
        }
    }

    return macroDefinitionClause
}

fun Call.maybeModularNameToModulars(useCall: Call? = null): Set<Call> =
    if (isCalling(KERNEL, __MODULE__, 0)) {
        org.elixir_lang.psi.__MODULE__
            .reference(__MODULE__Call = this, useCall = useCall)
            .maybeModularNameToModulars(incompleteCode = false)
    } else {
        emptySet()
    }

fun Call.whileInStabBodyChildExpressions(
    forward: Boolean = true,
    keepProcessing: (childExpression: PsiElement) -> Boolean
): Boolean =
    stabBodyChildExpressions(forward)
        ?.let { whileIn(it, keepProcessing) }
        ?: true

fun Call.stabBodyChildExpressions(forward: Boolean = true): Sequence<PsiElement>? =
    doBlock
        ?.stab
        ?.stabBody
        ?.childExpressions(forward)

object CallImpl {
    @Contract(pure = true)
    @JvmStatic
    fun functionName(call: Call): String? =
        call.functionNameElement()?.let { element ->
            computeReadAction(Computable<String> { element.text })
        }

    /**
     * @return `null` because the `IDENTIFIER`, `foo` in `@foo 1` is not the local name of a function, but the name of a
     * Module attribute.
     */
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun functionNameElement(
        @Suppress("UNUSED_PARAMETER") atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>
    ): PsiElement? = null

    /**
     * @return `null` because the expression before the `.` is a variable name and not a function name.
     */
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun functionNameElement(@Suppress("UNUSED_PARAMETER") dotCall: DotCall<*>): PsiElement? = null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun functionNameElement(@Suppress("UNUSED_PARAMETER") notIn: NotIn): PsiElement? = null

    @Contract(pure = true)
    @JvmStatic
    fun functionNameElement(operation: Operation): PsiElement = operation.operator()

    @JvmStatic
    fun functionNameElement(qualified: Qualified): PsiElement = qualified.relativeIdentifier

    @Contract(pure = true)
    @JvmStatic
    fun functionNameElement(unqualified: Unqualified): PsiElement = unqualified.firstChild

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun getDoBlock(
        @Suppress("UNUSED_PARAMETER")
        unqualifiedNoParenthesesManyArgumentsCall: ElixirUnqualifiedNoParenthesesManyArgumentsCall
    ): ElixirDoBlock? =
        null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun getDoBlock(@Suppress("UNUSED_PARAMETER") notIn: NotIn): ElixirDoBlock? = null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun getDoBlock(@Suppress("UNUSED_PARAMETER") operation: Operation): ElixirDoBlock? = null

    @Contract(pure = true)
    @JvmStatic
    fun getDoBlock(@Suppress("UNUSED_PARAMETER") matchedCall: MatchedCall): ElixirDoBlock? = null

    @JvmStatic
    fun hasDoBlockOrKeyword(call: Call): Boolean =
        call.doBlock != null || call.keywordArgument("do") != null

    @JvmStatic
    fun hasDoBlockOrKeyword(stubBased: StubBased<Stub<*>>): Boolean =
        stubBased.stub?.hasDoBlockOrKeyword() ?: hasDoBlockOrKeyword(stubBased as Call)

    /**
     * Whether the `call` is calling the given `functionName` in the `resolvedModuleName` with any arity
     *
     * @param call               the call element
     * @param resolvedModuleName the expected [Call.resolvedModuleName]
     * @param functionName       the expected [Call.functionName]
     * @return `true` if the `call` has non-`null` [Call.resolvedModuleName] that equals
     * `resolvedModuleName` and has non-`null` [Call.functionName] that equals
     * `functionName`; otherwise, `false`.
     */
    @JvmStatic
    fun isCalling(
        call: Call,
        resolvedModuleName: String,
        functionName: String
    ): Boolean {
        val callResolvedModuleName = call.resolvedModuleName()
        val callFunctionName = call.functionName()

        return callResolvedModuleName != null && callResolvedModuleName == resolvedModuleName &&
                callFunctionName != null && callFunctionName == functionName
    }

    /**
     * Whether the `call` is calling the given `functionName` in the `resolvedModuleName` with the
     * `resolvedFinalArity`
     *
     * @param call               the call element
     * @param resolvedModuleName the expected [Call.resolvedModuleName]
     * @param functionName       the expected [Call.functionName]
     * @param resolvedFinalArity the expected [Call.resolvedFinalArity]
     * @return `true` if the `call` has non-`null` [Call.resolvedModuleName] that equals
     * `resolvedModuleName` and has non-`null` [Call.functionName] that equals
     * `functionName` and the [Call.resolvedFinalArity]; otherwise, `false`.
     */
    @JvmStatic
    fun isCalling(
        call: Call,
        resolvedModuleName: String,
        functionName: String,
        resolvedFinalArity: Int
    ): Boolean =
        call.isCalling(resolvedModuleName, functionName) && call.resolvedFinalArity() == resolvedFinalArity

    /**
     * Whether `call` is of the named macro.
     *
     *
     * Differs from [ElixirPsiImplUtil.isCallingMacro] because no arity is necessary,
     * which is useful for special forms, which don't have a set arity.  (That's actually why they need to be special
     * forms since Erlang/Elixir doesn't support variable arity functions otherwise.)
     *
     * @param call               the call element
     * @param resolvedModuleName the expected [Call.resolvedModuleName]
     * @param functionName       the expected [Call.functionName]
     * @return `true` if all arguments match and [Call.getDoBlock] is not `null`; `false`.
     */
    @JvmStatic
    fun isCallingMacro(
        call: Call,
        resolvedModuleName: String,
        functionName: String
    ): Boolean =
        call.isCalling(resolvedModuleName, functionName) && call.hasDoBlockOrKeyword()

    /**
     * Whether `call` is of the named macro.
     *
     *
     * Differs from [ElixirPsiImplUtil.isCalling] because this function ensures there
     * is a `do` block.  If the macro can be called without a `do` block, then
     * [ElixirPsiImplUtil.isCalling] should be called instead.
     *
     * @param call               the call element
     * @param resolvedModuleName the expected [Call.resolvedModuleName]
     * @param functionName       the expected [Call.functionName]
     * @param resolvedFinalArity the expected [Call.resolvedFinalArity]
     * @return `true` if all arguments match and [Call.getDoBlock] is not `null`; `false`.
     */
    @Contract(pure = true)
    @JvmStatic
    fun isCallingMacro(
        call: Call,
        resolvedModuleName: String,
        functionName: String,
        resolvedFinalArity: Int
    ): Boolean =
        call.isCalling(resolvedModuleName, functionName, resolvedFinalArity) && call.hasDoBlockOrKeyword()

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(
        @Suppress("UNUSED_PARAMETER")
        atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>
    ): String? =
        // Always null because it's unqualified.
        null

    // Always null because anonymous
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(@Suppress("UNUSED_PARAMETER") dotCall: DotCall<*>): String? = null

    // Always null because it's unqualified.
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(@Suppress("UNUSED_PARAMETER") notIn: NotIn): String? = null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(@Suppress("UNUSED_PARAMETER") operation: Operation): String? = null

    // Always null because it's unqualified.
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(@Suppress("UNUSED_PARAMETER") unqualified: Unqualified): String? = null

    // TODO handle more complex qualifiers besides Aliases
    @Contract(pure = true)
    @JvmStatic
    fun moduleName(qualified: Qualified): String = computeReadAction(Computable<String> { qualified.firstChild.text })

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(dotCall: DotCall<*>): Array<PsiElement> = dotCall.parenthesesArgumentsList[0].arguments()

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(infix: Infix): Array<PsiElement> {
        val children = infix.children
        val operatorIndex = Normalized.operatorIndex(children)
        val leftOperand = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)
        val rightOperand = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex)

        return if (leftOperand != null) {
            if (rightOperand != null) {
                arrayOf<PsiElement>(leftOperand, rightOperand)
            } else {
                arrayOf<PsiElement>(leftOperand)
            }
        } else {
            if (rightOperand != null) {
                arrayOf<PsiElement>(rightOperand)
            } else {
                emptyArray()
            }
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(unqualifiedNoParenthesesManyArgumentsCall: ElixirUnqualifiedNoParenthesesManyArgumentsCall): Array<PsiElement> {
        val arguments = unqualifiedNoParenthesesManyArgumentsCall.noParenthesesStrict

        return if (arguments != null) {
            arguments.arguments()
        } else {
            /* noParenthesesManyArguments is a private rule, so when noParenthesesStrict is not present, then the
               noParenthesesManyArguments are direct children, but so is he identifier, so the identifier needs to be
               ignored  */
            val children = unqualifiedNoParenthesesManyArgumentsCall.children

            assert(children[0] is ElixirIdentifier)

            Arrays.copyOfRange(children, 1, children.size)
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(@Suppress("UNUSED_PARAMETER") none: None): Array<PsiElement>? = null

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(notIn: NotIn): Array<PsiElement> {
        val children = notIn.children
        val leftOperand = org.elixir_lang.psi.operation.not_in.Normalized.leftOperand(children)

        return if (leftOperand != null) {
            val rightOperand = org.elixir_lang.psi.operation.not_in.Normalized.rightOperand(children)

            if (rightOperand != null) {
                arrayOf(leftOperand, rightOperand)
            } else {
                arrayOf(leftOperand)
            }
        } else {
            emptyArray()
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(noParenthesesOneArgument: NoParenthesesOneArgument): Array<PsiElement> =
        noParenthesesOneArgument.noParenthesesOneArgument.arguments()

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(parentheses: Parentheses): Array<PsiElement> {
        val matchedParenthesesArguments = parentheses.matchedParenthesesArguments
        val parenthesesArgumentsList = matchedParenthesesArguments.parenthesesArgumentsList

        val primaryParenthesesArguments = parenthesesArgumentsList[0]
        return primaryParenthesesArguments.arguments()
    }

    @Contract(pure = true)
    @JvmStatic
    fun primaryArguments(prefix: Prefix): Array<PsiElement> =
        prefix
            .operand()
            ?.let { arrayOf(it) }
            ?: emptyArray()

    @Contract(pure = true)
    @JvmStatic
    fun primaryArity(call: Call): Int? = call.primaryArguments()?.size

    @Contract(pure = true)
    @JvmStatic
    fun secondaryArguments(dotCall: DotCall<*>): Array<PsiElement>? {
        val parenthesesArgumentsList = dotCall.parenthesesArgumentsList

        return if (parenthesesArgumentsList.size < 2) {
            null
        } else {
            val parenthesesArguments = parenthesesArgumentsList[1]
            parenthesesArguments.arguments()
        }
    }

    @Contract(pure = true)
    @JvmStatic
    fun secondaryArguments(@Suppress("UNUSED_PARAMETER") infix: Infix): Array<PsiElement>? = null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun secondaryArguments(@Suppress("UNUSED_PARAMETER") none: None): Array<PsiElement>? = null

    @Contract(pure = true)
    @JvmStatic
    fun secondaryArguments(@Suppress("UNUSED_PARAMETER") notIn: NotIn): Array<PsiElement>? = null

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun secondaryArguments(@Suppress("UNUSED_PARAMETER") noParentheses: NoParentheses): Array<PsiElement>? = null

    @Contract(pure = true)
    @JvmStatic
    fun secondaryArguments(parentheses: Parentheses): Array<PsiElement>? {
        val matchedParenthesesArguments = parentheses.matchedParenthesesArguments
        val parenthesesArgumentsList = matchedParenthesesArguments.parenthesesArgumentsList

        return if (parenthesesArgumentsList.size < 2) {
            null
        } else {
            val parenthesesArguments = parenthesesArgumentsList[1]
            parenthesesArguments.arguments()
        }
    }

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun secondaryArguments(@Suppress("UNUSED_PARAMETER") prefix: Prefix): Array<PsiElement>? = null

    @Contract(pure = true)
    @JvmStatic
    fun secondaryArity(call: Call): Int? = call.secondaryArguments()?.size

    // TODO handle resolving module name from module attribute's declaration
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun resolvedModuleName(
        @Suppress("UNUSED_PARAMETER") atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>
    ): String? = null

    // TODO handle resolving module name from any capture from variable declaration
    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun resolvedModuleName(@Suppress("UNUSED_PARAMETER") dotCall: DotCall<*>): String? = null

    /* TODO handle resolving module name from imports.  Assume KERNEL for now, but some are actually from Bitwise */
    @Contract(pure = true)
    @JvmStatic
    fun resolvedModuleName(@Suppress("UNUSED_PARAMETER") infix: Infix): String = KERNEL

    @Contract(pure = true)
    @JvmStatic
    fun resolvedModuleName(@Suppress("UNUSED_PARAMETER") notIn: NotIn): String = KERNEL

    /* TODO handle resolving module name from imports.  Assume KERNEL for now. */
    @Contract(pure = true)
    @JvmStatic
    fun resolvedModuleName(@Suppress("UNUSED_PARAMETER") prefix: Prefix): String = KERNEL

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun resolvedModuleName(qualified: org.elixir_lang.psi.call.qualification.Qualified): String =
        (qualified as? StubBased<Stub<*>>)?.stub?.resolvedFunctionName() ?: stripElixirPrefix(qualified.moduleName())

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun resolvedModuleName(unqualified: Unqualified): String =
        (unqualified as? StubBased<Stub<*>>)?.let { stubBased ->
            ApplicationManager.getApplication().runReadAction(Computable {
                stubBased.stub
            })?.resolvedModuleName()
        } ?: KERNEL

    // TODO handle `import`s and determine whether actually a local variable
    @Contract(pure = true)
    @JvmStatic
    fun resolvedModuleName(
        @Suppress("UNUSED_PARAMETER") unqualifiedNoArgumentsCall: UnqualifiedNoArgumentsCall<*>
    ): String = KERNEL

    @Contract(pure = true)
    @JvmStatic
    fun resolvedPrimaryArity(call: Call): Int? {
        val primaryArity = call.primaryArity()
        var resolvedPrimaryArity = primaryArity

        /* do block and piping attach to the outer most parentheses, only count do block and piping for primary if there
           are no secondary. */
        if (call.secondaryArity() == null) {
            if (call.doBlock != null) {
                resolvedPrimaryArity = (resolvedPrimaryArity ?: 0) + 1
            }

            val parent = computeReadAction(Computable<PsiElement> { call.parent })

            if (parent.isPipe()) {
                val parentPipeOperation = parent as Arrow
                val pipedInto = parentPipeOperation.rightOperand()

                /* only the right operand has its arity increased because it is the operand that has the output of the
                   left operand prepended to its arguments */
                if (pipedInto != null && call.isEquivalentTo(pipedInto)) {
                    resolvedPrimaryArity = (resolvedPrimaryArity ?: 0) + 1
                }
            }
        }

        return resolvedPrimaryArity
    }

    @Contract(pure = true)
    @JvmStatic
    fun resolvedFinalArity(call: Call): Int = call.resolvedSecondaryArity() ?: call.resolvedPrimaryArity() ?: 0

    @Contract(pure = true)
    @JvmStatic
    fun resolvedFinalArity(stubBased: StubBased<Stub<*>>): Int =
        stubBased.stub?.resolvedFinalArity() ?: resolvedFinalArity(stubBased as Call)

    @Contract(pure = true)
    @JvmStatic
    fun resolvedFinalArityInterval(call: Call): ArityInterval =
        call.finalArguments().let { ArityInterval.fromArguments(it) }

    @Contract(pure = true)
    @JvmStatic
    fun resolvedSecondaryArity(call: Call): Int? =
        call.secondaryArity()?.let { secondaryArity ->
            if (call.doBlock != null) {
                secondaryArity + 1
            } else {
                secondaryArity
            }
        }

    // Private Functions

    /**
     * Whether the `arrow` is a pipe operation.
     *
     * @param this@isPipe the parent (or futher ancestor of a [Call] that may be piped.
     * @return `` true if `arrow` is using the `"|>"` operator token.
     */
    private fun Arrow.isPipe(): Boolean =
        operator().node.getChildren(ARROW_OPERATOR_TOKEN_SET).let { arrowOperatorChildren ->
            arrowOperatorChildren.size == 1 && arrowOperatorChildren[0].text == "|>"
        }

    /**
     * Whether the `callAncestor` is a pipe operation.
     *
     * @param this@isPipe the parent (or further ancestor) of a [Call] that may be piped
     * @return `` true if `callAncestor` is an [Arrow] using the `"|>"` operator token.
     */
    private fun PsiElement.isPipe(): Boolean = (this as? Arrow)?.isPipe() ?: false
}

