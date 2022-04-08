package org.elixir_lang.psi.scope.call_definition_clause

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.psi.ElixirAtom
import org.elixir_lang.psi.ElixirIdentifier
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.ENTRANCE
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.putInitialVisitedElement
import org.elixir_lang.psi.putVisitedElement
import org.elixir_lang.psi.scope.CallDefinitionClause
import org.elixir_lang.semantic.call.definition.Delegation

class Variants : CallDefinitionClause() {
    private var lookupElementByPsiElementName: MutableMap<Pair<PsiElement, String>, LookupElement> = mutableMapOf()

    private val lookupElementCollection: Collection<LookupElement>
        get() = lookupElementByPsiElementName.values

    override fun execute(definition: org.elixir_lang.semantic.call.Definition, state: ResolveState): Boolean {
        definition.nameArityInterval?.let { nameArityInterval ->
            val name = nameArityInterval.name

            definition.clauses.forEach { clause ->
                val psiElement = clause.psiElement

                lookupElementByPsiElementName.computeIfAbsent(psiElement to name) { (element, name) ->
                    LookupElementBuilder.createWithSmartPointer(
                        name,
                        element
                    ).withRenderer(
                        org.elixir_lang.code_insight.lookup.element_renderer.CallDefinitionClause(name)
                    )
                }
            }
        }

        return true
    }

    override fun execute(delegation: Delegation, state: ResolveState): Boolean {
        delegation.name?.let { name ->
            val psiElement = delegation.psiElement

            lookupElementByPsiElementName.computeIfAbsent(psiElement to name) { (_, name) ->
                LookupElementBuilder.createWithSmartPointer(
                    name,
                    psiElement
                ).withRenderer(
                    org.elixir_lang.code_insight.lookup.element_renderer.Delegation(name)
                )
            }
        }

        return true
    }

    override fun executeOnEExFunctionFrom(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.let { arguments ->
            arguments[1].stripAccessExpression().let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { name ->
                lookupElementByPsiElementName.computeIfAbsent(element to name) { (_, name) ->
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

    override fun executeOnMixGeneratorEmbed(element: Call, state: ResolveState): Boolean {
        element.finalArguments()?.first()?.stripAccessExpression()
            ?.let { it as? ElixirAtom }?.node?.lastChildNode?.text?.let { prefix ->
                val suffix = element.functionName()!!.removePrefix("embed_")
                val name = "${prefix}_${suffix}"

                lookupElementByPsiElementName.computeIfAbsent(element to name) { (element, name) ->
                    val renderer = when (suffix) {
                        "template" -> org.elixir_lang.code_insight.lookup.element_renderer.mix.generator.EmbedTemplate(
                            name
                        )
                        "text" -> org.elixir_lang.code_insight.lookup.element_renderer.mix.generator.EmbedText(name)
                        else -> TODO()
                    }

                    LookupElementBuilder
                        .createWithSmartPointer(name, element)
                        .withRenderer(renderer)
                }
            }

        return true
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
