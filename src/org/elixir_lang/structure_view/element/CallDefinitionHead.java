package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.Visibility;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.stripAccessExpression;
import static org.elixir_lang.psi.impl.PsiNamedElementImpl.unquoteName;
import static org.elixir_lang.psi.operation.Normalized.operatorIndex;

public class CallDefinitionHead extends Element<Call> implements Presentable, Visible {
    /*
     * Fields
     */

    @NotNull
    private final CallDefinition callDefinition;
    @NotNull
    private final Visibility visibility;

    /*
     * Static Methods
     */

    @Nullable
    public static Call argumentEnclosingDelegationCall(@NotNull PsiElement arguments) {
        Call delegationCall = null;

        if (arguments instanceof ElixirNoParenthesesOneArgument) {
            PsiElement argumentsParent = arguments.getParent();

            if (argumentsParent instanceof Call) {
                Call argumentsParentCall = (Call) argumentsParent;

                if (Delegation.is(argumentsParentCall)) {
                    delegationCall = argumentsParentCall;
                }
            }
        }

        return delegationCall;
    }

    @Nullable
    public static Call enclosingDelegationCall(@NotNull Call call) {
        // reverse of {@link org.elixir_lang.structure_view.element.Delegation.filterCallDefinitionHeadCallList()}
        PsiElement parent = call.getParent();
        Call delegationCall = null;

        if (parent instanceof ElixirList) {
            PsiElement grandParent = parent.getParent();

            if (grandParent instanceof ElixirAccessExpression) {
                PsiElement greatGrandParent = parent.getParent();

                delegationCall = argumentEnclosingDelegationCall(greatGrandParent);
            }
        } else {
            delegationCall = argumentEnclosingDelegationCall(parent);
        }

        return delegationCall;
    }

    public static boolean is(Call call) {
        return call instanceof UnqualifiedParenthesesCall;
    }

    @NotNull
    public static PsiElement nameIdentifier(PsiElement head) {
        PsiElement nameIdentifier;

        if (head instanceof ElixirMatchedAtNonNumericOperation) {
            AtNonNumericOperation atNonNumericOperation = (AtNonNumericOperation) head;
            nameIdentifier = atNonNumericOperation.operator();
        } else {
            PsiElement stripped = strip(head);

            if (stripped instanceof Call) {
                Call strippedCall = (Call) stripped;
                nameIdentifier = strippedCall.functionNameElement();
            } else {
                nameIdentifier = stripped;
            }
        }

        return nameIdentifier;
    }

    @Nullable
    public static Pair<String, IntRange> nameArityRange(PsiElement head) {
        String name;
        IntRange arityRange;
        Pair<String, IntRange> pair = null;

        if (head instanceof ElixirMatchedAtNonNumericOperation) {
            ElixirMatchedAtNonNumericOperation matchedAtNonNumericOperation = (ElixirMatchedAtNonNumericOperation) head;

            name = matchedAtNonNumericOperation.operator().getText().trim();
            arityRange = new IntRange(1);
            pair = pair(name, arityRange);
        } else {
            PsiElement stripped = strip(head);

            if (stripped instanceof Call) {
                Call strippedCall = (Call) stripped;

                name = unquoteName(strippedCall, strippedCall.functionName());
                arityRange = strippedCall.resolvedFinalArityRange();

                pair = pair(name, arityRange);
            }
        }

        return pair;
    }

    /**
     * Head without parentheses around the guard or guarded head
     *
     * @param head {@code ((((name(arg, ...))) when ...))}
     * @return {@code name(arg, ...)}
     */
    @NotNull
    public static PsiElement strip(@NotNull final PsiElement head) {
        PsiElement strippedGuarded = stripAllOuterParentheses(head);
        PsiElement unguarded = stripGuard(strippedGuarded);
        return stripAllOuterParentheses(unguarded);
    }

    /**
     * The head without the guard clause
     *
     * @param head {@code name(arg, ...) when ...}
     * @return {@code name(arg, ...)}.  {@code head} if no guard clause.
     */
    @NotNull
    public static PsiElement stripGuard(@NotNull final PsiElement head) {
        PsiElement stripped = head;

        if (head instanceof ElixirMatchedWhenOperation) {
            ElixirMatchedWhenOperation whenOperation = (ElixirMatchedWhenOperation) head;

            PsiElement[] children = whenOperation.getChildren();

            int operatorIndex = operatorIndex(children);
            int onlyNonErrorIndex = -1;

            for (int i = 0; i < operatorIndex; i++) {
                if (!(children[i] instanceof PsiErrorElement)) {
                    if (onlyNonErrorIndex == -1) {
                        // first
                        onlyNonErrorIndex = i;
                    } else {
                        // more than one
                        onlyNonErrorIndex = -1;

                        break;
                    }
                }
            }

            if (onlyNonErrorIndex != -1) {
                stripped = stripGuard(children[onlyNonErrorIndex]);
            }
        }

        return stripped;
    }

    /**
     * Strips each set of outer parentheses from {@code head} until there aren't anymore.
     *
     * @param head {@code ((value))}
     * @return {@code value}.  {@code head} if no outer parentheses
     */
    @NotNull
    public static PsiElement stripAllOuterParentheses(@NotNull final PsiElement head) {
        PsiElement stripped = head;
        PsiElement previousStripped;

        do {
            previousStripped = stripped;
            stripped = stripOuterParentheses(previousStripped);
        } while (previousStripped != stripped);

        return stripped;
    }

    /**
     * Strips outer parentheses from {@code head}.
     *
     * @param head {@code (value)}
     * @return {@code value}.  {@code head} if no outer parentheses
     */
    @NotNull
    public static PsiElement stripOuterParentheses(PsiElement head) {
        PsiElement stripped = stripAccessExpression(head);

        if (stripped instanceof ElixirParentheticalStab) {
            ElixirParentheticalStab parentheticalStab = (ElixirParentheticalStab) stripped;

            PsiElement[] parentheticalStabChildren = parentheticalStab.getChildren();

            if (parentheticalStabChildren.length == 1) {
                PsiElement parentheticalStabChild = parentheticalStabChildren[0];

                if (parentheticalStabChild instanceof ElixirStab) {
                    ElixirStab stab = (ElixirStab) parentheticalStabChild;

                    PsiElement[] stabChildren = stab.getChildren();

                    if (stabChildren.length == 1) {
                        PsiElement stabChild = stabChildren[0];

                        if (stabChild instanceof ElixirStabBody) {
                            ElixirStabBody stabBody = (ElixirStabBody) stabChild;

                            PsiElement[] stabBodyChildren = stabBody.getChildren();

                            if (stabBodyChildren.length == 1) {
                                stripped = stabBodyChildren[0];
                            }
                        }
                    }
                }
            }
        }

        return stripped;
    }

    /*
     * Constructors
     */

    public CallDefinitionHead(@NotNull CallDefinition callDefinition,
                              @NotNull Visibility visibility,
                              @NotNull Call call) {
        super(call);
        this.callDefinition = callDefinition;
        this.visibility = visibility;
    }

    /*
     * Instance Methods
     */

    /**
     * Heads have no children since they have no body.
     *
     * @return empty array
     */
    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return new TreeElement[0];
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
                (NameArity) callDefinition.getPresentation(),
                visibility,
                navigationItem
        );
    }

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility#PUBLIC}.
     */
    @NotNull
    @Override
    public Visibility visibility() {
        return visibility;
    }
}
