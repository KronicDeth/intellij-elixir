package org.elixir_lang.structure_view.element.call_definition_by_name_arity;


import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.Pair;
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.CallDefinition;
import org.elixir_lang.structure_view.element.CallDefinitionSpecification;
import org.elixir_lang.structure_view.element.Exception;
import org.elixir_lang.structure_view.element.Timed;
import org.elixir_lang.structure_view.element.modular.Modular;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.intellij.openapi.util.Pair.pair;

public class FunctionByNameArity extends TreeElementList {
    /*
     * Fields
     */

    @Nullable
    private Exception exception = null;

    /*
     * Constructors
     */

    public FunctionByNameArity(int size,
                               @NotNull List<TreeElement> treeElementList,
                               @NotNull Modular modular) {
        super(size, treeElementList, modular, Timed.Time.RUN);
    }

    @Override
    public void addToTreeElementList(CallDefinition function) {
        if (exception != null && Exception.isCallback(pair(function.name(), function.arity()))) {
            exception.callback(function);
        } else {
            super.addToTreeElementList(function);
        }
    }

    /*
     * Instance Methods
     */

    public void addSpecificationToCallDefinition(Call moduleAttributeDefinition) {
        assert moduleAttributeDefinition instanceof AtUnqualifiedNoParenthesesCall;

        Pair<String, Integer> nameArity = CallDefinitionSpecification.moduleAttributeNameArity(
                moduleAttributeDefinition
        );

        if (nameArity != null) {
            CallDefinition function = putNew(nameArity);
            function.specification((AtUnqualifiedNoParenthesesCall) moduleAttributeDefinition);
        }
    }

    @Nullable
    public Exception getException() {
        return exception;
    }

    public void setException(@NotNull Exception exception) {
        this.exception = exception;
        treeElementList.add(exception);
    }
}
