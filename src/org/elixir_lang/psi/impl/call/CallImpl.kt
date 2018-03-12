package org.elixir_lang.psi.impl.call

import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiElement
import org.apache.commons.lang.math.IntRange
import org.elixir_lang.mix.importWizard.computeReadAction
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.StubBased
import org.elixir_lang.psi.call.arguments.None
import org.elixir_lang.psi.call.arguments.star.NoParentheses
import org.elixir_lang.psi.call.arguments.star.NoParenthesesOneArgument
import org.elixir_lang.psi.call.arguments.star.Parentheses
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.call.name.Module.stripElixirPrefix
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.*
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.qualification.Qualified
import org.elixir_lang.psi.qualification.Unqualified
import org.elixir_lang.psi.stub.call.Stub
import org.jetbrains.annotations.Contract
import java.util.*

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
            call.doBlock != null || keywordArgument(call, "do") != null

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
    fun isCalling(call: Call,
                  resolvedModuleName: String,
                  functionName: String): Boolean {
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
    fun isCalling(call: Call,
                  resolvedModuleName: String,
                  functionName: String,
                  resolvedFinalArity: Int): Boolean =
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
    fun isCallingMacro(call: Call,
                       resolvedModuleName: String,
                       functionName: String): Boolean =
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
    fun isCallingMacro(call: Call,
                       resolvedModuleName: String,
                       functionName: String,
                       resolvedFinalArity: Int): Boolean =
            call.isCalling(resolvedModuleName, functionName, resolvedFinalArity) && call.hasDoBlockOrKeyword()

    @Contract(pure = true, value = "_ -> null")
    @JvmStatic
    fun moduleName(@Suppress("UNUSED_PARAMETER")
                   atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>): String? =
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
    fun primaryArguments(infix: Infix): Array<PsiElement?> {
        val children = infix.children
        val operatorIndex = Normalized.operatorIndex(children)
        val leftOperand = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(children, operatorIndex)
        val rightOperand = org.elixir_lang.psi.operation.infix.Normalized.rightOperand(children, operatorIndex)

        return arrayOf(leftOperand, rightOperand)
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
    fun primaryArguments(notIn: NotIn): Array<PsiElement?> {
        val children = notIn.children
        val leftOperand = org.elixir_lang.psi.operation.not_in.Normalized.leftOperand(children)
        val rightOperand = org.elixir_lang.psi.operation.not_in.Normalized.rightOperand(children)

        return arrayOf(leftOperand, rightOperand)
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
    fun primaryArguments(prefix: Prefix): Array<PsiElement?> = arrayOf(prefix.operand())

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
    fun resolvedModuleName(qualified: org.elixir_lang.psi.call.qualification.Qualified): String? =
        (qualified as? org.elixir_lang.psi.call.StubBased<Stub<*>>)?.stub?.resolvedFunctionName() ?:
        stripElixirPrefix(qualified.moduleName())

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun resolvedModuleName(unqualified: Unqualified): String? =
        (unqualified as? org.elixir_lang.psi.call.StubBased<Stub<*>>)?.stub?.resolvedModuleName() ?: KERNEL

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
                if (primaryArity == null) {
                    resolvedPrimaryArity = 1
                } else {
                    resolvedPrimaryArity!!
                    resolvedPrimaryArity += 1
                }
            }

            val parent = computeReadAction(Computable<PsiElement> { call.parent })

            if (parent.isPipe()) {
                val parentPipeOperation = parent as Arrow
                val pipedInto = parentPipeOperation.rightOperand()

                /* only the right operand has its arity increased because it is the operand that has the output of the
                   left operand prepended to its arguments */
                if (pipedInto != null && call.isEquivalentTo(pipedInto)) {
                    if (primaryArity == null) {
                        resolvedPrimaryArity = 1
                    } else {
                        resolvedPrimaryArity!!
                        resolvedPrimaryArity += 1
                    }
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
    fun resolvedFinalArity(stubBased: org.elixir_lang.psi.call.StubBased<Stub<*>>): Int =
            stubBased.stub?.resolvedFinalArity() ?: resolvedFinalArity(stubBased as Call) ?: 0

    @Contract(pure = true)
    @JvmStatic
    fun resolvedFinalArityRange(call: Call): IntRange =
            ElixirPsiImplUtil.finalArguments(call)?.let { finalArguments ->
                val defaultCount = finalArguments.defaultArgumentCount()
                val maximum = finalArguments.size
                val minimum = maximum - defaultCount
                IntRange(minimum, maximum)
            } ?: IntRange(0)

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
     * The number of arguments that have defaults.
     *
     * @param this@defaultArgumentCount arguments to a definition call
     */
    private fun Array<PsiElement>.defaultArgumentCount(): Int = count { it.isDefaultArgument() }

    /**
     * Whether the given element presents a default argument (with `\\` in it.
     *
     * @param this@isDefaultArgument an argument to a [Call]
     * @return `true` if in match operation with `\\` operator; otherwise, `false`.
     */
    private fun PsiElement.isDefaultArgument(): Boolean {
        var defaultArgument = false

        if (this is InMatch) {
            val operation = this as Operation

            if (operation.operator().text.trim { it <= ' ' } == DEFAULT_OPERATOR) {
                defaultArgument = true
            }
        }

        return defaultArgument
    }

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
