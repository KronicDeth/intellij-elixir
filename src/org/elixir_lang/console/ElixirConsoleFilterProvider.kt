package org.elixir_lang.console

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

/**
 * Installs the Elixir console filters on every console the IDE builds, not just the ones this
 * plugin's own run configurations build.
 *
 * The filters used to be attached per run state, which meant `iex -S mix`, `mix test` and
 * `mix phx.server` started from the Terminal tool window got no Elixir stack-trace linking at all -
 * the Terminal builds its console from this extension point, and nothing of ours was registered on
 * it. On the Reworked terminal engine this also links a wrapped entry to its exact line rather than
 * to the top of the file, because that pipeline hands filters document offsets.
 *
 * This is now the only place either filter is installed. A console computes these filters itself -
 * [com.intellij.execution.impl.ConsoleViewImpl] via `updatePredefinedFiltersLater`, the terminal via
 * [com.intellij.terminal.CompositeFilterWrapper] - so attaching them per run state as well would run
 * each filter twice and put a duplicate hyperlink over every path.
 *
 * ### Why there is no "is this an Elixir project" gate
 *
 * There was one, and it was wrong. A terminal computes its filter set once and keeps it: both
 * `CompositeFilterWrapper`s cache the composite and rebuild it only when the extension point's own
 * list changes, unlike [com.intellij.execution.impl.ConsoleViewImpl], which recomputes on module,
 * root, facet and dumb-mode events. A gated provider therefore answered for the lifetime of a
 * terminal session from whatever the project looked like when its first line arrived - so a tab
 * restored before the Mix import finished, or open when the SDK was configured, silently got no
 * linking at all until it was closed.
 *
 * Answering unconditionally costs a project without Elixir in it two regular expressions per console
 * line, both of which fail before anything is looked up: [LiteralStackTraceFilter] needs
 * `file: … , line: …` and [FileReferenceFilter] needs a path followed by `:` and digits. It also
 * keeps a read action out of console construction, which the gate needed for the module and SDK
 * models.
 */
internal class ElixirConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> =
        arrayOf(
            LiteralStackTraceFilter(project),
            FileReferenceFilter(project, FileReferenceFilter.COMPILATION_ERROR_PATH),
        )
}
