package org.elixir_lang.heex.xml

import com.intellij.psi.xml.XmlTag

/**
 * Describes a HEEx slot tag (`<:col>`, `<:action>`). Left to the platform's HTML lookup,
 * `XmlTag.getLocalName()` strips everything up to the first `:`, so `<:col>` is validated as the
 * void HTML `<col>` element and `<:action>` as an unknown tag. A slot is declared by the `slot`
 * macro, not a single definition, so it declares itself.
 */
class HeexSlotDescriptor(tag: XmlTag) : HeexTagDescriptor(tag)
