package org.elixir_lang.psi.impl

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.ex_unit.Assertions
import org.elixir_lang.psi.ex_unit.Case
import org.elixir_lang.psi.impl.call.CallImpl.hasDoBlockOrKeyword
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.declarations.UseScopeImpl
import org.elixir_lang.psi.impl.declarations.UseScopeImpl.selector
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.operation.infix.Position
import org.elixir_lang.psi.operation.infix.Triple
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.reference.ModuleAttribute
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.Delegation

object ProcessDeclarationsImpl {
    @JvmField
    val DECLARING_SCOPE = Key<Boolean>("DECLARING_SCOPE")

    /**
     * `{:ok, value} = func() && value == literal`
     */
    @JvmStatic
    fun processDeclarations(
        and: And,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        @Suppress("UNUSED_PARAMETER") place: PsiElement
    ): Boolean {
        var keepProcessing = true

        if (Normalized.operator(and).text == "&&") {
            val leftOperand = org.elixir_lang.psi.operation.infix.Normalized.leftOperand(and)

            if (leftOperand != null && !PsiTreeUtil.isAncestor(leftOperand, lastParent!!, false)) {
                // the left operand is not inherently declaring, it should only be if a match is in the left operand
                keepProcessing = processor.execute(leftOperand, state.put(DECLARING_SCOPE, false))
            }
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(
        atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
        processor: PsiScopeProcessor,
        state: ResolveState,
        @Suppress("UNUSED_PARAMETER") lastParent: PsiElement?,
        @Suppress("UNUSED_PARAMETER") place: PsiElement
    ): Boolean {
        val identifierName = atUnqualifiedNoParenthesesCall.atIdentifier.identifierName()

        return if (ModuleAttribute.isTypeSpecName(identifierName)) {
            processor.execute(atUnqualifiedNoParenthesesCall, state) &&
                    atUnqualifiedNoParenthesesCall.finalArguments()?.singleOrNull()?.let { argument ->
                        processDeclarationInTypeSpecArgument(argument, processor, state)
                    } ?: true
        } else {
            processor.execute(atUnqualifiedNoParenthesesCall, state)
        }
    }

    private fun processDeclarationInTypeSpecArgument(
        argument: PsiElement,
        processor: PsiScopeProcessor,
        state: ResolveState
    ): Boolean =
        when (argument) {
            // `Type` are gotten going up, don't need to go down here
            is Type -> true
            is When -> processDeclarationInTypeSpecArgument(argument, processor, state)
            else -> true
        }

    private fun processDeclarationInTypeSpecArgument(
        argument: When,
        processor: PsiScopeProcessor,
        state: ResolveState
    ): Boolean =
        argument.rightOperand()?.stripAccessExpression()?.let {
            processDeclarationInTypeSpecRestrictions(it, processor, state)
        } ?: true

    private fun processDeclarationInTypeSpecRestrictions(
        restrictions: PsiElement,
        processor: PsiScopeProcessor,
        state: ResolveState
    ): Boolean =
        when (restrictions) {
            is ElixirList -> restrictions.whileInChildExpressions {
                processDeclarationInTypeSpecRestrictions(it, processor, state)
            }
            is QuotableKeywordList -> whileIn(restrictions.quotableKeywordPairList()) {
                processDeclarationInTypeSpecRestrictions(it, processor, state)
            }
            is QuotableKeywordPair -> processor.execute(restrictions.keywordKey, state)
            is Type -> restrictions.leftOperand()?.let {
                processor.execute(it, state)
            } ?: true
            // The `when` isn't finished being type, so its argument is the `def` like:
            // `@spec foo(bar) :: term() when\ndef foo(`
            is Call,
                // Fixes #2577
            is ElixirTuple -> true
            else -> {
                Logger.error(
                    restrictions::class.java,
                    "Don't know how find type variable restrictions",
                    restrictions
                )

                true
            }
        }

    /**
     * `def(macro)?p?`, `for`, or `with` can declare variables
     */
    @JvmStatic
    fun processDeclarations(
        call: Call,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ): Boolean =
        // need to check if call is place because lastParent is set to place at start of treeWalkUp
        if (!call.isEquivalentTo(lastParent) || call.isEquivalentTo(place)) {
            when {
                call.isCalling(KERNEL, ALIAS) ||
                        CallDefinitionClause.`is`(call) || // call parameters
                        Callback.`is`(call) ||
                        Case.isChild(call, state) ||
                        Delegation.`is`(call) || // delegation call parameters
                        Exception.`is`(call) ||
                        Implementation.`is`(call) ||
                        Import.`is`(call) ||
                        Module.`is`(call) ||
                        Protocol.`is`(call) ||
                        Use.`is`(call) ||
                        call.isCalling(KERNEL, DESTRUCTURE) || // left operand
                        call.isCallingMacro(KERNEL, IF) || // match in condition
                        call.isCallingMacro(KERNEL, Function.FOR) || // comprehension match variable
                        call.isCalling(KERNEL, MATCH_QUESTION_MARK) ||
                        call.isCalling(KERNEL, REQUIRE) ||
                        call.isCallingMacro(KERNEL, UNLESS) || // match in condition
                        call.isCallingMacro(KERNEL, "with") || // <- or = variable
                        QuoteMacro.`is`(call) || // quote :bind_quoted keys for Variable resolver OR call definitions for Callable resolver
                        org.elixir_lang.psi.mix.Generator.isEmbed(call, state) ||
                        Assertions.isChild(call, state)
                -> processor.execute(call, state)
                org.elixir_lang.ecto.Schema.isChild(call, state) -> {
                    processor.execute(call, state)
                }
                hasDoBlockOrKeyword(call) ->
                    // unknown macros that take do blocks often allow variables to be declared in their arguments
                    processor.execute(call, state)
                org.elixir_lang.ecto.Query.isChild(call, state) -> {
                    processor.execute(call, state)
                }
                else -> true
            }
        } else {
            true
        }

    @JvmStatic
    fun processDeclarations(
        alias: ElixirAlias,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        processDeclarationsRecursively(
            alias,
            processor,
            state,
            lastParent,
            place
        )

    @JvmStatic
    fun processDeclarations(
        file: ElixirFile,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean {
        val keepProcessing = processDeclarationsInPreviousSibling(file, processor, state, lastParent, place)

        if (keepProcessing) {
            processor.execute(file, state)
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(
        multipleAliases: ElixirMultipleAliases,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        entrance: PsiElement
    ): Boolean {
        return processDeclarationsRecursively(
            multipleAliases,
            processor,
            state,
            lastParent,
            entrance
        )
    }

    @JvmStatic
    fun processDeclarations(
        scope: ElixirEex,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        processDeclarationsInPreviousSibling(scope, processor, state, lastParent, place)

    @JvmStatic
    fun processDeclarations(
        scope: ElixirEexTag,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        if (scope.isEquivalentTo(lastParent.parent)) {
            processDeclarationsInPreviousSibling(scope, processor, state, lastParent, place)
        } else {
            scope
                .childExpressions(forward = false)
                .let { processDeclarations(it, processor, state, lastParent, place) }
        }

    @JvmStatic
    fun processDeclarations(
        scope: ElixirStabBody,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        processDeclarationsInPreviousSibling(scope, processor, state, lastParent, place)

    @JvmStatic
    fun processDeclarations(
        stabOperation: ElixirStabOperation,
        processor: PsiScopeProcessor,
        state: ResolveState,
        @Suppress("UNUSED_PARAMETER") lastParent: PsiElement,
        @Suppress("UNUSED_PARAMETER") place: PsiElement
    ): Boolean {
        var keepProcessing = true
        val signature = stabOperation.leftOperand()

        if (signature != null) {
            val declaringScope = isDeclaringScope(stabOperation)

            keepProcessing = processor.execute(signature, state.put(DECLARING_SCOPE, declaringScope))
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(
        match: Match,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        @Suppress("UNUSED_PARAMETER") place: PsiElement
    ): Boolean {
        val rightOperand = match.rightOperand()
        val leftOperand = match.leftOperand()
        var checkRight = false
        var checkLeft = false

        val triple = Triple(match.children)
        val position = triple.ancestorPosition(lastParent)

        if (position != null) {
            when (position) {
                Position.LEFT -> {
                    checkLeft = true
                    checkRight = false
                }
                Position.OPERATOR -> {
                    checkLeft = true
                    checkRight = true
                }
                Position.RIGHT -> {
                    checkLeft = false
                    checkRight = true
                }
            }
        } else {
            checkLeft = true
            checkRight = true
        }

        var keepProcessing = true

        if (checkRight || checkLeft) {
            // check right-operand first if both sides need to be checked because only left-side can do rebinding
            if (checkRight && rightOperand != null) {
                keepProcessing = processor.execute(rightOperand, state)
            }

            if (checkLeft && leftOperand != null && keepProcessing) {
                keepProcessing = processor.execute(leftOperand, state)
            }
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(
        type: Type,
        processor: PsiScopeProcessor,
        state: ResolveState
    ): Boolean =
        type
            .leftOperand()
            ?.let { processor.execute(it, state) }
            ?: true

    @JvmStatic
    fun processDeclarations(
        qualifiedAlias: QualifiedAlias,
        processor: PsiScopeProcessor,
        state: ResolveState,
        @Suppress("UNUSED_PARAMETER") lastParent: PsiElement,
        @Suppress("UNUSED_PARAMETER") place: PsiElement
    ): Boolean {
        return processor.execute(qualifiedAlias, state)
    }

    // Private Functions

    /**
     * @see [Elixir Scoping](https://elixir-lang.readthedocs.io/en/latest/technical/scoping.html)
     *
     * @see [](https://github.com/alco/elixir/wiki/Scoping-Rules-in-Elixir-
    ) */
    private fun createsNewScope(element: PsiElement): Boolean {
        return selector(element) == UseScopeImpl.UseScopeSelector.SELF
    }

    @JvmStatic
    fun isDeclaringScope(stabOperation: ElixirStabOperation): Boolean {
        var declaringScope = true
        val parent = stabOperation.parent

        if (parent is ElixirStab) {
            val grandParent = parent.getParent()

            if (grandParent is ElixirBlockItem) {
                val blockIdentifier = grandParent.blockIdentifier

                val blockIdentifierText = blockIdentifier.text

                /* `after` is not a declaring scope because the timeout value does not need to be pinned even though it
                    must be a literal or declared in an outer scope */
                if (blockIdentifierText == "after") {
                    declaringScope = false
                }
            } else if (grandParent is ElixirDoBlock) {
                val call = grandParent.getParent() as Call

                if (call.isCalling(KERNEL, COND)) {
                    declaringScope = false
                }
            }
        }

        return declaringScope
    }

    /**
     * Processes declarations in siblings of `lastParent` backwards from `lastParent`.
     *
     * @param scope an [ElixirStabBody] or [ElixirFile] that has a sequence of expressions as children
     */
    private fun processDeclarationsInPreviousSibling(
        scope: PsiElement,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        if (scope.isEquivalentTo(lastParent.parent)) {
            lastParent
                .siblingExpressions(forward = false, withSelf = false)
                .let { processDeclarations(it, processor, state, lastParent, place) }
        } else {
            if (lastParent !is ElixirFile) {
                org.elixir_lang.errorreport.Logger.error(
                    PsiElement::class.java,
                    "Scope is not lastParent's parent\nlastParent:\n" + lastParent.text,
                    scope
                )
            }

            true
        }

    fun processDeclarations(
        sequence: Sequence<PsiElement>,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean =
        sequence
            .filter { !createsNewScope(it) }
            .map {
                it.processDeclarations(processor, state, lastParent, place)
            }
            .takeWhile { it }
            .lastOrNull()
            ?: true

    private fun processDeclarationsRecursively(
        psiElement: PsiElement,
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement,
        place: PsiElement
    ): Boolean {
        var keepProcessing = processor.execute(psiElement, state)

        if (keepProcessing) {
            var child: PsiElement? = psiElement.firstChild

            while (child !== null && child !== lastParent) {
                if (!child.processDeclarations(processor, state, lastParent, place)) {
                    keepProcessing = false

                    break
                }

                child = child.nextSibling
            }
        }

        return keepProcessing
    }
}
