package org.elixir_lang.model.psi.type

import com.intellij.model.Symbol
import com.intellij.model.psi.PsiExternalReferenceHost
import com.intellij.model.psi.PsiSymbolReference
import com.intellij.model.psi.PsiSymbolReferenceHints
import com.intellij.model.psi.PsiSymbolReferenceProvider
import com.intellij.model.search.SearchRequest
import com.intellij.openapi.project.Project
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.psi.AtUnqualifiedNoParenthesesCall
import org.elixir_lang.psi.ElixirAccessExpression
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.qualification.Qualified
import org.elixir_lang.psi.impl.ElixirPsiImplUtil.moduleAttributeName
import org.elixir_lang.psi.impl.stripAccessExpression
import org.elixir_lang.psi.operation.Operation
import org.elixir_lang.psi.operation.Type as TypeOperation
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
        if (!isTypeNameUsage(element)) return emptyList()

        val nameElement = element.functionNameElement() ?: return emptyList()
        val rangeInElement = nameElement.textRange.shiftLeft(element.textRange.startOffset)
        return listOf(TypeReference(element, rangeInElement))
    }

    override fun getSearchRequests(project: Project, target: Symbol): Collection<SearchRequest> = emptyList()
}

@RequiresReadLock
internal fun isTypeNameUsage(call: Call): Boolean {
    if (call is AtUnqualifiedNoParenthesesCall<*>) return false
    if (call is Operation) return false
    if (call.ancestorTypeSpec() == null) return false
    if (isTypespecHeadFunction(call) || isTypeDeclarationHead(call) || isSpecHeadFunction(call)) return false
    if (isNamedTypeLabel(call)) return false
    if (isQualifierOfQualifiedType(call)) return false
    if (TypeVariableSymbol.isDeclaration(call)) return false
    return true
}

/**
 * A named annotation `name :: type` (e.g. `reason` in `{:error, reason :: term()}`) labels the type on the right;
 * the label on the left is not itself a type usage. Only the direct left operand is a label - a name nested inside a
 * function call such as `a` in a `convert(a)` spec head is a genuine parameter-type usage.
 */
@RequiresReadLock
private fun isNamedTypeLabel(call: Call): Boolean {
    val typeOperation = PsiTreeUtil.getParentOfType(call, TypeOperation::class.java) ?: return false
    return typeOperation.leftOperand()?.stripAccessExpression() === call
}

/**
 * The qualifier of a qualified type (e.g. `__MODULE__` in `__MODULE__.t()`) names the module, not a type; the type
 * usage is the function name on the right of the dot. Aliases like `Changeset` are not [Call]s, but macro qualifiers
 * such as `__MODULE__` are, so they must be excluded explicitly.
 */
@RequiresReadLock
private fun isQualifierOfQualifiedType(call: Call): Boolean {
    val qualified = generateSequence(call.parent) { it.parent }
        .takeWhile { it is ElixirAccessExpression || it is Qualified }
        .filterIsInstance<Qualified>()
        .firstOrNull()
        ?: return false
    return qualified.qualifier() === call
}

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
