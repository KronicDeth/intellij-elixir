package org.elixir_lang.structure_view.element.call_definition_by_name_arity;

import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionClause;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

import static com.intellij.openapi.util.Pair.pair;

/**
 * A {@link CallDefinitionbyNameArity} that inserts the {@link org.elixir_lang.structure_view.element.CallDefinition} it
 * generates into a {@code List<TreeElement>}.
 */
public class TreeElementList
        extends HashMap<Pair<String, Integer>, CallDefinition>
        implements CallDefinitionbyNameArity {
    /*
     * Fields
     */

    @NotNull
    protected Modular modular;
    @NotNull
    private Timed.Time time;
    @NotNull
    protected List<TreeElement> treeElementList;

    /*
     * Constructor
     */

    public TreeElementList(int size,
                           @NotNull List<TreeElement> treeElementList,
                           @NotNull Modular modular,
                           @NotNull Timed.Time time) {
        super(size);
        this.modular = modular;
        this.time = time;
        this.treeElementList = treeElementList;
    }

    /*
     * Instance Methods
     */

    public void addClausesToCallDefinition(@NotNull Call call) {
        Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(call);

        if (nameArityRange != null) {
            String name = nameArityRange.first;
            IntRange arityRange = nameArityRange.second;

            addClausesToCallDefinition(call, name, arityRange);
        }
    }

    public void addClausesToCallDefinition(@NotNull Call call, @NotNull String name, @NotNull IntRange arityRange) {
        for (int arity = arityRange.getMinimumInteger(); arity <= arityRange.getMaximumInteger(); arity++) {
            Pair<String, Integer> nameArity = pair(name, arity);
            CallDefinition callDefinition = putNew(nameArity);

            callDefinition.clause(call);
        }
    }

    public void addToTreeElementList(CallDefinition callDefinition) {
        treeElementList.add(callDefinition);
    }

    /**
     * Generates a {@link CallDefinition} for the given {@code nameArity} if it does not exist.
     * <p/>
     * The {@link CallDefinition} is
     *
     * @param nameArity
     * @return pre-existing {@link CallDefinition} or new {@link CallDefinition} add to the {@code List<TreeElement>}
     */
    @NotNull
    @Override
    public CallDefinition putNew(@NotNull Pair<String, Integer> nameArity) {
        CallDefinition callDefinition = super.get(nameArity);

        if (callDefinition == null) {
            callDefinition = new CallDefinition(
                    modular,
                    time,
                    nameArity.first,
                    nameArity.second
            );
            put(nameArity, callDefinition);
            addToTreeElementList(callDefinition);
        }

        return callDefinition;
    }
}
