package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.model.Pointer
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.refactoring.rename.api.ModifiableRenameUsage
import com.intellij.refactoring.rename.api.PsiModifiableRenameUsage
import com.intellij.refactoring.rename.api.RenameUsage
import com.intellij.refactoring.rename.api.RenameUsageSearchParameters
import com.intellij.refactoring.rename.api.RenameUsageSearcher
import com.intellij.refactoring.rename.api.fileRangeUpdater
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
                    val psiUsage = usage as? PsiUsage
                        ?: error("ElixirUsageQueries produced a non-PsiUsage: ${usage::class.java.name}")
                    val usageTextByName = (psiUsage as? ElixirPsiUsage)?.usageTextByName
                    if (usageTextByName != null) {
                        AdjustedTextRenameUsage(psiUsage.file, psiUsage.range, psiUsage.declaration, usageTextByName)
                    } else {
                        PsiModifiableRenameUsage.defaultPsiModifiableRenameUsage(psiUsage)
                    }
                }
            }
    }
}

/**
 * A rename usage whose written text is a transformation of the target's new name - e.g. a
 * multi-alias group member gets the new name relative to the group qualifier, and a bare aliased
 * reference gets only the last segment (see [ElixirPsiUsage.usageTextByName]). Built on the
 * platform's [fileRangeUpdater], which exists for exactly this "usage text differs from target
 * name" case.
 */
@Suppress("UnstableApiUsage")
private class AdjustedTextRenameUsage(
    override val file: PsiFile,
    override val range: TextRange,
    override val declaration: Boolean,
    private val usageTextByName: (String) -> String
) : PsiModifiableRenameUsage {
    override val fileUpdater: ModifiableRenameUsage.FileUpdater = fileRangeUpdater(usageTextByName)

    override fun createPointer(): Pointer<out PsiModifiableRenameUsage> {
        val declaration = this.declaration
        val usageTextByName = this.usageTextByName
        return Pointer.fileRangePointer(file, range) { restoredFile, restoredRange ->
            AdjustedTextRenameUsage(restoredFile, restoredRange, declaration, usageTextByName)
        }
    }
}
