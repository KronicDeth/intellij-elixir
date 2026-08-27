package org.elixir_lang.heex.xml

import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.elixir_lang.heex.isInHeex

/**
 * Recognizes HEEx component tags (`<.button>`, `<MyAppWeb.CoreComponents.button>`) and slot tags
 * (`<:col>`) as HEEx's own elements; plain HTML tags return `null` and are left to the platform.
 */
class HeexComponentDescriptorProvider : XmlElementDescriptorProvider {
    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        if (!tag.isInHeex()) {
            return null
        }

        return when (ComponentTagName.parse(tag.name)) {
            is ComponentTagName.Slot -> HeexSlotDescriptor(tag)
            is ComponentTagName.Local, is ComponentTagName.Remote -> HeexComponentDescriptor(tag)
            null -> null
        }
    }
}
