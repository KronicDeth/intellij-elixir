package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.testFramework.registerOrReplaceServiceInstance
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.wsl.MockWslCompatService
import org.elixir_lang.sdk.wsl.WslCompatService
import org.jdom.Element
import org.junit.Assume
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

class ErlangSdkResolverTest : PlatformTestCase() {
    private val resolver = DefaultErlangSdkResolver()

    override fun setUp() {
        super.setUp()
        ApplicationManager.getApplication().registerOrReplaceServiceInstance(
            WslCompatService::class.java,
            MockWslCompatService(),
            testRootDisposable,
        )
    }

    fun testNotConfigured() {
        val elixirSdk = createElixirSdk { sdk -> SdkAdditionalData(sdk) }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel())

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.NOT_CONFIGURED, missing.reason)
    }

    fun testNotFound() {
        val configuredName = "Erlang SDK"
        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel())

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.NOT_FOUND, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    fun testInvalidType() {
        val configuredName = "Not Erlang"
        // Use ElixirSdkType - it's not a valid Erlang dependency
        val invalidSdk = createSdk(
            name = configuredName,
            sdkType = ElixirSdkType.instance,
            homePath = "/tmp/invalid"
        )
        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(invalidSdk))

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.INVALID_TYPE, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    fun testMissingHomePath() {
        val configuredName = "Erlang SDK"
        val erlangSdk = createSdk(
            name = configuredName,
            sdkType = ErlangSdkType.instance,
            homePath = null
        )

        Assume.assumeTrue(Type.staticIsValidDependency(erlangSdk))

        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(erlangSdk))

        assertTrue(result is ErlangSdkResult.Missing)
        val missing = result as ErlangSdkResult.Missing
        assertEquals(MissingErlangSdkReason.MISSING_HOME_PATH, missing.reason)
        assertEquals(configuredName, missing.erlangSdkName)
    }

    fun testResolveByHomePath_success() {
        val erlangHomePath = "/fake/erlang/26.0"
        val erlangSdk = createSdk(name = "Erlang 26", sdkType = ErlangSdkType.instance, homePath = erlangHomePath)

        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply {
                readExternal(erlangSdkElement(name = "Erlang 26", homePath = erlangHomePath))
            }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(erlangSdk))

        assertTrue("Should resolve successfully by home path", result is ErlangSdkResult.Success)
        assertEquals(erlangSdk, (result as ErlangSdkResult.Success).sdk)
    }

    fun testResolveByHomePath_survivesRename() {
        val erlangHomePath = "/fake/erlang/26.0"
        // SDK was renamed from "Erlang 26" to "My Erlang"
        val erlangSdk = createSdk(name = "My Erlang", sdkType = ErlangSdkType.instance, homePath = erlangHomePath)

        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply {
                // Persisted with old name but matching home path
                readExternal(erlangSdkElement(name = "Erlang 26", homePath = erlangHomePath))
            }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(erlangSdk))

        assertTrue("Should resolve by path even after rename", result is ErlangSdkResult.Success)
        assertEquals(erlangSdk, (result as ErlangSdkResult.Success).sdk)
    }

    fun testResolveByName_legacyFallback() {
        val configuredName = "Erlang 26"
        val erlangSdk = createSdk(name = configuredName, sdkType = ErlangSdkType.instance, homePath = "/fake/erlang/26.0")

        // Legacy config: name only, no home path
        val elixirSdk = createElixirSdk { sdk ->
            SdkAdditionalData(sdk).apply { readExternal(erlangSdkElement(configuredName)) }
        }

        val result = resolver.resolveErlangSdkResult(elixirSdk, sdkModel(erlangSdk))

        assertTrue("Should resolve by name as legacy fallback", result is ErlangSdkResult.Success)
        assertEquals(erlangSdk, (result as ErlangSdkResult.Success).sdk)
    }

    /**
     * Creates a [ProjectJdkImpl] configured as an Elixir SDK with the given additional data.
     */
    private fun createElixirSdk(
        additionalDataProvider: (Sdk) -> SdkAdditionalData?
    ): Sdk {
        val sdk = ProjectJdkImpl("Elixir SDK", ElixirSdkType.instance, "/fake/elixir/1.15", "")
        val data = additionalDataProvider(sdk)
        if (data != null) {
            WriteAction.run<Throwable> {
                sdk.sdkModificator.apply {
                    setSdkAdditionalData(data)
                    commitChanges()
                }
            }
        }
        return sdk
    }

    /**
     * Creates a [ProjectJdkImpl] with the given properties.
     */
    private fun createSdk(
        name: String,
        sdkType: SdkType,
        homePath: String?
    ): Sdk {
        return ProjectJdkImpl(name, sdkType, homePath ?: "", "")
    }

    private fun erlangSdkElement(name: String, homePath: String? = null): Element =
        Element("sdk").apply {
            setAttribute("erlang-sdk-name", name)
            if (homePath != null) setAttribute("erlang-sdk-home-path", homePath)
        }

    /**
     * Simple [SdkModel] implementation backed by a fixed array of SDKs.
     */
    private fun sdkModel(vararg sdks: Sdk): SdkModel = object : SdkModel {
        override fun getSdks(): Array<out Sdk> = arrayOf(*sdks)
        override fun findSdk(sdkName: String?): Sdk? = sdks.find { it.name == sdkName }
        override fun addSdk(sdk: Sdk) {}
        override fun addListener(listener: SdkModel.Listener) {}
        override fun removeListener(listener: SdkModel.Listener) {}
        override fun getMulticaster(): SdkModel.Listener = object : SdkModel.Listener {}
    }
}
