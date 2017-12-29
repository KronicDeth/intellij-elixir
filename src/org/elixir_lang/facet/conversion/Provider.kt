package org.elixir_lang.facet.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider
import com.intellij.conversion.ProjectConverter

class Provider : ConverterProvider("elixir-external-tools-to-facet") {
    override fun getConversionDescription(): String =
            "Small (single-language, non-IntelliJ) IDEs will now have access to normal SDKs instead of only " +
                    "tracking the SDK path as a Library.\n" +
                    "\n" +
                    "- Preferences > Other Settings > Elixir External Tools is removed\n" +
                    "+ Preferences > Languages & Frameworks > Elixir will allow you to pick the Elixir SDK\n" +
                    "+ Preferences > Languages & Frameworks > Elixir > SDKs will allow you to create, edit, " +
                    "and remove Elixir SDKs"

    override fun createConverter(conversionContext: ConversionContext): ProjectConverter = Converter(conversionContext)
}
