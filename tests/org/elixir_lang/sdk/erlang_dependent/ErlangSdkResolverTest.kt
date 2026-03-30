package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkType
import org.elixir_lang.PlatformTestCase
import org.jdom.Element
import org.junit.Assume
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class ErlangSdkResolverTest : PlatformTestCase() {
    private val resolver = DefaultErlangSdkResolver()

    fun testNotConfigured() {
        val elixirSdk = mockElixirSdk { sdk -> SdkAdditionalData(sdk) }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel())

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.NOT_CONFIGURED, missing.reason)
    }

    fun testNotFound() {
        val configuredName = "Erlang SDK"
        val elixirSdk = mockElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkNameElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel())

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.NOT_FOUND, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    fun testInvalidType() {
        val configuredName = "Not Erlang"
        val invalidSdk = mockSdk(
            name = configuredName,
            sdkType = mock(SdkType::class.java),
            homePath = "/tmp/invalid"
        )
        val elixirSdk = mockElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkNameElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(invalidSdk))

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.INVALID_TYPE, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    fun testMissingHomePath() {
        val configuredName = "Erlang SDK"
        val sdkType = org.elixir_lang.sdk.elixir.Type.erlangSdkType()
        val erlangSdk = mockSdk(
            name = configuredName,
            sdkType = sdkType,
            homePath = null
        )

        Assume.assumeTrue(Type.staticIsValidDependency(erlangSdk))

        val elixirSdk = mockElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkNameElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(erlangSdk))

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.MISSING_HOME_PATH, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    private fun mockElixirSdk(
        additionalDataProvider: (Sdk) -> SdkAdditionalData?
    ): Sdk {
        val sdk = mock(Sdk::class.java)
        `when`(sdk.name).thenReturn("Elixir SDK")
        `when`(sdk.sdkAdditionalData).thenReturn(additionalDataProvider(sdk))
        return sdk
    }

    private fun mockSdk(
        name: String,
        sdkType: SdkType,
        homePath: String?
    ): Sdk {
        val sdk = mock(Sdk::class.java)
        `when`(sdk.name).thenReturn(name)
        `when`(sdk.sdkType).thenReturn(sdkType)
        `when`(sdk.homePath).thenReturn(homePath)
        return sdk
    }

    private fun erlangSdkNameElement(name: String): Element =
        Element("sdk").apply { setAttribute("erlang-sdk-name", name) }

    private fun sdkModel(vararg sdks: Sdk): SdkModel {
        val sdkModel = mock(SdkModel::class.java)
        `when`(sdkModel.sdks).thenReturn(sdks)
        return sdkModel
    }
}
