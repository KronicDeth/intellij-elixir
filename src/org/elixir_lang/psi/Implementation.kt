package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.usageView.UsageViewTypeLocation
import com.intellij.util.Processor
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.keywordValue
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.stub.index.ModularName
import org.elixir_lang.structure_view.element.CallDefinitionClause
import org.elixir_lang.structure_view.element.modular.Modular

object Implementation {
    @JvmStatic
    fun `is`(call: Call): Boolean {
        return call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DEFIMPL, 2) ||
                call.isCallingMacro(org.elixir_lang.psi.call.name.Module.KERNEL, Function.DEFIMPL, 3)
    }
    /**
     * @return `null` if protocol or module for the implementation cannot be derived or if the `for` argument is a
     * list.
     */
    fun name(call: Call): String? = nameCollection(CallDefinitionClause.enclosingModular(call), call)?.singleOrNull()

    private fun nameCollection(enclosingModular: Modular?, call: Call): Collection<String>? {
        val protocolName = protocolName(call)
        val forNameCollection = forNameCollection(enclosingModular, call)

        return if (protocolName != null && forNameCollection != null) {
            forNameCollection.map { forName -> "$protocolName.$forName" }
        } else {
            null
        }
    }

    fun elementDescription(location: ElementDescriptionLocation): String? =
            if (location === UsageViewTypeLocation.INSTANCE) {
                "implementation"
            } else {
                null
            }

    private fun forNameCollection(forNameElement: ElixirAccessExpression): Collection<String> =
            forNameCollection(forNameElement.children)

    private fun forNameCollection(forNameElement: ElixirList): Collection<String> =
            forNameCollection(forNameElement.children)

    fun forNameCollection(forNameElement: PsiElement): Collection<String>? = when (forNameElement) {
        is ElixirAccessExpression -> forNameCollection(forNameElement)
        is ElixirList -> forNameCollection(forNameElement)
        is QualifiableAlias -> forNameCollection(forNameElement)
        is PsiNamedElement -> forNameCollection(forNameElement)
        else -> listOf(forNameElement.text)
    }

    private fun forNameCollection(children: Array<PsiElement>): Collection<String> =
            children.flatMap { child -> forNameCollection(child) ?: emptyList() }

    private fun forNameCollection(forNameElement: PsiNamedElement): Collection<String>? =
            forNameElement.name?.let { listOf(it) }

    private fun forNameCollection(forNameElement: QualifiableAlias): Collection<String>? =
            forNameElement.name?.let { listOf(it) }

    fun forNameCollection(enclosingModular: Modular?, call: Call): Collection<String>? {
        val forNameElement = forNameElement(call)
        return when {
            forNameElement != null -> forNameCollection(forNameElement)
            enclosingModular != null -> {
                enclosingModular
                        .presentation
                        .let { it as org.elixir_lang.navigation.item_presentation.Parent }
                        .locatedPresentableText
                        .let { listOf(it) }
            }
            else -> null
        }
    }

    fun forNameElement(call: Call): PsiElement? =
            call.finalArguments()?.lastOrNull()?.let { it as? QuotableKeywordList }?.keywordValue(Function.FOR)

    fun processProtocols(defimpl: Call, consumer: Processor<in PsiElement>) {
        protocolName(defimpl)?.let { protocolName ->
            val project = defimpl.project

            StubIndex
                    .getInstance()
                    .processElements(
                            ModularName.KEY,
                            protocolName,
                            project,
                            GlobalSearchScope.everythingScope(project),
                            NamedElement::class.java,
                            consumer
                    )
        }
    }

    @JvmStatic
    fun protocolName(call: Call): String? = protocolNameElement(call)?.let { protocolName(it)  }

    fun protocolNameElement(call: Call): QualifiableAlias? {
        val finalArguments = call.finalArguments()

        return if (finalArguments != null && finalArguments.isNotEmpty()) {
            when (val firstFinalArgument = finalArguments[0]) {
                is ElixirAccessExpression -> firstFinalArgument.stripAccessExpression() as? QualifiableAlias
                is QualifiableAlias -> firstFinalArgument
                else -> null
            }
        } else {
            null
        }
    }

    private fun protocolName(qualifiableAlias: QualifiableAlias): String? =
            qualifiableAlias.fullyQualifiedName()?.replace("Elixir.", "")
}
