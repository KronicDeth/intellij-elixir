package org.elixir_lang.structure_view.element.modular

import com.intellij.ide.structureView.StructureViewTreeElement
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.structure_view.Model

/**
 * `defimpl P, for: T` compiles to a module named `P.T`, but the node presents the protocol and the
 * type rather than that generated name's qualifier and final segment.
 */
class ImplementationNodeTest : PlatformTestCase() {
    private fun implementation(code: String): StructureViewTreeElement {
        myFixture.configureByText("demo.ex", code)

        val found = mutableListOf<StructureViewTreeElement>()

        fun walk(element: StructureViewTreeElement) {
            if (element is Implementation) {
                found.add(element)
            }

            for (child in element.children) {
                if (child is StructureViewTreeElement) {
                    walk(child)
                }
            }
        }

        walk(Model(myFixture.file as ElixirFile, null).root)
        assertEquals("the fixture must build exactly one implementation node", 1, found.size)

        return found.single()
    }

    fun testQualifiedFor() {
        val presentation =
            implementation("defimpl Enumerable, for: Foo.Bar do\n  def count(_), do: 0\nend\n").presentation

        assertEquals("Foo.Bar", presentation.presentableText)
        assertEquals("Enumerable", presentation.locationString)
    }

    fun testUnqualifiedFor() {
        val presentation =
            implementation("defimpl Enumerable, for: Bar do\n  def count(_), do: 0\nend\n").presentation

        assertEquals("Bar", presentation.presentableText)
        assertEquals("Enumerable", presentation.locationString)
    }
}
