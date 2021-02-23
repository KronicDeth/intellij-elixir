package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import com.intellij.usageView.UsageViewLongNameLocation
import com.intellij.usageView.UsageViewNodeTextLocation
import com.intellij.usageView.UsageViewShortNameLocation
import com.intellij.usageView.UsageViewTypeLocation
import org.elixir_lang.annotator.Parameter
import org.elixir_lang.beam.psi.impl.ModuleImpl
import org.elixir_lang.find_usages.Provider
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.hasKeywordKey
import org.elixir_lang.reference.Callable
import org.elixir_lang.structure_view.element.*
import org.elixir_lang.structure_view.element.modular.Implementation
import org.elixir_lang.structure_view.element.modular.Module
import org.elixir_lang.structure_view.element.modular.Protocol
import org.elixir_lang.structure_view.element.structure.Structure

/**
 * Dual to [Provider], where instead of each location being a separate method, they
 * are all one method, which means the same code can be used to detect the type of an element and then group all the
 * text ([Provider.getDescriptiveName],
 * [Provider.getHelpId],
 * [Provider.getNodeText]
 * [Provider.getType]) together together.
 */
class ElementDescriptionProvider : com.intellij.psi.ElementDescriptionProvider {
    override tailrec fun getElementDescription(element: PsiElement, location: ElementDescriptionLocation): String? =
            when (element) {
                is AtNonNumericOperation -> getElementDescription(element, location)
                is Call -> getElementDescription(element, location)
                is ElixirIdentifier -> getElementDescription(element, location)
                is ElixirKeywordKey -> getElementDescription(element, location)
                is ElixirVariable -> getElementDescription(element, location)
                is MaybeModuleName -> getElementDescription(element, location)
                is ModuleImpl<*> -> getElementDescription(element.navigationElement, location)
                else -> null
            }

    /*
     * Private Instance Methods
     */

    private fun getElementDescription(atNonNumericOperation: AtNonNumericOperation, location: ElementDescriptionLocation): String? =
            when (location) {
                UsageViewNodeTextLocation.INSTANCE -> atNonNumericOperation.text
                UsageViewTypeLocation.INSTANCE -> "module attribute"
                else -> null
            }

    private fun getElementDescription(identifier: ElixirIdentifier, location: ElementDescriptionLocation): String? {
        val parameter = Parameter(identifier)
        val type = Parameter.putParameterized(parameter).type

        return when (type) {
            Parameter.Type.FUNCTION_NAME -> when (location) {
                UsageViewShortNameLocation.INSTANCE -> identifier.text
                UsageViewTypeLocation.INSTANCE -> "function"
                else -> null
            }
            Parameter.Type.MACRO_NAME -> when (location) {
                UsageViewShortNameLocation.INSTANCE -> identifier.text
                UsageViewTypeLocation.INSTANCE -> "macro"
                else -> null
            }
            else -> null
        }
    }

    private fun getElementDescription(keywordKey: ElixirKeywordKey,
                                      location: ElementDescriptionLocation): String? {
        var elementDescription: String? = keywordKey
                .parent.let { it as? ElixirKeywordPair }
                ?.parent?.let { it as? ElixirKeywords }
                ?.parent?.let { it as? ElixirList }
                ?.parent?.let { it as ElixirAccessExpression }
                ?.parent?.let { it as? QuotableKeywordPair }
                ?.keywordKey?.let { outerKeywordKey ->
            if (outerKeywordKey.text == "bind_quoted" && location === UsageViewTypeLocation.INSTANCE) {
                "quote bound variable"
            } else {
                null
            }
        }

        if (elementDescription == null && location === UsageViewTypeLocation.INSTANCE) {
            elementDescription = "keyword key"
        }

        return elementDescription
    }

    private fun getElementDescription(variable: ElixirVariable, location: ElementDescriptionLocation): String? =
            if (location === UsageViewLongNameLocation.INSTANCE || location === UsageViewShortNameLocation.INSTANCE) {
                variable.name
            } else if (location === UsageViewTypeLocation.INSTANCE) {
                VARIABLE_USAGE_VIEW_TYPE_LOCATION_ELEMENT_DESCRIPTION
            } else {
                null
            }

