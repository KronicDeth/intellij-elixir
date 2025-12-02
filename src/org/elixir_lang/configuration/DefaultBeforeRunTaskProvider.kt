package org.elixir_lang.configuration

import com.intellij.execution.BeforeRunTask
import com.intellij.openapi.util.Key

class DefaultBeforeRunTaskProvider : BeforeRunTaskProvider {
    override fun isBuildStep(providerID: Key<out BeforeRunTask<*>>?): Boolean {
        return false // WebStorm has no "Make/Build" step
    }
}
