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
 * Describes a HEEx-owned tag (component or slot) to the platform's XML plumbing: any content, any
 * attribute (`attr`/`slot` validation is out of scope), and the special attributes offered for
 * completion (the platform does not consult [HeexAttributeDescriptorsProvider] for a tag that
 * already has a descriptor).
 *
 * [getDeclaration] defaults to the tag itself. Returning `null` from a non-`Any` descriptor makes
 * `TagNameReference` a failed reference, which `XmlHighlightVisitor` reports as "Cannot resolve
 * symbol" from the annotator, out of reach of any `InspectionSuppressor`.
 */
abstract class HeexTagDescriptor(protected val tag: XmlTag) : XmlElementDescriptor {
    override fun getDeclaration(): PsiElement = tag

    override fun getName(context: PsiElement): String = getName()
    override fun getName(): String = tag.name
    override fun init(element: PsiElement) {}
    override fun getQualifiedName(): String = tag.name
    override fun getDefaultName(): String = tag.name

    override fun getElementsDescriptors(context: XmlTag): Array<XmlElementDescriptor> = XmlElementDescriptor.EMPTY_ARRAY
    override fun getElementDescriptor(childTag: XmlTag, contextTag: XmlTag): XmlElementDescriptor? = null

    override fun getAttributesDescriptors(context: XmlTag?): Array<XmlAttributeDescriptor> =
        HeexAttributeDescriptorsProvider.specialAttributeDescriptors()
    override fun getAttributeDescriptor(attributeName: String, context: XmlTag?): XmlAttributeDescriptor =
        AnyXmlAttributeDescriptor(attributeName)
    override fun getAttributeDescriptor(attribute: XmlAttribute): XmlAttributeDescriptor =
        AnyXmlAttributeDescriptor(attribute.name)

    override fun getNSDescriptor(): XmlNSDescriptor? = null
    override fun getTopGroup(): XmlElementsGroup? = null
    override fun getContentType(): Int = XmlElementDescriptor.CONTENT_TYPE_ANY
    override fun getDefaultValue(): String? = null
}
