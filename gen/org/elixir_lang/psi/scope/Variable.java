package org.elixir_lang.psi.scope;

import com.intellij.openapi.util.Key;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.call.name.Function;
import org.elixir_lang.psi.call.name.Module;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.psi.operation.Type;
import org.elixir_lang.reference.Callable;
import org.elixir_lang.structure_view.element.CallDefinitionHead;
import org.elixir_lang.structure_view.element.Delegation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.lang.parser.GeneratedParserUtilBase.DUMMY_BLOCK;
import static org.elixir_lang.psi.call.name.Function.*;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.*;
import static org.elixir_lang.psi.impl.ProcessDeclarationsImpl.DECLARING_SCOPE;
import static org.elixir_lang.psi.impl.ProcessDeclarationsImpl.isDeclaringScope;
import static org.elixir_lang.psi.impl.PsiElementImplKt.stripAccessExpression;
import static org.elixir_lang.psi.impl.call.CallImplKt.finalArguments;
import static org.elixir_lang.psi.impl.call.CallImplKt.keywordArgument;
import static org.elixir_lang.psi.operation.Normalized.operatorIndex;
import static org.elixir_lang.psi.operation.infix.Normalized.leftOperand;
import static org.elixir_lang.psi.operation.infix.Normalized.rightOperand;

public abstract class Variable implements PsiScopeProcessor {
    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * @param element candidate element.
     * @param state   current state of resolver.
     * @return false to stop processing.
     */
    @Override
    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (element instanceof Addition || element instanceof And) {
            keepProcessing = executeNonDeclaringScopeInfix((Infix) element, state);
        } else if (element instanceof ElixirAccessExpression ||
                element instanceof ElixirAssociations ||
                element instanceof ElixirAssociationsBase ||
                element instanceof ElixirBitString ||
                element instanceof ElixirEexTag ||
                element instanceof ElixirList ||
                element instanceof ElixirMapConstructionArguments ||
                element instanceof ElixirMultipleAliases ||
                element instanceof ElixirNoParenthesesArguments ||
                element instanceof ElixirNoParenthesesOneArgument ||
                element instanceof ElixirParenthesesArguments ||
                element instanceof ElixirParentheticalStab ||
                element instanceof ElixirStab ||
                element instanceof ElixirStabBody ||
                element instanceof ElixirTuple) {
            keepProcessing = execute(element.getChildren(), state);
        } else if (element instanceof ElixirContainerAssociationOperation) {
            keepProcessing = execute((ElixirContainerAssociationOperation) element, state);
        } else if (element instanceof ElixirMapArguments) {
            keepProcessing = execute((ElixirMapArguments) element, state);
        } else if (element instanceof ElixirMapOperation) {
            keepProcessing = execute((ElixirMapOperation) element, state);
        } else if (element instanceof ElixirMatchedWhenOperation) {
            keepProcessing = execute((ElixirMatchedWhenOperation) element, state);
        } else if (element instanceof ElixirStabOperation) {
            keepProcessing = execute((ElixirStabOperation) element, state);
        } else if (element instanceof ElixirStabNoParenthesesSignature) {
            keepProcessing = execute((ElixirStabNoParenthesesSignature) element, state);
        } else if (element instanceof ElixirStabParenthesesSignature) {
            keepProcessing = execute((ElixirStabParenthesesSignature) element, state);
        } else if (element instanceof ElixirStructOperation) {
            keepProcessing = execute((ElixirStructOperation) element, state);
        } else if (element instanceof ElixirVariable) {
            keepProcessing = executeOnVariable((PsiNamedElement) element, state);
        } else if (element instanceof In) {
            keepProcessing = execute((In) element, state);
        } else if (element instanceof InMatch) { // MUST be before Call as InMatch is a Call
            keepProcessing = execute((InMatch) element, state);
        } else if (element instanceof Match) {
            keepProcessing = execute((Match) element, state);
        } else if (element instanceof And ||
                element instanceof Pipe ||
                element instanceof Two) {
            keepProcessing = execute((Infix) element, state);
        } else if (element instanceof Type) {
            keepProcessing = execute((Type) element, state);
        } else if (element instanceof UnaryNonNumericOperation) {
            keepProcessing = execute((UnaryNonNumericOperation) element, state);
        } else if (element instanceof UnqualifiedNoArgumentsCall) {
            keepProcessing = executeOnMaybeVariable((UnqualifiedNoArgumentsCall) element, state);
        } else if (element instanceof Call) {
            keepProcessing = execute((Call) element, state);
        } else if (element instanceof QualifiedMultipleAliases) {
            /* Occurs when qualified call occurs over a line with assignment to a tuple, such as
               `Qualifier.\n{:ok, value} = call()` */
            keepProcessing = execute((QualifiedMultipleAliases) element, state);
        } else if (element instanceof PsiFile) {
            // stop at file.  No reason to look in directories
            keepProcessing = false;
        } else if (element instanceof QuotableKeywordList) {
            /* KeywordLists happen in map, struct and {@code do: <body>} matches, while KeywordKey happens only in
               bindQuoted */
            keepProcessing = execute((QuotableKeywordList) element, state);
        } else {
            if (!(element instanceof AtNonNumericOperation || // a module attribute reference
                    element instanceof AtUnqualifiedBracketOperation || // a module attribute reference with access
                    element instanceof Heredoc ||
                    element instanceof BracketOperation ||
                    /* an anonymous function is a new scope, so it can't be used to declare a variable.  This won't ever
                       be hit if the element is declared in the {@code fn} signature because that upward resolution
                       from resolveVariable stops before this level */
                    element instanceof ElixirAnonymousFunction ||
                    element instanceof ElixirAtom ||
                    element instanceof ElixirAtomKeyword ||
                    element instanceof ElixirCharToken ||
                    element instanceof ElixirDecimalFloat ||
                    element instanceof ElixirEmptyParentheses ||
                    element instanceof ElixirEndOfExpression ||
                    // noParenthesesManyStrictNoParenthesesExpression exists only to be marked as an error
                    element instanceof ElixirNoParenthesesManyStrictNoParenthesesExpression ||
                    element instanceof LeafPsiElement ||
                    element instanceof Line ||
                    element instanceof PsiErrorElement ||
                    element instanceof PsiWhiteSpace ||
                    element instanceof QualifiableAlias ||
                    element instanceof QualifiedBracketOperation ||
                    element instanceof UnqualifiedBracketOperation ||
                    element instanceof WholeNumber ||
                    element.getNode().getElementType().equals(DUMMY_BLOCK))) {
                Logger.error(Callable.class, "Don't know how to resolve variable in match", element);
            }
        }

