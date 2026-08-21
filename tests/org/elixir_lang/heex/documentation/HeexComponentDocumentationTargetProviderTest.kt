package org.elixir_lang.heex.documentation

import com.intellij.lang.html.HTMLLanguage
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTokenType
import org.elixir_lang.documentation.ElixirDocumentationProvider
import org.elixir_lang.heex.reference.HeexHostTestCase
import org.elixir_lang.heex.reference.firstInjectedHeexTag
import org.elixir_lang.heex.reference.fixtureText
import org.elixir_lang.heex.reference.heexSigilModuleText
import org.elixir_lang.heex.xml.HeexComponentResolver
import org.elixir_lang.psi.CallDefinitionClause
import org.elixir_lang.psi.call.Call

/**
 * [HeexComponentDocumentationTargetProvider] widens Quick Docs' target from the def's name
 * identifier to the `def`/`defp` `Call` that [ElixirDocumentationProvider] renders. The provider is
 * called with the same inputs `DocumentationTargetFinder` passes it, and the resulting `Call` is
 * rendered to confirm the `@doc` text appears.
 */
class HeexComponentDocumentationTargetProviderTest : HeexHostTestCase() {
    private val provider = HeexComponentDocumentationTargetProvider()

    override fun getTestDataPath(): String = "testData/org/elixir_lang/heex/documentation"

    fun testResolvedComponentTagYieldsRenderableDocumentation() {
        myFixture.configureByFiles("component/page_live.html.heex", "component/page_live.ex")
        val tag = componentTag(".button")
        assertResolvesToRenderableDocumentation(tag, startTagNameElement(tag))
    }

    fun testUnresolvedComponentTagYieldsNoDocumentationTarget() {
        myFixture.configureByText("unresolved.html.heex", "<.nonexistent>Click</.nonexistent>")
        val tag = componentTag(".nonexistent")
        assertNull(HeexComponentResolver.resolveDeclaration(tag))

        // HeexComponentDescriptor.getDeclaration() declares the unresolved tag itself.
        assertNull(provider.documentationTarget(tag, startTagNameElement(tag)))
    }

    /**
     * Quick Docs computes `originalElement` from the host `.ex` file (`PsiUtilBase.getPsiFileInEditor`
     * then `findElementAt`, never entering the injection), so it is computed the same way here.
     */
    fun testLocalComponentTagInsideHSigilYieldsRenderableDocumentation() {
        val tag = configureHSigilAndFindTag(
            heexSigilModuleText(
                entranceModuleText = myFixture.fixtureText("component/page_live.ex"),
                heexBody = myFixture.fixtureText("component/page_live.html.heex")
            )
        )
        assertResolvesToRenderableDocumentation(tag, hostContextElementAtCaret())
    }

    fun testRemoteComponentTagInsideHSigilYieldsRenderableDocumentation() {
        val tag = configureHSigilAndFindTag(
            heexSigilModuleText(
                entranceModuleText = "defmodule Test do\nend",
                heexBody = myFixture.fixtureText("remote/page_live.html.heex"),
                myFixture.fixtureText("remote/core_components.ex")
            )
        )
        assertResolvesToRenderableDocumentation(tag, hostContextElementAtCaret())
    }

    fun testOriginalElementOutsideHeexYieldsNull() {
        myFixture.configureByFiles("component/page_live.ex")
        val nameIdentifier = myFixture.file.findElementAt(myFixture.caretOffset)
        assertNotNull(nameIdentifier)

        assertNull(provider.documentationTarget(nameIdentifier!!, nameIdentifier))
    }

    private fun assertResolvesToRenderableDocumentation(tag: XmlTag, originalElement: PsiElement) {
        val resolved = HeexComponentResolver.resolveDeclaration(tag)
        assertNotNull("Expected ${tag.name} to resolve", resolved)

        assertNotNull("Expected a documentation target for ${tag.name}", provider.documentationTarget(resolved!!, originalElement))

        // The nearest Call ancestor of a def's name identifier is the argument-list wrapper
        // (`button(assigns)`); the def clause is the first CallDefinitionClause ancestor.
        val call = generateSequence(resolved) { it.parent }
            .filterIsInstance<Call>()
            .firstOrNull { CallDefinitionClause.`is`(it) }
        assertNotNull(call)

        val doc = ElixirDocumentationProvider().generateDoc(call!!, null)
        assertNotNull("Expected the Elixir doc pipeline to render the resolved def", doc)
        assertTrue("Expected the function name in the rendered doc, got: $doc", doc!!.contains("button"))
        assertTrue("Expected the @doc text in the rendered doc, got: $doc", doc.contains("clickable button"))
    }

    private fun componentTag(tagName: String): XmlTag {
        val htmlRoot = myFixture.file.viewProvider.getPsi(HTMLLanguage.INSTANCE)
        return PsiTreeUtil.findChildrenOfType(htmlRoot, XmlTag::class.java).first { it.name == tagName }
    }

    private fun startTagNameElement(tag: XmlTag) =
        tag.node.findChildByType(XmlTokenType.XML_NAME)?.psi ?: tag

    /**
     * Real Quick Docs never sees the fixture's caret-in-injection swap (`CommonDataKeys.EDITOR` is
     * the host editor), so the swap is disabled to keep [hostContextElementAtCaret] faithful.
     */
    private fun configureHSigilAndFindTag(text: String): XmlTag {
        myFixture.setCaresAboutInjection(false)
        myFixture.configureByText("test.ex", text)
        return myFixture.firstInjectedHeexTag()
    }

    private fun hostContextElementAtCaret(): PsiElement {
        val hostFile = PsiUtilBase.getPsiFileInEditor(myFixture.editor, project)
        checkNotNull(hostFile) { "Expected a PsiFile for the editor" }
        return hostFile.findElementAt(myFixture.caretOffset)
            ?: error("No element at caret offset ${myFixture.caretOffset} in $hostFile")
    }
}
