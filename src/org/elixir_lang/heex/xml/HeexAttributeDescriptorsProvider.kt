package org.elixir_lang.heex.xml

import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlAttributeDescriptor
import com.intellij.xml.XmlAttributeDescriptorsProvider
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor
import org.elixir_lang.heex.isInHeex

/**
 * Describes the attributes HEEx allows on any tag but no HTML schema knows: the special attributes
 * `Phoenix.LiveView.TagEngine` strips before HTML output (`:let`, `:if`, `:for`, `:key`, `:type`)
 * and the `phx-*` bindings. Without a descriptor, `HtmlUnknownAttributeInspection` reports each as
 * "not allowed here" on plain HTML tags.
 *
 * [AnyXmlAttributeDescriptor] adds no value validation and is the type
 * `HtmlUnknownBooleanAttributeInspectionBase` already exempts, so a valueless `:if` is not flagged
 * either. `phx-*` is matched by prefix because `phx-value-*` is open-ended; which tags may carry
 * which attribute is `attr`/`slot` validation, out of scope.
 */
class HeexAttributeDescriptorsProvider : XmlAttributeDescriptorsProvider {
    override fun getAttributeDescriptor(attributeName: String?, context: XmlTag?): XmlAttributeDescriptor? =
        if (attributeName != null && isHeexAttributeName(attributeName) && context?.isInHeex() == true) {
            AnyXmlAttributeDescriptor(attributeName)
        } else {
            null
        }

    // Completion feed; phx-* is absent because a prefix cannot be enumerated.
    override fun getAttributeDescriptors(context: XmlTag?): Array<XmlAttributeDescriptor> =
        if (context?.isInHeex() == true) specialAttributeDescriptors() else XmlAttributeDescriptor.EMPTY

    companion object {
        private val SPECIAL_ATTRIBUTE_NAMES = listOf(":let", ":if", ":for", ":key", ":type")
        private const val BINDING_PREFIX = "phx-"
        private val SPECIAL_ATTRIBUTE_DESCRIPTORS: Array<XmlAttributeDescriptor> =
            SPECIAL_ATTRIBUTE_NAMES.map { AnyXmlAttributeDescriptor(it) }.toTypedArray()

        private fun isHeexAttributeName(attributeName: String): Boolean =
            attributeName in SPECIAL_ATTRIBUTE_NAMES || attributeName.startsWith(BINDING_PREFIX)

        fun specialAttributeDescriptors(): Array<XmlAttributeDescriptor> = SPECIAL_ATTRIBUTE_DESCRIPTORS.copyOf()
    }
}
