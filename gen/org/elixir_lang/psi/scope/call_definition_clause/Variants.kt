package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.EEx
import org.elixir_lang.EEx.FUNCTION_FROM_FILE_ARITY_RANGE
import org.elixir_lang.EEx.FUNCTION_FROM_STRING_ARITY_RANGE
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.Named
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.scope.CallDefinitionClause
import org.elixir_lang.structure_view.element.CallDefinitionHead
import org.elixir_lang.structure_view.element.Callback
import java.util.*

class Variants : CallDefinitionClause() {
    private var lookupElementByPsiElement: MutableMap<PsiElement, LookupElement> = mutableMapOf()

    private val lookupElementCollection: Collection<LookupElement>
        get() = lookupElementByPsiElement.values

    /**
     * Called on every [Call] where [org.elixir_lang.structure_view.element.CallDefinitionClause. is] is
     * `true` when checking tree with [.execute]
     *
     * @return `true` to keep searching up tree; `false` to stop searching.
     */
    override fun executeOnCallDefinitionClause(element: Call, state: ResolveState): Boolean {
        val entranceCallDefinitionClause = state.get(ENTRANCE_CALL_DEFINITION_CLAUSE)

        if ((entranceCallDefinitionClause == null || !element.isEquivalentTo(entranceCallDefinitionClause)) && element is Named) {
            addCallDefinitionClauseToLookupElementByPsiElement(element)
        }

        return true
    }

    private fun addCallDefinitionClauseToLookupElementByPsiElement(named: Named) {
        named.name?.let { name ->
            lookupElementByPsiElement.computeIfAbsent(named) { element ->
                LookupElementBuilder.createWithSmartPointer(
                        name,
                        element
                ).withRenderer(
                        org.elixir_lang.code_insight.lookup.element_renderer.CallDefinitionClause(name)
                )
            }
        }
    }

    override fun executeOnCallback(element: AtUnqualifiedNoParenthesesCall<*>, state: ResolveState): Boolean {
        Callback.headCall(element)
                ?.let { it as? Named }
                ?.let { addCallbackToLookupElementByPsiElement(element, it) }

        return true
    }

    private fun addCallbackToLookupElementByPsiElement(element: AtUnqualifiedNoParenthesesCall<*>, head: Named) =
            head.name?.let { name ->
                lookupElementByPsiElement.computeIfAbsent(head) { head ->
                    LookupElementBuilder.createWithSmartPointer(
                            name,
                            element
                    ).withRenderer(
                            org.elixir_lang.code_insight.lookup.element_renderer.Callback(name)
                    )
                }
            }

    override fun executeOnDelegation(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.takeIf { it.size == 2 }?.let { arguments ->
            val head = arguments[0]

            CallDefinitionHead.nameArityInterval(head, state)?.let { headNameArityInterval ->
                val headName = headNameArityInterval.name

                lookupElementByPsiElement.computeIfAbsent(head) { head ->
                    LookupElementBuilder.createWithSmartPointer(
                            headName,
                            element
                    ).withRenderer(
                            org.elixir_lang.code_insight.lookup.element_renderer.Delegation(headName)
                    )
                }
            }
        }

        return true
    }

    override fun executeOnEExFunctionFrom(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.let { arguments ->
            arguments[1].stripAccessExpression().let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { name ->
                lookupElementByPsiElement.computeIfAbsent(element) { element ->
                    LookupElementBuilder.createWithSmartPointer(
                            name,
                            element
                    ).withRenderer(
                            org.elixir_lang.code_insight.lookup.element_renderer.EExFunctionFrom(name)
                    )
                }
            }
       }

        return true
    }

    override fun executeOnException(element: Call, state: ResolveState): Boolean {
        TODO()
    }

    override fun executeOnMixGeneratorEmbed(element: Call, state: ResolveState): Boolean {
        TODO()
    }

    /**
     * Whether to continue searching after each Module's children have been searched.
     *
     * @return `true` to keep searching up the PSI tree; `false` to stop searching.
     */
    override fun keepProcessing(): Boolean = false


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
