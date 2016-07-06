package org.elixir_lang.structure_view.element;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewTypeLocation;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.navigation.item_presentation.NameArity;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static org.elixir_lang.psi.call.name.Function.*;
import static org.elixir_lang.psi.call.name.Module.KERNEL;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.enclosingMacroCall;

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

    /**
     * Description of element used in {@link org.elixir_lang.FindUsagesProvider}.
     *
     * @param call a {@link Call} that has already been checked with {@link #is(Call)}
     * @param location where the description will be used
     * @return
     */
    @Nullable
    public static String elementDescription(@NotNull Call call, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (isFunction(call)) {
            elementDescription = functionElementDescription(call, location);
        } else if (isMacro(call)) {
            elementDescription = macroElementDescription(call, location);
        }

        return elementDescription;
    }

    /**
     * The module or {@code quote} that encapsulates {@code call}
     *
     * @param call a def(macro)?p?
     * @return {@code null} if gets to the enclosing file without finding a quote or module
     */
    @Contract(pure = true)
    @Nullable
    public static Modular enclosingModular(@NotNull Call call) {
        Modular modular = null;
        Call enclosingMacroCall = enclosingModularMacroCall(call);

        if (enclosingMacroCall != null) {
            modular = modular(enclosingMacroCall);
        }

        return modular;
    }

    /**
     * The enclosing macro call that acts as the modular scope of {@code call}.  Ignores enclosing {@code for} calls that
     * {@link ElixirPsiImplUtil#enclosingMacroCall} doesn't.
     *
     * @param call a def(macro)?p?
     */
    @Nullable
    public static Call enclosingModularMacroCall(@NotNull Call call) {
        Call enclosedCall = call;
        Call enclosingMacroCall;

        while(true) {
            enclosingMacroCall = enclosingMacroCall(enclosedCall);

            if (enclosingMacroCall != null && enclosingMacroCall.isCallingMacro(KERNEL, FOR, 2)) {
                enclosedCall = enclosingMacroCall;
            } else {
                break;
            }
        }

        return enclosingMacroCall;
    }

    @Contract(pure = true)
    @Nullable
    public static Modular modular(@NotNull Call enclosingMacroCall) {
        Modular modular = null;

        // All classes under {@link org.elixir_lang.structure_view.element.Modular}
        if (Implementation.is(enclosingMacroCall)) {
            Modular grandScope = enclosingModular(enclosingMacroCall);
            modular = new Implementation(grandScope, enclosingMacroCall);
        } else if (Module.is(enclosingMacroCall)) {
            Modular grandScope = enclosingModular(enclosingMacroCall);
            modular = new Module(grandScope, enclosingMacroCall);
        } else if (Protocol.is(enclosingMacroCall)) {
            Modular grandScope = enclosingModular(enclosingMacroCall);
            modular = new Protocol(grandScope, enclosingMacroCall);
        } else if (Quote.is(enclosingMacroCall)) {
            Call quoteEnclosingMacroCall = enclosingMacroCall(enclosingMacroCall);
            Quote quote;

            if (quoteEnclosingMacroCall == null) {
                quote = new Quote(enclosingMacroCall);
            } else if (CallDefinitionClause.is(quoteEnclosingMacroCall)) {
                quote = new Quote(
                        new CallDefinitionClause(quoteEnclosingMacroCall),
                        enclosingMacroCall
                );
            } else {
                quote = new Quote(modular(quoteEnclosingMacroCall), enclosingMacroCall);
            }

            modular = quote.modular();
        }

        return modular;
    }

    /**
     * The head of the call definition.
     *
     * @param call a call that {@link #is(Call)}.
     * @return element for {@code name(arg, ...) when ...} in {@code def* name(arg, ...) when ...}
     */
    @Nullable
    public static PsiElement head(@NotNull Call call) {
        PsiElement[] primaryArguments = call.primaryArguments();
        PsiElement head = null;

        if (primaryArguments != null && primaryArguments.length > 0) {
            head = primaryArguments[0];
        }

        return head;
    }

    public static boolean is(@NotNull Call call) {
        return isFunction(call) || isMacro(call);
    }

    public static boolean isFunction(@NotNull Call call) {
        return isPrivateFunction(call) || isPublicFunction(call);
    }

    public static boolean isMacro(@NotNull Call call) {
        return isPrivateMacro(call) || isPublicMacro(call);
    }

    public static boolean isPrivateFunction(@NotNull Call call) {
        return isCallingKernelMacroOrHead(call, DEFP);
    }

    public static boolean isPrivateMacro(@NotNull Call call) {
        return isCallingKernelMacroOrHead(call, DEFMACROP);
    }

    public static boolean isPublicFunction(@NotNull Call call) {
        return isCallingKernelMacroOrHead(call, DEF);
    }

    public static boolean isPublicMacro(@NotNull Call call) {
        return isCallingKernelMacroOrHead(call, DEFMACRO);
    }

    @Nullable
    public static PsiElement nameIdentifier(@NotNull Call call) {
        PsiElement head = head(call);
        PsiElement nameIdentifier = null;

        if (head != null) {
            nameIdentifier = CallDefinitionHead.nameIdentifier(head);
        }

        return nameIdentifier;
    }

    /**
     * The name and arity range of the call definition this clause belongs to.
     *
     * @param call
     * @return The name and arities of the {@link CallDefinition} this clause belongs.  Multiple arities occur when
     * default arguments are used, which produces an arity for each default argument that is turned on and off.
     * @see Call#resolvedFinalArityRange()
     */
    @Nullable
    public static Pair<String, IntRange> nameArityRange(Call call) {
        PsiElement head = head(call);
        Pair<String, IntRange> pair = null;

        if (head != null) {
            pair = CallDefinitionHead.nameArityRange(head);
        }

        return pair;
    }

    /**
     * Whether the {@code call} is defining something for runtime, like a function, or something for compile time, like
     * a macro.
     *
     * @param call def(macro)?p?
     * @return {@link Timed.Time#COMPILE} for defmacrop?; {@link Timed.Time#RUN} for defp?
     */
    @NotNull
    public static Timed.Time time(Call call) {
        Timed.Time time = null;

        if (isFunction(call)) {
            time = Timed.Time.RUN;
        } else if (isMacro(call)) {
            time = Timed.Time.COMPILE;
        }

        //noinspection ConstantConditions
        return time;
    }

    /**
     * @param call
     * @return {@code Visible.Visibility.PUBLIC} for {@code def} or {@code defmacro}; {@code Visible.Visibility.PRIVATE}
     * for {@code defp} and {@code defmacrop}; {@code null} only if {@code call} is unrecognized
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

    @Nullable
    private static String functionElementDescription(@NotNull Call call, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "function";
        }

        return elementDescription;
    }

    private static boolean isCallingKernelMacroOrHead(@NotNull final Call call, @NotNull final String resolvedName) {
        return call.isCallingMacro(KERNEL, resolvedName, 2) ||
                call.isCalling(KERNEL, resolvedName, 1);
    }

    private static String macroElementDescription(@NotNull Call call, @NotNull ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "macro";
        }

        return elementDescription;
    }

    /*
     * Constructors
     */

    /**
     * Constructs {@link #callDefinition} from {@code code}, such as when showing structure in Go To Symbol
     *
     * @param call a def(macro)?p? call
     */
    public CallDefinitionClause(Call call) {
        this(new CallDefinition(call), call);
    }

    /**
     * Constructs a clause for {@code callDefinition}.
     *
     * @param callDefinition holds all sibling clauses for {@code call} for the same name, arity. and time
     * @param call           a def(macro)?p? call
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
        PsiElement head = head(navigationItem);

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
     * {@code false}
     */
    public boolean isMatch(PsiElement[] arguments) {
        return false;
    }

}

