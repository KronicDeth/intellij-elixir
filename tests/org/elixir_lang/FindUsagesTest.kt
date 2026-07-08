package org.elixir_lang

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.UsageOptions
import com.intellij.find.usages.impl.AllSearchOptions
import com.intellij.find.usages.impl.buildQuery
import com.intellij.find.usages.impl.searchTargets
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.openapi.util.Version
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.usages.UsageInfo2UsageAdapter
import org.elixir_lang.find_usages.handler.AlreadyResolved
import java.util.concurrent.Callable

@Suppress("UnstableApiUsage")
class FindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/find_usages"
    }

    fun testFunctionRecursiveDeclaration() {
        if (AlreadyResolved.alreadyResolved) {
            val offsets = functionSymbolUsageOffsetsAtCaret("function_recursive_declaration.ex", "kernel.ex")
            assertEquals(3, offsets.size)
            assertContainsElements(offsets, listOf(63, 93))
            return
        }
        val usageInfos = myFixture.testFindUsages("function_recursive_declaration.ex", "kernel.ex").toList()

        assertEquals(3, usageInfos.size)
        assertContainsElements(usageInfos.map { it.element!!.textOffset }, listOf(63, 93))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals(
            """<root> (3)
 ${usages()} (3)
  Call definition clause (2)
   light_idea_test_case (2)
     (2)
     function_recursive_declaration.ex (2)
      2def function([], acc), do: acc
      4def function([h | t], acc) do
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_recursive_declaration.ex (1)
      5function(t, [h | acc])
""",
            usageViewTreeTextRepresentation
        )
    }

    fun testFunctionRecursiveUsage() {
        if (AlreadyResolved.alreadyResolved) {
            val offsets = functionSymbolUsageOffsetsAtCaret("function_recursive_usage.ex", "kernel.ex")
            assertEquals(3, offsets.size)
            assertContainsElements(offsets, listOf(63, 93))
            return
        }
        val usageInfos = myFixture.testFindUsages("function_recursive_usage.ex", "kernel.ex").toList()

        assertEquals(3, usageInfos.size)
        assertContainsElements(usageInfos.map { it.element!!.textOffset }, listOf(63, 93))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals(
            """<root> (3)
 ${usages()} (3)
  Call definition clause (2)
   light_idea_test_case (2)
     (2)
     function_recursive_usage.ex (2)
      2def function([], acc), do: acc
      4def function([h | t], acc) do
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_recursive_usage.ex (1)
      5function(t, [h | acc])
""",
            usageViewTreeTextRepresentation
        )
    }

    fun testFunctionSingleClauseUnused() {
        if (AlreadyResolved.alreadyResolved) {
            val offsets = functionSymbolUsageOffsetsAtCaret("function_single_clause_unused.ex", "kernel.ex")
            assertEquals(1, offsets.size)
            assertContainsElements(offsets, listOf(26))
            return
        }
        val usageInfos = myFixture.testFindUsages("function_single_clause_unused.ex", "kernel.ex").toList()

        assertEquals(1, usageInfos.size)
        assertContainsElements(usageInfos.map { it.element!!.textOffset }, listOf(26))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals(
            """<root> (1)
 ${usages()} (1)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_single_clause_unused.ex (1)
      2def function, do: :ok
""",
            usageViewTreeTextRepresentation
        )
    }

    fun testFunctionMultipleClausesUnused() {
        if (AlreadyResolved.alreadyResolved) {
            val offsets = functionSymbolUsageOffsetsAtCaret("function_multiple_clauses_unused.ex", "kernel.ex")
            assertEquals(2, offsets.size)
            assertContainsElements(offsets, listOf(26))
            return
        }
        val usageInfos = myFixture.testFindUsages("function_multiple_clauses_unused.ex", "kernel.ex").toList()

        assertEquals(2, usageInfos.size)
        assertContainsElements(usageInfos.map { it.element!!.textOffset }, listOf(26))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals(
            """<root> (2)
 ${usages()} (2)
  Call definition clause (2)
   light_idea_test_case (2)
     (2)
     function_multiple_clauses_unused.ex (2)
      2def function(list) when is_list(list), do: []
      3def function(map) when is_map(map), do: %{}
""",
            usageViewTreeTextRepresentation
        )
    }

    fun testFunctionMultipleModulesDeclaration() {
        val offsets = usageOffsetsAtCaret(
            "function_multiple_modules_declaration_target.ex",
            "function_multiple_modules_declaration_usage.ex",
            "kernel.ex"
        )
        // Declaration-side SearchTarget coverage for cross-module qualified dispatch is asserted in
        // testFunctionMultipleModulesUsage; here we keep declaration anchor behavior.
        assertTrue("Expected declaration usage at offset 31", offsets.contains(31))
    }

    fun testFunctionMultipleModulesUsage() {
        val offsets = usageOffsetsAtCaret(
            "function_multiple_modules_usage_target.ex",
            "function_multiple_modules_usage_declaration.ex",
            "kernel.ex"
        )
        assertTrue(
            "Expected cross-module qualified call-site usage at offset 50",
            offsets.contains(50)
        )
    }

    fun testFunctionImportDeclaration() {
        val offsets = usageOffsetsAtCaret(
            "function_import_declaration_target.ex",
            "function_import_declaration_usage.ex",
            "kernel.ex"
        )
        // Imported call-site coverage is asserted in testFunctionImportUsage; keep declaration anchor behavior here.
        assertTrue("Expected declaration usage at offset 31", offsets.contains(31))
    }

    fun testFunctionImportUsage() {
        val offsets = usageOffsetsAtCaret(
            "function_import_usage_target.ex",
            "function_import_usage_declaration.ex",
            "kernel.ex"
        )
        assertTrue(
            "Expected imported unqualified call-site usage at offset 60",
            offsets.contains(60)
        )
    }

    fun testParameterDeclaration() {
        val offsets = usageOffsetsAtCaret("parameter_declaration.ex", "kernel.ex")
        assertEquals(2, offsets.size)
        assertContainsElements(offsets, listOf(39, 70))
    }

    fun testParameterUnused() {
        val offsets = usageOffsetsAtCaret("parameter_unused.ex", "kernel.ex")
        assertEquals(1, offsets.size)
        assertContainsElements(offsets, listOf(39))
    }

    fun testParameterUsage() {
        val offsets = usageOffsetsAtCaret("parameter_usage.ex", "kernel.ex")
        assertEquals(2, offsets.size)
        assertContainsElements(offsets, listOf(39))
    }

    fun testVariableDeclaration() {
        val offsets = usageOffsetsAtCaret("variable_declaration.ex", "kernel.ex")
        assertEquals(2, offsets.size)
        assertContainsElements(offsets, listOf(46, 99))
    }

    fun testVariableUnused() {
        val offsets = usageOffsetsAtCaret("variable_unused.ex", "kernel.ex")
        assertEquals(1, offsets.size)
        assertContainsElements(offsets, listOf(46))
    }

    fun testVariableUsage() {
        val offsets = usageOffsetsAtCaret("variable_usage.ex", "kernel.ex")
        assertEquals(2, offsets.size)
        assertContainsElements(offsets, listOf(46))
    }

    fun testModuleAttributeDeclaration() {
        val usages = myFixture.testFindUsages("module_attribute_declaration.ex", "kernel.ex")
            .map { UsageInfo2UsageAdapter(it) }

        assertEquals(2, usages.size)
        assertContainsElements(usages.map { it.element!!.textOffset }, listOf(31, 69))
    }

    fun testModuleAttributeUsage() {
        val usages = myFixture.testFindUsages("module_attribute_usage.ex", "kernel.ex")
            .map { UsageInfo2UsageAdapter(it) }

        assertEquals(2, usages.size)
        assertContainsElements(usages.map { it.element!!.textOffset }, listOf(31, 69))
    }

    fun testIssue2374() {
        if (AlreadyResolved.alreadyResolved) {
            val offsets = functionSymbolUsageOffsetsAtCaret("issue_2374.ex", "kernel.ex")
            assertEquals(3, offsets.size)
            assertContainsElements(offsets, listOf(23, 53, 53))
            return
        }
        val usages = myFixture.testFindUsages("issue_2374.ex", "kernel.ex").map { UsageInfo2UsageAdapter(it) }

        assertEquals(3, usages.size)

        usages.map { it.element!!.textOffset }.sorted()

        assertContainsElements(usages.map { it.element!!.textOffset }, listOf(23, 53, 53))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals(
            """<root> (3)
 ${usages()} (3)
  Call definition clause (3)
   light_idea_test_case (3)
     (3)
     issue_2374.ex (3)
      2def foo, do: "foo"
      3def bar, do: foo()
      4def baz, do: foo()
""",
            usageViewTreeTextRepresentation
        )
    }

    private fun usages(): String {
        val ideVersion = Version.parseVersion(ApplicationInfoEx.getInstance().fullVersion)
        val usagesString = if (ideVersion === null || ideVersion.lessThan(2021,2,0)) {
            "Found usages"
        } else if (ideVersion.lessThan(2024,1,0)) {
            "Usages in"
        } else {
            "Usages"
        }
        return usagesString
    }

    private fun functionSymbolUsageOffsetsAtCaret(vararg fileNames: String): kotlin.collections.List<Int> {
        return usageOffsetsAtCaret(*fileNames)
    }

    private fun usageOffsetsAtCaret(vararg fileNames: String): kotlin.collections.List<Int> {
        myFixture.configureByFiles(*fileNames)
        val file = myFixture.file
        val offset = myFixture.caretOffset
        val allOptions = AllSearchOptions(
            UsageOptions.createOptions(GlobalSearchScope.allScope(project)),
            textSearch = false
        )
        return ApplicationManager.getApplication().executeOnPooledThread(Callable {
            ReadAction.nonBlocking(Callable {
                val targets = searchTargets(file, offset)
                if (targets.isEmpty()) {
                    emptyList()
                } else {
                    buildQuery(project, targets.single(), allOptions).findAll()
                        .filterIsInstance<PsiUsage>()
                        .map { it.range.startOffset }
                }
            }).executeSynchronously()
        }).get()
    }
}
