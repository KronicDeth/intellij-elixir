package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.openapi.util.Pair.pair;

/**
 * A call definition @spec
 */
public class CallDefinitionSpecification extends Element<AtUnqualifiedNoParenthesesCall> {
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
            String  moduleAttributeName = atUnqualifiedNoParenthesesCall.moduleAttributeName();

            if (moduleAttributeName.equals("@spec")) {
                is = true;
            }
        }

        return is;
    }

    @Nullable
    public static Pair<String, Integer> moduleAttributeNameArity(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        Call specification = specification(atUnqualifiedNoParenthesesCall);
        Pair<String, Integer> nameArity = null;

        if (specification != null) {
            nameArity = specificationNameArity(specification);
        }

        return nameArity;
    }

    @Nullable
    public static Call specification(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        PsiElement[] arguments = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument().arguments();
        Call specification = null;

        if (arguments.length == 1) {
            PsiElement argument = arguments[0];

            if (argument instanceof Call) {
                specification = (Call) argument;
            }
        }

        return specification;
    }

    @Nullable
    public static Pair<String, Integer> moduleAttributeNameArity(Call call) {
        Pair<String, Integer> nameArity = null;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            nameArity = moduleAttributeNameArity((AtUnqualifiedNoParenthesesCall) call);
        }

        return nameArity;
    }

    @Nullable
    public static Pair<String, Integer> specificationNameArity(Call specification) {
        Pair<String, Integer> nameArity = null;

        if (specification instanceof ElixirMatchedTypeOperation) {
            nameArity = typeNameArity((ElixirMatchedTypeOperation) specification);
        } else if (specification instanceof ElixirMatchedWhenOperation) {
            nameArity = typeNameArity((ElixirMatchedWhenOperation) specification);
        }

        return nameArity;
    }


    @NotNull
    public static Pair<String, Integer> typeNameArity(Call call) {
        String name = call.functionName();
        int arity = call.resolvedFinalArity();

        return pair(name, arity);
    }

    @Nullable
    public static Pair<String, Integer> typeNameArity(ElixirMatchedTypeOperation matchedTypeOperation) {
        PsiElement leftOperand = matchedTypeOperation.leftOperand();
        Pair<String, Integer> nameArity = null;

        if (leftOperand instanceof Call) {
            nameArity = typeNameArity((Call) leftOperand);
        }

        return nameArity;
    }

    @Nullable
    public static Pair<String, Integer> typeNameArity(ElixirMatchedWhenOperation matchedWhenOperation) {
        PsiElement leftOperand = matchedWhenOperation.leftOperand();
        Pair<String, Integer> nameArity = null;

        if (leftOperand instanceof ElixirMatchedTypeOperation) {
            nameArity = typeNameArity((ElixirMatchedTypeOperation) leftOperand);
        }

        return nameArity;
    }

    /*
     * Constructors
     */

    public CallDefinitionSpecification(@NotNull Modular modular,
                                       @NotNull AtUnqualifiedNoParenthesesCall moduleAttributeDefinition) {
        super(moduleAttributeDefinition);
        this.modular = modular;
    }

    /*
     * Instance Methods
     */

    /**
     * No children.
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
        return new org.elixir_lang.navigation.item_presentation.CallDefinitionSpecification(
                specification(navigationItem),
                Timed.Time.RUN
        );
    }

}
