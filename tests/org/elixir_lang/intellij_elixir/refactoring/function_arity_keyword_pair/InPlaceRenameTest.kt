package org.elixir_lang.intellij_elixir.refactoring.function_arity_keyword_pair

import org.elixir_lang.intellij_elixir.refactoring.InPlaceSymbolRenameTestCase

/**
 * Exercises the real IDE Shift+F6 (in-place) rename gesture on the `name: arity` keyword-pair
 * construct - the shared form that names a function/macro by name+arity inside `import :only`/
 * `:except`, `@compile :inline`, and `@dialyzer` directives.
 *
 * Each construct is renamed from **both** carets:
 *  - the function *definition* caret (`def per<caret>form`) - the keyword-pair occurrence must update
 *    along with the def and its call sites, and
 *  - the keyword-*key* caret (`only: [per<caret>form: 0]`) - Shift+F6 must find a rename target there
 *    (otherwise the platform types the new name as literal text and corrupts the file).
 *
 * These drive the inline template and its asynchronous commit - the actual user path.
 */
class InPlaceRenameTest : InPlaceSymbolRenameTestCase() {
    override fun getTestDataPath(): String {
        return "testData/org/elixir_lang/refactoring/function_arity_keyword_pair/rename"
    }

    fun testImportOnlyFromDeclarationInPlaceRename() {
        myFixture.configureByFiles("import_only.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("import_only.ex", "import_only_after.ex", false)
    }

    fun testImportOnlyFromKeywordKeyInPlaceRename() {
        myFixture.configureByFiles("import_only_from_key.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("import_only_from_key.ex", "import_only_after.ex", false)
    }

    fun testImportExceptFromDeclarationInPlaceRename() {
        myFixture.configureByFiles("import_except.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("import_except.ex", "import_except_after.ex", false)
    }

    fun testCompileInlineFromDeclarationInPlaceRename() {
        myFixture.configureByFiles("compile_inline.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("compile_inline.ex", "compile_inline_after.ex", false)
    }

    fun testCompileInlineFromKeywordKeyInPlaceRename() {
        myFixture.configureByFiles("compile_inline_from_key.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("compile_inline_from_key.ex", "compile_inline_after.ex", false)
    }

    fun testCompileInlineTupleFromDeclarationInPlaceRename() {
        myFixture.configureByFiles("compile_inline_tuple.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("compile_inline_tuple.ex", "compile_inline_tuple_after.ex", false)
    }

    fun testCompileInlineTupleFromKeywordKeyInPlaceRename() {
        myFixture.configureByFiles("compile_inline_tuple_from_key.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("compile_inline_tuple_from_key.ex", "compile_inline_tuple_after.ex", false)
    }

    fun testDialyzerNowarnFromDeclarationInPlaceRename() {
        myFixture.configureByFiles("dialyzer_nowarn.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("dialyzer_nowarn.ex", "dialyzer_nowarn_after.ex", false)
    }

    fun testDialyzerNowarnFromKeywordKeyInPlaceRename() {
        myFixture.configureByFiles("dialyzer_nowarn_from_key.ex")

        inPlaceRenameAtCaret("execute")

        myFixture.checkResultByFile("dialyzer_nowarn_from_key.ex", "dialyzer_nowarn_after.ex", false)
    }
}
