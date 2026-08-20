@file:Suppress("UnstableApiUsage")

package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.openapi.application.ReadAction
import com.intellij.util.AbstractQuery
import com.intellij.util.Processor
import java.util.concurrent.Callable

/**
 * A [com.intellij.util.Query] that yields a single, pre-computed [PsiUsage]. Elixir counterpart of
 * Markdown's `MarkdownDirectUsageQuery`. Used for the self-declaration usage (and for
 * semantically-computed implementation usages that are not backed by a
 * [com.intellij.model.psi.PsiSymbolReference]).
 *
 * [processResults] runs in a blocking (non-suspend) context, so the read action uses the
 * cancellable non-suspend API per AGENTS.md § "Read action APIs" - the `Callable { }` wrapper is
 * required (a bare lambda binds to the deprecated `Runnable` overload).
 */
class ElixirDirectUsageQuery(private val usage: PsiUsage) : AbstractQuery<PsiUsage>() {
    override fun processResults(consumer: Processor<in PsiUsage>): Boolean =
        ReadAction.nonBlocking(Callable { consumer.process(usage) }).executeSynchronously()
}
