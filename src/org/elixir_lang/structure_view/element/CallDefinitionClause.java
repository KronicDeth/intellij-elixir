package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CallDefinitionClause extends Element<Call> implements Presentable, Visible {
    /*
     * Constants
     */

    /*
     * Fields
     */

    private final CallDefinition callDefinition;
    @NotNull
    private final Visibility visibility;

    /*
     * Public Static Methods
     */

    public static boolean isFunction(Call call) {
        return isPrivateFunction(call) || isPublicFunction(call);
    }

    public static boolean isMacro(Call call) {
        return isPrivateMacro(call) || isPublicMacro(call);
    }

    public static boolean isPrivateFunction(Call call) {
        return isCallingKernelMacroOrHead(call, "defp");
    }

    public static boolean isPrivateMacro(Call call) {
        return isCallingKernelMacroOrHead(call, "defmacrop");
    }

    public static boolean isPublicFunction(Call call) {
        return isCallingKernelMacroOrHead(call, "def");
    }

    public static boolean isPublicMacro(Call call) {
        return isCallingKernelMacroOrHead(call, "defmacro");
    }

    /**
     * The name and arity range of the call definition this clause belongs to.
     *
     * @param call
     * @return The name and arities of the {@link CallDefinition} this clause belongs.  Multiple arities occur when
     *   default arguments are used, which produces an arity for each default argument that is turned on and off.
     * @see Call#resolvedFinalArityRange()
     */
    @Nullable
    public static Pair<String, IntRange> nameArityRange(Call call) {
        PsiElement[] primaryArguments = call.primaryArguments();
        Pair<String, IntRange> pair = null;

        if (primaryArguments != null && primaryArguments.length > 0) {
            PsiElement head = primaryArguments[0];
            pair = CallDefinitionHead.nameArityRange(head);
        }

        return pair;
    }

    /**
     *
     * @param call
     * @return {@code Visible.Visibility.PUBLIC} for {@code def} or {@code defmacro}; {@code Visible.Visibility.PRIVATE}
     *   for {@code defp} and {@code defmacrop}; {@code null} only if {@code call} is unrecognized
     */
    @Nullable
    public static Visible.Visibility visibility(Call call) {
        Visible.Visibility callVisibility = null;

        if (isPublicFunction(call) || isPublicMacro(call)) {
            callVisibility = Visible.Visibility.PUBLIC;
        } else if (isPrivateFunction(call) || isPrivateMacro(call)) {
            callVisibility = Visible.Visibility.PRIVATE;
        }

        return callVisibility;
    }

    /*
     * Private Static Methods
     */

    private static boolean isCallingKernelMacroOrHead(@NotNull final Call call, @NotNull final String resolvedName) {
        return call.isCallingMacro("Elixir.Kernel", resolvedName, 2) ||
                call.isCalling("Elixir.Kernel", resolvedName, 1);
    }

    /*
     * Constructors
     */

    public CallDefinitionClause(CallDefinition callDefinition, Call call) {
        super(call);
        this.callDefinition = callDefinition;
        //noinspection ConstantConditions
        this.visibility = visibility(call);
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        Call[] childCalls = ElixirPsiImplUtil.macroChildCalls(navigationItem);
        TreeElement[] children = childCallTreeElements(childCalls);

        if (children == null) {
            children = new TreeElement[0];
        }

        return children;
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        PsiElement[] primaryArguments = navigationItem.primaryArguments();

        assert primaryArguments != null;
        assert primaryArguments.length > 0;

        PsiElement head = primaryArguments[0];

        return new org.elixir_lang.navigation.item_presentation.CallDefinitionHead(
                (NameArity) callDefinition.getPresentation(),
                visibility(),
                head
        );
    }

    /**
     * The visibility of the element.
     *
     * @return {@code Visible.Visibility.PUBLIC} for public call definitions ({@code def} and {@code defmacro});
     * {@code Visible.Visibility.PRIVATE} for private call definitions ({@code defp} and {@code defmacrop}).
     */
    @NotNull
    @Override
    public Visibility visibility() {
        return visibility;
    }

    /*
     * Private Instance Methods
     */

    private void addChildCall(List<TreeElement> treeElementList, Call childCall) {
        TreeElement childCallTreeElement = null;

        if (Implementation.is(childCall)) {
            childCallTreeElement = new Implementation(callDefinition.getModular(), childCall);
        } else if (Module.is(childCall)) {
            childCallTreeElement = new Module(callDefinition.getModular(), childCall);
        } else if (Quote.is(childCall)) {
            childCallTreeElement = new Quote(this, childCall);
        }

        if (childCallTreeElement != null) {
            treeElementList.add(childCallTreeElement);
        }
    }

    @Contract(pure = true)
    @Nullable
    private TreeElement[] childCallTreeElements(@Nullable Call[] childCalls) {
        TreeElement[] treeElements = null;

        if (childCalls != null) {
            List<TreeElement> treeElementList = new ArrayList<TreeElement>(childCalls.length);

            for (Call childCall : childCalls) {
                addChildCall(treeElementList, childCall);
            }

            treeElements = treeElementList.toArray(new TreeElement[treeElementList.size()]);
        }

        return treeElements;
    }

    /**
     * Whether this clause would match the given arguments and be called.
     *
     * @param arguments argument being passed to this clauses' function.
     * @return {@code true} if arguments matches up-to the available information about the arguments; otherwise,
     *   {@code false}
     */
    public boolean isMatch(PsiElement[] arguments) {
        return false;
    }
}

