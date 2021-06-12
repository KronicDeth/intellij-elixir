package org.elixir_lang.reference.module

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNamedElement
import org.elixir_lang.errorreport.Logger
import org.elixir_lang.psi.*
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.call.name.Function
import org.elixir_lang.psi.call.name.Function.ALIAS
import org.elixir_lang.psi.call.name.Module.KERNEL
import org.elixir_lang.psi.impl.call.finalArguments
import org.elixir_lang.psi.impl.call.maybeModularNameToModulars
import org.elixir_lang.psi.impl.hasKeywordKey

object UnaliasedName {
    fun unaliasedName(namedElement: PsiNamedElement): String? =
            if (namedElement is QualifiableAlias) {
                unaliasedName(namedElement)
            } else if (namedElement is Call && namedElement.isCalling(KERNEL, Function.__MODULE__, 0)) {
                __MODULE__
                        .reference(namedElement, useCall = null)
                        .resolve()
                        ?.let { it as? PsiNamedElement }
                        ?.name
            } else {
                namedElement.name
            }

    private tailrec fun down(element: PsiElement): String? =
            when (element) {
                is Call ->
                    element
                            .maybeModularNameToModulars(null)
                            .mapNotNull { it.name }
                            .toSet()
                            .singleOrNull()
                is ElixirAccessExpression -> {
                    val children = element.children

                    assert(children.size >= 1)

                    down(children[0])
                }
                is QualifiableAlias -> element.name
                else -> {
                    Logger.error(
                            javaClass,
                            "Don't know how to search down below ${element.javaClass} for unaliased name",
                            element
                    )

                    "?"
                }
            }

    private fun unaliasedName(qualifiableAlias: QualifiableAlias): String? =
            up(qualifiableAlias.parent, qualifiableAlias)

    private fun up(call: Call, entrance: QualifiableAlias): String? =
            if (call.isCalling(KERNEL, ALIAS)) {
                val finalArguments = call.finalArguments()

                if (finalArguments != null && finalArguments.isNotEmpty()) {
                    val firstArgument = finalArguments[0]

                    down(firstArgument)
                } else {
                    null
                }
            } else {
                entrance.name
            }

    private tailrec fun up(element: PsiElement?, entrance: QualifiableAlias): String? =
            when (element) {
                is Call ->
                    up(element, entrance)
                is QualifiedMultipleAliases ->
                    up(element, entrance)
                is ElixirAccessExpression,
                is ElixirNoParenthesesOneArgument,
                is QuotableArguments,
                is QuotableKeywordList ->
                    up(element.parent, entrance)
                is ElixirMultipleAliases ->
                    entrance.fullyQualifiedName()
                is QuotableKeywordPair ->
                    up(element, entrance)
                else ->
                    null
            }

    private fun up(element: QuotableKeywordPair, entrance: QualifiableAlias): String? =
            if (element.hasKeywordKey("as")) {
                up(element.parent, entrance)
            } else {
                null
            }
}
