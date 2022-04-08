package org.elixir_lang.psi

import com.intellij.psi.ElementDescriptionLocation
import com.intellij.psi.PsiElement
import org.elixir_lang.find_usages.Provider
import org.elixir_lang.semantic.semantic

/**
 * Dual to [Provider], where instead of each location being a separate method, they
 * are all one method, which means the same code can be used to detect the type of an element and then group all the
 * text ([Provider.getDescriptiveName],
 * [Provider.getHelpId],
 * [Provider.getNodeText]
 * [Provider.getType]) together together.
 */
class ElementDescriptionProvider : com.intellij.psi.ElementDescriptionProvider {
    override fun getElementDescription(element: PsiElement, location: ElementDescriptionLocation): String? =
        element.semantic?.elementDescription(location)
}
