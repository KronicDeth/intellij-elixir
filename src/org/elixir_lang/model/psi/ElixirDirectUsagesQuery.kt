@file:Suppress("UnstableApiUsage")

package org.elixir_lang.model.psi

import com.intellij.find.usages.api.PsiUsage
import com.intellij.openapi.application.ReadAction
import com.intellij.util.AbstractQuery
import com.intellij.util.Processor
import java.util.concurrent.Callable

class ElixirDirectUsagesQuery(private val usages: Collection<PsiUsage>) : AbstractQuery<PsiUsage>() {
    override fun processResults(consumer: Processor<in PsiUsage>): Boolean =
        ReadAction.nonBlocking(Callable {
            usages.all { usage -> consumer.process(usage) }
        }).executeSynchronously()
}
