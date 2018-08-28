package org.elixir_lang.facet.external_library.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider
import com.intellij.conversion.ProjectConverter

class Provider : ConverterProvider("elixir-facet-add-external-library") {
    override fun getPrecedingConverterIds(): Array<String> = arrayOf("elixir-external-tools-to-facet")

    override fun getConversionDescription(): String =
            "Small (single-language, non-IntelliJ) IDEs require the Elixir Facet SDK to be replicated as an External Library, so that the IDE will index the SDK files, which enables Completion, Find Usages, Go To Definition and Go To Symbol for SDK modules and functions.\n" +
                    "\n" +
                    "The SDK will appear in Project (Cmd+1) > External Libraries and can be expanded to see the `.beam` files within, which replicates how the Elixir SDK is shown in IntelliJ IDEA."

    override fun createConverter(conversionContext: ConversionContext): ProjectConverter  = Converter()

}
