package org.elixir_lang.model.psi.function_arity_keyword_pair

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.code_insight.assertShowUsagesChosenAtCaret
import org.elixir_lang.code_insight.singleTargetPsiUsagesAtCaret
import org.elixir_lang.psi.QuotableKeywordPair
import com.intellij.psi.util.PsiTreeUtil

/**
 * Behavior-level tests for forward Find/Show Usages on the `name: arity` keyword-pair construct:
 * invoking Find Usages on a function/macro `def` (or, for `defoverridable`, on the `@callback`) lists
 * the keyword-**key** occurrences that name it inside `import :only`/`:except`, `@compile :inline`,
 * `@dialyzer`, and `defoverridable` directives.
 *
 * Drives the SAME production search pipeline the Find Usages action uses - the [com.intellij.find.usages.impl.searchTargets]
 * resolved at the caret fed through [com.intellij.find.usages.impl.buildQuery] (which runs `ElixirSymbolUsageSearcher` and yields
 * the very [com.intellij.find.usages.api.PsiUsage]s the tool window would display) - exactly like
 * [org.elixir_lang.model.psi.function.FunctionFindUsagesTest]. Assertions never touch internal
 * resolver classes, so they survive refactoring.
 */
class FindUsagesTest : PlatformTestCase() {
    override fun getTestDataPath(): String =
        "testData/org/elixir_lang/model/psi/function_arity_keyword_pair"

    fun testImportOnlyKeyIsFoundAmongFunctionUsages() {
        assertTrue(
            "Expected the `only: [perform: 0]` keyword key among the function's usages",
            keywordKeyUsageCount("usages_import_only.ex") >= 1
        )
    }

    fun testCompileInlineKeyIsFoundAmongFunctionUsages() {
        assertTrue(
            "Expected the `@compile inline: [perform: 0]` keyword key among the function's usages",
            keywordKeyUsageCount("usages_compile_inline.ex") >= 1
        )
    }

    fun testDialyzerNowarnKeyIsFoundAmongFunctionUsages() {
        assertTrue(
            "Expected the `@dialyzer {:nowarn_function, perform: 0}` keyword key among the function's usages",
            keywordKeyUsageCount("usages_dialyzer_nowarn.ex") >= 1
        )
    }

    fun testDefoverridableKeyIsFoundAmongCallbackUsages() {
        assertTrue(
            "Expected the `defoverridable perform: 0` keyword key among the callback's usages",
            keywordKeyUsageCount("usages_defoverridable.ex", "kernel.ex") >= 1
        )
    }

    /**
     * Runs Find Usages on the symbol at the caret in [files] and counts non-declaration usages whose
     * location is inside the **key** of a [QuotableKeywordPair] - i.e. a `name: arity` keyword-pair
     * occurrence. Restricting to the key (not the whole pair) avoids matching call sites that merely sit
     * inside a keyword-pair *value* such as `def run, do: perform()`.
     */
    @Suppress("UnstableApiUsage")
    private fun keywordKeyUsageCount(vararg files: String): Int {
        myFixture.configureByFiles(*files)
        myFixture.assertShowUsagesChosenAtCaret()

        return myFixture.singleTargetPsiUsagesAtCaret(project)
            .filterNot { it.declaration }
            .count { usage ->
                val element = usage.file.findElementAt(usage.range.startOffset)
                element != null && generateSequence(element) { it.parent }
                    .filterIsInstance<QuotableKeywordPair>()
                    .any { pair -> PsiTreeUtil.isAncestor(pair.keywordKey, element, false) }
            }
    }
}
