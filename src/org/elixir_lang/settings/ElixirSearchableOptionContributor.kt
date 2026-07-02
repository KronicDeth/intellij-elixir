package org.elixir_lang.settings

import com.intellij.ide.ui.search.SearchableOptionContributor
import com.intellij.ide.ui.search.SearchableOptionProcessor

/**
 * Keeps Elixir settings discoverable in search without requiring Elixir-prefixed labels.
 */
internal class ElixirSearchableOptionContributor : SearchableOptionContributor() {
    override fun processOptions(processor: SearchableOptionProcessor) {
        addAliases(
            processor = processor,
            configurableId = "language.elixir",
            configurableDisplayName = "Elixir",
            hit = "Elixir",
            text = "elixir language framework settings"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir.credo",
            configurableDisplayName = "Credo",
            hit = "Credo",
            text = "elixir credo lint linter"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir.dialyzer",
            configurableDisplayName = "Dialyzer",
            hit = "Dialyzer",
            text = "elixir dialyzer typespec static analysis"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir",
            configurableDisplayName = "Elixir",
            hit = "Elixir",
            text = "elixir settings liveview heex sigil injection mix deps"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir.sdks.elixir",
            configurableDisplayName = "SDKs",
            hit = "SDKs",
            text = "elixir sdk interpreter"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir.sdks.erlang",
            configurableDisplayName = "Internal Erlang SDKs",
            hit = "Internal Erlang SDKs",
            text = "elixir erlang sdk otp"
        )

        addAliases(
            processor = processor,
            configurableId = "language.elixir.tool_managers",
            configurableDisplayName = "Tool Managers",
            hit = "Tool Managers",
            text = "elixir tool manager mise asdf sdk version automatic configure experimental"
        )
    }

    private fun addAliases(
        processor: SearchableOptionProcessor,
        configurableId: String,
        configurableDisplayName: String,
        hit: String,
        text: String
    ) {
        processor.addOptions(text, null, hit, configurableId, configurableDisplayName, true)
    }
}
