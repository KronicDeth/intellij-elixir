package org.elixir_lang.configuration

import com.intellij.compiler.options.CompileStepBeforeRun
import com.intellij.execution.BeforeRunTask
import com.intellij.openapi.util.Key

class JpsBeforeRunTaskProvider : BeforeRunTaskProvider {
    override fun isBuildStep(providerID: Key<out BeforeRunTask<*>>?): Boolean {
        return providerID === CompileStepBeforeRun.ID
    }
}
