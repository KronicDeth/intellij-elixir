package org.elixir_lang.structure_view.element.modular;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionUtil;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Delegation;
import org.elixir_lang.structure_view.element.Exception;
import org.elixir_lang.structure_view.element.Overridable;
import org.elixir_lang.structure_view.element.Quote;
import org.elixir_lang.structure_view.element.call_definition_by_name_arity.FunctionByNameArity;
import org.elixir_lang.structure_view.element.call_definition_by_name_arity.MacroByNameArity;
import org.elixir_lang.structure_view.element.structure.Structure;
import org.elixir_lang.structure_view.node_provider.Used;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.KERNEL_MODULE_NAME;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.enclosingMacroCall;

public class Module extends Element<Call> implements Modular {
    /*
     * Fields
     */

    @Nullable
    protected final Modular parent;

    /*
     *
     * Static Methods
     *
     */

    /*
     * Public Static Methods
     */

    public static void addClausesToCallDefinition(
            @NotNull Call call,
            @NotNull Map<Pair<String, Integer>, CallDefinition> callDefinitionByNameArity,
            @NotNull Modular modular,
            @NotNull Timed.Time time,
            @NotNull Inserter<CallDefinition> callDefinitionInserter
    ) {
        Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(call);

        if (nameArityRange != null) {
            String name = nameArityRange.first;
            IntRange arityRange = nameArityRange.second;

            addClausesToCallDefinition(
                    call,
                    name,
                    arityRange,
                    callDefinitionByNameArity,
                    modular,
                    time,
                    callDefinitionInserter
            );
        }
    }

    public static void addClausesToCallDefinition(
            @NotNull Call call,
            @NotNull String name,
            @NotNull IntRange arityRange,
            @NotNull Map<Pair<String, Integer>, CallDefinition> callDefinitionByNameArity,
            @NotNull Modular modular,
            @NotNull Timed.Time time,
            @NotNull Inserter<CallDefinition> callDefinitionInserter
    ) {
        for (int arity = arityRange.getMinimumInteger(); arity <= arityRange.getMaximumInteger(); arity++) {
            Pair<String, Integer> nameArity = pair(name, arity);

            CallDefinition callDefinition = callDefinitionByNameArity.get(nameArity);

            if (callDefinition == null) {
                callDefinition = new CallDefinition(
                        modular,
                        time,
                        name,
                        arity
                );
                callDefinitionByNameArity.put(nameArity, callDefinition);

                callDefinitionInserter.insert(callDefinition);
            }

            callDefinition.clause(call);
        }
    }


    @NotNull
    public static TreeElement[] callChildren(@NotNull Modular modular, @NotNull Call call) {
        Call[] childCalls = ElixirPsiImplUtil.macroChildCalls(call);
        TreeElement[] children = childCallTreeElements(modular, childCalls);

        if (children == null) {
            children = new TreeElement[0];
        }

        return children;
    }

    @Nullable
    public static String elementDescription(Call call, ElementDescriptionLocation location) {
        String elementDescription = null;

        if (location == UsageViewLongNameLocation.INSTANCE) {
            Call enclosingCall = enclosingMacroCall(call);
            // indirect recursion through ElementDescriptionUtil.getElementDescription because it is @NotNull and will
            // default to element text when not implemented, so a bug, but not an error will result.
            String relative = ElementDescriptionUtil.getElementDescription(call, UsageViewShortNameLocation.INSTANCE);

            if (enclosingCall != null) {
                String qualified = ElementDescriptionUtil.getElementDescription(enclosingCall, location);
                elementDescription = qualified + "." + relative;
            } else {
                elementDescription = relative;
            }
        } else if (location == UsageViewShortNameLocation.INSTANCE) {
           elementDescription = call.getName();
        } else if (location == UsageViewTypeLocation.INSTANCE) {
            elementDescription = "module";
        }

        return elementDescription;
    }

    public static boolean is(Call call) {
        return call.isCallingMacro(KERNEL_MODULE_NAME, "defmodule", 2);
    }

    public static PsiElement nameIdentifier(Call call) {
        PsiElement[] primaryArguments = call.primaryArguments();
        PsiElement nameIdentifier = null;

        if (primaryArguments != null && primaryArguments.length > 0) {
            nameIdentifier = primaryArguments[0];
        }

        return nameIdentifier;
    }

    /*
     * Private Static Methods
     */

