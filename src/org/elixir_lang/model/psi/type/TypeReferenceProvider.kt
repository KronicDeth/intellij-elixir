package org.elixir_lang.model.psi.type

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.moduleAttributeName
import org.elixir_lang.psi.scope.ancestorTypeSpec
import org.elixir_lang.structure_view.element.CallDefinitionSpecification
import org.elixir_lang.structure_view.element.Type as TypeElement

@Suppress("UnstableApiUsage")
internal class TypeReferenceProvider : PsiSymbolReferenceProvider {
    @RequiresReadLock
    override fun getReferences(
        element: PsiExternalReferenceHost,
        hints: PsiSymbolReferenceHints
    ): Collection<PsiSymbolReference> {
        if (element !is Call) return emptyList()
        if (element is AtUnqualifiedNoParenthesesCall<*>) return emptyList()
        if (element.ancestorTypeSpec() == null) return emptyList()
        if (isTypespecHeadFunction(element) || isTypeDeclarationHead(element) || isSpecHeadFunction(element)) return emptyList()
        if (TypeVariableSymbol.isDeclaration(element)) return emptyList()

        val nameElement = element.functionNameElement() ?: return emptyList()
        val rangeInElement = nameElement.textRange.shiftLeft(element.textRange.startOffset)
        return listOf(TypeReference(element, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()

    @RequiresReadLock
    private fun isTypeDeclarationHead(call: Call): Boolean {
        val typeAttribute = generateSequence(call.parent) { it.parent }
            .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
            .firstOrNull { TypeElement.`is`(it) }
            ?: return false
        val specification = CallDefinitionSpecification.specification(typeAttribute) ?: return false
        val typeHead = CallDefinitionSpecification.specificationType(specification) ?: return false
        return typeHead.isEquivalentTo(call)
    }

    @RequiresReadLock
    private fun isSpecHeadFunction(call: Call): Boolean {
        val moduleAttribute = generateSequence(call.parent) { it.parent }
            .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
            .firstOrNull()
            ?: return false
        if (moduleAttributeName(moduleAttribute) != "@spec") return false
        val specification = CallDefinitionSpecification.specification(moduleAttribute) ?: return false
        val specHead = CallDefinitionSpecification.specificationType(specification) ?: return false
        return specHead.isEquivalentTo(call)
    }

    @RequiresReadLock
    private fun isTypespecHeadFunction(call: Call): Boolean {
        val moduleAttribute = generateSequence(call.parent) { it.parent }
            .filterIsInstance<AtUnqualifiedNoParenthesesCall<*>>()
            .firstOrNull()
            ?: return false
        val attributeName = moduleAttributeName(moduleAttribute)
        if (attributeName != "@callback" && attributeName != "@macrocallback") return false
        val specification = CallDefinitionSpecification.specification(moduleAttribute) ?: return false
        val head = CallDefinitionSpecification.specificationType(specification) ?: return false
        return head.isEquivalentTo(call)
    }
}
