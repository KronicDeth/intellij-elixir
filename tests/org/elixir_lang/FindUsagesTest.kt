package org.elixir_lang

import com.intellij.openapi.application.ex.ApplicationInfoEx
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.usages.UsageInfo2UsageAdapter

class FindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/find_usages"
    }

    fun testFunctionRecursiveDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("function_recursive_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(3, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(63, 93))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (3)
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
                usageViewTreeTextRepresentation)
    }

    fun testFunctionRecursiveUsage() {
        val usages = myFixture.testFindUsagesUsingAction("function_recursive_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(3, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(63, 93))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (3)
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
                usageViewTreeTextRepresentation)
    }

    fun testFunctionSingleClauseUnused() {
        val usages = myFixture.testFindUsagesUsingAction("function_single_clause_unused.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(1, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(26))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (1)
 ${usages()} (1)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_single_clause_unused.ex (1)
      2def function, do: :ok
""",
                usageViewTreeTextRepresentation)
    }

    fun testFunctionMultipleClausesUnused() {
        val usages = myFixture.testFindUsagesUsingAction("function_multiple_clauses_unused.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(26))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Call definition clause (2)
   light_idea_test_case (2)
     (2)
     function_multiple_clauses_unused.ex (2)
      2def function(list) when is_list(list), do: []
      3def function(map) when is_map(map), do: %{}
""",
                usageViewTreeTextRepresentation)
    }

    fun testFunctionMultipleModulesDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("function_multiple_modules_declaration_target.ex", "function_multiple_modules_declaration_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(31, 50))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_multiple_modules_declaration_target.ex (1)
      2def declaration, do: :ok
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_multiple_modules_declaration_usage.ex (1)
      3Declaration.declaration()
""",
                usageViewTreeTextRepresentation)
    }

    fun testFunctionMultipleModulesUsage() {
        val usages = myFixture.testFindUsagesUsingAction("function_multiple_modules_usage_target.ex", "function_multiple_modules_usage_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(31, 50))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_multiple_modules_usage_declaration.ex (1)
      2def declaration, do: :ok
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_multiple_modules_usage_target.ex (1)
      3Declaration.declaration()
""",
                usageViewTreeTextRepresentation)
    }

    fun testFunctionImportDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("function_import_declaration_target.ex", "function_import_declaration_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(31, 60))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_import_declaration_target.ex (1)
      2def declaration, do: :ok
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_import_declaration_usage.ex (1)
      5declaration()
""",
                usageViewTreeTextRepresentation)
    }

    fun testFunctionImportUsage() {
        myFixture.configureByFiles("function_import_usage_target.ex", "function_import_usage_declaration.ex")

        val reference = myFixture.getReferenceAtCaretPositionWithAssertion() as PsiPolyVariantReference
        assertNotNull(reference)

        val resolved = reference.multiResolve(false)
        assertEquals(2, resolved.size)

        val usageInfos = myFixture.findUsages(resolved[0].element!!)
        assertEquals(2, usageInfos.size)
        assertContainsElements( usageInfos.map { it.element!!.textOffset }, listOf(31, 60))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals("""<root> (2)
 ${usages()} (2)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     function_import_usage_declaration.ex (1)
      2def declaration, do: :ok
  Value read (1)
   light_idea_test_case (1)
     (1)
     function_import_usage_target.ex (1)
      5declaration()
""",
                usageViewTreeTextRepresentation)
    }

    fun testParameterDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("parameter_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(39, 70))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Parameter declaration (1)
   light_idea_test_case (1)
     (1)
     parameter_declaration.ex (1)
      2defp function(parameter) do
  Value read (1)
   light_idea_test_case (1)
     (1)
     parameter_declaration.ex (1)
      3%{parameter: parameter}
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleRecursiveDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("module_recursive_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(10, 33))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Alias (1)
   light_idea_test_case (1)
     (1)
     module_recursive_declaration.ex (1)
      2alias Declaration
  Module definition (1)
   light_idea_test_case (1)
     (1)
     module_recursive_declaration.ex (1)
      1defmodule Declaration do
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleRecursiveUsage() {
        myFixture.configureByFile("module_recursive_usage.ex")

        val reference = myFixture.getReferenceAtCaretPositionWithAssertion() as PsiPolyVariantReference
        assertNotNull(reference)

        val resolved = reference.multiResolve(false)
        assertEquals(2, resolved.size)

        val usageInfos = myFixture.findUsages(resolved[0].element!!)
        assertEquals(2, usageInfos.size)
        assertContainsElements( usageInfos.map { it.element!!.textOffset }, listOf(10, 33))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals("""<root> (2)
 ${usages()} (2)
  Alias (1)
   light_idea_test_case (1)
     (1)
     module_recursive_usage.ex (1)
      2alias Declaration
  Module definition (1)
   light_idea_test_case (1)
     (1)
     module_recursive_usage.ex (1)
      1defmodule Declaration do
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleNestedRecursiveDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("module_nested_recursive_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(3, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(10, 40, 75))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (3)
 ${usages()} (3)
  Alias (2)
   light_idea_test_case (2)
     (2)
     module_nested_recursive_declaration.ex (2)
      2alias Parent.Declaration
      3alias Parent.{Declaration}
  Module definition (1)
   light_idea_test_case (1)
     (1)
     module_nested_recursive_declaration.ex (1)
      1defmodule Parent.Declaration do
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleMultipleModulesDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("module_multiple_modules_declaration_target.ex", "module_multiple_modules_declaration_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(10, 27))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Alias (1)
   light_idea_test_case (1)
     (1)
     module_multiple_modules_declaration_usage.ex (1)
      2alias Declaration
  Module definition (1)
   light_idea_test_case (1)
     (1)
     module_multiple_modules_declaration_target.ex (1)
      1defmodule Declaration do
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleMultipleModulesUsage() {
        myFixture.configureByFiles("module_multiple_modules_usage_target.ex", "module_multiple_modules_usage_declaration.ex")

        val reference = myFixture.getReferenceAtCaretPositionWithAssertion() as PsiPolyVariantReference
        assertNotNull(reference)

        val resolved = reference.multiResolve(false)
        assertEquals(2, resolved.size)

        val usageInfos = myFixture.findUsages(resolved[0].element!!)
        assertEquals(2, usageInfos.size)
        assertContainsElements( usageInfos.map { it.element!!.textOffset }, listOf(10, 27))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usageInfos)

        assertEquals("""<root> (2)
 ${usages()} (2)
  Alias (1)
   light_idea_test_case (1)
     (1)
     module_multiple_modules_usage_target.ex (1)
      2alias Declaration
  Module definition (1)
   light_idea_test_case (1)
     (1)
     module_multiple_modules_usage_declaration.ex (1)
      1defmodule Declaration do
""",
                usageViewTreeTextRepresentation)
    }

    fun testParameterUnused() {
        val usages = myFixture.testFindUsagesUsingAction("parameter_unused.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(1, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(39))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (1)
 ${usages()} (1)
  Parameter declaration (1)
   light_idea_test_case (1)
     (1)
     parameter_unused.ex (1)
      2defp function(_parameter) do
""",
                usageViewTreeTextRepresentation)
    }

    fun testParameterUsage() {
        val usages = myFixture.testFindUsagesUsingAction("parameter_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(39))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Parameter declaration (1)
   light_idea_test_case (1)
     (1)
     parameter_usage.ex (1)
      2defp function(parameter) do
  Value read (1)
   light_idea_test_case (1)
     (1)
     parameter_usage.ex (1)
      3%{parameter: parameter}
""",
                usageViewTreeTextRepresentation)
    }

    fun testVariableDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("variable_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(46, 99))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Value read (1)
   light_idea_test_case (1)
     (1)
     variable_declaration.ex (1)
      5variable
  Value write (1)
   light_idea_test_case (1)
     (1)
     variable_declaration.ex (1)
      3variable = Application.get_env(:variable, :key)
""",
                usageViewTreeTextRepresentation)
    }

    fun testVariableUnused() {
        val usages = myFixture.testFindUsagesUsingAction("variable_unused.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(1, usages.size)

        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(46))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (1)
 ${usages()} (1)
  Value write (1)
   light_idea_test_case (1)
     (1)
     variable_unused.ex (1)
      3variable = Application.get_env(:variable, :key)
""",
                usageViewTreeTextRepresentation)
    }

    fun testVariableUsage() {
        val usages = myFixture.testFindUsagesUsingAction("variable_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(46))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Value read (1)
   light_idea_test_case (1)
     (1)
     variable_usage.ex (1)
      5variable
  Value write (1)
   light_idea_test_case (1)
     (1)
     variable_usage.ex (1)
      3variable = Application.get_env(:variable, :key)
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleAttributeDeclaration() {
        val usages = myFixture.testFindUsagesUsingAction("module_attribute_declaration.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(31, 69))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Module attribute accumulate or override (1)
   light_idea_test_case (1)
     (1)
     module_attribute_declaration.ex (1)
      2@module_attribute 1
  Module attribute read (1)
   light_idea_test_case (1)
     (1)
     module_attribute_declaration.ex (1)
      4def usage, do: @module_attribute
""",
                usageViewTreeTextRepresentation)
    }

    fun testModuleAttributeUsage() {
        val usages = myFixture.testFindUsagesUsingAction("module_attribute_usage.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(2, usages.size)
        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(31, 69))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (2)
 ${usages()} (2)
  Module attribute accumulate or override (1)
   light_idea_test_case (1)
     (1)
     module_attribute_usage.ex (1)
      2@module_attribute 1
  Module attribute read (1)
   light_idea_test_case (1)
     (1)
     module_attribute_usage.ex (1)
      4def usage, do: @module_attribute
""",
                usageViewTreeTextRepresentation)
    }

    fun testIssue2374() {
        val usages = myFixture.testFindUsagesUsingAction("issue_2374.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(3, usages.size)

        usages.map { it.element!!.textOffset }.sorted()

        assertContainsElements( usages.map { it.element!!.textOffset }, listOf(23, 53, 53))

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (3)
 ${usages()} (3)
  Call definition clause (3)
   light_idea_test_case (3)
     (3)
     issue_2374.ex (3)
      2def foo, do: "foo"
      3def bar, do: foo()
      4def baz, do: foo()
""",
                usageViewTreeTextRepresentation)
    }

    fun testDefUsage() {
        val usages = myFixture.testFindUsagesUsingAction("def_usage.ex", "kernel.ex").map { it as UsageInfo2UsageAdapter }

        assertEquals(98, usages.size)

        val usageViewTreeTextRepresentation = myFixture.getUsageViewTreeTextRepresentation(usages.map { it.usageInfo })

        assertEquals("""<root> (98)
 ${usages()} (98)
  Call definition clause (1)
   light_idea_test_case (1)
     (1)
     kernel.ex (1)
      619defmacro def(call, expr) do
  Function call (97)
   light_idea_test_case (97)
     (97)
     def_usage.ex (1)
      2def usage, do: :ok
     kernel.ex (96)
      2678def left != right do
      2702def left !== right do
      2718def left * right do
      2734def (+value) do
      2750def left + right do
      2788def left ++ right do
      2804def (-value) do
      2820def left - right do
      2861def left -- right do
      2891def left / right do
      2911def left < right do
      2931def left <= right do
      2955def left == right do
      2983def left === right do
      3020def left =~ "" when is_binary(left) do
      3024def left =~ right when is_binary(left) and is_binary(right) do
      3028def left =~ right when is_binary(left) do
      3048def left > right do
      3068def left >= right do
      3072def __info__(p0) do
      3091def abs(number) do
      3108def apply(fun, args) do
      3129def apply(module, function_name, args) do
      3155def binary_part(binary, start, length) do
      3174def bit_size(bitstring) do
      3197def byte_size(bitstring) do
      3210def ceil(number) do
      3244def div(dividend, divisor) do
      3269def elem(tuple, index) do
      3335def exit(reason) do
      3348def floor(number) do
      3374def function_exported?(module, function, arity) do
      3442def get_and_update_in(data, [head], fun) when :erlang.is_function(head, 3) do
      3446def get_and_update_in(data, [head | tail], fun) when :erlang.is_function(head, 3) do
      3450def get_and_update_in(data, [head], fun) when :erlang.is_function(fun, 1) do
      3454def get_and_update_in(data, [head | tail], fun) when :erlang.is_function(fun, 1) do
      3504def get_in(data, [h]) when :erlang.is_function(h) do
      3508def get_in(data, [h | t]) when :erlang.is_function(h) do
      3512def get_in(nil, [_]) do
      3516def get_in(nil, [_ | t]) do
      3520def get_in(data, [h]) do
      3524def get_in(data, [h | t]) do
      3548def hd(list) do
      3552def inspect(x0) do
      3612def inspect(term, opts) when is_list(opts) do
      3632def is_atom(term) do
      3652def is_binary(term) do
      3670def is_bitstring(term) do
      3681def is_boolean(term) do
      3691def is_float(term) do
      3701def is_function(term) do
      3720def is_function(term, arity) do
      3730def is_integer(term) do
      3740def is_list(term) do
      3750def is_map(term) do
      3762def is_map_key(map, key) do
      3773def is_number(term) do
      3783def is_pid(term) do
      3793def is_port(term) do
      3803def is_reference(term) do
      3813def is_tuple(term) do
      3829def length(list) do
      3854def macro_exported?(module, macro, arity) when is_atom(module) and is_atom(macro) and is_integer(arity) and (arity >= 0 and arity <= 255) do
      3873def make_ref() do
      3893def map_size(map) do
      3924def max(first, second) do
      3955def min(first, second) do
      3959def module_info() do
      3963def module_info(p0) do
      3974def node() do
      3986def node(arg) do
      4004def not(value) do
      4031def pop_in(nil, [key | _]) do
      4035def pop_in(data, [_ | _] = keys) do
      4052def put_elem(tuple, index, value) do
      4074def put_in(data, [_ | _] = keys, value) do
      4098def rem(dividend, divisor) do
      4131def round(number) do
      4141def self() do
      4161def send(dest, message) do
      4190def spawn(fun) do
      4213def spawn(module, fun, args) do
      4243def spawn_link(fun) do
      4267def spawn_link(module, fun, args) do
      4293def spawn_monitor(fun) do
      4316def spawn_monitor(module, fun, args) do
      4320def struct(x0) do
      4366def struct(struct, fields) do
      4380def struct!(x0) do
      4404def struct!(struct, fields) when is_atom(struct) do
      4408def struct!(struct, fields) when is_map(struct) do
      4425def throw(term) do
      4455def tl(list) do
      4477def trunc(number) do
      4495def tuple_size(tuple) do
      4523def update_in(data, [_ | _] = keys, fun) when :erlang.is_function(fun) do
""",
                usageViewTreeTextRepresentation)
    }

    private fun usages(): String =
        if (ApplicationInfoEx.getInstance().fullVersion == "2021.1.3") {
            "Found usages"
        } else {
            "Usages in"
        }
}