        return keepProcessing;
    }

    @Nullable
    @Override
    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }

    @Override
    public void handleEvent(@NotNull Event event, @Nullable Object associated) {
    }

    /*
     * Protected Instance Methods
     */

    /**
     * Decides whether {@code match} matches the criteria being searched for.  All other {@link #execute} methods
     * eventually end here.
     *
     * @return {@code true} to keep processing; {@code false} to stop processing.
     */
    protected abstract boolean executeOnVariable(@NotNull final PsiNamedElement match, @NotNull ResolveState state);

    protected boolean isInDeclaringScope(@NotNull Call call, @NotNull ResolveState state) {
        Boolean declaringScope = state.get(DECLARING_SCOPE);
        boolean inDeclaringScope;

        if (declaringScope != null) {
            inDeclaringScope = declaringScope;
        } else {
            inDeclaringScope = false;
            PsiElement maybeDeclaringScopeContext = PsiTreeUtil.getContextOfType(
                    call,
                    false,
                    ElixirStabOperation.class,
                    InMatch.class
            );

            if (maybeDeclaringScopeContext != null) {
                if (maybeDeclaringScopeContext instanceof ElixirStabOperation) {
                    ElixirStabOperation stabOperation = (ElixirStabOperation) maybeDeclaringScopeContext;
                    PsiElement signature = stabOperation.leftOperand();

                    if (PsiTreeUtil.isAncestor(signature, call, false)) {
                        inDeclaringScope = isDeclaringScope(stabOperation);
                    }
                } else if (maybeDeclaringScopeContext instanceof InMatch) {
                    InMatch inMatch = (InMatch) maybeDeclaringScopeContext;

                    if (PsiTreeUtil.isAncestor(inMatch.leftOperand(), call, false)) {
                        inDeclaringScope = true;
                    }
                }
            }
        }

        return inDeclaringScope;
    }

    /*
     * Private Instance Methods
     */

    private boolean execute(@NotNull final Call match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        if (org.elixir_lang.psi.CallDefinitionClause.is(match)) {
            PsiElement head = org.elixir_lang.psi.CallDefinitionClause.head(match);

            if (head != null) {
                PsiElement stripped = CallDefinitionHead.Companion.strip(head);

                if (stripped instanceof AtNonNumericOperation) {
                    AtNonNumericOperation strippedAtNonNumericOperation = (AtNonNumericOperation) stripped;

                    PsiElement operand = strippedAtNonNumericOperation.operand();

                    if (operand instanceof ElixirAccessExpression) {
                        keepProcessing = execute(operand, state);
                    }
                } else if (stripped instanceof Call) {
                    keepProcessing = executeStrippedCallDefinitionHead((Call) stripped, state);
                }
            }
        } else if (Delegation.is(match)) {
            for (Call callDefinitionHead : Delegation.callDefinitionHeadCallList(match)) {
                keepProcessing = executeStrippedCallDefinitionHead(callDefinitionHead, state);

                if (!keepProcessing) {
                    break;
                }
            }
        } else if  (match.isCalling(Module.KERNEL, Function.DESTRUCTURE, 2)) {
            PsiElement[] finalArguments = finalArguments(match);

            if (finalArguments != null) {
                keepProcessing = execute(finalArguments[0], state.put(DECLARING_SCOPE, true));
            }
        } else if (match.isCallingMacro(Module.KERNEL, Function.FOR) || match.isCallingMacro(Module.KERNEL, "with")) {
            PsiElement[] finalArguments = finalArguments(match);

            if (finalArguments != null) {
                PsiElement entrance = state.get(ENTRANCE);
                /* if the entrance isn't in the arguments, then it is part of the block and so search should start from
                   from the last argument */
                int entranceArgumentIndex = finalArguments.length - 1;

                // have to check in reverse order as a variable can be rebound in the `<-` and `=` of a `for` or `with`
                for (int i = finalArguments.length - 1; i >= 0; i--) {
                    if (PsiTreeUtil.isAncestor(finalArguments[i], entrance, false)) {
                        entranceArgumentIndex = i;
                        break;
                    }
                }

                for (int i = entranceArgumentIndex; i >= 0; i--) {
                    PsiElement finalArgument = finalArguments[i];

                    /* force to be non-declaring scope, so that variable {@code do} body ({@code a} in
                       {@code for a <- b, do: a}) will look in early arguments for declaration */
                    keepProcessing = execute(finalArgument, state.put(DECLARING_SCOPE, false));

                    if (!keepProcessing) {
                        break;
                    }
                }
            }
        } else if (match.isCallingMacro(Module.KERNEL, CASE) ||
                match.isCallingMacro(Module.KERNEL, COND) ||
                match.isCallingMacro(Module.KERNEL, IF) ||
                match.isCallingMacro(Module.KERNEL, UNLESS)) {
            PsiElement[] finalArguments = finalArguments(match);

            if (finalArguments != null && finalArguments.length > 0) {
                keepProcessing = execute(
                        finalArguments[0],
                        /* prevents variable condition (`if foo do .. end`) from counting as declaration of that
                           variable (`foo`) */
                        state.put(DECLARING_SCOPE, false)
                );
            }
        } else if (QuoteMacro.is(match)) {
            PsiElement bindQuoted = keywordArgument(match, "bind_quoted");

            if (bindQuoted instanceof ElixirAccessExpression) {
                PsiElement child = stripAccessExpression(bindQuoted);

                if (child instanceof ElixirList) {
                    ElixirList list = (ElixirList) child;

                    PsiElement[] listChildren = list.getChildren();

                    if (listChildren.length == 1) {
                        PsiElement listChild = listChildren[0];

                        if (listChild instanceof ElixirKeywords) {
                            ElixirKeywords bindQuotedKeywords = (ElixirKeywords) listChild;

                            List<ElixirKeywordPair> keywordPairList = bindQuotedKeywords.getKeywordPairList();

                            for (ElixirKeywordPair keywordPair : keywordPairList) {
                                keepProcessing = executeOnVariable(keywordPair.getKeywordKey(), state);
                            }
                        }
                    }
                }
            }
        } else if (hasDoBlockOrKeyword(match)) {
            PsiElement[] finalArguments = finalArguments(match);

            if (finalArguments != null) {
                ResolveState macroArgumentsState = state.put(DECLARING_SCOPE, true);

                for (PsiElement finalArgument : finalArguments) {
                    keepProcessing = execute(finalArgument, macroArgumentsState);

                    if (!keepProcessing) {
                        break;
                    }
                }
            }
        } else {
            // unquote(var) can't declare var, only use it
            if (!match.isCalling(Module.KERNEL, UNQUOTE, 1)) {
                int resolvedFinalArity = match.resolvedFinalArity();

                // UnqualifiedNorArgumentsCall prevents `foo()` from being treated as a variable.
                // resolvedFinalArity prevents `|> foo` from being counted as 0-arity
                if (match instanceof UnqualifiedNoArgumentsCall && resolvedFinalArity == 0) {
                    keepProcessing = executeOnVariable((PsiNamedElement) match, state);
                } else if (maybeMacro(match, state)) {
                    /* macros uses in stab signatures see
                       @see https://github.com/elixir-lang/elixir/blob/0c9e72c8d7be3ee502c43762e0ccbbf244198aeb/lib/elixir/lib/stream/reducers.ex#L7 */
                    PsiElement[] finalArguments = finalArguments(match);

                    if (finalArguments != null) {
                        keepProcessing = execute(finalArguments, state);
                    }
                }
            }
        }

        return keepProcessing;
    }

    /**
     * Only checks the right operand of the container association operation because the left operand is either a literal
     * or a pinned variable, which means the variable is being used and was declared elsewhere.
     */
    private boolean execute(@NotNull final ElixirContainerAssociationOperation match, @NotNull ResolveState state) {
        boolean keepProcessing = true;
        PsiElement[] children = match.getChildren();

        if (children.length > 1) {
            keepProcessing = execute(children[1], state);
        }

        return keepProcessing;
    }

    /**
     * Only checks {@link ElixirMapArguments#getMapConstructionArguments()} and not
     * {@link ElixirMapArguments#getMapUpdateArguments()} since an update is not valid in a pattern match.
     */
    private boolean execute(@NotNull ElixirMapArguments match, @NotNull ResolveState state) {
        boolean keepProcessing = true;
        ElixirMapConstructionArguments mapConstructionArguments = match.getMapConstructionArguments();

        if (mapConstructionArguments != null) {
            keepProcessing = execute(mapConstructionArguments, state);
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull ElixirMapOperation match, @NotNull ResolveState state) {
        return execute(match.getMapArguments(), state);
    }

    private boolean execute(@NotNull ElixirMatchedWhenOperation match, @NotNull ResolveState state) {
        return executeLeftOperand(match, state);
    }

    private boolean execute(@NotNull ElixirStabNoParenthesesSignature match, @NotNull ResolveState state) {
        return execute(match.getNoParenthesesArguments(), state);
    }

    private boolean execute(@NotNull ElixirStabOperation match, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        PsiElement[] children = match.getChildren();
        int operatorIndex = operatorIndex(children);
        PsiElement leftOperand = leftOperand(children, operatorIndex);

        if (leftOperand != null) {
            keepProcessing = execute(leftOperand, state);
        }

        if (keepProcessing) {
            PsiElement rightOperand = rightOperand(children, operatorIndex);

            if (rightOperand != null) {
                keepProcessing = execute(rightOperand, state);
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull ElixirStabParenthesesSignature match, @NotNull ResolveState state) {
        return execute(match.getParenthesesArguments(), state);
    }

    private boolean execute(@NotNull ElixirStructOperation match, @NotNull ResolveState state) {
        return execute(match.getMapArguments(), state);
    }

    /**
     * {@code in} can declare variable for {@code rescue} clauses like {@code rescue e in RuntimeException ->}
     */
    private boolean execute(@NotNull In match, @NotNull ResolveState state) {
        return executeLeftOperand(match, state);
    }

    /**
     * Infix operations where either side can declare a variable in a match
     */
    private boolean execute(@NotNull Infix match, @NotNull ResolveState state) {
        boolean keepProcessing = executeLeftOperand(match, state);

        if (keepProcessing) {
            PsiElement rightOperand = match.rightOperand();

            if (rightOperand != null) {
                keepProcessing = execute(rightOperand, state);
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull InMatch match, @NotNull ResolveState state) {
        Operator operator = match.operator();
        String operatorText = operator.getText();
        boolean keepProcessing = true;

        if (operatorText.equals(DEFAULT_OPERATOR)) {
            PsiElement defaulted = match.leftOperand();

            if (defaulted instanceof PsiNamedElement) {
                keepProcessing = executeOnVariable((PsiNamedElement) defaulted, state);
            }
        } else if (operatorText.equals("<-")) {
            PsiElement entrance = state.get(ENTRANCE);
            PsiElement rightOperand = match.rightOperand();

            // variable on right of <- can't be declared on left because right expression generates left expression
            if (!PsiTreeUtil.isAncestor(rightOperand, entrance, false)) {
                // counter {@code state.put(DECLARING_SCOPE, false)} in #boolean(Call, Resolve) for {@code for}
                keepProcessing = executeLeftOperand(match, state.put(DECLARING_SCOPE, true));
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull Match match, @NotNull ResolveState state) {
        /* ensure DECLARING_SCOPE is `true` to counter `DECLARING_SCOPE` being `false` for -> signature under
           `after` or `cond` */
        ResolveState matchState = state.put(DECLARING_SCOPE, true);
        return execute((Infix) match, matchState);
    }

    private boolean execute(@NotNull PsiElement[] parameters, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        for (PsiElement parameter : parameters) {
            if (parameter != null) {
                keepProcessing = execute(parameter, state);

                if (!keepProcessing) {
                    break;
                }
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull QualifiedMultipleAliases match, @NotNull ResolveState state) {
        PsiElement[] children = match.getChildren();

        assert children.length == 3;

        // MultipleAliases assumed to be a tuple on next line
        return execute(children[children.length - 1], state);
    }

    private boolean execute(@NotNull QuotableKeywordList match, @NotNull ResolveState state) {
        List<QuotableKeywordPair> keywordPairList = match.quotableKeywordPairList();
        boolean keepProcessing = true;

        for (QuotableKeywordPair keywordPair : keywordPairList) {
            keepProcessing = execute(keywordPair, state);

            if (!keepProcessing) {
                break;
            }
        }

        return keepProcessing;
    }

    private boolean execute(@NotNull QuotableKeywordPair match, @NotNull ResolveState state) {
        return execute(match.getKeywordValue(), state);
    }

    private boolean execute(@NotNull org.elixir_lang.psi.operation.Type match, @NotNull ResolveState state) {
        return executeLeftOperand(match, state);
    }

    private boolean execute(@NotNull UnaryNonNumericOperation match, @NotNull ResolveState state) {
        Operator operator = match.operator();
        String operatorText = operator.getText();
        boolean keepProcessing = true;

        // pinned expressions cannot be declared at pin site
        if (!operatorText.equals("^")) {
            keepProcessing = execute((Call) match, state);
        }

        return keepProcessing;
    }

    private boolean executeLeftOperand(@NotNull Infix match, @NotNull ResolveState state) {
        boolean keepProcessing = true;
        PsiElement leftOperand = match.leftOperand();

        if (leftOperand != null) {
            keepProcessing = execute(leftOperand, state);
        }

        return keepProcessing;
    }

    /**
     * Turns off any DECLARING_SCOPE because the {@link Infix} subclass can never be used to declare a variable in a
     * match.
     *
     * The rule is, if a lone variable by itself as an operand would declare that variable in a match then call,
     * {@link #execute(Infix, ResolveState)}; otherwise, call this method.
     */
    private boolean executeNonDeclaringScopeInfix(@NotNull final Infix match, @NotNull ResolveState state) {
       return execute(match, state.put(DECLARING_SCOPE, false));
    }

    private boolean executeOnMaybeVariable(@NotNull UnqualifiedNoArgumentsCall match, @NotNull ResolveState state) {
        boolean keepProcessing;

        // ignore piped no argument calls that really have arity-1
        if (match.resolvedFinalArity() == 0) {
            keepProcessing = executeOnVariable(match, state);
        } else {
            keepProcessing = execute(match, state);
        }

        return keepProcessing;
    }

    private boolean executeStrippedCallDefinitionHead(@NotNull Call strippedCallDefinitionHead, @NotNull ResolveState state) {
        boolean keepProcessing = true;

        PsiElement[] finalArguments = finalArguments(strippedCallDefinitionHead);

        if (finalArguments != null) {
            // set scope to declaring so that calls inside the arguments are treated as maybe macros
            keepProcessing = execute(finalArguments, state.put(DECLARING_SCOPE, true));
        }

        return keepProcessing;
    }

    private boolean maybeMacro(@NotNull Call call, @NotNull ResolveState state) {
        return !hasDoBlockOrKeyword(call) && isInDeclaringScope(call, state);
    }
}
