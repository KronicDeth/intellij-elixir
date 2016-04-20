package org.elixir_lang.reference;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xml.Resolve;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.psi.operation.*;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.CallDefinitionHead;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.DEFAULT_OPERATOR;

public class Callable extends PsiReferenceBase<Call> implements PsiPolyVariantReference {
    /*
     * CONSTANTS
     */

    private static final Condition<ResolveResult> HAS_VALID_RESULT_CONDITION = new Condition<ResolveResult>() {
        @Override
        public boolean value(ResolveResult resolveResult) {
            return resolveResult.isValidResult();
        }
    };
    public static final String IGNORED = "_";

    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    @Contract(pure = true)
    public static boolean isIgnored(@NotNull Call call) {
        boolean isIgnored = false;

        if (call instanceof UnqualifiedNoArgumentsCall) {
            String name = call.getName();

            if (name != null && name.equals(IGNORED)) {
                isIgnored = true;
            }
        }

        return isIgnored;
    }

    public static boolean isParameter(@NotNull Call call) {
        boolean isParameter;

        if (CallDefinitionClause.is(call)) {
            isParameter = true;
        } else {
            PsiElement parent = call.getParent();

            if (parent != null) {
                isParameter = isParameter(call.getParent());
            } else {
                isParameter = false;
            }
        }

        return isParameter;
    }

    public static boolean isParameterWithDefault(@NotNull Call call) {
        PsiElement parent = call.getParent();
        boolean isParameterWithDefault = false;

        if (parent instanceof InMatch) {
            Infix parentOperation = (InMatch) parent;
            Operator operator = parentOperation.operator();
            String operatorText = operator.getText();

            if (operatorText.equals(DEFAULT_OPERATOR)) {
                PsiElement defaulted = parentOperation.leftOperand();

                if (defaulted.isEquivalentTo(call)) {
                    isParameterWithDefault = isParameter((Call) parentOperation);
                }
            }
        }

        return isParameterWithDefault;
    }

    @Contract(pure = true)
    public static boolean isVariable(@NotNull Call call) {
        boolean isVariable = false;

        if (call instanceof UnqualifiedNoArgumentsCall) {
            String name = call.getName();

            // _ is an "ignored" not a variable
            if (name == null || !name.equals(IGNORED)) {
                PsiElement parent = call.getParent();
                isVariable = isVariable(parent);
            }
        } else if (call.isCallingMacro("Elixir.Kernel", "for")) {
            isVariable = true;
        }

        return isVariable;
    }

    /*
     * Private Static Methods
     */

