package org.elixir_lang.navigation;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.NamedElement;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.stub.index.AllName;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Modular;
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
            if (element instanceof Call) {
                Call call = (Call) element;

                if (CallDefinitionClause.is(call)) {
                    Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(call);

                    if (nameArityRange != null) {
                        String callName = nameArityRange.first;
                        IntRange arityRange = nameArityRange.second;

                        Timed.Time time = CallDefinitionClause.time(call);
                        Modular modular = enclosingModularByCall.putNew(call);

                        assert modular != null;

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
                } else if (CallDefinitionHead.is(call)) {
                    Call delegationCall = CallDefinitionHead.enclosingDelegationCall(call);

                    assert delegationCall != null;

                    Modular modular = enclosingModularByCall.putNew(delegationCall);

                    assert modular != null;

                    String callDefinitionName = call.functionName();

                    assert callDefinitionName != null;

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
                    Visible.Visibility visibility = Visible.Visibility.PUBLIC;

                    //noinspection ConstantConditions
                    CallDefinitionHead callDefinitionHead = new CallDefinitionHead(callDefinition, visibility, call);
                    items.add(callDefinitionHead);
                } else if (CallDefinitionSpecification.is(call)) {
                    Modular modular = enclosingModularByCall.putNew(call);

                    assert modular != null;

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
                } else if (Callback.is(call)) {
                    Modular modular = enclosingModularByCall.putNew(call);

                    assert modular != null;

                    Callback callback = new Callback(modular, call);
                    items.add(callback);
                }
            }
        }

        return items.toArray(new NavigationItem[items.size()]);
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
}
