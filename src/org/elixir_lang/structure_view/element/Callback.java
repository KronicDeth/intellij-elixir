package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Callback extends Element<AtUnqualifiedNoParenthesesCall> implements Timed {
    /*
     * Fields
     */

    @NotNull
    private final Modular modular;

    /*
     * Static Methods
     */

    @Contract(pure = true)
    public static boolean is(@NotNull final Call call) {
        boolean is = false;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) call;
            String moduleAttributeName = atUnqualifiedNoParenthesesCall.moduleAttributeName();

            if (moduleAttributeName.equals("@callback") || moduleAttributeName.equals("@macrocallback")) {
                is = true;
            }
        }

        return is;
    }

    /*
     * Constructors
     */

    public Callback(@NotNull Modular modular, Call navigationItem) {
        super((AtUnqualifiedNoParenthesesCall) navigationItem);
        this.modular = modular;
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
     * Returns the list of children of the tree element.
     *
     * @return the list of children.
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
        Parent parentPresentation = (Parent) modular.getPresentation();
        String location = parentPresentation.getLocatedPresentableText();

        PsiElement[] arguments = ElixirPsiImplUtil.finalArguments(navigationItem);

        assert arguments != null;

        // pseudo-named-arguments
        boolean callback = true;
        Visible.Visibility visibility = Visible.Visibility.PUBLIC;
        boolean overridable = false;
        boolean override = false;

        Call headCall = headCall();
        String name = headCall.functionName();
        int arity = headCall.resolvedFinalArity();

        //noinspection ConstantConditions
        return new NameArity(
                location,
                callback,
                time(),
                visibility,
                overridable,
                override,
                name,
                arity
        );
    }

    /**
     * When the defined call is usable
     *
     * @return {@link Time#COMPILE} for compile time ({@code defmacro}, {@code defmacrop});
     * {@link Time#RUN} for run time {@code def}, {@code defp})
     */
    @NotNull
    @Override
    public Time time() {
        String moduleAttributeName = navigationItem.moduleAttributeName();
        Time time = null;

        if (moduleAttributeName.equals("@callback")) {
            time = Time.RUN;
        } else if (moduleAttributeName.equals("@macrocallback")) {
            time = Time.COMPILE;
        }

        assert time != null;

        return time;
    }

    /*
     * Private Instance Methods
     */

    @Nullable
    private Call headCall() {
        ElixirNoParenthesesOneArgument noParenthesesOneArgument = navigationItem.getNoParenthesesOneArgument();
        PsiElement[] grandChildren = noParenthesesOneArgument.getChildren();
        Call headCall = null;

        if (grandChildren.length == 1) {
            headCall = specificationHeadCall(grandChildren[0]);
        }

        return headCall;
    }

    @Nullable
    private Call parameterizedTypeHeadCall(ElixirMatchedWhenOperation whenOperation) {
        PsiElement leftOperand = whenOperation.leftOperand();
        Call headCall = null;

        if (leftOperand instanceof ElixirMatchedTypeOperation) {
            headCall = typeHeadCall((ElixirMatchedTypeOperation) leftOperand);
        }

        return headCall;
    }

    @Nullable
    private Call specificationHeadCall(PsiElement specification) {
        Call headCall = null;

        if (specification instanceof ElixirMatchedTypeOperation) {
            headCall = typeHeadCall((ElixirMatchedTypeOperation) specification);
        } else if (specification instanceof ElixirMatchedWhenOperation) {
            headCall = parameterizedTypeHeadCall((ElixirMatchedWhenOperation) specification);
        }

        return headCall;
    }

    @Nullable
    private Call typeHeadCall(ElixirMatchedTypeOperation typeOperation) {
        PsiElement leftOperand = typeOperation.leftOperand();
        Call headCall = null;

        if (leftOperand instanceof Call) {
            headCall = (Call) leftOperand;
        }

        return headCall;
    }
}
