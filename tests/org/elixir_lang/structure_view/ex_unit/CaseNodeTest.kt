package org.elixir_lang.structure_view.ex_unit

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.psi.PsiElement
import com.intellij.psi.ResolveState
import com.intellij.psi.util.PsiTreeUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.psi.ElixirFile
import org.elixir_lang.psi.call.Call
import org.elixir_lang.psi.ex_unit.Case
import org.elixir_lang.psi.operation.Or
import org.elixir_lang.structure_view.ChildCall
import org.elixir_lang.structure_view.Model
import org.elixir_lang.structure_view.element.Callback
import org.elixir_lang.structure_view.element.Delegation
import org.elixir_lang.structure_view.element.Type
import org.elixir_lang.structure_view.element.modular.Unknown
import org.elixir_lang.structure_view.element.structure.Structure
import org.elixir_lang.structure_view.element.ex_unit.case.Describe as DescribeElement
import org.elixir_lang.structure_view.element.ex_unit.case.Test as TestElement

/**
 * `ExUnit.Case`'s two child forms, which [Model.isSuitable] and the tree dispatch decide separately.
 */
class CaseNodeTest : PlatformTestCase() {
    override fun getTestDataPath(): String = "testData/org/elixir_lang/structure_view/ex_unit"

    private class Tree(
        val byLabel: Map<String, StructureViewTreeElement>,
        val duplicatedLabels: Set<String>,
        val elementByValue: Map<PsiElement, StructureViewTreeElement>
    ) {
        val values: Set<PsiElement> get() = elementByValue.keys
    }

    /** The tree repeats labels - a `defdelegate` builds two `values/1` nodes - so refuse an ambiguous one. */
    private fun Tree.at(label: String): StructureViewTreeElement? {
        assertFalse("more than one node is labelled \"$label\", so the lookup is ambiguous", label in duplicatedLabels)

        return byLabel[label]
    }

    private fun build(): Tree {
        myFixture.configureByFiles("case_test.exs", "ex_unit_case.ex")

        val byLabel = mutableMapOf<String, StructureViewTreeElement>()
        val duplicatedLabels = mutableSetOf<String>()
        val elementByValue = mutableMapOf<PsiElement, StructureViewTreeElement>()

        fun walk(element: StructureViewTreeElement) {
            element.presentation.presentableText?.let { label ->
                if (byLabel.putIfAbsent(label, element) != null) {
                    duplicatedLabels.add(label)
                }
            }
            // Outermost wins: a child can carry the same PSI as its parent, as `Callback.getChildren()`
            // does over the same `@callback` call.
            (element.value as? PsiElement)?.let { elementByValue.putIfAbsent(it, element) }

            for (child in element.children) {
                if (child is StructureViewTreeElement) {
                    walk(child)
                }
            }
        }

        walk(Model(myFixture.file as ElixirFile, null).root)

        return Tree(byLabel, duplicatedLabels, elementByValue)
    }

    fun testTopLevelTestGetsItsOwnNode() {
        val tree = build()

        val element = tree.at("test \"at the top level, outside any describe\"")

        assertNotNull(
            "a `test` outside a `describe` must get its own node; built nodes were ${tree.byLabel.keys}",
            element
        )
        assertInstanceOf(element, TestElement::class.java)
    }

    fun testTestInsideDescribeGetsItsOwnNode() {
        val tree = build()

        val element = tree.at("test \"nested inside the describe\"")

        assertNotNull(
            "a `test` inside a `describe` must get its own node; built nodes were ${tree.byLabel.keys}",
            element
        )
        assertInstanceOf(element, TestElement::class.java)

        assertEquals(
            "a `test` inside a `describe` is qualified by the describe, not the module or the file path",
            "/src/case_test.exs defmodule CaseTest describe \"a describe block\"",
            element!!.presentation.locationString
        )
    }

