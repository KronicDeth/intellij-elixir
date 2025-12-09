package org.elixir_lang.configuration

import com.intellij.execution.BeforeRunTask
import com.intellij.openapi.util.Key

interface BeforeRunTaskProvider {
    fun isBuildStep(providerID: Key<out BeforeRunTask<*>>?): Boolean
}
