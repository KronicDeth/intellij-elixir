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
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.IntRange;
import org.elixir_lang.psi.ElixirAccessExpression;
import org.elixir_lang.psi.QualifiableAlias;
import org.elixir_lang.psi.call.Call;
import org.elixir_lang.psi.impl.ElixirPsiImplUtil;
import org.elixir_lang.structure_view.element.*;
import org.elixir_lang.structure_view.element.modular.Module;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

import static com.intellij.openapi.util.Pair.pair;
import static org.elixir_lang.structure_view.element.modular.Module.addClausesToCallDefinition;
import static org.elixir_lang.structure_view.element.modular.Module.is;

public class Used implements FileStructureNodeProvider<TreeElement>, ActionShortcutProvider {
    /*
     * CONSTANTS
     */

    @NonNls
    public static final String ID = "SHOW_USED";
    public static final String USING = "__using__";

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
        Collection<TreeElement> nodes = new ArrayList<TreeElement>();

        if (node instanceof Module) {
            Module module = (Module) node;
            TreeElement[] children = module.getChildren();

            for (TreeElement child : children) {
                Collection<TreeElement>  nodesFromChild = provideNodesFromChild(child);
                nodes.addAll(nodesFromChild);
            }
        }

        return nodes;
    }

    private Collection<TreeElement> provideNodesFromChild(@NotNull TreeElement child) {
        Collection<TreeElement> nodes = null;

        if (child instanceof Use) {
            Use use = (Use) child;
            PsiElement[] finalArguments = ElixirPsiImplUtil.finalArguments(use.call());

            assert finalArguments != null;

            if (finalArguments.length > 0) {
                PsiElement firstFinalArgument = finalArguments[0];

                if (firstFinalArgument instanceof ElixirAccessExpression) {
                    ElixirAccessExpression accessExpression = (ElixirAccessExpression) firstFinalArgument;

                    PsiElement[] accessExpressionChildren = accessExpression.getChildren();

                    if (accessExpressionChildren.length == 1) {
                        PsiElement accessExpressionChild = accessExpressionChildren[0];

                        if (accessExpressionChild instanceof QualifiableAlias) {
                            PsiReference reference = accessExpressionChild.getReference();

                            if (reference != null) {
                                PsiElement ancestor = reference.resolve();

                                while (ancestor != null && !(ancestor instanceof PsiFile)) {
                                    if (ancestor instanceof Call) {
                                        Call call = (Call) ancestor;

                                        if (Module.is(call)) {
                                            Module module = new Module(call);
                                            Call[] childCalls = ElixirPsiImplUtil.macroChildCalls(call);

                                            if (childCalls != null) {
                                                Map<Pair<String, Integer>, CallDefinition> macroByNameArity = new HashMap<Pair<String, Integer>, CallDefinition>(childCalls.length);

                                                for (Call childCall : childCalls) {
                                                    /* portion of {@link org.elixir_lang.structure_view.element.modular.Module#childCallTreeElements}
                                                       dealing with macros, restricted to __using__/1 */
                                                    if (CallDefinitionClause.isMacro(childCall)) {
                                                        Pair<String, IntRange> nameArityRange = CallDefinitionClause.nameArityRange(childCall);
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
                                                                    new Inserter<CallDefinition>() {
                                                                        @Override
                                                                        public void insert(CallDefinition element) {
                                                                        }
                                                                    }
                                                            );
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
                                                                TreeElement[] quoteChildren = injectedQuote.getChildren();
                                                                nodes = Arrays.asList(quoteChildren);
                                                                break;
                                                            }
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
        }

        if (nodes == null) {
            nodes = Collections.emptyList();
        }

        return nodes;
    }
}
