package org.elixir_lang.structure_view.node_provider

import com.intellij.icons.AllIcons
import com.intellij.ide.util.ActionShortcutProvider
import com.intellij.ide.util.FileStructureNodeProvider
import com.intellij.ide.util.treeView.smartTree.ActionPresentation
import com.intellij.ide.util.treeView.smartTree.ActionPresentationData
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.openapi.actionSystem.Shortcut
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import org.elixir_lang.NameArity
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.macroChildCalls
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.semantic
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.call.definition.Clause
import org.elixir_lang.structure_view.element.modular.Module
import org.jetbrains.annotations.NonNls
import java.util.*

class Used : FileStructureNodeProvider<TreeElement>, ActionShortcutProvider {
    override fun getActionIdForShortcut(): String = "FileStructurePopup"
    override fun getCheckBoxText(): String = "Show Used"

    /**
     * Returns a unique identifier for the action.
     *
     * @return the action identifier.
     */
    override fun getName(): String = ID

    /**
     * Returns the presentation for the action.
     *
     * @return the action presentation.
     * @see ActionPresentationData.ActionPresentationData
     */
    override fun getPresentation(): ActionPresentation =
        ActionPresentationData("Show Used", null, AllIcons.Hierarchy.Supertypes)

    override fun getShortcut(): Array<Shortcut> = throw IncorrectOperationException("see getActionIdForShortcut()")

    override fun provideNodes(node: TreeElement): Collection<TreeElement> =
        (node as? Module)?.children?.toList()?.let { childCollection ->
            provideNodesFromChildren(childCollection).let { filterOverridden(it, childCollection) }
        } ?: emptyList()

    companion object {
        @NonNls
        const val ID = "SHOW_USED"
        private const val USING = "__using__"

        private fun filterOverridden(
            nodesFromChildren: Collection<TreeElement>,
            children: Collection<TreeElement>
        ): Collection<TreeElement> {
            val childFunctionByNameArity = functionByNameArity(children)

            return nodesFromChildren
                .filterIsInstance<Definition>()
                // only functions work with `defoverridable`
                .filter { it.time == Time.RUN }
                .filterNot { definition ->
                    definition.semantic.nameArityInterval?.let { nameArityInterval ->
                        val name = nameArityInterval.name

                        nameArityInterval.arityInterval.closed().all { arity ->
                            val nameArity = NameArity(name, arity)

                            childFunctionByNameArity.containsKey(nameArity)
                        }
                    } ?: false
                }
        }

        fun functionByNameArity(children: Collection<TreeElement>): Map<NameArity, Definition> =
            children
                .filterIsInstance<Definition>()
                .filter { it.time == Time.RUN }
                .flatMap { definition ->
                    definition
                        .semantic
                        .nameArityInterval?.let { nameArityInterval ->
                            val name = nameArityInterval.name

                            nameArityInterval.arityInterval.closed().map { arity ->
                                NameArity(name, arity) to definition
                            }
                        }
                        .orEmpty()
                }
                .toMap()

        private fun provideNodesFromChild(child: TreeElement): Collection<TreeElement> {
            var nodes: MutableCollection<TreeElement>? = null

            if (child is Use) {
                val finalArguments = child.call().finalArguments()!!

                if (finalArguments.isNotEmpty()) {
                    val firstFinalArgument = finalArguments[0]

                    if (firstFinalArgument is ElixirAccessExpression) {
                        val accessExpressionChild = firstFinalArgument.stripAccessExpression()

                        if (accessExpressionChild is QualifiableAlias) {
                            val reference = accessExpressionChild.getReference()

                            if (reference != null) {
                                var ancestor = reference.resolve()

                                while (ancestor != null && ancestor !is PsiFile) {
                                    if (ancestor is Call) {
                                        val call = ancestor
                                        val semantic = call.semantic

                                        if (semantic is org.elixir_lang.semantic.Module) {
                                            val module =
                                                Module(semantic = semantic as org.elixir_lang.semantic.Modular)
                                            val childCalls = call.macroChildCalls()

                                            val macroByNameArity = HashMap<NameArity, Definition>(childCalls.size)

                                            for (childCall in childCalls) {
                                                val childSemantic = childCall.semantic

                                                /* portion of {@link org.elixir_lang.structure_view.element.enclosingModular.Module#childCallTreeElements}
                                                   dealing with macros, restricted to __using__/1 */
                                                if (childSemantic
                                                            is org.elixir_lang.semantic.call.definition.Clause
                                                ) {
                                                    val nameArityInterval = childSemantic.nameArityInterval

                                                    if (nameArityInterval != null) {
                                                        val name = nameArityInterval.name
                                                        val arityInterval = nameArityInterval.arityInterval

                                                        if (name == USING && arityInterval.contains(1)) {
                                                            TODO()
                                                        }
                                                    }
                                                }
                                            }

                                            if (macroByNameArity.size > 0) {
                                                val usingArguments: Array<PsiElement>
                                                val macro: Definition?
                                                var matchingClause: Clause? = null

                                                if (finalArguments.size > 1) {
                                                    usingArguments =
                                                        Arrays.copyOfRange(finalArguments, 1, finalArguments.size)
                                                    val nameArity = NameArity(USING, usingArguments.size)
                                                    macro = macroByNameArity[nameArity]

                                                    if (macro != null) {
                                                        matchingClause = macro.matchingClause(usingArguments)
                                                    }
                                                } else {
                                                    /* `use <ALIAS>` will calls `__using__/1` even though there is
                                                       no additional argument, but it obviously can't select a clause. */
                                                    val nameArity = NameArity(USING, 1)
                                                    macro = macroByNameArity[nameArity]
                                                    val macroClauseList = macro!!.clauseList()

                                                    if (macroClauseList.size == 1) {
                                                        matchingClause = macroClauseList[0]
                                                    } else {
                                                        // TODO match default argument clause/head to non-default argument clause that would be executed.
                                                    }
                                                }

                                                if (matchingClause != null) {
                                                    val callDefinitionClauseChildren = matchingClause.children
                                                    val length = callDefinitionClauseChildren.size

                                                    if (length > 0) {
                                                        val lastCallDefinitionClauseChild =
                                                            callDefinitionClauseChildren[length - 1]

                                                        if (lastCallDefinitionClauseChild is Quote) {
                                                            val injectedQuote =
                                                                lastCallDefinitionClauseChild.used(child)
                                                            val injectedQuoteChildren = injectedQuote.children
                                                            nodes = ArrayList(injectedQuoteChildren.size)

                                                            for (injectedQuoteChild in injectedQuoteChildren) {
                                                                if (injectedQuoteChild !is Overridable) {
                                                                    nodes.add(injectedQuoteChild)
                                                                }
                                                            }

                                                            break
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            break
                                        }
                                    }

                                    ancestor = ancestor.parent
                                }
                            }
                        }
                    }
                }
            }

            if (nodes == null) {
                nodes = mutableListOf()
            }

            return nodes
        }

        fun provideNodesFromChildren(children: Collection<TreeElement>): Collection<TreeElement> =
            children.flatMap { provideNodesFromChild(it) }
    }
}
