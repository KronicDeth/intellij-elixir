package org.elixir_lang.code_insight.line_marker_provider
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.psi.PsiElement
import org.elixir_lang.PlatformTestCase
class ProtocolLeafElementTest : PlatformTestCase() {
    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/code_insight/line_marker_provider/protocol_implementation"
    fun testGetLineMarkerInfoReturnsNullForCompositeElements() {
        myFixture.configureByFile("leaf_element_contract.ex")
        val provider = Protocol()
        val violating = compositeElements(myFixture.file).filter { provider.getLineMarkerInfo(it) != null }
        assertTrue(
            "Protocol.getLineMarkerInfo returned non-null for ${violating.size} composite element(s): " +
                "${violating.take(5).map(::formatElement)}.",
            violating.isEmpty()
        )
    }
    fun testProducesMarkersFromLeafAnchorsOnly() {
        myFixture.configureByFile("leaf_element_contract.ex")
        val provider = Protocol()
        val markersFromLeaves: List<Pair<PsiElement, LineMarkerInfo<*>>> =
            leafElements(myFixture.file).mapNotNull { leaf ->
                provider.getLineMarkerInfo(leaf)?.let { leaf to it }
            }
        assertTrue(
            "Expected Protocol markers from leaf elements, but got none.",
            markersFromLeaves.isNotEmpty()
        )
        markersFromLeaves.forEach { (leaf, marker) ->
            val markerElement = marker.element
            assertNotNull("LineMarkerInfo.element is null", markerElement)
            assertSame(
                "Protocol marker must be anchored to the same leaf element it was queried with",
                leaf,
                markerElement
            )
            assertTrue(
                "Protocol marker element is not a leaf: ${formatElement(markerElement!!)}",
                markerElement.firstChild == null
            )
        }
    }
    private fun compositeElements(root: PsiElement): List<PsiElement> {
        val compositeElements = mutableListOf<PsiElement>()
        fun collect(element: PsiElement) {
            if (element.firstChild != null) {
                compositeElements.add(element)
                var child = element.firstChild
                while (child != null) {
                    collect(child)
                    child = child.nextSibling
                }
            }
        }
        collect(root)
        return compositeElements
    }
    private fun leafElements(root: PsiElement): List<PsiElement> {
        val leaves = mutableListOf<PsiElement>()
        fun collect(element: PsiElement) {
            if (element.firstChild == null) {
                leaves.add(element)
            } else {
                var child = element.firstChild
                while (child != null) {
                    collect(child)
                    child = child.nextSibling
                }
            }
        }
        collect(root)
        return leaves
    }
    private fun formatElement(element: PsiElement): String =
        "${element.node.elementType} (${element.javaClass.simpleName}) text='${element.text.take(30)}'"
}
