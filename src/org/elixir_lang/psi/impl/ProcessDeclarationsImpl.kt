package org.elixir_lang.psi.impl

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.ResolveState
import com.intellij.psi.impl.source.tree.CompositeElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.siblings
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Function.*
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.CallImpl.hasDoBlockOrKeyword
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.declarations.UseScopeImpl
import org.elixir_lang.psi.impl.declarations.UseScopeImpl.selector
import org.elixir_lang.psi.operation.And
import org.elixir_lang.psi.operation.Match
import org.elixir_lang.psi.operation.Normalized
import org.elixir_lang.psi.operation.infix.Position
import org.elixir_lang.psi.operation.infix.Triple
import org.elixir_lang.structure_view.element.Delegation
import org.elixir_lang.structure_view.element.modular.Module

object ProcessDeclarationsImpl {
    @JvmField
    val DECLARING_SCOPE = Key<Boolean>("DECLARING_SCOPE")

    /**
     * `{:ok, value} = func() && value == literal`
     */
    @JvmStatic
    fun processDeclarations(and: And,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement?,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean {
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

    /**
     * `def(macro)?p?`, `for`, or `with` can declare variables
     */
    @JvmStatic
    fun processDeclarations(call: Call,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement?,
                            place: PsiElement): Boolean {
        var keepProcessing = true

        // need to check if call is place because lastParent is set to place at start of treeWalkUp
        if (!call.isEquivalentTo(lastParent) || call.isEquivalentTo(place)) {
            when {
                call.isCalling(KERNEL, ALIAS) -> keepProcessing = processor.execute(call, state)
                CallDefinitionClause.`is`(call) || // call parameters
                        Delegation.`is`(call) || // delegation call parameters
                        Module.`is`(call) || // module Alias
                        call.isCalling(KERNEL, DESTRUCTURE) || // left operand
                        call.isCallingMacro(KERNEL, IF) || // match in condition
                        call.isCallingMacro(KERNEL, Function.FOR) || // comprehension match variable
                        call.isCallingMacro(KERNEL, UNLESS) || // match in condition
                        call.isCallingMacro(KERNEL, "with") // <- or = variable
                -> keepProcessing = processor.execute(call, state)
                QuoteMacro.`is`(call) -> { // quote :bind_quoted keys{
                    val bindQuoted = call.keywordArgument("bind_quoted")
                    /* the bind_quoted keys declare variable only valid inside the do block, so any place in the
                       bindQuoted already must be the bind_quoted values that must be declared before the quote */
                    if (bindQuoted != null && !PsiTreeUtil.isAncestor(bindQuoted, place, false)) {
                        keepProcessing = processor.execute(call, state)
                    }
                }
                hasDoBlockOrKeyword(call) -> // unknown macros that take do blocks often allow variables to be declared in their arguments
                    keepProcessing = processor.execute(call, state)
            }
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(alias: ElixirAlias,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            place: PsiElement): Boolean =
            processDeclarationsRecursively(
                    alias,
                    processor,
                    state,
                    lastParent,
                    place
            )

    @JvmStatic
    fun processDeclarations(file: ElixirFile,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean {
        val keepProcessing = processDeclarationsInPreviousSibling(file, processor, state, lastParent)

        if (keepProcessing) {
            processor.execute(file, state)
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(multipleAliases: ElixirMultipleAliases,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            entrance: PsiElement): Boolean {
        return processDeclarationsRecursively(
                multipleAliases,
                processor,
                state,
                lastParent,
                entrance
        )
    }

    @JvmStatic
    fun processDeclarations(scope: ElixirEex,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean =
            processDeclarationsInPreviousSibling(scope, processor, state, lastParent)

    @JvmStatic
    fun processDeclarations(scope: ElixirEexTag,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            place: PsiElement): Boolean =
        if (scope.isEquivalentTo(lastParent.parent)) {
            processDeclarationsInPreviousSibling(scope, processor, state, lastParent)
        } else {
            scope
                    .lastChild
                    .siblings(forward = false)
                    .let { processDeclarations(it, processor, state) }
        }

    @JvmStatic
    fun processDeclarations(scope: ElixirStabBody,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean =
            processDeclarationsInPreviousSibling(scope, processor, state, lastParent)

    @JvmStatic
    fun processDeclarations(stabOperation: ElixirStabOperation,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            @Suppress("UNUSED_PARAMETER") lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean {
        var keepProcessing = true
        val signature = stabOperation.leftOperand()

        if (signature != null) {
            val declaringScope = isDeclaringScope(stabOperation)

            keepProcessing = processor.execute(signature, state.put(DECLARING_SCOPE, declaringScope))
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(match: Match,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean {
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
        } else if (PsiTreeUtil.isAncestor(match, lastParent, false)) {
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
        } else {
            org.elixir_lang.errorreport.Logger.error(
                    Match::class.java,
                    "Could not determine whether to check left operand, right operand, or both of match, " + "so checking none when processing declarations",
                    match
            )
        }

        return keepProcessing
    }

    @JvmStatic
    fun processDeclarations(qualifiedAlias: QualifiedAlias,
                            processor: PsiScopeProcessor,
                            state: ResolveState,
                            @Suppress("UNUSED_PARAMETER") lastParent: PsiElement,
                            @Suppress("UNUSED_PARAMETER") place: PsiElement): Boolean {
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
    private fun processDeclarationsInPreviousSibling(scope: PsiElement,
                                                     processor: PsiScopeProcessor,
                                                     state: ResolveState,
                                                     lastParent: PsiElement): Boolean =
        if (scope.isEquivalentTo(lastParent.parent)) {
            lastParent
                    .siblings(forward = false, withSelf = false)
                    .let { processDeclarations(it, processor, state) }
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

    private fun processDeclarations(sequence: Sequence<PsiElement>, processor: PsiScopeProcessor, state: ResolveState): Boolean =
            sequence
                    .filter { it.node is CompositeElement }
                    .filter { !(it is ElixirEndOfExpression || it is PsiComment || it is PsiWhiteSpace) }
                    .filter { !createsNewScope(it) }
                    .map { processor.execute(it, state) }
                    .takeWhile { it }
                    .lastOrNull()
                    ?: true

    private fun processDeclarationsRecursively(psiElement: PsiElement,
                                               processor: PsiScopeProcessor,
                                               state: ResolveState,
                                               lastParent: PsiElement,
                                               place: PsiElement): Boolean {
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
