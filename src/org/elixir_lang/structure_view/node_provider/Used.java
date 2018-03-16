package org.elixir_lang.structure_view.node_provider;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.ActionShortcutProvider;
import com.intellij.ide.util.FileStructureNodeProvider;
import com.intellij.ide.util.treeView.smartTree.ActionPresentation;
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.psi.impl.ElixirPsiImplUtil.stripAccessExpression;
import static org.elixir_lang.psi.impl.call.CallImplKt.finalArguments;
import static org.elixir_lang.psi.impl.call.CallImplKt.macroChildCalls;
import static org.elixir_lang.structure_view.element.modular.Module.addClausesToCallDefinition;

public class Used implements FileStructureNodeProvider<TreeElement>, ActionShortcutProvider {
    /*
     * CONSTANTS
     */

    @NonNls
    public static final String ID = "SHOW_USED";
    private static final String USING = "__using__";

    /*
     * Static Methods
     */

    private static Collection<TreeElement> filterOverridden(@NotNull Collection<TreeElement> nodesFromChildren,
                                                            @NotNull Collection<TreeElement> children) {
        Map<Pair<String, Integer>, CallDefinition> childFunctionByNameArity = functionByNameArity(children);
        Collection<TreeElement> filtered = new ArrayList<>(nodesFromChildren.size());

        for (TreeElement nodeFromChildren : nodesFromChildren) {
            if (nodeFromChildren instanceof CallDefinition) {
                CallDefinition callDefinition = (CallDefinition) nodeFromChildren;

                // only functions work with defoverridable
                if (callDefinition.time() == Timed.Time.RUN) {
                    Pair<String, Integer> nameArity = pair(callDefinition.name(), callDefinition.arity());

                    if (childFunctionByNameArity.containsKey(nameArity)) {
                        continue;
                    }
                }
            }

            filtered.add(nodeFromChildren);
        }

        return filtered;
    }

    public static Map<Pair<String, Integer>, CallDefinition> functionByNameArity(@NotNull Collection<TreeElement> children) {
        Map<Pair<String, Integer>, CallDefinition> functionByNameArity = new HashMap<>(children.size());

        for (TreeElement child : children) {
            if (child instanceof CallDefinition) {
                CallDefinition callDefinition = (CallDefinition) child;

                if (callDefinition.time() == Timed.Time.RUN) {
                    Pair<String, Integer> nameArity = pair(callDefinition.name(), callDefinition.arity());
                    functionByNameArity.put(nameArity, callDefinition);
                }
            }
        }

        return functionByNameArity;
    }

