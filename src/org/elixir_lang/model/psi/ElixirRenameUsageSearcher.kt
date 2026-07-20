package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.refactoring.rename.api.PsiModifiableRenameUsage
import com.intellij.refactoring.rename.api.RenameUsage
import com.intellij.refactoring.rename.api.RenameUsageSearchParameters
import com.intellij.refactoring.rename.api.RenameUsageSearcher
import com.intellij.util.Query

/**
 * Shared [RenameUsageSearcher] for Elixir [ElixirSymbolWithUsages] targets. A thin shim: it
 * unwraps [RenameUsageSearchParameters], delegates to [ElixirUsageQueries] (the same queries as
 * Find Usages), and maps each [PsiUsage] to a modifiable rename usage.
 */
@Suppress("UnstableApiUsage")
internal class ElixirRenameUsageSearcher : RenameUsageSearcher {
    // Explicit no-op overrides of Searcher's @ApiStatus.OverrideOnly hooks; see
    // ElixirSymbolUsageSearcher for the full rationale - same trick, same reason.
    override fun collectSearchRequest(parameters: RenameUsageSearchParameters): Query<out RenameUsage>? = null

    override fun collectImmediateResults(parameters: RenameUsageSearchParameters): Collection<RenameUsage> =
        emptyList()

    override fun collectSearchRequests(parameters: RenameUsageSearchParameters): Collection<Query<out RenameUsage>> {
        val target = parameters.target as? ElixirSymbolWithUsages ?: return emptyList()

        return ElixirUsageQueries.searchRequests(parameters.project, target, parameters.searchScope)
            .map { query ->
                query.mapping { usage ->
                    PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(
                        usage as? PsiUsage
                            ?: error("ElixirUsageQueries produced a non-PsiUsage: ${usage::class.java.name}")
                    )
                }
            }
    }
}
