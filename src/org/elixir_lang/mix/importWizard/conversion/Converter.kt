package org.elixir_lang.mix.importWizard.conversion

import com.intellij.conversion.ConversionProcessor
import com.intellij.conversion.ModuleSettings
import com.intellij.conversion.ProjectConverter

class Converter : ProjectConverter() {
    override fun isConversionNeeded()= false
    override fun createModuleFileConverter(): ConversionProcessor<ModuleSettings> = ModuleSettings()
}
