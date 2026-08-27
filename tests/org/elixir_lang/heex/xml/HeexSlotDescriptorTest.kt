package org.elixir_lang.heex.xml

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.XmlElementDescriptor
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.heexSigilModuleText
import org.elixir_lang.heex.reference.injectedHeexRoots

/** How a HEEx slot tag's [XmlTag.getDescriptor] resolves, in a `.heex` file and a `~H` sigil. */
class HeexSlotDescriptorTest : HeexHostTestCase() {    fun testSlotTagGetsSlotDescriptor() {
        val tag = tagNamed(":col", "<.table><:col>x</:col></.table>")

        assertInstanceOf(tag.descriptor, HeexSlotDescriptor::class.java)
    }

    fun testSlotTagGetsSlotDescriptorInsideHSigil() {
        val tag = tagNamedInSigil(":col", "<.table><:col>x</:col></.table>")

        assertInstanceOf(tag.descriptor, HeexSlotDescriptor::class.java)
    }

    /** `XmlTag.getLocalName()` strips up to the first `:`, which is why a slot needs its own descriptor. */
    fun testSlotTagLocalNameCollidesWithHtmlElementName() {
        val tag = tagNamed(":col", "<.table><:col>x</:col></.table>")

        assertEquals("col", tag.localName)
    }

    fun testSlotDescriptorDeclarationIsTheTag() {
        val tag = tagNamed(":action", "<.table><:action>x</:action></.table>")

        assertSame(tag, tag.descriptor!!.getDeclaration())
    }

    fun testSlotDescriptorDeclarationIsTheTagInsideHSigil() {
        val tag = tagNamedInSigil(":action", "<.table><:action>x</:action></.table>")

        assertSame(tag, tag.descriptor!!.getDeclaration())
    }

    fun testSlotDescriptorContentTypeIsAny() {
        val tag = tagNamed(":col", "<.table><:col>x</:col></.table>")

        assertEquals(XmlElementDescriptor.CONTENT_TYPE_ANY, tag.descriptor!!.contentType)
    }

    fun testSlotDescriptorContentTypeIsAnyInsideHSigil() {
        val tag = tagNamedInSigil(":col", "<.table><:col>x</:col></.table>")

        assertEquals(XmlElementDescriptor.CONTENT_TYPE_ANY, tag.descriptor!!.contentType)
    }

    fun testComponentTagStillGetsComponentDescriptor() {
        val tag = tagNamed(".button", "<.button>Click</.button>")

        assertInstanceOf(tag.descriptor, HeexComponentDescriptor::class.java)
    }

    fun testComponentTagStillGetsComponentDescriptorInsideHSigil() {
        val tag = tagNamedInSigil(".button", "<.button>Click</.button>")

        assertInstanceOf(tag.descriptor, HeexComponentDescriptor::class.java)
    }

    fun testPlainHtmlTagGetsNoHeexDescriptor() {
        val tag = tagNamed("div", "<div>x</div>")

        val descriptor = tag.descriptor
        assertTrue(descriptor !is HeexComponentDescriptor && descriptor !is HeexSlotDescriptor)
    }

    fun testPlainHtmlTagGetsNoHeexDescriptorInsideHSigil() {
        val tag = tagNamedInSigil("div", "<div>x</div>")

        val descriptor = tag.descriptor
        assertTrue(descriptor !is HeexComponentDescriptor && descriptor !is HeexSlotDescriptor)
    }

    private fun tagNamed(name: String, text: String): XmlTag {
        myFixture.configureByText("x.html.heex", text)
        val htmlRoot = myFixture.file.viewProvider.getPsi(HTMLLanguage.INSTANCE)
        return PsiTreeUtil.findChildrenOfType(htmlRoot, XmlTag::class.java).first { it.name == name }
    }

    private fun tagNamedInSigil(name: String, text: String): XmlTag {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(entranceModuleText = "defmodule Test do\nend", heexBody = text)
        )
        val htmlRoot = myFixture.injectedHeexRoots().single().viewProvider.getPsi(HTMLLanguage.INSTANCE)
        return PsiTreeUtil.findChildrenOfType(htmlRoot, XmlTag::class.java).first { it.name == name }
    }
}