    /** A `test` is a modular, so its body's declarations get nodes and stay qualified by the module. */
    fun testATestBodyKeepsItsChildren() {
        val tree = build()

        assertNotNull(
            "a module defined inside a top-level `test` must still get a node; built nodes were " +
                "${tree.byLabel.keys}",
            tree.at("defmodule DefinedInsideATest")
        )
        assertNotNull(
            "and the walk must continue into that module; built nodes were ${tree.byLabel.keys}",
            tree.at("helper/0")
        )

        assertEquals(
            "a declaration inside a top-level `test` is qualified by both the module and the test",
            "/src/case_test.exs defmodule CaseTest test \"at the top level, outside any describe\"",
            tree.at("defmodule DefinedInsideATest")?.presentation?.locationString
        )

        assertEquals(
            "and the `test` node itself is qualified by the module",
            "/src/case_test.exs defmodule CaseTest",
            tree.at("test \"at the top level, outside any describe\"")?.presentation?.locationString
        )
    }

    /** `test` is arity 1..3, so a message-less one has no argument to put in the label. */
    fun testAMessageLessTestIsLabelledWithoutANullMessage() {
        val tree = build()

        assertNotNull(
            "a message-less `test` must still get a node; built nodes were ${tree.byLabel.keys}",
            tree.at("test")
        )
        assertInstanceOf(tree.at("test"), TestElement::class.java)
        assertTrue(
            "no node may be labelled with an interpolated null; built nodes were ${tree.byLabel.keys}",
            tree.byLabel.keys.none { it.contains("null") }
        )
    }

    /** Each of these builds an element that casts its parent's presentation to `Parent` unguarded. */
    fun testDeclarationsInsideADescribeBuildTheirOwnNodes() {
        val tree = build()

        // Scoped to the describe's subtree, not the file: a module-level `defstruct` added later for
        // something else would otherwise keep this green.
        val describe = tree.at("describe \"a describe block\"")
        assertNotNull("the describe node must exist for this to assert anything", describe)

        val built = mutableSetOf<Class<*>>()

        fun collect(element: StructureViewTreeElement) {
            built.add(element.javaClass)

            for (child in element.children) {
                if (child is StructureViewTreeElement) {
                    collect(child)
                }
            }
        }

        for (child in describe!!.children) {
            if (child is StructureViewTreeElement) {
                collect(child)
            }
        }

        for (element in listOf(Structure::class.java, Type::class.java, Callback::class.java, Delegation::class.java)) {
            assertTrue(
                "`${element.simpleName}` must be built inside a `describe`, or its unguarded `as Parent` " +
                    "cast goes unreached; built element classes were ${built.map { it.simpleName }}",
                element in built
            )
        }

        // The struct is compiled onto the case module, so it is named after it and not after the
        // `describe` it is written in.
        assertEquals(
            "%CaseTest{}",
            describe.children.filterIsInstance<Structure>().single().presentation.presentableText
        )

        // By node, not class: `Callback.getChildren()` supplies the class regardless of the `@spec`.
        assertNotNull(
            "the `@spec` inside the `describe` must build its own node; built nodes were ${tree.byLabel.keys}",
            tree.at("a_spec/0")
        )
    }

    /**
     * A `test` walks its body as a `def` clause does, so an unmodelled call builds nothing rather than
     * an [Unknown]. Its `for` and `case` are claimed by `unknown` in the modular dispatch and never
     * reach this walk.
     */
    fun testATestShowsNoNodeForItsControlFlow() {
        val tree = build()

        val test = tree.at("test \"at the top level, outside any describe\"")
        assertNotNull("the top-level test node must exist for this to assert anything", test)

        val unmodelled = test!!.children
            .filterIsInstance<Unknown>()
            .map { it.presentation.presentableText }

        assertEquals("a test's control flow must build no node", emptyList<String?>(), unmodelled)

        assertNotNull(
            "but its declarations must survive; built nodes were ${tree.byLabel.keys}",
            tree.at("defmodule DefinedInsideATest")
        )
    }

