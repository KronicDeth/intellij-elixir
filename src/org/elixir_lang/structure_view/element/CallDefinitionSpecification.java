package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.ElixirMatchedTypeOperation;
import org.elixir_lang.psi.ElixirMatchedWhenOperation;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
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

    private final boolean callback;
    @NotNull
    private final Modular modular;
    @NotNull
    private final Timed.Time time;

    /*
     * Static Methods
     */

    @Contract(pure = true)
    public static boolean is(@NotNull final Call call) {
        boolean is = false;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) call;
            String  moduleAttributeName = ElixirPsiImplUtil.moduleAttributeName(atUnqualifiedNoParenthesesCall);

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
    public static Pair<String, Integer> moduleAttributeNameArity(Call call) {
        Pair<String, Integer> nameArity = null;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            nameArity = moduleAttributeNameArity((AtUnqualifiedNoParenthesesCall) call);
        }

        return nameArity;
    }

    @Nullable
    public static PsiElement nameIdentifier(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        Call specification = specification(atUnqualifiedNoParenthesesCall);
        PsiElement nameIdentifier = null;

        if (specification != null) {
            nameIdentifier = specificationNameIdentifier(specification);
        }

        return nameIdentifier;
    }

    @Nullable
    public static PsiElement nameIdentifier(Call call) {
        PsiElement nameIdentifier = null;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            nameIdentifier = nameIdentifier((AtUnqualifiedNoParenthesesCall) call);
        }

        return nameIdentifier;
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
    public static Pair<String, Integer> specificationNameArity(Call specification) {
        Pair<String, Integer> nameArity = null;

        if (specification instanceof ElixirMatchedTypeOperation) {
            nameArity = typeNameArity((ElixirMatchedTypeOperation) specification);
        } else if (specification instanceof ElixirMatchedWhenOperation) {
            nameArity = typeNameArity((ElixirMatchedWhenOperation) specification);
        }

        return nameArity;
    }

    @Nullable
    public static PsiElement specificationNameIdentifier(Call specification) {
        PsiElement nameIdentifier = null;

        if (specification instanceof ElixirMatchedTypeOperation) {
            nameIdentifier = typeNameIdentifier((ElixirMatchedTypeOperation) specification);
        } else if (specification instanceof ElixirMatchedWhenOperation) {
            nameIdentifier = typeNameIdentifier((ElixirMatchedWhenOperation) specification);
        }

        return nameIdentifier;
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

    @Contract(pure = true)
    @Nullable
    public static PsiElement typeNameIdentifier(Call call) {
        return call.functionNameElement();
    }

    @Nullable
    public static PsiElement typeNameIdentifier(ElixirMatchedTypeOperation matchedTypeOperation) {
        PsiElement leftOperand = matchedTypeOperation.leftOperand();
        PsiElement nameIdentifier = null;

        if (leftOperand instanceof Call) {
            nameIdentifier = typeNameIdentifier((Call) leftOperand);
        }

        return nameIdentifier;
    }

    @Nullable
    public static PsiElement typeNameIdentifier(ElixirMatchedWhenOperation matchedWhenOperation) {
        PsiElement leftOperand = matchedWhenOperation.leftOperand();
        PsiElement nameIdentifier = null;

        if (leftOperand instanceof ElixirMatchedTypeOperation) {
            nameIdentifier = typeNameIdentifier((ElixirMatchedTypeOperation) leftOperand);
        }

        return nameIdentifier;
    }

    /*
     * Constructors
     */

    public CallDefinitionSpecification(@NotNull Modular modular,
                                       @NotNull AtUnqualifiedNoParenthesesCall moduleAttributeDefinition,
                                       boolean callback,
                                       @NotNull Timed.Time time) {
        super(moduleAttributeDefinition);
        this.callback = callback;
        this.modular = modular;
        this.time = time;
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
        Parent parentPresentation = (Parent) modular.getPresentation();
        String location = parentPresentation.getLocatedPresentableText();

        return new org.elixir_lang.navigation.item_presentation.CallDefinitionSpecification(
                location,
                specification(navigationItem),
                callback,
                time
        );
    }

}
