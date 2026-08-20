package org.elixir_lang.heex.xml

import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.elixir_lang.heex.HeexLanguage

/**
 * Recognizes HEEx component tags (`<.button>`, `<MyAppWeb.CoreComponents.button>`) so the
 * platform resolves, navigates and renames them like any other described XML element.
 * `<:slot>` tags and plain HTML tags are left to the platform's own HTML descriptor by returning
 * `null` - they are not components.
 */
class HeexComponentDescriptorProvider : XmlElementDescriptorProvider {
    override fun getDescriptor(tag: XmlTag): XmlElementDescriptor? {
        if (!tag.containingFile.viewProvider.hasLanguage(HeexLanguage.INSTANCE)) {
            return null
        }

        return ComponentTagName.parse(tag.name)?.let { HeexComponentDescriptor(tag) }
    }
}