    @Nullable
    private static List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                           @NotNull String name,
                                           boolean incompleteCode,
                                           @NotNull PsiNamedElement match) {
        String matchName = match.getName();

        if (matchName != null) {
            if (matchName.equals(name)) {
                resolveResultList = add(resolveResultList, new PsiElementResolveResult(match, true));
            } else if (incompleteCode && matchName.startsWith(name)) {
                resolveResultList = add(resolveResultList, new PsiElementResolveResult(match, false));
            }
        }

        return resolveResultList;
    }

    /**
     * Adds {@code resolveResult} to {@code resolveResultList} if it exists; otherwise, create new
     * {@code List<ResolveList>}, add {@code resolveResult} to it.  Return the modified or new
     * {@code List<ResolveResult>}.
     *
     * @param resolveResultList The current accumulated {@link ResolveResult}s, or {@code null} if no results have been
     *                          found yet.
     * @param resolveResult     The element to add to {@code resolveResultList}
     * @return {@code resolveResult} if it is not {@code null}; otherwise, a new {@code List<ResolveResult>}.
     */
    @NotNull
    private static List<ResolveResult> add(@Nullable List<ResolveResult> resolveResultList,
                                           @NotNull ResolveResult resolveResult) {
        if (resolveResultList == null) {
            resolveResultList = new ArrayList<ResolveResult>();
        }

        resolveResultList.add(resolveResult);

        return resolveResultList;
    }

    /**
     * Whether the {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()}
     * is {@code true}.
     *
     * @param resolveResultList
     * @return {@code false} if {@code resolveResultList} is {@code null}; otherwise, {@code true} if the
     * {@code resolveResultList} has any {@link ResolveResult} where {@link ResolveResult#isValidResult()} is
     * {@code true}.
     */
    @Contract(pure = true)
    private static boolean hasValidResult(@Nullable List<ResolveResult> resolveResultList) {
        boolean hasValidResult = false;

        if (resolveResultList != null) {
            hasValidResult = ContainerUtil.exists(resolveResultList, HAS_VALID_RESULT_CONDITION);
        }

        return hasValidResult;
    }

    @Contract(pure = true)
    private static boolean isParameter(@NotNull PsiElement ancestor) {
        PsiElement parent = ancestor.getParent();
        boolean isParameter = false;

        if (parent instanceof Call) {
            isParameter = isParameter((Call) parent);
        } else if (parent instanceof ElixirAccessExpression ||
                parent instanceof ElixirAssociations ||
                parent instanceof ElixirAssociationsBase ||
                parent instanceof ElixirBitString ||
                parent instanceof ElixirKeywordPair ||
                parent instanceof ElixirKeywords ||
                parent instanceof ElixirList ||
                parent instanceof ElixirMapArguments ||
                parent instanceof ElixirMapConstructionArguments ||
                parent instanceof ElixirMapOperation ||
                parent instanceof ElixirMatchedParenthesesArguments ||
                parent instanceof ElixirNoParenthesesArguments ||
                parent instanceof ElixirNoParenthesesKeywordPair ||
                parent instanceof ElixirNoParenthesesKeywords ||
                parent instanceof ElixirNoParenthesesOneArgument ||
                parent instanceof ElixirParenthesesArguments ||
                parent instanceof ElixirStabNoParenthesesSignature ||
                parent instanceof ElixirStabOperation ||
                parent instanceof ElixirStabParenthesesSignature ||
                parent instanceof ElixirStructOperation ||
                parent instanceof ElixirTuple) {
            isParameter = isParameter(parent);
        } else if (parent instanceof ElixirAnonymousFunction || parent instanceof InMatch) {
            isParameter = true;
        } else {
            if (!(parent instanceof ElixirStab || parent instanceof ElixirStabBody)) {
                Logger.error(Callable.class, "Don't know how to check if parameter", parent);
            }
        }

        return isParameter;
    }

    @Contract(pure = true)
    private static boolean isVariable(@NotNull PsiElement ancestor) {
        boolean isVariable = false;

        if (ancestor instanceof ElixirMatchedParenthesesArguments ||
                ancestor instanceof ElixirStabNoParenthesesSignature ||
                ancestor instanceof ElixirStabParenthesesSignature ||
                ancestor instanceof InMatch ||
                ancestor instanceof Match) {
            isVariable = true;
        } else if (ancestor instanceof ElixirAccessExpression ||
                ancestor instanceof ElixirAssociations ||
                ancestor instanceof ElixirAssociationsBase ||
                ancestor instanceof ElixirBitString ||
                ancestor instanceof ElixirContainerAssociationOperation ||
                ancestor instanceof ElixirKeywordPair ||
                ancestor instanceof ElixirKeywords ||
                ancestor instanceof ElixirList ||
                ancestor instanceof ElixirMapArguments ||
                ancestor instanceof ElixirMapConstructionArguments ||
                ancestor instanceof ElixirMapOperation ||
                ancestor instanceof ElixirNoParenthesesOneArgument ||
                ancestor instanceof ElixirNoParenthesesArguments ||
                ancestor instanceof ElixirNoParenthesesKeywordPair ||
                ancestor instanceof ElixirNoParenthesesKeywords ||
                ancestor instanceof ElixirParenthesesArguments ||
                ancestor instanceof ElixirParentheticalStab ||
                ancestor instanceof ElixirStab ||
                ancestor instanceof ElixirStabBody ||
                ancestor instanceof ElixirStructOperation ||
                ancestor instanceof ElixirTuple ||
                ancestor instanceof Type) {
            isVariable = isVariable(ancestor.getParent());
        } else if (ancestor instanceof Call) {
            // MUST be after any operations because operations also implement Call
            isVariable = isVariable((Call) ancestor);
        } else {
            Logger.error(Callable.class, "Don't know how to check if variable", ancestor);
        }

        return isVariable;
    }

    @Nullable
    private static List<ResolveResult> resolveVariable(@NotNull PsiElement referrer,
                                                       @NotNull String name,
                                                       boolean incompleteCode,
                                                       @NotNull Call ancestor,
                                                       @Nullable List<ResolveResult> resolveResultList) {
        if (CallDefinitionClause.is(ancestor) || ancestor.isCallingMacro("Elixir.Kernel", "for")) {
            resolveResultList = resolveVariableInMatch(referrer, name, incompleteCode, ancestor, resolveResultList);
        }

        if (!stop(incompleteCode, resolveResultList)) {
            // use generic parent-type-check resolveVariable
            resolveResultList = resolveVariable(
                    referrer, name, incompleteCode, (PsiElement) ancestor, resolveResultList
            );
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariable(@NotNull final PsiElement referrer,
                                                       @NotNull String name,
                                                       boolean incompleteCode,
                                                       @NotNull ElixirStabOperation ancestor,
                                                       @Nullable List<ResolveResult> resolveResultList) {
        PsiElement signature = ancestor.leftOperand();

        if (signature != null) {
            resolveResultList = resolveVariableInMatch(referrer, name, incompleteCode, signature, resolveResultList);
        }

        if (!stop(incompleteCode, resolveResultList)) {
            resolveResultList = resolveVariable(referrer, name, incompleteCode, ancestor.getParent(), resolveResultList);
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariable(@NotNull Call call, boolean incompleteCode) {
        List<ResolveResult> resolveResultList = null;
        String name = call.getName();

        if (name != null) {
            if (!incompleteCode && name.equals(IGNORED)) {
                resolveResultList = Collections.<ResolveResult>singletonList(new PsiElementResolveResult(call));
            } else {
                resolveResultList = resolveVariable(call, name, incompleteCode, (PsiElement) call, null);
            }
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariable(@NotNull PsiElement referrer,
                                                       @NotNull String name,
                                                       boolean incompleteCode,
                                                       @NotNull PsiElement ancestor,
                                                       @Nullable List<ResolveResult> resolveResultList) {
        if (!(ancestor instanceof PsiFile)) {
            PsiElement parent = ancestor.getParent();

            if (parent instanceof Match) {
                Infix matchOperation = (Match) parent;
                PsiElement rightOperand = matchOperation.rightOperand();

                // a variable in the right operand can't be declared in the left operand
                if (rightOperand == null || !rightOperand.isEquivalentTo(ancestor)) {
                    resolveResultList = resolveVariableInMatch(
                            referrer, name, incompleteCode, matchOperation.leftOperand(), resolveResultList
                    );
                }

                if (!stop(incompleteCode, resolveResultList)) {
                    // use generic parent-type-check resolveVariable
                    resolveResultList = resolveVariable(referrer, name, incompleteCode, parent, resolveResultList);
                }
            } else if (parent instanceof BracketOperation ||
                    parent instanceof ElixirAccessExpression ||
                    parent instanceof ElixirAnonymousFunction ||
                    parent instanceof ElixirAssociations ||
                    parent instanceof ElixirAssociationsBase ||
                    parent instanceof ElixirAtom ||
                    parent instanceof ElixirBitString ||
                    parent instanceof ElixirBlockItem ||
                    parent instanceof ElixirBlockList ||
                    parent instanceof ElixirBracketArguments ||
                    parent instanceof ElixirCharListLine ||
                    parent instanceof ElixirContainerAssociationOperation ||
                    parent instanceof ElixirDoBlock ||
                    parent instanceof ElixirInterpolatedStringBody ||
                    parent instanceof ElixirInterpolatedStringSigilLine ||
                    parent instanceof ElixirInterpolation ||
                    parent instanceof ElixirKeywordPair ||
                    parent instanceof ElixirKeywords ||
                    parent instanceof ElixirList ||
                    parent instanceof ElixirMapArguments ||
                    parent instanceof ElixirMapConstructionArguments ||
                    parent instanceof ElixirMapOperation ||
                    parent instanceof ElixirMapUpdateArguments ||
                    parent instanceof ElixirMatchedParenthesesArguments ||
                    parent instanceof ElixirNoParenthesesArguments ||
                    parent instanceof ElixirNoParenthesesKeywordPair ||
                    parent instanceof ElixirNoParenthesesKeywords ||
                    parent instanceof ElixirNoParenthesesOneArgument ||
                    parent instanceof ElixirParenthesesArguments ||
                    parent instanceof ElixirParentheticalStab ||
                    parent instanceof ElixirQuoteCharListBody ||
                    parent instanceof ElixirQuoteStringBody ||
                    parent instanceof ElixirStab ||
                    parent instanceof ElixirStabNoParenthesesSignature ||
                    parent instanceof ElixirStringHeredoc ||
                    parent instanceof ElixirStringHeredocLine ||
                    parent instanceof ElixirStringLine ||
                    parent instanceof ElixirStructOperation ||
                    parent instanceof ElixirTuple ||
                    parent instanceof Operation ||
                    parent instanceof QualifiedAlias ||
                    parent instanceof QualifiedBracketOperation ||
                    parent instanceof UnqualifiedBracketOperation) {
                resolveResultList = resolveVariable(referrer, name, incompleteCode, parent, resolveResultList);
            } else if (parent instanceof ElixirStabBody) {
                resolveResultList = resolveVariableInBlock(
                        referrer, name, incompleteCode, ancestor, resolveResultList
                );

                if (!stop(incompleteCode, resolveResultList)) {
                    resolveResultList = resolveVariable(referrer, name, incompleteCode, parent, resolveResultList);
                }
            } else if (parent instanceof ElixirStabOperation) {
                resolveResultList = resolveVariable(
                        referrer, name, incompleteCode, (ElixirStabOperation) parent, resolveResultList
                );
            } else if (parent instanceof Call) {
                resolveResultList = resolveVariable(referrer, name, incompleteCode, (Call) parent, resolveResultList);
            } else {
                if (!(parent instanceof PsiFile)) {
                    Logger.error(Callable.class, "Don't know how to resolve variable", parent);
                }
            }
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInBlock(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull PsiElement previousSibling,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        previousSibling = previousSibling.getPrevSibling();

        while (previousSibling != null) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, previousSibling, resolveResultList
            );

            if (stop(incompleteCode, resolveResultList)) {
                break;
            }

            previousSibling = previousSibling.getPrevSibling();
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull Call match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        if (CallDefinitionClause.is(match)) {
            PsiElement head = CallDefinitionClause.head(match);

            if (head != null) {
                PsiElement stripped = CallDefinitionHead.strip(head);

                if (stripped instanceof Call) {
                    Call strippedCall = (Call) stripped;

                    PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(strippedCall);

                    if (finalArguments != null) {
                        resolveResultList = resolveVariableInMatch(
                                referrer, name, incompleteCode, finalArguments, resolveResultList
                        );
                    }
                }
            }
        } else if (match.isCallingMacro("Elixir.Kernel", "for")) {
            PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(match);

            if (finalArguments != null) {
                // skip last argument since it is the keyword arguments
                for (int i = 0; i < finalArguments.length; i++) {
                    PsiElement finalArgument = finalArguments[i];

                    resolveResultList = resolveVariableInMatch(
                            referrer, name, incompleteCode, finalArgument, resolveResultList
                    );

                    if (stop(incompleteCode, resolveResultList)) {
                        break;
                    }
                }
            }
        } else {
            // unquote(var) can't declare var, only use it
            if (!match.isCalling("Elixir.Kernel", "unquote", 1)) {
                int resolvedFinalArity = match.resolvedFinalArity();

                if (resolvedFinalArity == 0) {
                    resolveResultList = add(resolveResultList, name, incompleteCode, (PsiNamedElement) match);
                }
            }
        }

        return resolveResultList;
    }

    /**
     * Only checks the right operand of the container association operation because the left operand is either a literal
     * or a pinned variable, which means the variable is being used and was declared elsewhere.
     */
    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirContainerAssociationOperation match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        PsiElement[] children = match.getChildren();

        if (children.length > 1) {
            resolveResultList = resolveVariableInMatch(referrer, name, incompleteCode, children[1], resolveResultList);
        }

        return resolveResultList;
    }

    /**
     * Only checks {@link ElixirMapArguments#getMapConstructionArguments()} and not
     * {@link ElixirMapArguments#getMapUpdateArguments()} since an update is not valid in a pattern match.
     */
    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirMapArguments match,
                                                              @Nullable List<ResolveResult> resolveResultList) {

        ElixirMapConstructionArguments mapConstructionArguments = match.getMapConstructionArguments();

        if (mapConstructionArguments != null) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, mapConstructionArguments, resolveResultList
            );
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirMapOperation match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.getMapArguments(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirMatchedWhenOperation match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.leftOperand(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirStabNoParenthesesSignature match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(
                referrer, name, incompleteCode, match.getNoParenthesesArguments(), resolveResultList
        );
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirStabParenthesesSignature match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.getParenthesesArguments(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull ElixirStructOperation match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.getMapArguments(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull Type match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.leftOperand(), resolveResultList);
    }

    /**
     * {@code in} can declare variable for {@code rescue} clauses like {@code rescue e in RuntimeException ->}
     */
    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull In match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.leftOperand(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull Infix match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        resolveResultList = resolveVariableInMatch(
                referrer, name, incompleteCode, match.leftOperand(), resolveResultList
        );

        if (!stop(incompleteCode, resolveResultList)) {
            PsiElement rightOperand = match.rightOperand();

            if (rightOperand != null) {
                resolveResultList = resolveVariableInMatch(
                        referrer, name, incompleteCode, rightOperand, resolveResultList
                );
            }
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull InMatch match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        Operator operator = match.operator();
        String operatorText = operator.getText();

        if (operatorText.equals(DEFAULT_OPERATOR)) {
            PsiElement defaulted = match.leftOperand();

            if (defaulted instanceof PsiNamedElement) {
                resolveResultList = add(resolveResultList, name, incompleteCode, (PsiNamedElement) defaulted);
            }
        } else if (operatorText.equals("<-")) {
            PsiElement leftOperand = match.leftOperand();

            resolveResultList = resolveVariableInMatch(referrer, name, incompleteCode, leftOperand, resolveResultList);
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull QuotableKeywordPair match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        return resolveVariableInMatch(referrer, name, incompleteCode, match.getKeywordValue(), resolveResultList);
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull PsiElement parameter,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        if (parameter instanceof ElixirAccessExpression ||
                parameter instanceof ElixirAssociations ||
                parameter instanceof ElixirAssociationsBase ||
                parameter instanceof ElixirBitString ||
                parameter instanceof ElixirList ||
                parameter instanceof ElixirMapConstructionArguments ||
                parameter instanceof ElixirNoParenthesesArguments ||
                parameter instanceof ElixirNoParenthesesOneArgument ||
                parameter instanceof ElixirParenthesesArguments ||
                parameter instanceof ElixirParentheticalStab ||
                parameter instanceof ElixirStab ||
                parameter instanceof ElixirStabBody ||
                parameter instanceof ElixirTuple) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, parameter.getChildren(), resolveResultList
            );
        } else if (parameter instanceof ElixirContainerAssociationOperation) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirContainerAssociationOperation) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirMapArguments) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirMapArguments) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirMapOperation) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirMapOperation) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirMatchedWhenOperation) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirMatchedWhenOperation) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirStabNoParenthesesSignature) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirStabNoParenthesesSignature) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirStabParenthesesSignature) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirStabParenthesesSignature) parameter, resolveResultList
            );
        } else if (parameter instanceof ElixirStructOperation) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (ElixirStructOperation) parameter, resolveResultList
            );
        } else if (parameter instanceof In) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (In) parameter, resolveResultList
            );
        } else if (parameter instanceof InMatch) { // MUST be before Call as InMatch is a Call
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (InMatch) parameter, resolveResultList
            );
        } else if (parameter instanceof Match ||
                parameter instanceof Pipe ||
                parameter instanceof Two) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (Infix) parameter, resolveResultList
            );
        } else if (parameter instanceof Type) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (Type) parameter, resolveResultList
            );
        } else if (parameter instanceof Call) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (Call) parameter, resolveResultList
            );
        } else if (parameter instanceof QuotableKeywordList) {
            resolveResultList = resolveVariableInMatch(
                    referrer, name, incompleteCode, (QuotableKeywordList) parameter, resolveResultList
            );
        } else {
            if (!(parameter instanceof AtNonNumericOperation || // a module attribute reference
                    parameter instanceof BracketOperation ||
                    /* an anonymous function is a new scope, so it can't be used to declare a variable.  This won't ever
                       be hit if the parameter is declared in the {@code fn} signature because that upward resolution
                       from resolveVariable stops before this level */
                    parameter instanceof ElixirAnonymousFunction ||
                    parameter instanceof ElixirAtom ||
                    parameter instanceof ElixirAtomKeyword ||
                    parameter instanceof ElixirCharListLine ||
                    parameter instanceof ElixirCharToken ||
                    parameter instanceof ElixirDecimalFloat ||
                    parameter instanceof ElixirDecimalWholeNumber ||
                    parameter instanceof ElixirEndOfExpression ||
                    parameter instanceof ElixirInterpolatedRegexLine ||
                    parameter instanceof ElixirInterpolatedWordsLine ||
                    parameter instanceof ElixirStringHeredoc ||
                    parameter instanceof ElixirStringLine ||
                    parameter instanceof PsiWhiteSpace ||
                    parameter instanceof QualifiableAlias ||
                    parameter instanceof QualifiedBracketOperation ||
                    parameter instanceof UnqualifiedBracketOperation)) {
                Logger.error(Callable.class, "Don't know how to resolve variable in match", parameter);
            }
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompleteCode,
                                                              @NotNull PsiElement[] parameters,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        for (PsiElement parameter : parameters) {
            resolveResultList = resolveVariableInMatch(referrer, name, incompleteCode, parameter, resolveResultList);

            if (stop(incompleteCode, resolveResultList)) {
                break;
            }
        }

        return resolveResultList;
    }

    @Nullable
    private static List<ResolveResult> resolveVariableInMatch(@NotNull PsiElement referrer,
                                                              @NotNull String name,
                                                              boolean incompletCode,
                                                              @NotNull QuotableKeywordList match,
                                                              @Nullable List<ResolveResult> resolveResultList) {
        List<QuotableKeywordPair> keywordPairList = match.quotableKeywordPairList();

        for (QuotableKeywordPair keywordPair : keywordPairList) {
            resolveResultList = resolveVariableInMatch(referrer, name, incompletCode, keywordPair, resolveResultList);

            if (stop(incompletCode, resolveResultList)) {
                break;
            }
        }

        return resolveResultList;
    }

    /**
     * Stop trying to resolve the reference if {@code resolveResultList} has a valid result and the code is complete
     * @param incompleteCode whether the code is incomplete, such as when still being typed.
     * @param resolveResultList the list of already found resolutions
     * @return {@code true} if {@link #hasValidResult(List)} and {@code incompleteCode} is {@code false}, so only one
     *   valid result is allowed.
     */
    @Contract(pure = true)
    private static boolean stop(boolean incompleteCode, @Nullable List<ResolveResult> resolveResultList) {
        return !incompleteCode && hasValidResult(resolveResultList);
    }

    /*
     * Constructors
     */

    public Callable(@NotNull Call call) {
        super(call, TextRange.create(0, call.getTextLength()));
    }

    /*
     *
     * Instance Methods
     *
     */

    /*
     * Public Instance Methods
     */

    /**
     * Returns the array of String, {@link PsiElement} and/or {@link com.intellij.codeInsight.lookup.LookupElement}
     * instances representing all identifiers that are visible at the location of the reference. The contents
     * of the returned array is used to build the lookup list for basic code completion. (The list
     * of visible identifiers may not be filtered by the completion prefix string - the
     * filtering is performed later by IDEA core.)
     *
     * @return the array of available identifiers.
     */
    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    /**
     * Returns the results of resolving the reference.
     *
     * @param incompleteCode if true, the code in the context of which the reference is
     *                       being resolved is considered incomplete, and the method may return additional
     *                       invalid results.
     * @return the array of results for resolving the reference.
     */
    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> resolveResultList = new ArrayList<ResolveResult>();
        // ensure that a pipe isn't make a no argument call really a 1-arity call
        int resolvedFinalArity = myElement.resolvedFinalArity();

        if (resolvedFinalArity == 0) {
            List<ResolveResult> resolvedVariableList = resolveVariable(myElement, incompleteCode);

            if (resolvedVariableList != null) {
                resolveResultList.addAll(resolvedVariableList);
            }
        }

        return resolveResultList.toArray(new ResolveResult[resolveResultList.size()]);
    }

    /**
     * Returns the element which is the target of the reference.
     *
     * @return the target element, or null if it was not possible to resolve the reference to a valid target.
     */
    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }
}
