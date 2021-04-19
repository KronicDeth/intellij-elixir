package org.elixir_lang.psi.scope

import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.CallDefinitionClause.head
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Module
import org.elixir_lang.psi.impl.ElixirPsiImplUtil
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl.DECLARING_SCOPE
import org.elixir_lang.psi.impl.ProcessDeclarationsImpl.isDeclaringScope
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.keywordArgument
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.*
import org.elixir_lang.psi.operation.Normalized.operatorIndex
import org.elixir_lang.psi.operation.Type
import org.elixir_lang.psi.operation.infix.Normalized
import org.elixir_lang.psi.scope.WhileIn.whileIn
import org.elixir_lang.reference.Callable
import org.elixir_lang.structure_view.element.CallDefinitionHead.Companion.strip
import org.elixir_lang.structure_view.element.Delegation
import org.elixir_lang.structure_view.element.Delegation.Companion.callDefinitionHeadCallList

abstract class Variable : PsiScopeProcessor {
    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    override fun execute(element: PsiElement, state: ResolveState): Boolean =
            when (element) {
                is Addition, is And -> executeNonDeclaringScopeInfix(element as Infix, state)
                is ElixirAccessExpression, is ElixirAssociations, is ElixirAssociationsBase, is ElixirBitString,
                is ElixirEexTag, is ElixirList, is ElixirMapConstructionArguments, is ElixirMultipleAliases,
                is ElixirNoParenthesesArguments, is ElixirNoParenthesesOneArgument, is ElixirParenthesesArguments,
                is ElixirParentheticalStab, is ElixirStab, is ElixirStabBody, is ElixirTuple -> {
                    execute(element.children, state)
                }
                is ElixirContainerAssociationOperation -> execute(element, state)
                is ElixirMapArguments -> execute(element, state)
                is ElixirMapOperation -> execute(element, state)
                is ElixirMatchedWhenOperation -> execute(element, state)
                is ElixirStabOperation -> execute(element, state)
                is ElixirStabNoParenthesesSignature -> execute(element, state)
                is ElixirStabParenthesesSignature -> execute(element, state)
                is ElixirStructOperation -> execute(element, state)
                is ElixirVariable -> executeOnVariable(element as PsiNamedElement, state)
                is In -> execute(element, state)
                // MUST be before Call as InMatch is a Call
                is InMatch -> execute(element, state)
                is Match -> execute(element, state)
                is Pipe, is Two -> execute(element as Infix, state)
                is Type -> execute(element, state)
                is UnaryNonNumericOperation -> execute(element, state)
                is UnqualifiedNoArgumentsCall<*> -> executeOnMaybeVariable(element, state)
                is Call -> execute(element, state)
                /* Occurs when qualified call occurs over a line with assignment to a tuple, such as
                   `Qualifier.\n{:ok, value} = call()` */
                is QualifiedMultipleAliases -> execute(element, state)
                // stop at file.  No reason to look in directories
                is PsiFile -> false
                /* KeywordLists happen in map, struct and {@code do: <body>} matches, while KeywordKey happens only in
                   bindQuoted */
                is QuotableKeywordList -> execute(element, state)
                else -> {
                    if (!(element is AtNonNumericOperation ||  // a module attribute reference
                                    element is AtUnqualifiedBracketOperation ||  // a module attribute reference with access
                                    element is Heredoc ||
                                    element is BracketOperation ||  /* an anonymous function is a new scope, so it can't be used to declare a variable.  This won't ever
                           be hit if the element is declared in the {@code fn} signature because that upward resolution
                           from resolveVariable stops before this level */
                                    element is ElixirAnonymousFunction ||
                                    element is ElixirAtom ||
                                    element is ElixirAtomKeyword ||
                                    element is ElixirCharToken ||
                                    element is ElixirDecimalFloat ||
                                    element is ElixirEmptyParentheses ||
                                    element is ElixirEndOfExpression ||  // noParenthesesManyStrictNoParenthesesExpression exists only to be marked as an error
                                    element is ElixirNoParenthesesManyStrictNoParenthesesExpression ||
                                    element is LeafPsiElement ||
                                    element is Line ||
                                    element is PsiErrorElement ||
                                    element is PsiWhiteSpace ||
                                    element is QualifiableAlias ||
                                    element is QualifiedBracketOperation ||
                                    element is UnqualifiedBracketOperation ||
                                    element is WholeNumber || element.node.elementType == GeneratedParserUtilBase.DUMMY_BLOCK)) {
                        Logger.error(Callable::class.java, "Don't know how to resolve variable in match", element)
                    }

                    true
                }
            }

