package org.elixir_lang.structure_view.element.structure

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.structure_view.Model

// Drives defstruct/defexception shapes through the structure view, none of which may throw.
class StructureChildrenShapeTest : BasePlatformTestCase() {
    private data class Shape(val label: String, val source: String)

    // Recurses because getChildren() is where the crash lives, not just the root's own children.
    private fun walk(element: StructureViewTreeElement) {
        for (child in element.children) {
            if (child is StructureViewTreeElement) {
                walk(child)
            }
        }
    }

    private fun failureFor(shape: Shape): String? =
        try {
            myFixture.configureByText("issue_2107_${shape.label}.ex", shape.source)
            walk(Model(myFixture.file as ElixirFile, null).root)
            null
        } catch (throwable: Throwable) {
            "${shape.label}: ${throwable.javaClass.name}: ${throwable.message} @ " +
                throwable.stackTrace.take(3).joinToString(" <- ") { "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})" }
        }

    fun testStructureAndExceptionShapesDoNotThrowOutOfTheTreeWalk() {
        val shapes = listOf(
            // defstruct - the shape Structure was written for, as a control.
            Shape("defstruct_list", "defmodule A do\n  defstruct [:a, :b]\nend\n"),
            Shape("defstruct_keywords", "defmodule A do\n  defstruct a: 1, b: 2\nend\n"),
            Shape("defstruct_no_parens_single", "defmodule A do\n  defstruct :a\nend\n"),
            Shape("defstruct_parens", "defmodule A do\n  defstruct([:a])\nend\n"),
            Shape("defstruct_empty_list", "defmodule A do\n  defstruct []\nend\n"),
            Shape("defstruct_qualified", "defmodule A do\n  Kernel.defstruct([:a])\nend\n"),
            // defexception - the path that hands a non-defstruct call to Structure.
            Shape("defexception_list", "defmodule A do\n  defexception [:message]\nend\n"),
            Shape("defexception_keywords", "defmodule A do\n  defexception message: \"boom\"\nend\n"),
            Shape("defexception_parens", "defmodule A do\n  defexception([:message])\nend\n"),
            Shape("defexception_empty_list", "defmodule A do\n  defexception []\nend\n"),
            Shape("defexception_qualified", "defmodule A do\n  Kernel.defexception([:message])\nend\n"),
            // do-block forms, where resolvedFinalArity and finalArguments are most likely to diverge.
            Shape("defexception_do_block", "defmodule A do\n  defexception do\n    :ok\n  end\nend\n"),
            Shape("defstruct_do_block", "defmodule A do\n  defstruct do\n    :ok\n  end\nend\n"),
            Shape(
                "defexception_list_and_do_block",
                "defmodule A do\n  defexception [:message] do\n    :ok\n  end\nend\n",
            ),
            // Incomplete source, which the structure view sees while it is being typed.
            Shape("defexception_bare", "defmodule A do\n  defexception\nend\n"),
            Shape("defstruct_bare", "defmodule A do\n  defstruct\nend\n"),
        )

        val failures = shapes.mapNotNull { shape -> failureFor(shape) }

        assertEquals(
            "structure view tree walk threw for:\n" + failures.joinToString("\n"),
            emptyList<String>(),
            failures,
        )
    }
}
