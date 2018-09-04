package org.elixir_lang.navigation

import com.intellij.navigation.ChooseByNameContributor
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.ArrayUtil
import org.elixir_lang.Visibility
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.stub.index.AllName
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol
import java.util.*

/**
 * @see [org.intellij.erlang.go.ErlangSymbolContributor](https://github.com/ignatov/intellij-erlang/blob/2f59e59a31ecbb2fbdf9b7a3547fb4f206b0807e/src/org/intellij/erlang/go/ErlangSymbolContributor.java)
 */
class GotoSymbolContributor : ChooseByNameContributor {
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
    override fun getItemsByName(name: String,
                                pattern: String,
                                project: Project,
                                includeNonProjectItems: Boolean): Array<NavigationItem> {
        val scope = globalSearchScope(project, includeNonProjectItems)

        val result = StubIndex.getElements(AllName.KEY, name, project, scope, NamedElement::class.java)
        val items = SourcePreferredItems()
        val enclosingModularByCall = EnclosingModularByCall()
        val callDefinitionByTuple = HashMap<CallDefinition.Tuple, CallDefinition>()

        for (element in result) {
            // Use navigation element so that source element is used for compiled elements
            val sourceElement = element.navigationElement

            if (sourceElement is Call) {
                getItemsByNameFromCall(
                        name,
                        items,
                        enclosingModularByCall,
                        callDefinitionByTuple,
                        sourceElement
                )
            }
        }

        return items.toTypedArray()
    }

    private fun getItemsByNameFromCall(name: String,
                                       items: SourcePreferredItems,
                                       enclosingModularByCall: EnclosingModularByCall,
                                       callDefinitionByTuple: MutableMap<CallDefinition.Tuple, CallDefinition>,
                                       call: Call) {
        when {
            CallDefinitionClause.`is`(call) -> getItemsFromCallDefinitionClause(items, enclosingModularByCall, callDefinitionByTuple, call)
            CallDefinitionHead.`is`(call) -> getItemsFromCallDefinitionHead(items, enclosingModularByCall, callDefinitionByTuple, call)
            CallDefinitionSpecification.`is`(call) -> getItemsFromCallDefinitionSpecification(items, enclosingModularByCall, call)
            Callback.`is`(call) -> getItemsFromCallback(items, enclosingModularByCall, call)
            Implementation.`is`(call) -> getItemsFromImplementation(name, items, enclosingModularByCall, call)
            Module.`is`(call) -> getItemsFromModule(items, enclosingModularByCall, call)
            Protocol.`is`(call) -> getItemsFromProtocol(items, enclosingModularByCall, call)
        }
    }

    private fun getItemsFromCallback(items: SourcePreferredItems,
                                     enclosingModularByCall: EnclosingModularByCall,
                                     call: Call) {
        enclosingModularByCall.putNew(call)?.let { Callback(it, call) }?.run {
            items.add(this)
        } ?:
        error("Cannot find enclosing Modular for Callback", call)
    }

    private fun getItemsFromCallDefinitionClause(
            items: SourcePreferredItems,
            enclosingModularByCall: EnclosingModularByCall,
            callDefinitionByTuple: MutableMap<CallDefinition.Tuple, CallDefinition>,
            call: Call
    ) {
        CallDefinitionClause.nameArityRange(call)?.let { (name, arityRange) ->
            val time = CallDefinitionClause.time(call)
            val modular = enclosingModularByCall.putNew(call)

            if (modular == null) {
                // don't throw an error if really EEX, but has wrong extension
                if (!call.text.contains("<%=")) {
                    error("Cannot find enclosing Modular", call)
                }
            } else {
                for (arity in arityRange) {
                    val tuple = CallDefinition.Tuple(modular, time, name, arity)
                    callDefinitionByTuple.computeIfAbsent(tuple) { (modular, time, name, arity) ->
                        CallDefinition(modular, time, name, arity)
                    }.clause(call).run {
                        items.add(this)
                    }
                }
            }
        }
    }

    private fun getItemsFromCallDefinitionHead(
            items: SourcePreferredItems,
            enclosingModularByCall: EnclosingModularByCall,
            callDefinitionByTuple: MutableMap<CallDefinition.Tuple, CallDefinition>,
            call: Call
    ) {
        val delegationCall = CallDefinitionHead.enclosingDelegationCall(call)

        if (delegationCall != null) {
            val modular = enclosingModularByCall.putNew(delegationCall)

            if (modular != null) {
                val callDefinitionName = call.functionName()

                if (callDefinitionName != null) {
                    val callDefinitionArity = call.resolvedFinalArity()

                    val tuple = CallDefinition.Tuple(
                            modular,
                            // Delegation can't delegate macros
                            Timed.Time.RUN,
                            callDefinitionName,
                            callDefinitionArity
                    )
                    var callDefinition: CallDefinition? = callDefinitionByTuple[tuple]

                    if (callDefinition == null) {
                        callDefinition = CallDefinition(tuple.modular, tuple.time, tuple.name, tuple.arity)
                        items.add(callDefinition)
                        callDefinitionByTuple[tuple] = callDefinition
                    }

                    // Delegation is always public as import should be used for private
                    val visibility = Visibility.PUBLIC


                    val callDefinitionHead = CallDefinitionHead(callDefinition, visibility, call)
                    items.add(callDefinitionHead)
                } else {
                    error("Call for CallDefinitionHead does not have function name", call)
                }
            } else {
                error("Cannot find enclosing Modular for Delegation call", delegationCall)
            }
        } else {
            error("Cannot find enclosing delegation call for CallDefinitionHead", call)
        }
    }

    private fun getItemsFromCallDefinitionSpecification(items: SourcePreferredItems,
                                                        enclosingModularByCall: EnclosingModularByCall,
                                                        call: Call) {
        enclosingModularByCall.putNew(call)?.let { modular ->
            CallDefinitionSpecification(
                    modular,
                    call as AtUnqualifiedNoParenthesesCall<*>,
                    false,
                    Timed.Time.RUN
            ).run {
                items.add(this)
            }
        } ?:
        error("Cannot find enclosing Modular for CallDefinitionSpecification", call)
    }

    private fun getItemsFromImplementation(name: String,
                                           items: SourcePreferredItems,
                                           enclosingModularByCall: EnclosingModularByCall,
                                           call: Call) {
        val modular = enclosingModularByCall.putNew(call)

        val forNameCollection = Implementation.forNameCollection(modular, call)

        if (forNameCollection != null) {
            for (forName in forNameCollection) {
                val forNameOverriddenImplementation = Implementation(modular, call, forName)
                val implementationName = forNameOverriddenImplementation.name

                if (implementationName != null && implementationName.contains(name)) {
                    items.add(forNameOverriddenImplementation)
                }
            }
        } else {
            val implementation = Implementation(modular, call)
            items.add(implementation)
        }
    }

    private fun getItemsFromModule(items: SourcePreferredItems,
                                   enclosingModularByCall: EnclosingModularByCall,
                                   call: Call) {
        enclosingModularByCall.putNew(call).let { Module(it, call) }.run {
            items.add(this)
        }
    }

    private fun getItemsFromProtocol(items: SourcePreferredItems,
                                     enclosingModularByCall: EnclosingModularByCall,
                                     call: Call) {
        enclosingModularByCall.putNew(call).let { Protocol(it, call) }.run {
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
        return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(AllName.KEY, project))
    }

    private fun error(userMessage: String, element: PsiElement) =
            Logger.error(this.javaClass, userMessage, element)
}

private fun CallDefinition.also(block: (CallDefinition) -> Unit): CallDefinition {
    block(this)
    return this
}
