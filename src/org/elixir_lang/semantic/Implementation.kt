package org.elixir_lang.semantic

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.ElixirList
import org.elixir_lang.psi.NamedElement
import org.elixir_lang.psi.QualifiableAlias
import org.elixir_lang.psi.stub.index.ModularName

interface Implementation : Modular {
    val protocolName: String
    val forNames: kotlin.collections.List<org.elixir_lang.semantic.implementation.ForName>
    val protocols: kotlin.collections.List<Protocol>

    companion object {
        fun <P : PsiElement, TypePsiElement : PsiElement, ClausePsiElement : PsiElement> computeProtocols(
            implementation: Implementation
        ): kotlin.collections.List<Protocol> {
            val protocols = mutableListOf<Protocol>()
            val project = implementation.psiElement.project

            StubIndex
                .getInstance()
                .processElements(
                    ModularName.KEY,
                    implementation.protocolName,
                    project,
                    GlobalSearchScope.everythingScope(project),
                    NamedElement::class.java
                ) { nameElement ->
                    nameElement.semantic?.let { it as? Protocol }?.let { protocol ->
                        protocols.add(protocol)
                    }

                    true
                }

            return protocols
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

        private fun forNameCollection(forNameElement: PsiElement): Collection<String>? = when (forNameElement) {
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
    }
}
