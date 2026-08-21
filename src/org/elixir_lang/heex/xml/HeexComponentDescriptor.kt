package org.elixir_lang.heex.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlTag

/**
 * Describes a HEEx component tag (`<.button>`, `<MyAppWeb.CoreComponents.button>`). The platform
 * turns [getDeclaration] into Go To Declaration through [XmlTag]'s `TagNameReference`; a tag that
 * does not resolve (dynamic name, unrecognised view module) declares itself, so it is neither an
 * error nor a typo.
 */
class HeexComponentDescriptor(tag: XmlTag) : HeexTagDescriptor(tag) {
    override fun getDeclaration(): PsiElement = HeexComponentResolver.resolveDeclaration(tag) ?: tag
}
