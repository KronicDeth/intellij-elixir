package org.elixir_lang.structure_view.element.call

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.NavigationItem
import com.intellij.psi.PsiElement
import org.elixir_lang.Timed
import org.elixir_lang.navigation.item_presentation.NameArityInterval
import org.elixir_lang.navigation.item_presentation.Parent
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.structure_view.element.Visible
import org.elixir_lang.structure_view.element.call.definition.delegation.Head
import org.elixir_lang.structure_view.element.modular.Modular
import java.util.*

/**
 * A definition for a call: either a function or a macro
 * @param modular The scoping module or quote
 */
class Definition(val modular: Modular, val semantic: org.elixir_lang.semantic.call.Definition) :
    StructureViewTreeElement, Timed, Visible, NavigationItem {
    /**
     * * `true` to mark as overridable by another function of the same name and arity;
     * * `false` to make as non-overridable.
     */
    var isOverridable: Boolean = false

    /**
     * `true` to mark as an override of another function; `false` to mark as an independent function
     */
    var override: Boolean = false

    // keeps track of total order of all children (clauses, heads, and specifications)
    private val childList = mutableListOf<TreeElement>()
    private val clauseList = mutableListOf<org.elixir_lang.structure_view.element.call.definition.Clause>()
    private val headList = mutableListOf<Head>()

    /**
     * All arguments to the CallDefinition constructor
     */
    data class Tuple(val modular: Modular, val time: Time, val name: String, val arity: Int)

    /**
     * Adds clause to macro
     *
     * @param clause the new clause for the macro
     */
    fun clause(clause: org.elixir_lang.semantic.call.definition.Clause):
            org.elixir_lang.structure_view.element.call.definition.Clause {
        val nameArityInterval = clause.nameArityInterval!!

        assert(semantic.nameArityInterval!!.contains(nameArityInterval))

        val callDefinitionClause = org.elixir_lang.structure_view.element.call.definition.Clause(this, clause)
        childList.add(callDefinitionClause)
        clauseList.add(callDefinitionClause)

        return callDefinitionClause
    }

    fun clauseList(): List<org.elixir_lang.structure_view.element.call.definition.Clause> {
        return clauseList
    }

    /**
     * Returns the clauses of the macro
     *
     * @return the list of [Clause] elements.
     */
    override fun getChildren(): Array<TreeElement> = childList.toTypedArray()

    /**
     * Returns the data object (usually a PSI element) corresponding to the
     * structure view element.
     *
     * @return the data object instance.
     */
    override fun getValue(): Any? = headList.firstOrNull() ?: clauseList.firstOrNull()

    /**
     * A macro groups together one or more [Clause] elements, so it can navigate if it has clauses.
     *
     * @return `true` if [.clauseList] size is greater than 0; otherwise, `false`.
     */
    override fun canNavigate(): Boolean = clauseList.size > 0

    /**
     * A macro groups together one or more [Clause] elements, so it can navigate if it has clauses.
     *
     * @return `true` if [.clauseList] size is greater than 0; otherwise, `false`.
     */
    override fun canNavigateToSource(): Boolean = clauseList.size > 0

    override fun getName(): String = semantic.nameArityInterval.toString()

    /**
     * Returns the presentation of the tree element.
     *
     * @return the element presentation.
     */
    override fun getPresentation(): NameArityInterval =
        (modular.presentation as? Parent)?.locatedPresentableText.let { location ->
            NameArityInterval(
                location,
                false,
                time,
                visibility(),
                isOverridable,
                override,
                semantic.nameArityInterval!!
            )
        }

    /**
     * Unlike a clause, a head is just the name and arguments, without the outer macro calls.  Heads occur in
     * `Kernel.defdelegate/2`.
     *
     * @param call
     */
    fun head(head: org.elixir_lang.semantic.call.definition.delegation.Head) {
        assert(semantic.nameArityInterval!!.contains(head.nameArityInterval!!))

        val callDefinitionHead = Head(this, head)
        childList.add(callDefinitionHead)
        headList.add(callDefinitionHead)
    }

    /**
     * The clause that matches the `arguments`.
     *
     * @param arguments the arguments the clause's arguments must match
     * @return `null` if no clauses match or if more than one clause match
     */
    fun matchingClause(arguments: Array<PsiElement>): org.elixir_lang.structure_view.element.call.definition.Clause? =
        matchingClauseList(arguments)?.singleOrNull()

    fun name(): String = name

    /**
     * Navigates to first clause in [.clauseList].
     *
     * @param requestFocus `true` if focus requesting is necessary
     */
    override fun navigate(requestFocus: Boolean) {
        if (canNavigate()) {
            clauseList.first().navigate(requestFocus)
        }
    }

    override fun visibility(): Visibility = semantic.visibility

    /**
     * All clauses that match the `arguments`.
     *
     * @param arguments the arguments the clauses' arguments must match
     * @return `null` if no clauses match; multiple clauses if the types of arguments cannot be inferred and
     * simpler, relaxed matching has to be used.
     */
    private fun matchingClauseList(arguments: Array<PsiElement>): List<org.elixir_lang.structure_view.element.call.definition.Clause> =
        clauseList.filter { it.isMatch(arguments) }

    override val time: Time
        get() = semantic.time
}