    override fun <T> getHint(hintKey: Key<T>): T? = null
    override fun handleEvent(event: PsiScopeProcessor.Event, associated: Any?) {}

    /**
     * Decides whether `match` matches the criteria being searched for.  All other [.execute] methods
     * eventually end here.
     *
     * @return `true` to keep processing; `false` to stop processing.
     */
    protected abstract fun executeOnVariable(match: PsiNamedElement, state: ResolveState): Boolean

    protected fun isInDeclaringScope(call: Call, state: ResolveState): Boolean =
            state.get(DECLARING_SCOPE)
                    ?: PsiTreeUtil
                            .getContextOfType(
                                    call,
                                    false,
                                    ElixirStabOperation::class.java,
                                    InMatch::class.java
                            )
                            ?.let { maybeDeclaringScopeContext ->
                                if (maybeDeclaringScopeContext is ElixirStabOperation) {
                                    val stabOperation = maybeDeclaringScopeContext
                                    val signature: PsiElement? = stabOperation.leftOperand()
                                    if (PsiTreeUtil.isAncestor(signature, call, false)) {
                                        isDeclaringScope(stabOperation)
                                    } else {
                                        false
                                    }
                                } else if (maybeDeclaringScopeContext is InMatch) {
                                    if (PsiTreeUtil.isAncestor(maybeDeclaringScopeContext.leftOperand(), call, false)) {
                                        true
                                    } else {
                                        false
                                    }
                                } else {
                                    false
                                }
                            }
                    ?: false


    private fun execute(match: Call, state: ResolveState): Boolean =
            when {
                CallDefinitionClause.`is`(match) -> {
                    head(match)?.let { head ->
                        val stripped = strip(head)

                        when (stripped) {
                            is AtNonNumericOperation -> {
                                stripped
                                        .operand()
                                        .let { it as? ElixirAccessExpression }
                                        ?.let { execute(it, state) }
                            }
                            is Call -> executeStrippedCallDefinitionHead(stripped, state)
                            else -> null
                        }
                    }
                }
                Delegation.`is`(match) -> whileIn(callDefinitionHeadCallList(match)) {
                    executeStrippedCallDefinitionHead(it, state)
                }
                match.isCalling(Module.KERNEL, Function.DESTRUCTURE, 2) -> {
                    match.finalArguments()?.first()?.let { pattern ->
                        execute(pattern, state.put(DECLARING_SCOPE, true))
                    }
                }
                match.isCallingMacro(Module.KERNEL, Function.FOR) ||
                        match.isCallingMacro(Module.KERNEL, "with") -> {
                    match.finalArguments()?.let { finalArguments ->
                        val entrance = state.get(ElixirPsiImplUtil.ENTRANCE)
                        /* if the entrance isn't in the arguments, then it is part of the block and so search should start from
                       from the last argument */
                        var entranceArgumentIndex = finalArguments.size - 1

                        // have to check in reverse order as a variable can be rebound in the `<-` and `=` of a `for` or `with`
                        for (i in finalArguments.indices.reversed()) {
                            if (PsiTreeUtil.isAncestor(finalArguments[i], entrance, false)) {
                                entranceArgumentIndex = i
                                break
                            }
                        }

                        var keepProcessing = true

                        for (i in entranceArgumentIndex downTo 0) {
                            val finalArgument = finalArguments[i]

                            /* force to be non-declaring scope, so that variable {@code do} body ({@code a} in
                           {@code for a <- b, do: a}) will look in early arguments for declaration */
                            keepProcessing = execute(finalArgument, state.put(DECLARING_SCOPE, false))
                            if (!keepProcessing) {
                                break
                            }
                        }

                        keepProcessing
                    }
                }
                match.isCallingMacro(Module.KERNEL, Function.CASE) || match.isCallingMacro(Module.KERNEL, Function.COND) || match.isCallingMacro(Module.KERNEL, Function.IF) || match.isCallingMacro(Module.KERNEL, Function.UNLESS) -> {
                    match.finalArguments()?.firstOrNull()?.let {
                        execute(
                                it,
                                /* prevents variable condition (`if foo do .. end`) from counting as declaration of that
                               variable (`foo`) */
                                state.put(DECLARING_SCOPE, false)
                        )
                    }
                }
                QuoteMacro.`is`(match) -> {
                    match
                            .keywordArgument("bind_quoted")?.let { it as? ElixirAccessExpression }
                            ?.stripAccessExpression()
                            ?.let { it as? ElixirList }
                            ?.children?.singleOrNull()
                            ?.let { it as? ElixirKeywords }
                            ?.keywordPairList?.let { keywordPairList ->
                                whileIn(keywordPairList) { keywordPair ->
                                    executeOnVariable(keywordPair.keywordKey, state)
                                }
                            }
                }
                ElixirPsiImplUtil.hasDoBlockOrKeyword(match) -> {
                    match.finalArguments()?.let { finalArguments ->
                        val macroArgumentsState = state.put(DECLARING_SCOPE, true)

                        whileIn(finalArguments) {
                            execute(it, macroArgumentsState)
                        }
                    }
                }
                // The variable is used as compile-time in the body of a defmodule, but declared in a `use`, such
                // `if code_reloading? do` in `MyApp.Endpoint`, which is declared in `use Phoenix.Endpoint, ...` at
                // the top of `MyApp.Endpoint`
                Use.`is`(match) -> {
                    Use.treeWalkUp(match, state, ::execute)
                }
                org.elixir_lang.ecto.Query.isDeclaringMacro(match, state) -> {
                    org.elixir_lang.ecto.Query.treeWalkUp(match, state, ::execute)
                }
                else -> {
                    // unquote(var) can't declare var, only use it
                    if (!match.isCalling(Module.KERNEL, Function.UNQUOTE, 1)) {
                        val resolvedFinalArity = match.resolvedFinalArity()

                        // UnqualifiedNorArgumentsCall prevents `foo()` from being treated as a variable.
                        // resolvedFinalArity prevents `|> foo` from being counted as 0-arity
                        when {
                            match is UnqualifiedNoArgumentsCall<*> && resolvedFinalArity == 0 -> {
                                executeOnVariable(match as PsiNamedElement, state)
                            }
                            maybeMacro(match, state) -> {
                                /* macros uses in stab signatures see
                                           @see https://github.com/elixir-lang/elixir/blob/0c9e72c8d7be3ee502c43762e0ccbbf244198aeb/lib/elixir/lib/stream/reducers.ex#L7 */
                                match.finalArguments()?.let { execute(it, state) }
                            }
                            else -> {
                                null
                            }
                        }
                    } else {
                        null
                    }
                }
            } ?: true