    /** No `do` block, so `Unknown.is` is false and nothing else matches it either. */
    fun testABodyLessTestIsStillBuilt() {
        val tree = build()

        val element = tree.at("test \"not implemented yet\"")

        assertNotNull(
            "a `test` with no do block must still get a node; built nodes were ${tree.byLabel.keys}",
            element
        )
        assertInstanceOf(element, TestElement::class.java)
    }

    /**
     * `CallDefinitionHead.is` is `call is UnqualifiedParenthesesCall<*>`, which this call satisfies, and
     * its entry sits above `unknown` carrying no handler. Without the filter `build` selects it and
     * throws on `entry.handle!!`.
     */
    fun testAHandlerLessEntryDoesNotSwallowACallAWiderEntryBuilds() {
        val tree = build()

        val call = PsiTreeUtil
            .findChildrenOfType(myFixture.file, Call::class.java)
            .firstOrNull { it.functionName() == "unrecognised_macro" }

        assertNotNull("the fixture must contain the call or this test proves nothing", call)

        // Control: without it this asserts only that some unremarkable call built an `Unknown`.
        assertTrue(
            "the handler-less entry's predicate must actually match the fixture call",
            ChildCall.ENTRIES.single { it.name == "call definition head" }.matches(call!!, ResolveState.initial())
        )

        assertNotNull(
            "an unqualified parenthesised call with a do block must still reach `unknown`; built nodes " +
                "were ${tree.byLabel.keys}",
            tree.at("unrecognised_macro")
        )
    }

    /** [Model.isSuitable] consulting the flag, rather than `ChildCallTest` reading it off the table. */
    fun testAnOrIsNotACaretSyncTarget() {
        val tree = build()

        val or = PsiTreeUtil
            .findChildrenOfType(myFixture.file, Or::class.java)
            .firstOrNull { it.parent is org.elixir_lang.psi.ElixirStabBody }
            ?: PsiTreeUtil.findChildrenOfType(myFixture.file, Or::class.java).firstOrNull()

        assertNotNull("the fixture must contain an `or` or this test proves nothing", or)

        val call = or as Call

        // Control: nothing else matches a bare `or`, so the negatives below would pass without it.
        assertTrue(
            "the `or` entry's predicate must actually match an `or`",
            ChildCall.ENTRIES.single { it.name == "or" }.matches(call, ResolveState.initial())
        )

        assertFalse(
            "an `or` is unwrapped by the dispatch, so the caret must not sync to it",
            Model.isSuitable(call)
        )
        assertFalse("and it must build no node of its own", call in tree.values)
    }

    /**
     * Both directions, and by node kind: drop the `ex_unit test` entry and a `test` is still claimed and
     * still gets a node, because `unknown` matches it and carries the same call as its value.
     */
    fun testCaretSyncTargetsInACaseAreTheNodesTheTreeBuilt() {
        val tree = build()

        val caseChildren = PsiTreeUtil
            .findChildrenOfType(myFixture.file, Call::class.java)
            .filter { call -> Case.isChild(call, ResolveState.initial()) }

        assertFalse(
            "the fixture must contain ExUnit.Case children or this test proves nothing",
            caseChildren.isEmpty()
        )

        val unclaimed = caseChildren.filterNot { call -> Model.isSuitable(call) }.map { it.text.lineSequence().first() }
        assertEquals("Model.isSuitable must claim every ExUnit.Case child", emptyList<String>(), unclaimed)

        val unbuilt = caseChildren.filterNot { call -> call in tree.values }.map { it.text.lineSequence().first() }
        assertEquals("the tree must build a node for every call isSuitable claims", emptyList<String>(), unbuilt)

        val wrongType = caseChildren
            .filterNot { call -> tree.elementByValue[call].let { it is TestElement || it is DescribeElement } }
            .map { it.text.lineSequence().first() }
        assertEquals(
            "an ExUnit.Case child must build its own node kind, not fall through to Unknown",
            emptyList<String>(),
            wrongType
        )
    }
}
