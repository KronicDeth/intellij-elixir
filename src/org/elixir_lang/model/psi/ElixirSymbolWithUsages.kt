package org.elixir_lang.model.psi

import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

/**
 * An [ElixirSymbol] that can be searched for. [ElixirSymbolUsageSearcher] dispatches on this type.
 *
 * @property file the file containing the declaring occurrence
 * @property range absolute range (in [file]) of the declaring occurrence, used for navigation and
 *   the self-declaration usage
 * @property searchText the bare name to anchor a text search on (e.g. `handle_call`)
 */
interface ElixirSymbolWithUsages : ElixirSymbol {
    val file: PsiFile
    val range: TextRange
    val searchText: String
}
