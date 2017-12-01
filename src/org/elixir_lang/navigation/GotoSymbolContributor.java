package org.elixir_lang.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.Visibility;
import org.elixir_lang.errorreport.Logger;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Implementation;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.elixir_lang.structure_view.element.modular.Module;
import org.elixir_lang.structure_view.element.modular.Protocol;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/ignatov/intellij-erlang/blob/2f59e59a31ecbb2fbdf9b7a3547fb4f206b0807e/src/org/intellij/erlang/go/ErlangSymbolContributor.java">org.intellij.erlang.go.ErlangSymbolContributor</a>
 */
public class GotoSymbolContributor implements ChooseByNameContributor {
    /*
     * Static Methods
     */

    @NotNull
    private static GlobalSearchScope globalSearchScope(Project project, boolean includeNonProjectItems) {
        GlobalSearchScope scope;

        if (includeNonProjectItems) {
            scope = GlobalSearchScope.allScope(project);
        } else {
            scope = GlobalSearchScope.projectScope(project);
        }

        return scope;
    }

    /*
     * Instance Methods
     */

    /**
     * Returns the list of navigation items matching the specified name.
     *
     * @param name                   the name selected from the list.
     * @param pattern                the original pattern entered in the dialog
     * @param project                the project in which the navigation is performed.
     * @param includeNonProjectItems if true, the navigation items for non-project items (for example,
     *                               library classes) should be included in the returned array.
     * @return the array of navigation items.
     */
    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name,
                                           String pattern,
                                           Project project,
                                           boolean includeNonProjectItems) {
        GlobalSearchScope scope = globalSearchScope(project, includeNonProjectItems);

        Collection<NamedElement> result = StubIndex.getElements(AllName.KEY, name, project, scope, NamedElement.class);
        List<NavigationItem> items = ContainerUtil.newArrayListWithCapacity(result.size());
        EnclosingModularByCall enclosingModularByCall = new EnclosingModularByCall();
        Map<CallDefinition.Tuple, CallDefinition> callDefinitionByTuple = new HashMap<CallDefinition.Tuple, CallDefinition>();

        for (final NamedElement element : result) {
            // Use navigation element so that source element is used for compiled elements
            PsiElement sourceElement = element.getNavigationElement();

            if (sourceElement instanceof Call) {
                getItemsByNameFromCall(
                        name,
                        items,
                        enclosingModularByCall,
                        callDefinitionByTuple,
                        (Call) sourceElement
                );
            }
        }

        return items.toArray(new NavigationItem[items.size()]);
    }

    private void getItemsByNameFromCall(@NotNull String name,
                                        @NotNull List<NavigationItem> items,
                                        @NotNull EnclosingModularByCall enclosingModularByCall,
                                        @NotNull Map<CallDefinition.Tuple, CallDefinition> callDefinitionByTuple,
                                        @NotNull Call call) {
        if (CallDefinitionClause.is(call)) {
            getItemsFromCallDefinitionClause(items, enclosingModularByCall, callDefinitionByTuple, call);
        } else if (CallDefinitionHead.is(call)) {
            getItemsFromCallDefinitionHead(items, enclosingModularByCall, callDefinitionByTuple, call);
        } else if (CallDefinitionSpecification.is(call)) {
            getItemsFromCallDefinitionSpecification(items, enclosingModularByCall, call);
        } else if (Callback.is(call)) {
            getItemsFromCallback(items, enclosingModularByCall, call);
        } else if (Implementation.is(call)) {
            getItemsFromImplementation(name, items, enclosingModularByCall, call);
        } else if (Module.is(call)) {
            getItemsFromModule(items, enclosingModularByCall, call);
        } else if (Protocol.is(call)) {
            getItemsFromProtocol(items, enclosingModularByCall, call);
        }
    }

    private void getItemsFromCallback(@NotNull List<NavigationItem> items,
                                      @NotNull EnclosingModularByCall enclosingModularByCall,
                                      @NotNull Call call) {
        Modular modular = enclosingModularByCall.putNew(call);

        if (modular != null) {
            Callback callback = new Callback(modular, call);
            items.add(callback);
        } else {
            error("Cannot find enclosing Modular for Callback", call);
        }
    }

    private void getItemsFromCallDefinitionClause(
            @NotNull List<NavigationItem> items,
            @NotNull EnclosingModularByCall enclosingModularByCall,
            @NotNull Map<CallDefinition.Tuple, CallDefinition> callDefinitionByTuple,
            @NotNull Call call
    ) {
        Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(call);

        if (nameArityRange != null) {
            String callName = nameArityRange.first;
            IntRange arityRange = nameArityRange.second;

            Timed.Time time = CallDefinitionClause.time(call);
            Modular modular = enclosingModularByCall.putNew(call);

            if (modular == null) {
                // don't throw an error if really EEX, but has wrong extension
                if (!call.getText().contains("<%=")) {
                    error("Cannot find enclosing Modular", call);
                }
            } else {
                for (int arity = arityRange.getMinimumInteger(); arity <= arityRange.getMaximumInteger(); arity++) {
                    CallDefinition.Tuple tuple = new CallDefinition.Tuple(modular, time, callName, arity);
                    CallDefinition callDefinition = callDefinitionByTuple.get(tuple);

                    if (callDefinition == null) {
                        callDefinition = new CallDefinition(tuple.modular, tuple.time, tuple.name, tuple.arity);
                        items.add(callDefinition);
                        callDefinitionByTuple.put(tuple, callDefinition);
                    }

                    CallDefinitionClause callDefinitionClause = callDefinition.clause(call);
                    items.add(callDefinitionClause);
                }
            }
        }
    }

    private void getItemsFromCallDefinitionHead(
            @NotNull List<NavigationItem> items,
            @NotNull EnclosingModularByCall enclosingModularByCall,
            @NotNull Map<CallDefinition.Tuple, CallDefinition> callDefinitionByTuple,
            @NotNull Call call
    ) {
        Call delegationCall = CallDefinitionHead.enclosingDelegationCall(call);

        if (delegationCall != null) {
            Modular modular = enclosingModularByCall.putNew(delegationCall);

            if (modular != null) {
                String callDefinitionName = call.functionName();

                if (callDefinitionName != null) {
                    int callDefinitionArity = call.resolvedFinalArity();

                    CallDefinition.Tuple tuple = new CallDefinition.Tuple(
                            modular,
                            // Delegation can't delegate macros
                            Timed.Time.RUN,
                            callDefinitionName,
                            callDefinitionArity
                    );
                    CallDefinition callDefinition = callDefinitionByTuple.get(tuple);

                    if (callDefinition == null) {
                        callDefinition = new CallDefinition(tuple.modular, tuple.time, tuple.name, tuple.arity);
                        items.add(callDefinition);
                        callDefinitionByTuple.put(tuple, callDefinition);
                    }

                    // Delegation is always public as import should be used for private
                    Visibility visibility = Visibility.PUBLIC;

                    //noinspection ConstantConditions
                    CallDefinitionHead callDefinitionHead = new CallDefinitionHead(callDefinition, visibility, call);
                    items.add(callDefinitionHead);
                } else {
                    error("Call for CallDefinitionHead does not have function name", call);
                }
            } else {
                error("Cannot find enclosing Modular for Delegation call", delegationCall);
            }
        } else {
            error("Cannot find enclosing delegation call for CallDefinitionHead", call);
        }
    }

    private void getItemsFromCallDefinitionSpecification(@NotNull List<NavigationItem> items,
                                                         @NotNull EnclosingModularByCall enclosingModularByCall,
                                                         @NotNull Call call) {
        Modular modular = enclosingModularByCall.putNew(call);

        if (modular != null) {
            // pseudo-named-arguments
            boolean callback = false;
            Timed.Time time = Timed.Time.RUN;

            //noinspection ConstantConditions
            CallDefinitionSpecification callDefinitionSpecification = new CallDefinitionSpecification(
                    modular,
                    (AtUnqualifiedNoParenthesesCall) call,
                    callback,
                    time
            );
            items.add(callDefinitionSpecification);
        } else {
            error("Cannot find enclosing Modular for CallDefinitionSpecification", call);
        }
    }

    private void getItemsFromImplementation(@NotNull String name,
                                            @NotNull List<NavigationItem> items,
                                            @NotNull EnclosingModularByCall enclosingModularByCall,
                                            @NotNull Call call) {
        Modular modular = enclosingModularByCall.putNew(call);

        Collection<String> forNameCollection = Implementation.forNameCollection(modular, call);

        if (forNameCollection != null) {
            for (String forName : forNameCollection) {
                Implementation forNameOverriddenImplementation = new Implementation(modular, call, forName);
                String implementationName = forNameOverriddenImplementation.getName();

                if (implementationName != null && implementationName.contains(name)) {
                    items.add(forNameOverriddenImplementation);
                }
            }
        }

        if (forNameCollection == null || forNameCollection.size() < 2) {
            Implementation implementation = new Implementation(modular, call);
            items.add(implementation);
        }
    }

    private void getItemsFromModule(@NotNull List<NavigationItem> items,
                                    @NotNull EnclosingModularByCall enclosingModularByCall,
                                    @NotNull Call call) {
        Modular modular = enclosingModularByCall.putNew(call);

        Module module = new Module(modular, call);
        items.add(module);
    }

    private void getItemsFromProtocol(@NotNull List<NavigationItem> items,
                                      @NotNull EnclosingModularByCall enclosingModularByCall,
                                      @NotNull Call call) {
        Modular modular = enclosingModularByCall.putNew(call);

        Protocol protocol = new Protocol(modular, call);
        items.add(protocol);
    }

    /**
     * Returns the list of names for the specified project to which it is possible to navigate
     * by name.
     *
     * @param project                the project in which the navigation is performed.
     * @param includeNonProjectItems if true, the names of non-project items (for example,
     *                               library classes) should be included in the returned array.
     * @return the array of names.
     */
    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(AllName.KEY, project));
    }

    /*
     * Private Instance Methods
     */

    private void error(@NotNull String userMessage, @NotNull PsiElement element) {
        Logger.error(this.getClass(), userMessage, element);
    }
}
