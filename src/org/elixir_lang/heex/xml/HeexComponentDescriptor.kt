package org.elixir_lang.heex.xml

import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlElementDescriptor
import com.intellij.xml.XmlElementsGroup
import com.intellij.xml.XmlNSDescriptor
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor

/**
 * Describes a HEEx component tag (`<.button>`, `<MyAppWeb.CoreComponents.button>`) to the
 * platform's XML plumbing. [getDeclaration] is the only method that does real work - the platform
 * itself turns that into navigation, find-usages and rename through [XmlTag]'s `TagNameReference`
 * (`descriptor.getDeclaration()` is exactly what that reference resolves to). Attributes are left
 * unrestricted ([AnyXmlAttributeDescriptor]): `attr`/`slot` validation is out of scope, and without
 * it every attribute on a resolved component would be flagged as unknown.
 */
class HeexComponentDescriptor(private val tag: XmlTag) : XmlElementDescriptor {
    override fun getDeclaration(): PsiElement? = HeexComponentResolver.resolveDeclaration(tag)

    override fun getName(context: PsiElement): String = getName()
    override fun getName(): String = tag.name
    override fun init(element: PsiElement) {}
    override fun getQualifiedName(): String = tag.name
    override fun getDefaultName(): String = tag.name

    override fun getElementsDescriptors(context: XmlTag): Array<XmlElementDescriptor> = XmlElementDescriptor.EMPTY_ARRAY
    override fun getElementDescriptor(childTag: XmlTag, contextTag: XmlTag): XmlElementDescriptor? = null

    override fun getAttributesDescriptors(context: XmlTag?): Array<XmlAttributeDescriptor> = XmlAttributeDescriptor.EMPTY
    override fun getAttributeDescriptor(attributeName: String, context: XmlTag?): XmlAttributeDescriptor =
        AnyXmlAttributeDescriptor(attributeName)
    override fun getAttributeDescriptor(attribute: XmlAttribute): XmlAttributeDescriptor =
        AnyXmlAttributeDescriptor(attribute.name)

    override fun getNSDescriptor(): XmlNSDescriptor? = null
    override fun getTopGroup(): XmlElementsGroup? = null
    override fun getContentType(): Int = XmlElementDescriptor.CONTENT_TYPE_ANY
    override fun getDefaultValue(): String? = null
}
