package org.elixir_lang.run.conversion

import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ProjectConverter
import com.intellij.conversion.RunManagerSettings

class Converter : ProjectConverter() {
    override fun createRunConfigurationsConverter(): ConversionProcessor<RunManagerSettings> =
            RunConfiguration()

    // All conversion done by [createRunConfigurationsConverter]
    override fun isConversionNeeded(): Boolean = false
}