    private static Collection<TreeElement> provideNodesFromChild(@NotNull TreeElement child) {
        Collection<TreeElement> nodes = null;

        if (child instanceof Use) {
            Use use = (Use) child;
            PsiElement[] finalArguments = finalArguments(use.call());

            assert finalArguments != null;

            if (finalArguments.length > 0) {
                PsiElement firstFinalArgument = finalArguments[0];

                if (firstFinalArgument instanceof ElixirAccessExpression) {
                    PsiElement accessExpressionChild = stripAccessExpression(firstFinalArgument);

                    if (accessExpressionChild instanceof QualifiableAlias) {
                        PsiReference reference = accessExpressionChild.getReference();

                        if (reference != null) {
                            PsiElement ancestor = reference.resolve();

                            while (ancestor != null && !(ancestor instanceof PsiFile)) {
                                if (ancestor instanceof Call) {
                                    Call call = (Call) ancestor;

                                    if (Module.is(call)) {
                                        Module module = new Module(call);
                                        Call[] childCalls = macroChildCalls(call);

                                        Map<Pair<String, Integer>, CallDefinition> macroByNameArity = new HashMap<>(childCalls.length);

                                        for (Call childCall : childCalls) {
                                                /* portion of {@link org.elixir_lang.structure_view.element.enclosingModular.Module#childCallTreeElements}
                                                   dealing with macros, restricted to __using__/1 */
                                            if (CallDefinitionClause.isMacro(childCall)) {
                                                Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(childCall);

                                                if (nameArityRange != null) {
                                                    String name = nameArityRange.first;
                                                    IntRange arityRange = nameArityRange.second;

                                                    if (name.equals(USING) && arityRange.containsInteger(1)) {
                                                        addClausesToCallDefinition(
                                                                childCall,
                                                                name,
                                                                arityRange,
                                                                macroByNameArity,
                                                                module,
                                                                Timed.Time.COMPILE,
                                                                element -> {
                                                                }
                                                        );
                                                    }
                                                }
                                            }
                                        }

                                        if (macroByNameArity.size() > 0) {
                                            PsiElement[] usingArguments;
                                            CallDefinition macro;
                                            CallDefinitionClause matchingClause = null;

                                            if (finalArguments.length > 1) {
                                                usingArguments = Arrays.copyOfRange(finalArguments, 1, finalArguments.length);
                                                Pair<String, Integer> nameArity = pair(USING, usingArguments.length);
                                                macro = macroByNameArity.get(nameArity);

                                                if (macro != null) {
                                                    matchingClause = macro.matchingClause(usingArguments);
                                                }
                                            } else {
                                                    /* `use <ALIAS>` will calls `__using__/1` even though there is
                                                       no additional argument, but it obviously can't select a clause. */
                                                Pair<String, Integer> nameArity = pair(USING, 1);
                                                macro = macroByNameArity.get(nameArity);
                                                List<CallDefinitionClause> macroClauseList = macro.clauseList();

                                                if (macroClauseList.size() == 1) {
                                                    matchingClause = macroClauseList.get(0);
                                                } else {
                                                    // TODO match default argument clause/head to non-default argument clause that would be executed.
                                                }
                                            }

                                            if (matchingClause != null) {
                                                TreeElement[] callDefinitionClauseChildren = matchingClause.getChildren();
                                                int length = callDefinitionClauseChildren.length;

                                                if (length > 0) {
                                                    TreeElement lastCallDefinitionClauseChild = callDefinitionClauseChildren[length - 1];

                                                    if (lastCallDefinitionClauseChild instanceof Quote) {
                                                        Quote quote = (Quote) lastCallDefinitionClauseChild;
                                                        Quote injectedQuote = quote.used(use);
                                                        TreeElement[] injectedQuoteChildren = injectedQuote.getChildren();
                                                        nodes = new ArrayList<>(injectedQuoteChildren.length);

                                                        for (TreeElement injectedQuoteChild : injectedQuoteChildren) {
                                                            if (!(injectedQuoteChild instanceof Overridable)) {
                                                                nodes.add(injectedQuoteChild);
                                                            }
                                                        }

                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        break;
                                    }
                                }

                                ancestor = ancestor.getParent();
                            }
                        }
                    }
                }
            }
        }

        if (nodes == null) {
            nodes = Collections.emptyList();
        }

        return nodes;
    }

    public static Collection<TreeElement> provideNodesFromChildren(@NotNull Collection<TreeElement> children) {
        Collection<TreeElement> nodes = new ArrayList<>();

        for (TreeElement child : children) {
            Collection<TreeElement>  nodesFromChild = provideNodesFromChild(child);
            nodes.addAll(nodesFromChild);
        }

        return nodes;
    }

    /*
     * Instance Methods
     */

    @NotNull
    @Override
    public String getActionIdForShortcut() {
        return "FileStructurePopup";
    }

    @NotNull
    @Override
    public String getCheckBoxText() {
        return "Show Used";
    }

    /**
     * Returns a unique identifier for the action.
     *
     * @return the action identifier.
     */
    @NotNull
    @Override
    public String getName() {
        return ID;
    }

    /**
     * Returns the presentation for the action.
     *
     * @return the action presentation.
     * @see ActionPresentationData#ActionPresentationData(String, String, Icon)
     */
    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Show Used", null, AllIcons.Hierarchy.Supertypes);
    }

    @NotNull
    @Override
    public Shortcut[] getShortcut() {
        throw new IncorrectOperationException("see getActionIdForShortcut()");
    }

    @NotNull
    @Override
    public Collection<TreeElement> provideNodes(@NotNull TreeElement node) {
        Collection<TreeElement> nodes = new ArrayList<>();

        if (node instanceof Module) {
            Module module = (Module) node;
            TreeElement[] children = module.getChildren();
            Collection<TreeElement> childCollection = Arrays.asList(children);

            nodes = provideNodesFromChildren(childCollection);
            nodes = filterOverridden(nodes, childCollection);
        }

        return nodes;
    }

}