    @Contract(pure = true)
    @Nullable
    private static TreeElement[] childCallTreeElements(@NotNull Modular modular, Call[] childCalls) {
        TreeElement[] treeElements = null;

        if (childCalls != null) {
            int length = childCalls.length;
            final List<TreeElement> treeElementList = new ArrayList<TreeElement>(length);
            FunctionByNameArity functionByNameArity = new FunctionByNameArity(length, treeElementList, modular);
            MacroByNameArity macroByNameArity = new MacroByNameArity(length, treeElementList, modular);
            Set<Overridable> overridableSet = new HashSet<Overridable>();
            Set<org.elixir_lang.structure_view.element.Use> useSet = new HashSet<org.elixir_lang.structure_view.element.Use>();

            for (Call childCall : childCalls) {
                if (Callback.is(childCall)) {
                    treeElementList.add(new Callback(modular, childCall));
                } else if (Delegation.is(childCall)) {
                    functionByNameArity.addDelegationToTreeElementList(childCall);
                } else if (Exception.is(childCall)) {
                    functionByNameArity.setException(new Exception(modular, childCall));
                } else if (CallDefinitionClause.isFunction(childCall)) {
                    functionByNameArity.addClausesToCallDefinition(childCall);
                } else if (CallDefinitionSpecification.is(childCall)) {
                    functionByNameArity.addSpecificationToCallDefinition(childCall);
                } else if (Implementation.is(childCall)) {
                    treeElementList.add(new Implementation(modular, childCall));
                } else if (CallDefinitionClause.isMacro(childCall)) {
                    macroByNameArity.addClausesToCallDefinition(childCall);
                } else if (Module.is(childCall)) {
                    treeElementList.add(new Module(modular, childCall));
                } else if (Overridable.is(childCall)) {
                    Overridable overridable = new Overridable(modular, childCall);
                    overridableSet.add(overridable);
                    treeElementList.add(overridable);
                } else if (Protocol.is(childCall)) {
                    treeElementList.add(new Protocol(modular, childCall));
                } else if (org.elixir_lang.structure_view.element.Quote.is(childCall)) {
                    treeElementList.add(new Quote(modular, childCall));
                } else if (Structure.is(childCall)) {
                    treeElementList.add(new Structure(modular, childCall));
                } else if (Type.is(childCall)) {
                    treeElementList.add(Type.fromCall(modular, childCall));
                } else if (org.elixir_lang.structure_view.element.Use.is(childCall)) {
                    org.elixir_lang.structure_view.element.Use use = new org.elixir_lang.structure_view.element.Use(modular, childCall);
                    useSet.add(use);
                    treeElementList.add(use);
                }
            }

            for (Overridable overridable : overridableSet) {
                for (TreeElement treeElement : overridable.getChildren()) {
                    CallReference callReference = (CallReference) treeElement;
                    Integer arity = callReference.arity();

                    if (arity != null) {
                        String name = callReference.name();

                        CallDefinition function = functionByNameArity.get(pair(name, arity));

                        if (function != null) {
                            function.setOverridable(true);
                        }
                    }
                }
            }

            Collection<TreeElement> useCollection = new HashSet<TreeElement>(useSet.size());
            useCollection.addAll(useSet);
            Collection<TreeElement> nodesFromUses = Used.provideNodesFromChildren(useCollection);
            Map<Pair<String, Integer>, CallDefinition> useFunctionByNameArity = Used.functionByNameArity(nodesFromUses);

            for (Map.Entry<Pair<String, Integer>, CallDefinition> useNameArityFunction : useFunctionByNameArity.entrySet()) {
                CallDefinition useFunction = useNameArityFunction.getValue();

                if (useFunction.isOverridable()) {
                    Pair<String, Integer> useNameArity = useNameArityFunction.getKey();

                    CallDefinition function = functionByNameArity.get(useNameArity);

                    if (function != null) {
                        function.setOverride(true);
                    }
                }
            }

            treeElements = treeElementList.toArray(new TreeElement[treeElementList.size()]);
        }

        return treeElements;
    }

    /*
     * Constructors
     */

    public Module(@NotNull Call call) {
        this(null, call);
    }

    /**
     *
     * @param parent the parent {@link Module} or {@link org.elixir_lang.structure_view.element.Quote} that scopes
     *   {@code call}.
     * @param call the {@code Kernel.defmodule/2} call nested in {@code parent}.
     */
    public Module(@Nullable Modular parent, @NotNull Call call) {
        super(call);
        this.parent = parent;
    }

    /*
     * Public Instance Methods
     */

    @NotNull
    @Override
    public TreeElement[] getChildren() {
        return callChildren(this, navigationItem);
    }

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        return new org.elixir_lang.navigation.item_presentation.modular.Module(location(), navigationItem);
    }

    /*
     * Protected Instanc Methods
     */

    @Nullable
    protected String location() {
        String location = null;

        if (parent != null) {
            ItemPresentation itemPresentation = parent.getPresentation();

            if (itemPresentation instanceof Parent) {
                Parent parentPresentation = (Parent) itemPresentation;
                location = parentPresentation.getLocatedPresentableText();
            }
        }

        return location;
    }

}

