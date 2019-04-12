package org.elixir_lang.facet.elixir_module_deletion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider
import com.intellij.conversion.ProjectConverter

class Provider : ConverterProvider("elixir-facet-delete-elixir-module") {
    override fun getPrecedingConverterIds(): Array<String> = arrayOf("elixir-facet-add-external-library")

    override fun getConversionDescription(): String =
            "Small (single-language, non-IntelliJ) IDE SDK could create Elixir modules, but the IDE cannot load the modules, so previously created Elixir modules will be deleted."

    override fun createConverter(conversionContext: ConversionContext): ProjectConverter = Converter(conversionContext)
}
