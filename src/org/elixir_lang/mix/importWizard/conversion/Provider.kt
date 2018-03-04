package org.elixir_lang.mix.importWizard.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider
import com.intellij.conversion.ProjectConverter

class Provider : ConverterProvider("elixir-include-compiler-output") {
    override fun getConversionDescription(): String {
        return "No longer excludes compiler output paths (`ebin` directories), so that Phoenix View `.beam` files " +
                "can be indexed to resolve EEx Template Elixir Line Breakpoints"
    }

    override fun createConverter(conversionContext: ConversionContext): ProjectConverter = Converter()
}
