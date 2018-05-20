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
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Module.Companion.addClausesToCallDefinition
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

        private fun filterOverridden(nodesFromChildren: Collection<TreeElement>,
                                     children: Collection<TreeElement>): Collection<TreeElement> {
            val childFunctionByNameArity = functionByNameArity(children)

            return nodesFromChildren
                    .filterIsInstance<CallDefinition>()
                    // only functions work with `defoverridable`
                    .filter { it.time() == Timed.Time.RUN }
                    .filterNot {
                        val nameArity = NameArity(it.name(), it.arity)

                        childFunctionByNameArity.containsKey(nameArity)
                    }
        }

        fun functionByNameArity(children: Collection<TreeElement>): Map<NameArity, CallDefinition> =
                children
                        .filterIsInstance<CallDefinition>()
                        .filter { it.time() == Timed.Time.RUN }
                        .associateBy { NameArity(it.name(), it.arity) }

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

                                        if (Module.`is`(call)) {
                                            val module = Module(call)
                                            val childCalls = call.macroChildCalls()

                                            val macroByNameArity = HashMap<NameArity, CallDefinition>(childCalls.size)

                                            for (childCall in childCalls) {
                                                /* portion of {@link org.elixir_lang.structure_view.element.enclosingModular.Module#childCallTreeElements}
                                                   dealing with macros, restricted to __using__/1 */
                                                if (CallDefinitionClause.isMacro(childCall)) {
                                                    val nameArityRange = CallDefinitionClause.nameArityRange(childCall)

                                                    if (nameArityRange != null) {
                                                        val name = nameArityRange.name
                                                        val arityRange = nameArityRange.arityRange

                                                        if (name == USING && arityRange.contains(1)) {
                                                            addClausesToCallDefinition(
                                                                    childCall,
                                                                    name,
                                                                    arityRange,
                                                                    macroByNameArity,
                                                                    module,
                                                                    Timed.Time.COMPILE
                                                            ) { _ -> }
                                                        }
                                                    }
                                                }
                                            }

                                            if (macroByNameArity.size > 0) {
                                                val usingArguments: Array<PsiElement>
                                                val macro: CallDefinition?
                                                var matchingClause: CallDefinitionClause? = null

                                                if (finalArguments.size > 1) {
                                                    usingArguments = Arrays.copyOfRange(finalArguments, 1, finalArguments.size)
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
                                                        val lastCallDefinitionClauseChild = callDefinitionClauseChildren[length - 1]

                                                        if (lastCallDefinitionClauseChild is Quote) {
                                                            val injectedQuote = lastCallDefinitionClauseChild.used(child)
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