    /**
     * Only checks the right operand of the container association operation because the left operand is either a literal
     * or a pinned variable, which means the variable is being used and was declared elsewhere.
     */
    private fun execute(match: ElixirContainerAssociationOperation, state: ResolveState): Boolean {
        val children = match.children

        return if (children.size > 1) {
            execute(children[1], state)
        } else {
            true
        }
    }

    /**
     * Only checks [ElixirMapArguments.getMapConstructionArguments] and not
     * [ElixirMapArguments.getMapUpdateArguments] since an update is not valid in a pattern match.
     */
    private fun execute(match: ElixirMapArguments, state: ResolveState): Boolean =
            match.mapConstructionArguments?.let {
                execute(it, state)
            } ?: true

    private fun execute(match: ElixirMapOperation, state: ResolveState): Boolean =
            execute(match.mapArguments, state)

    private fun execute(match: ElixirMatchedWhenOperation, state: ResolveState): Boolean =
            executeLeftOperand(match, state)

    private fun execute(match: ElixirStabNoParenthesesSignature, state: ResolveState): Boolean =
            execute(match.noParenthesesArguments, state)

    private fun execute(match: ElixirStabOperation, state: ResolveState): Boolean {
        val children = match.children
        val operatorIndex = operatorIndex(children)
        val leftOperandKeepProcessing = Normalized.leftOperand(children, operatorIndex)?.let { execute(it, state) }
                ?: true

        return leftOperandKeepProcessing &&
                Normalized.rightOperand(children, operatorIndex)?.let { execute(it, state) } ?: true
    }

    private fun execute(match: ElixirStabParenthesesSignature, state: ResolveState): Boolean =
            execute(match.parenthesesArguments, state)

    private fun execute(match: ElixirStructOperation, state: ResolveState): Boolean =
            execute(match.mapArguments, state)

