package org.elixir_lang.navigation

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.stubs.StubIndexKey
import com.intellij.util.ArrayUtil
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.semantic.Semantic
import org.elixir_lang.semantic.call.definition.Clause
import org.elixir_lang.semantic.call.definition.clause.Time
import org.elixir_lang.semantic.call.definition.clause.Visibility
import org.elixir_lang.semantic.semantic
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.call.Definition
import org.elixir_lang.structure_view.element.call.definition.delegation.Head
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol
import org.elixir_lang.structure_view.element.type.definition.source.Callback
import java.util.*

/**
 * @see [org.intellij.erlang.go.ErlangSymbolContributor](https://github.com/ignatov/intellij-erlang/blob/2f59e59a31ecbb2fbdf9b7a3547fb4f206b0807e/src/org/intellij/erlang/go/ErlangSymbolContributor.java)
 */
open class ChooseByNameContributor(private val stubIndexKey: StubIndexKey<String, NamedElement>) :
    ChooseByNameContributor {
    private fun globalSearchScope(project: Project, includeNonProjectItems: Boolean): GlobalSearchScope =
        if (includeNonProjectItems) {
            GlobalSearchScope.allScope(project)
        } else {
            GlobalSearchScope.projectScope(project)
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
     * library classes) should be included in the returned array.
     * @return the array of navigation items.
     */
    override fun getItemsByName(
        name: String,
        pattern: String,
        project: Project,
        includeNonProjectItems: Boolean
    ): Array<NavigationItem> {
        val scope = globalSearchScope(project, includeNonProjectItems)

        val result = StubIndex.getElements(stubIndexKey, name, project, scope, NamedElement::class.java)
        val items = SourcePreferredItems()
        val enclosingModularByMaybeEnclosed = EnclosingModularByMaybeEnclosed()
        val callDefinitionByTuple = HashMap<Definition.Tuple, Definition>()

        for (element in result) {
            val semantic = element.semantic

            if (semantic != null) {
                getItemsByNameFromCall(
                    name,
                    items,
                    enclosingModularByMaybeEnclosed,
                    callDefinitionByTuple,
                    semantic
                )
            }
        }

        return items.toTypedArray()
    }

    private fun getItemsByNameFromCall(
        name: String,
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        callDefinitionByTuple: MutableMap<Definition.Tuple, Definition>,
        semantic: Semantic
    ) {
        when (semantic) {
            is Clause ->
                getItemsFromCallDefinitionClause(
                    items,
                    enclosingModularByMaybeEnclosed,
                    callDefinitionByTuple,
                    semantic
                )
            is org.elixir_lang.semantic.type.definition.source.Specification ->
                getItemsFromCallDefinitionSpecification(items, enclosingModularByMaybeEnclosed, semantic)
            is org.elixir_lang.semantic.type.definition.source.Callback ->
                getItemsFromCallback(items, enclosingModularByMaybeEnclosed, semantic)
            is org.elixir_lang.semantic.Implementation ->
                getItemsFromImplementation(name, items, enclosingModularByMaybeEnclosed, semantic)
            is org.elixir_lang.semantic.Module ->
                getItemsFromModule(items, enclosingModularByMaybeEnclosed, semantic)
            is org.elixir_lang.semantic.Protocol ->
                getItemsFromProtocol(items, enclosingModularByMaybeEnclosed, semantic)
            is org.elixir_lang.semantic.call.definition.delegation.Head ->
                getItemsFromDelegationHead(items, enclosingModularByMaybeEnclosed, callDefinitionByTuple, semantic)
        }
    }

    private fun getItemsFromCallback(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        callback: org.elixir_lang.semantic.type.definition.source.Callback
    ) {
        enclosingModularByMaybeEnclosed.putNew(callback)
            ?.let { Callback(it, callback) }
            ?.run {
                items.add(this)
            }
            ?: error("Cannot find enclosing Modular for Callback", callback.psiElement)
    }

    private fun getItemsFromCallDefinitionClause(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        callDefinitionByTuple: MutableMap<Definition.Tuple, Definition>,
        clause: Clause
    ) {
        val definition = clause.definition

        definition.nameArityInterval?.let { (name, arityInterval) ->
            val time = definition.time
            val modular = enclosingModularByMaybeEnclosed.putNew(clause)

            if (modular == null) {
                val psiElement = clause.psiElement
                // don't throw an error if really EEX, but has wrong extension
                if (!psiElement.text.contains("<%=")) {
                    error("Cannot find enclosing Modular", psiElement)
                }
            } else {
                for (arity in arityInterval.closed()) {
                    val tuple = Definition.Tuple(modular, time, name, arity)
                    callDefinitionByTuple
                        .computeIfAbsent(tuple) { (modular, _, _, _) ->
                            Definition(modular, definition)
                        }
                        .clause(clause)
                        .run {
                            items.add(this)
                        }
                }
            }
        }
    }

    private fun getItemsFromDelegationHead(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        callDefinitionByTuple: MutableMap<Definition.Tuple, Definition>,
        head: org.elixir_lang.semantic.call.definition.delegation.Head
    ) {
        val delegation = head.delegation
        val modular = enclosingModularByMaybeEnclosed.putNew(delegation)

        if (modular != null) {
            val nameArityInterval = head.nameArityInterval

            if (nameArityInterval != null) {
                val name = nameArityInterval.name
                val arityInterval = nameArityInterval.arityInterval

                val tuple = Definition.Tuple(
                    modular,
                    // Delegation can't delegate macros
                    Time.RUN,
                    name,
                    arityInterval.minimum
                )
                var callDefinition: Definition? = callDefinitionByTuple[tuple]

                if (callDefinition == null) {
                    callDefinition = Definition(tuple.modular, delegation)
                    items.add(callDefinition)
                    callDefinitionByTuple[tuple] = callDefinition
                }

                // Delegation is always public as import should be used for private
                val visibility = Visibility.PUBLIC

                val callDefinitionHead = Head(callDefinition, head)
                items.add(callDefinitionHead)
            } else {
                error("Delegation head does not have name or arity interval", head.psiElement)
            }
        } else {
            error("Cannot find enclosing Modular for Delegation", delegation.psiElement)
        }
    }

    private fun getItemsFromCallDefinitionSpecification(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        specification: org.elixir_lang.semantic.type.definition.source.Specification
    ) {
        enclosingModularByMaybeEnclosed.putNew(specification)?.let { modular ->
            org.elixir_lang.structure_view.element.type.definition.source.Specification(
                modular,
                specification,
                false,
                Time.RUN
            ).run {
                items.add(this)
            }
        } ?: error("Cannot find enclosing Modular for CallDefinitionSpecification", specification.psiElement)
    }

    private fun getItemsFromImplementation(
        name: String,
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        implementation: org.elixir_lang.semantic.Implementation
    ) {
        val modular = enclosingModularByMaybeEnclosed.putNew(implementation)

        val implementation = Implementation(modular, implementation)
        items.add(implementation)
    }

    private fun getItemsFromModule(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        module: org.elixir_lang.semantic.Module
    ) {
        enclosingModularByMaybeEnclosed.putNew(module).let { Module(it, module) }.run {
            items.add(this)
        }
    }

    private fun getItemsFromProtocol(
        items: SourcePreferredItems,
        enclosingModularByMaybeEnclosed: EnclosingModularByMaybeEnclosed,
        protocol: org.elixir_lang.semantic.Protocol
    ) {
        enclosingModularByMaybeEnclosed.putNew(protocol).let { Protocol(it, protocol) }.run {
            items.add(this)
        }
    }

    /**
     * Returns the list of names for the specified project to which it is possible to navigate
     * by name.
     *
     * @param project                the project in which the navigation is performed.
     * @param includeNonProjectItems if true, the names of non-project items (for example,
     * library classes) should be included in the returned array.
     * @return the array of names.
     */
    override fun getNames(project: Project, includeNonProjectItems: Boolean): Array<String> {
        return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(stubIndexKey, project))
    }

    private fun error(userMessage: String, element: PsiElement) =
        Logger.error(this.javaClass, userMessage, element)
}

private fun Definition.also(block: (Definition) -> Unit): Definition {
    block(this)
    return this
}
