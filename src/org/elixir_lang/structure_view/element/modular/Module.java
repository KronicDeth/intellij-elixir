package org.elixir_lang.structure_view.element.modular;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Function;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Delegation;
import org.elixir_lang.structure_view.element.Exception;
import org.elixir_lang.structure_view.element.Implementation;
import org.elixir_lang.structure_view.element.Overridable;
import org.elixir_lang.structure_view.element.Quote;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.openapi.util.Pair.pair;

public class Module extends Element<Call> implements Modular {
    /*
     * Fields
     */

    @Nullable
    protected final Modular parent;

    /*
     * Static Methods
     */

    public static void addClausesToCallDefinition(
            @NotNull Call call,
            @NotNull Map<Pair<String, Integer>, CallDefinition> callDefinitionByNameArity,
            @NotNull Modular modular,
            @NotNull Timed.Time time,
            @NotNull Inserter<CallDefinition> callDefinitionInserter
    ) {
        Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(call);
        String name = nameArityRange.first;
        IntRange arityRange = nameArityRange.second;

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

    public static boolean is(Call call) {
        return call.isCallingMacro("Elixir.Kernel", "defmodule", 2);
    }

    @Contract(pure = true)
    @Nullable
    private static TreeElement[] childCallTreeElements(@NotNull Modular modular, Call[] childCalls) {
        TreeElement[] treeElements = null;

        if (childCalls != null) {
            int length = childCalls.length;
            final List<TreeElement> treeElementList = new ArrayList<TreeElement>(length);
            Map<Pair<String, Integer>, CallDefinition> functionByNameArity = new HashMap<Pair<String, Integer>, CallDefinition>(length);
            Map<Pair<String, Integer>, CallDefinition> macroByNameArity = new HashMap<Pair<String, Integer>, CallDefinition>(length);
            Set<Overridable> overridableSet = new HashSet<Overridable>();
            // has to be in an array so it can be final to share with function inserter
            final Exception[] exceptions = new Exception[]{ null };

            for (Call childCall : childCalls) {
                if (Delegation.is(childCall)) {
                    treeElementList.add(new Delegation(modular, childCall));
                } else if (Exception.is(childCall)) {
                    exceptions[0] = new Exception(modular, childCall);
                    treeElementList.add(exceptions[0]);
                } else if (CallDefinitionClause.isFunction(childCall)) {
                    addClausesToCallDefinition(
                            childCall,
                            functionByNameArity,
                            modular,
                            Timed.Time.RUN,
                            new Inserter<CallDefinition>() {
                                @Override
                                public void insert(CallDefinition function) {
                                    // callbacks are nested under the behavior they are for
                                    if (exceptions[0] != null &&
                                            Exception.isCallback(pair(function.name(), function.arity()))) {
                                        exceptions[0].callback(function);
                                    } else {
                                        treeElementList.add(function);
                                    }
                                }
                            }
                    );
                } else if (Implementation.is(childCall)) {
                    treeElementList.add(new Implementation(modular, childCall));
                } else if (CallDefinitionClause.isMacro(childCall)) {
                    addClausesToCallDefinition(
                            childCall,
                            macroByNameArity,
                            modular,
                            Timed.Time.COMPILE,
                            new Inserter<CallDefinition>() {
                                @Override
                                public void insert(CallDefinition macro) {
                                    treeElementList.add(macro);
                                }
                            }
                    );
                } else if (Module.is(childCall)) {
                    treeElementList.add(new Module(modular, childCall));
                } else if (Overridable.is(childCall)) {
                    Overridable overridable = new Overridable(modular, childCall);
                    overridableSet.add(overridable);
                    treeElementList.add(overridable);
                } else if (org.elixir_lang.structure_view.element.Quote.is(childCall)) {
                    treeElementList.add(new Quote(modular, childCall));
                } else if (org.elixir_lang.structure_view.element.Use.is(childCall)) {
                    treeElementList.add(new Use(modular, childCall));
                }
            }

            for (Overridable overridable : overridableSet) {
                for (TreeElement treeElement : overridable.getChildren()) {
                    CallReference callReference = (CallReference) treeElement;
                    Integer arity = callReference.arity();

                    if (arity != null) {
                        String name = callReference.name();

                        CallDefinition function = functionByNameArity.get(pair(name, arity));
                        function.setOverridable(true);
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
        String location = null;

        if (parent != null) {
            ItemPresentation itemPresentation = parent.getPresentation();

            if (itemPresentation instanceof Parent) {
                Parent parentPresentation = (Parent) itemPresentation;
                location = parentPresentation.getLocatedPresentableText();
            }
        }

        return new org.elixir_lang.navigation.item_presentation.modular.Module(location, navigationItem);
    }

}