    /**
     * `in` can declare variable for `rescue` clauses like `rescue e in RuntimeException ->`
     */
    private fun execute(match: In, state: ResolveState): Boolean =
            executeLeftOperand(match, state)

    /**
     * Infix operations where either side can declare a variable in a match
     */
    private fun execute(match: Infix, state: ResolveState): Boolean {
        var keepProcessing = executeLeftOperand(match, state)
        if (keepProcessing) {
            val rightOperand: PsiElement? = match.rightOperand()
            if (rightOperand != null) {
                keepProcessing = execute(rightOperand, state)
            }
        }
        return keepProcessing
    }

    private fun execute(match: InMatch, state: ResolveState): Boolean {
        val operator = match.operator()
        val operatorText = operator.text
        var keepProcessing = true
        if (operatorText == ElixirPsiImplUtil.DEFAULT_OPERATOR) {
            executeLeftOperand(match, state)
        } else if (operatorText == "<-") {
            val entrance = state.get(ElixirPsiImplUtil.ENTRANCE)
            val rightOperand: PsiElement? = match.rightOperand()

            // variable on right of <- can't be declared on left because right expression generates left expression
            if (!PsiTreeUtil.isAncestor(rightOperand, entrance, false)) {
                // counter {@code state.put(DECLARING_SCOPE, false)} in #boolean(Call, Resolve) for {@code for}
                keepProcessing = executeLeftOperand(match, state.put(DECLARING_SCOPE, true))
            }
        }
        return keepProcessing
    }

    private fun execute(match: Match, state: ResolveState): Boolean {
        /* ensure DECLARING_SCOPE is `true` to counter `DECLARING_SCOPE` being `false` for -> signature under
           `after` or `cond` */
        val matchState = state.put(DECLARING_SCOPE, true)
        return execute((match as Infix), matchState)
    }

    private fun execute(parameters: Array<PsiElement>, state: ResolveState): Boolean =
            whileIn(parameters) {
                execute(it, state)
            }

    private fun execute(match: QualifiedMultipleAliases, state: ResolveState): Boolean {
        val children = match.children
        assert(children.size == 3)

        // MultipleAliases assumed to be a tuple on next line
        return execute(children[children.size - 1], state)
    }

    private fun execute(match: QuotableKeywordList, state: ResolveState): Boolean {
        val keywordPairList = match.quotableKeywordPairList()

        return whileIn(keywordPairList) {
            execute(it, state)
        }
    }

    private fun execute(match: QuotableKeywordPair, state: ResolveState): Boolean =
            execute(match.keywordValue, state)

    private fun execute(match: Type, state: ResolveState): Boolean =
            executeLeftOperand(match, state)

    private fun execute(match: UnaryNonNumericOperation, state: ResolveState): Boolean {
        val operator = match.operator()
        val operatorText = operator.text

        // pinned expressions cannot be declared at pin site
        return operatorText != "^" && execute(match as Call, state)
    }

    private fun executeLeftOperand(match: Infix, state: ResolveState): Boolean =
            match.leftOperand()?.let { execute(it, state) } ?: true

    /**
     * Turns off any DECLARING_SCOPE because the [Infix] subclass can never be used to declare a variable in a
     * match.
     *
     * The rule is, if a lone variable by itself as an operand would declare that variable in a match then call,
     * [.execute]; otherwise, call this method.
     */
    private fun executeNonDeclaringScopeInfix(match: Infix, state: ResolveState): Boolean =
            execute(match, state.put(DECLARING_SCOPE, false))

    private fun executeOnMaybeVariable(match: UnqualifiedNoArgumentsCall<*>, state: ResolveState): Boolean =
            if (match.resolvedFinalArity() == 0) {
                executeOnVariable(match, state)
            } else {
                // ignore piped no argument calls that really have arity-1
                execute(match, state)
            }

    private fun executeStrippedCallDefinitionHead(strippedCallDefinitionHead: Call, state: ResolveState): Boolean =
            strippedCallDefinitionHead.finalArguments()?.let { finalArguments ->
                // set scope to declaring so that calls inside the arguments are treated as maybe macros
                execute(finalArguments, state.put(DECLARING_SCOPE, true))
            } ?: true


    private fun maybeMacro(call: Call, state: ResolveState): Boolean =
            !ElixirPsiImplUtil.hasDoBlockOrKeyword(call) && isInDeclaringScope(call, state)
}
