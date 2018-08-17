package org.elixir_lang.run.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider
import com.intellij.conversion.ProjectConverter

class Provider : ConverterProvider("elixir-v8.0.0-run-configurations") {
    override fun getConversionDescription(): String =
            """
            Mix and Mix ExUnit run configurations now allow more command line options to be set.

            Mix Program Arguments is renamed to "`mix` arguments" and gains "`elixir` arguments" and "`erl` arguments".
            Mix ExUnit Programs Arguments is renamed to "`mix test` arguments" and gains "`elixir` arguments" and "`erl` arguments".
            """.trimIndent()

    override fun createConverter(conversionContext: ConversionContext): ProjectConverter = Converter()
}
