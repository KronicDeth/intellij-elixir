package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.scope.CallDefinitionClause
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.putVisitedElement
import java.util.*

class Variants : CallDefinitionClause() {

    /*
     * Fields
     */

    private var lookupElementByPsiElement: MutableMap<PsiElement, LookupElement>? = null

    private val lookupElementCollection: Collection<LookupElement>
        get() = lookupElementByPsiElement?.values ?: emptySet()

    /*
     * Protected Instance Methods
     */

    /**
     * Called on every [Call] where [org.elixir_lang.structure_view.element.CallDefinitionClause. is] is
     * `true` when checking tree with [.execute]
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean {
        val entranceCallDefinitionClause = state.get(ENTRANCE_CALL_DEFINITION_CLAUSE)

        if (entranceCallDefinitionClause == null || !element.isEquivalentTo(entranceCallDefinitionClause)) {
            addToLookupElementByPsiElement(element)
        }

        return true
    }

    override fun executeOnDelegation(element: Call, state: ResolveState): Boolean {
        TODO()
    }

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return `true` to keep searching up the PSI tree; `false` to stop searching.
     */
    override fun keepProcessing(): Boolean = false

    /*
     * Private Instance Methods
     */

    private fun addToLookupElementByPsiElement(call: Call) {
        when (call) {
            is Named -> addToLookupElementByPsiElement(call)
        }
    }

    private fun addToLookupElementByPsiElement(named: Named) {
        named.name?.let { name ->
            val lookupElementByPsiElement = lookupElementByPsiElement  ?: mutableMapOf()

            lookupElementByPsiElement.computeIfAbsent(named) { element ->
                LookupElementBuilder.createWithSmartPointer(
                        name,
                        element
                ).withRenderer(
                        org.elixir_lang.code_insight.lookup.element_renderer.CallDefinitionClause(name)
                )
            }

            this.lookupElementByPsiElement = lookupElementByPsiElement
        }
    }

    companion object {
        private val ENTRANCE_CALL_DEFINITION_CLAUSE = Key<Call>("ENTRANCE_CALL_DEFINITION_CLAUSE")

        @JvmStatic
        fun lookupElementList(entrance: Call): List<LookupElement> {
            val parameter = Parameter.putParameterized(Parameter(entrance))
            val entranceCallDefinitionClause: Call? = if (parameter.isCallDefinitionClauseName) {
                parameter.parameterized as Call?
            } else {
                null
            }

            return lookupElementList(entrance, entranceCallDefinitionClause)
        }

        @JvmStatic
        fun lookupElementList(entrance: ElixirIdentifier): List<LookupElement> = lookupElementList(entrance, null)

        private fun lookupElementList(entrance: PsiElement, entranceCallDefinitionClause: Call?): List<LookupElement> {
            val variants = Variants()

            val resolveState = ResolveState
                    .initial()
                    .put(ENTRANCE, entrance)
                    .put(ENTRANCE_CALL_DEFINITION_CLAUSE, entranceCallDefinitionClause)
                    .putInitialVisitedElement(entrance)

            if (entranceCallDefinitionClause != null) {
                resolveState.putVisitedElement(entranceCallDefinitionClause)
            }

            PsiTreeUtil.treeWalkUp(
                    variants,
                    entrance,
                    entrance.containingFile,
                    resolveState
            )
            val lookupElementList = ArrayList<LookupElement>()
            lookupElementList.addAll(variants.lookupElementCollection)

            return lookupElementList
        }
    }
}
