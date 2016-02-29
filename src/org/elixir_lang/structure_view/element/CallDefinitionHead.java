package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.Pair.pair;

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

    public static boolean is(Call call) {
        return call instanceof UnqualifiedParenthesesCall;
    }

    @Nullable
    public static Pair<String, IntRange> nameArityRange(PsiElement head) {
        String name;
        IntRange arityRange;
        Call headCall = null;
        Pair<String, IntRange> pair= null;

        if (head instanceof ElixirMatchedWhenOperation) {
            ElixirMatchedWhenOperation whenOperation = (ElixirMatchedWhenOperation) head;

            headCall = (Call) whenOperation.leftOperand();
        } else if (head instanceof Call) {
            headCall = (Call) head;
        } else if (head instanceof ElixirAccessExpression) {
            PsiElement[] headChildren = head.getChildren();

            if (headChildren.length == 1) {
                PsiElement headChild = headChildren[0];

                if (headChild instanceof ElixirParentheticalStab) {
                    ElixirParentheticalStab parentheticalStab = (ElixirParentheticalStab) headChild;

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
                                        PsiElement stabBodyChild = stabBodyChildren[0];

                                        if (stabBodyChild instanceof Call) {
                                            headCall = (Call) stabBodyChild;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else if (head instanceof ElixirMatchedAtNonNumericOperation) {
            ElixirMatchedAtNonNumericOperation matchedAtNonNumericOperation = (ElixirMatchedAtNonNumericOperation) head;

            name = matchedAtNonNumericOperation.operator().getText().trim();
            arityRange = new IntRange(1);
            pair = pair(name, arityRange);
        }

        if (headCall != null) {
            name = headCall.functionName();
            arityRange = headCall.resolvedFinalArityRange();
            pair = pair(name, arityRange);
        }

        return pair;
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
