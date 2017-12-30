package org.elixir_lang.sdk.elixir.conversion

import com.intellij.conversion.ConversionContext
import com.intellij.conversion.ConverterProvider

class Provider : ConverterProvider("elixir-sdk-put-default-erlang-sdk") {
    override fun createConverter(conversionContext: ConversionContext) = Converter()
    override fun getConversionDescription() = "Elixir SDKs will now have an Internal Erlang SDK"
}
