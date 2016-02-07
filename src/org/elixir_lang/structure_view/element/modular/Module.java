package org.elixir_lang.structure_view.element.modular;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.NotImplementedException;
import org.elixir_lang.navigation.item_presentation.*;
import org.elixir_lang.navigation.item_presentation.Parent;
import org.elixir_lang.psi.*;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Delegation;
import org.elixir_lang.structure_view.element.Exception;
import org.elixir_lang.structure_view.element.Implementation;
import org.elixir_lang.structure_view.element.Quote;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Module extends Element<Call> implements Modular {
    /*
     * Fields
     */

    @Nullable
    protected final Modular parent;

    /*
     * Static Methods
     */

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
            List<TreeElement> treeElementList = new ArrayList<TreeElement>(length);
            Map<Pair<String, Integer>, CallDefinition> functionByNameArity = new HashMap<Pair<String, Integer>, CallDefinition>(length);
            Map<Pair<String, Integer>, CallDefinition> macroByNameArity = new HashMap<Pair<String, Integer>, CallDefinition>(length);
            Exception exception = null;

            for (Call childCall : childCalls) {
                if (Delegation.is(childCall)) {
                    treeElementList.add(new Delegation(modular, childCall));
                } else if (Exception.is(childCall)) {
                    exception = new Exception(modular, childCall);
                    treeElementList.add(exception);
                } else if (CallDefinitionClause.isFunction(childCall)) {
                    Pair<String, Integer> nameArity = CallDefinitionClause.nameArity(childCall);

                    CallDefinition function = functionByNameArity.get(nameArity);

                    if (function == null) {
                        function = new CallDefinition(
                                modular,
                                Timed.Time.RUN,
                                nameArity.first,
                                nameArity.second
                        );
                        functionByNameArity.put(nameArity, function);

                        // callbacks are nested under the behavior they are for
                        if (exception != null && Exception.isCallback(nameArity)) {
                            exception.callback(function);
                        } else {
                            treeElementList.add(function);
                        }
                    }

                    function.clause(childCall);
                } else if (Implementation.is(childCall)) {
                    treeElementList.add(new Implementation(modular, childCall));
                } else if (CallDefinitionClause.isMacro(childCall)) {
                    Pair<String, Integer> nameArity = CallDefinitionClause.nameArity(childCall);

                    CallDefinition macro = macroByNameArity.get(nameArity);

                    if (macro == null) {
                        macro = new CallDefinition(
                                modular,
                                Timed.Time.COMPILE,
                                nameArity.first,
                                nameArity.second
                        );
                        macroByNameArity.put(nameArity, macro);
                        treeElementList.add(macro);
                    }

                    macro.clause(childCall);
                } else if (Module.is(childCall)) {
                    treeElementList.add(new Module(modular, childCall));
                } else if (org.elixir_lang.structure_view.element.Quote.is(childCall)) {
                    treeElementList.add(new Quote(modular, childCall));
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

