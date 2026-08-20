package org.elixir_lang.code_insight.line_marker_provider

import com.intellij.codeInsight.ContainerProvider
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.elixir_lang.psi.ElixirTypes
import org.elixir_lang.psi.call.Call

/**
 * Extracts a leaf [PsiElement] suitable for use as a [com.intellij.codeInsight.daemon.LineMarkerInfo] anchor.
 *
 * The [LineMarkerProvider contract][com.intellij.codeInsight.daemon.LineMarkerProvider.getLineMarkerInfo] requires
 * that line marker info is created for **leaf elements only** (the smallest possible elements).  Passing a composite
 * element causes gutter-icon blinking as the two-pass highlighting alternately adds and removes the marker.
 *
 * For a [Call], the leaf is the `IDENTIFIER_TOKEN` child of the function-name element, falling back to the
 * function-name element itself when it is already a leaf (e.g. an operator token).
 */
internal fun markerAnchor(call: Call): PsiElement? {
    val functionNameElement = call.functionNameElement()

    return (functionNameElement?.node?.findChildByType(ElixirTypes.IDENTIFIER_TOKEN) as? LeafPsiElement)
        ?: (functionNameElement as? LeafPsiElement)
}

/**
 * Returns an XML-escaped display name for [element], suitable for gutter-icon popup titles.
 *
 * Uses the element's [NavigationItem.presentation] when available, falling back to [PsiElement.getText]
 * (appropriate for leaf tokens such as identifiers).
 */
internal fun escapedName(element: PsiElement): String {
    val presentation = (element as? NavigationItem)?.presentation
    val containerText = containerText(element)
    val prefix = if (containerText == null) {
        ""
    } else {
        "$containerText."
    }
    val fullName = prefix + (presentation?.presentableText ?: element.text)

    return StringUtil.escapeXmlEntities(fullName)
}

/**
 * Returns the presentable text of the container of [element], or `null` if none.
 */
internal fun containerText(element: PsiElement): String? {
    val container = container(element)
    val containerPresentation =
        if (container == null || container is PsiFile) {
            null
        } else {
            (container as? NavigationItem)?.presentation
        }
    return containerPresentation?.presentableText
}

private fun container(refElement: PsiElement): PsiElement? {
    for (provider in ContainerProvider.EP_NAME.extensions) {
        val container = provider.getContainer(refElement)
        if (container != null) return container
    }
    return refElement.parent
}
