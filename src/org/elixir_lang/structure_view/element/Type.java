package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Type extends Element<AtUnqualifiedNoParenthesesCall> implements Visible {
    /*
     * Fields
     */

    private final boolean opaque;
    @NotNull
    private final Modular modular;
    @NotNull
    private final Visible.Visibility visibility;

    /*
     * Static Methods
     */

    @NotNull
    public static Type fromCall(@NotNull Modular modular, Call call) {
        return fromAtUnqualifiedNoParenthesesCall(modular, (AtUnqualifiedNoParenthesesCall) call);
    }

    @NotNull
    public static Type fromAtUnqualifiedNoParenthesesCall(
            @NotNull Modular modular,
            @NotNull AtUnqualifiedNoParenthesesCall moduleAttributeDefinition) {
        String moduleAttributeName = moduleAttributeDefinition.moduleAttributeName();
        boolean opaque = isOpaque(moduleAttributeName);
        Visibility visibility = visibility(moduleAttributeName);

        return new Type(modular, moduleAttributeDefinition, opaque, visibility);
    }

    public static boolean is(@NotNull Call call) {
        boolean is = false;

        if (call instanceof AtUnqualifiedNoParenthesesCall) {
            AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall = (AtUnqualifiedNoParenthesesCall) call;
            String moduleAttributeName = atUnqualifiedNoParenthesesCall.moduleAttributeName();

            is = moduleAttributeName.equals("@opaque") ||
                    moduleAttributeName.equals("@type") ||
                    moduleAttributeName.equals("@typep");
        }

        return is;
    }

    public static boolean isOpaque(@NotNull String moduleAttributeName) {
        return moduleAttributeName.equals("@opaque");
    }

    @Nullable
    public static Call type(AtUnqualifiedNoParenthesesCall atUnqualifiedNoParenthesesCall) {
        PsiElement[] arguments = atUnqualifiedNoParenthesesCall.getNoParenthesesOneArgument().arguments();
        Call type = null;

        if (arguments.length == 1) {
            PsiElement argument = arguments[0];

            if (argument instanceof Call) {
                type = (Call) argument;
            }
        }

        return type;
    }

    @NotNull
    public static Visibility visibility(@NotNull String moduleAttributeName) {
        Visibility visibility = null;

        if (moduleAttributeName.equals("@opaque") || moduleAttributeName.equals("@type")) {
            visibility = Visibility.PUBLIC;
        } else if (moduleAttributeName.equals("@typep")) {
            visibility = Visibility.PRIVATE;
        }

        assert visibility != null;

        return visibility;
    }

    /*
     * Constructors
     */

    public Type(@NotNull Modular modular,
                @NotNull AtUnqualifiedNoParenthesesCall moduleAttributeDefinition,
                boolean opaque,
                @NotNull Visibility visibility) {
        super(moduleAttributeDefinition);
        this.modular = modular;
        this.opaque = opaque;
        this.visibility = visibility;
    }

    /*
     * Instance Methods
     */

    /**
     * No children.
     *
     * @return empty array.
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

        return new org.elixir_lang.navigation.item_presentation.Type(
                location,
                type(navigationItem),
                opaque,
                visibility
        );
    }

    /**
     * The visibility of the element.
     *
     * @return {@link Visibility#PUBLIC} for {@code @type} and {@code @opaque}; {@link Visibility#PRIVATE} for
     * {@code @typep}
     */
    @Nullable
    @Override
    public Visibility visibility() {
        return visibility;
    }
}