    private fun getElementDescription(maybeModuleName: MaybeModuleName,
                                      location: ElementDescriptionLocation): String? {
        var elementDescription: String? = if (maybeModuleName is QualifiableAlias) {
            if (location === UsageViewShortNameLocation.INSTANCE) {
                val fullyQualifiedName = maybeModuleName.fullyQualifiedName()

                if (isAliasCallAs(maybeModuleName)) {
                    "as: $fullyQualifiedName"
                } else {
                    fullyQualifiedName
                }
            } else {
                null
            }
        } else {
            null
        }

        if (location === UsageViewTypeLocation.INSTANCE) {
            elementDescription = if (isAliasCallArgument(maybeModuleName)) {
                "alias"
            } else {
                "module"
            }
        }

        return elementDescription
    }

    private fun getElementDescription(call: Call, location: ElementDescriptionLocation): String? =
            when {
                CallDefinitionClause.`is`(call) -> CallDefinitionClause.elementDescription(call, location)
                CallDefinitionSpecification.`is`(call) -> CallDefinitionSpecification.elementDescription(call, location)
                Callback.`is`(call) -> Callback.elementDescription(call, location)
                Delegation.`is`(call) -> Delegation.elementDescription(call, location)
                org.elixir_lang.structure_view.element.Exception.`is`(call) -> org.elixir_lang.structure_view.element.Exception.elementDescription(call, location)
                Implementation.`is`(call) -> Implementation.elementDescription(location)
                Import.`is`(call) -> Import.elementDescription(call, location)
                Module.`is`(call) -> Module.elementDescription(call, location)
                Overridable.`is`(call) -> Overridable.elementDescription(call, location)
                Protocol.`is`(call) -> Module.elementDescription(call, location)
                QuoteMacro.`is`(call) -> org.elixir_lang.structure_view.element.Quote.elementDescription(call, location)
                Structure.`is`(call) -> Structure.elementDescription(call, location)
                Type.`is`(call) -> Type.elementDescription(call, location)
                Use.`is`(call) -> Use.elementDescription(call, location)
                call is AtUnqualifiedNoParenthesesCall<*> -> getElementDescription(call, location)
                Callable.isBitStreamSegmentOption(call) -> Callable.bitStringSegmentOptionElementDescription(call, location)
                Callable.isIgnored(call) -> Callable.ignoredElementDescription(location)
                Callable.isParameter(call) -> Callable.parameterElementDescription(call, location)
                Callable.isParameterWithDefault(call) -> Callable.parameterWithDefaultElementDescription(location)
                Callable.isVariable(call) -> Callable.variableElementDescription(call, location)
                else ->
                    if (location === UsageViewTypeLocation.INSTANCE) {
                        "call"
                    } else {
                        null
                    }
            }

    private fun getElementDescription(atUnqualifiedNoParenthesesCall: AtUnqualifiedNoParenthesesCall<*>,
                                      location: ElementDescriptionLocation): String? =
            if (location === UsageViewLongNameLocation.INSTANCE || location === UsageViewShortNameLocation.INSTANCE) {
                atUnqualifiedNoParenthesesCall.name
            } else if (location === UsageViewTypeLocation.INSTANCE) {
                "module attribute"
            } else {
                null
            }

    private fun isAliasCallArgument(element: Call): Boolean = element.isCalling(KERNEL, ALIAS)

    private tailrec fun isAliasCallArgument(element: PsiElement): Boolean =
            when (element) {
                is Call ->
                    isAliasCallArgument(element)
                is Arguments,
                is ElixirAccessExpression,
                is ElixirMultipleAliases,
                is QualifiableAlias,
                is QualifiedMultipleAliases ->
                    isAliasCallArgument(element.parent)
                is QuotableKeywordPair ->
                    isAliasCallAs(element)
                else ->
                    false
            }

    private fun isAliasCallAs(element: QuotableKeywordPair): Boolean =
            if (element.hasKeywordKey("as")) {
                val parent = element.parent

                if (parent is QuotableKeywordList) {
                    val grandParent = parent.parent

                    isAliasCallArgument(grandParent)
                } else {
                    false
                }
            } else {
                false
            }

    private tailrec fun isAliasCallAs(element: PsiElement): Boolean =
            if (element is ElixirAccessExpression || element is QualifiableAlias) {
                isAliasCallAs(element.parent)
            } else if (element is QuotableKeywordPair) {
                isAliasCallAs(element)
            } else {
                false
            }

    companion object {
        const val VARIABLE_USAGE_VIEW_TYPE_LOCATION_ELEMENT_DESCRIPTION = "variable"
    }
}
