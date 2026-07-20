package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.find.usages.api.SearchTarget
import com.intellij.find.usages.api.UsageSearchParameters
import com.intellij.openapi.project.Project
import com.intellij.refactoring.rename.api.PsiModifiableRenameUsage
import com.intellij.refactoring.rename.api.RenameUsage
import com.intellij.refactoring.rename.api.RenameUsageSearchParameters
import com.intellij.refactoring.rename.api.RenameUsageSearcher
import com.intellij.psi.search.SearchScope
import com.intellij.util.Query

@Suppress("UnstableApiUsage")
internal class ElixirRenameUsageSearcher : RenameUsageSearcher {
    override fun collectSearchRequests(parameters: RenameUsageSearchParameters): Collection<Query<out RenameUsage>> {
        val searchTarget = parameters.target
        if (searchTarget !is ElixirSymbolWithUsages || searchTarget !is SearchTarget) return emptyList()

        // Adapter: RenameUsageSearchParameters and UsageSearchParameters have the same search inputs
        // (project + target + scope) but different result types. We reuse the existing Symbol usages
        // search pipeline by exposing rename inputs through a UsageSearchParameters view, then map
        // PsiUsage results to modifiable rename usages below.
        val usageQueries = ElixirSymbolUsageSearcher().collectSearchRequests(
            object : UsageSearchParameters {
                override fun getProject(): Project = parameters.project
                override fun areValid(): Boolean = parameters.areValid()
                override val target: SearchTarget = searchTarget
                override val searchScope: SearchScope = parameters.searchScope
            }
        )

        val psiUsageQueries = usageQueries.map { usageQuery ->
            usageQuery.mapping { usage ->
                usage as? PsiUsage
                    ?: error("ElixirSymbolUsageSearcher produced a non-PsiUsage: ${usage::class.java.name}")
            }
        }

        return psiUsageQueries.map { psiUsageQuery ->
            psiUsageQuery.mapping { psiUsage ->
                PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(psiUsage)
            }
        }
    }
}
