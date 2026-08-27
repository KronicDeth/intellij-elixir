package org.elixir_lang.heex.xml

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.xml.XmlTag
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.heexSigilModuleText
import org.elixir_lang.heex.reference.injectedHeexRoots

/**
 * Direct tests for [HeexAttributeDescriptorsProvider], the contract `HtmlUnknownAttributeInspection`
 * reads through `RelaxedHtmlFromSchemaElementDescriptor`; [org.elixir_lang.heex.inspections.HeexHtmlInspectionTest]
 * checks the inspection end to end. The outside-HEEx tests have no `~H` counterpart.
 */
class HeexAttributeDescriptorsProviderTest : HeexHostTestCase() {
    private val provider = HeexAttributeDescriptorsProvider()
    fun testSpecialAttributesGetADescriptor() {
        assertSpecialAttributesGetADescriptor(plainTag("<div :let={x} :if={y} :for={z} :key={k} :type={t}></div>"))
    }

    fun testSpecialAttributesGetADescriptorInsideHSigil() {
        assertSpecialAttributesGetADescriptor(
            plainTagInSigil("<div :let={x} :if={y} :for={z} :key={k} :type={t}></div>")
        )
    }

    private fun assertSpecialAttributesGetADescriptor(tag: XmlTag) {
        for (name in listOf(":let", ":if", ":for", ":key", ":type")) {
            assertNotNull("$name should have a descriptor", provider.getAttributeDescriptor(name, tag))
        }
    }

    fun testPhxPrefixedAttributesGetADescriptor() {
        assertPhxPrefixedAttributesGetADescriptor(plainTag("<div phx-click=\"go\" phx-value-anything=\"1\"></div>"))
    }

    fun testPhxPrefixedAttributesGetADescriptorInsideHSigil() {
        assertPhxPrefixedAttributesGetADescriptor(plainTagInSigil("<div phx-click=\"go\" phx-value-anything=\"1\"></div>"))
    }

    private fun assertPhxPrefixedAttributesGetADescriptor(tag: XmlTag) {
        assertNotNull(provider.getAttributeDescriptor("phx-click", tag))
        assertNotNull(provider.getAttributeDescriptor("phx-value-anything", tag))
    }

    fun testDescriptorIsAnyXmlAttributeDescriptor() {
        // HtmlUnknownBooleanAttributeInspectionBase explicitly skips AnyXmlAttributeDescriptor, so
        // this is the property that keeps a valueless `:if`/`phx-no-format` from being flagged as
        // "not a boolean attribute" once it is no longer unknown.
        val tag = plainTag("<div :if={y}></div>")

        assertInstanceOf(provider.getAttributeDescriptor(":if", tag), AnyXmlAttributeDescriptor::class.java)
    }

    fun testDescriptorIsAnyXmlAttributeDescriptorInsideHSigil() {
        val tag = plainTagInSigil("<div :if={y}></div>")

        assertInstanceOf(provider.getAttributeDescriptor(":if", tag), AnyXmlAttributeDescriptor::class.java)
    }

    fun testOrdinaryAttributeGetsNoDescriptor() {
        assertOrdinaryAttributeGetsNoDescriptor(plainTag("<div class=\"x\" data-foo=\"y\" bogus=\"z\"></div>"))
    }

    fun testOrdinaryAttributeGetsNoDescriptorInsideHSigil() {
        assertOrdinaryAttributeGetsNoDescriptor(plainTagInSigil("<div class=\"x\" data-foo=\"y\" bogus=\"z\"></div>"))
    }

    private fun assertOrdinaryAttributeGetsNoDescriptor(tag: XmlTag) {
        assertNull(provider.getAttributeDescriptor("class", tag))
        assertNull(provider.getAttributeDescriptor("data-foo", tag))
        assertNull(provider.getAttributeDescriptor("bogus", tag))
    }

    fun testNamespacedAttributeGetsNoDescriptor() {
        // Guards startsWith(":") rather than contains(":") - a real XML namespace prefix must not
        // be swallowed by the special-attribute check.
        val tag = plainTag("<svg xlink:href=\"#x\"></svg>")

        assertNull(provider.getAttributeDescriptor("xlink:href", tag))
    }

    fun testNamespacedAttributeGetsNoDescriptorInsideHSigil() {
        val tag = plainTagInSigil("<svg xlink:href=\"#x\"></svg>")

        assertNull(provider.getAttributeDescriptor("xlink:href", tag))
    }

    fun testNoDescriptorOutsideHeex() {
        myFixture.configureByText("plain.html", "<div></div>")
        val tag = PsiTreeUtil.findChildOfType(myFixture.file, XmlTag::class.java)!!

        assertNull(provider.getAttributeDescriptor(":for", tag))
        assertNull(provider.getAttributeDescriptor("phx-click", tag))
    }

    fun testAttributeDescriptorsOfferTheSpecials() {
        assertAttributeDescriptorsOfferTheSpecials(plainTag("<div></div>"))
    }

    fun testAttributeDescriptorsOfferTheSpecialsInsideHSigil() {
        assertAttributeDescriptorsOfferTheSpecials(plainTagInSigil("<div></div>"))
    }

    private fun assertAttributeDescriptorsOfferTheSpecials(tag: XmlTag) {
        val names = provider.getAttributeDescriptors(tag).map { it.name }
        assertEquals(listOf(":let", ":if", ":for", ":key", ":type"), names)
    }

    fun testAttributeDescriptorsEmptyOutsideHeex() {
        myFixture.configureByText("plain.html", "<div></div>")
        val tag = PsiTreeUtil.findChildOfType(myFixture.file, XmlTag::class.java)!!

        assertTrue(provider.getAttributeDescriptors(tag).isEmpty())
    }

    private fun plainTag(text: String): XmlTag {
        myFixture.configureByText("x.html.heex", text)
        val htmlRoot = myFixture.file.viewProvider.getPsi(HTMLLanguage.INSTANCE)
        return PsiTreeUtil.findChildOfType(htmlRoot, XmlTag::class.java)!!
    }

    private fun plainTagInSigil(text: String): XmlTag {
        myFixture.configureByText(
            "test.ex",
            heexSigilModuleText(entranceModuleText = "defmodule Test do\nend", heexBody = text)
        )
        val htmlRoot = myFixture.injectedHeexRoots().single().viewProvider.getPsi(HTMLLanguage.INSTANCE)
        return PsiTreeUtil.findChildOfType(htmlRoot, XmlTag::class.java)!!
    }
}
