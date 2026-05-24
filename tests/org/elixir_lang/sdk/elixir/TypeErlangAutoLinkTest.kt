package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.SdkHomeKey
import org.elixir_lang.sdk.SdkHomePaths
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver

class TypeErlangAutoLinkTest : PlatformTestCase() {

    private val registeredSdks = mutableListOf<Sdk>()

    override fun tearDown() {
        try {
            val table = ProjectJdkTable.getInstance()
            WriteAction.run<Throwable> {
                registeredSdks.forEach { table.removeJdk(it) }
            }
            registeredSdks.clear()
        } finally {
            super.tearDown()
        }
    }

    fun testFindRegisteredErlangSdk_returnsRegisteredSdk() {
        val erlangSdk = ProjectJdkImpl("Test Erlang SDK", ErlangSdkType()).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/erlang/28.0"
                    commitChanges()
                }
            }
        }

        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().addJdk(erlangSdk)
        }
        registeredSdks.add(erlangSdk)

        val result = ErlangSdkResolver.findAnyRegistered()
        assertNotNull("Should find the registered Erlang SDK", result)
        assertEquals("Test Erlang SDK", result!!.name)
    }

    fun testFindRegisteredErlangSdk_returnsNullWhenNoneRegistered() {
        // Remove any existing Erlang SDKs
        val table = ProjectJdkTable.getInstance()
        val existing = table.allJdks.filter { it.sdkType is ErlangSdkType }
        WriteAction.run<Throwable> {
            existing.forEach { table.removeJdk(it) }
        }

        val result = ErlangSdkResolver.findAnyRegistered()
        assertNull("Should return null when no Erlang SDK is registered", result)
    }

    fun testFindRegisteredErlangSdk_returnsFirstWithHomePath() {
        // Verify it returns an SDK when one exists with a homePath set
        val sdkWithHome = ProjectJdkImpl("Erlang With Home", ErlangSdkType()).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/erlang/28.0"
                    commitChanges()
                }
            }
        }

        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().addJdk(sdkWithHome)
        }
        registeredSdks.add(sdkWithHome)

        val result = ErlangSdkResolver.findAnyRegistered()
        assertNotNull("Should find the Erlang SDK with homePath", result)
        assertEquals("Erlang With Home", result!!.name)
    }

    fun testPromptForMiseErlangSdk_returnsNullForNonMiseElixirSdk() {
        val elixirSdk = ProjectJdkImpl("Test Elixir SDK", Type.instance).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/usr/local/lib/elixir"
                    commitChanges()
                }
            }
        }

        val result = Type.promptForMiseErlangSdk(elixirSdk)
        assertNull("Should return null for non-mise Elixir SDK", result)
    }

    fun testPromptForMiseErlangSdk_returnsNullWhenNoHomePath() {
        val elixirSdk = ProjectJdkImpl("Test Elixir SDK", Type.instance)

        val result = Type.promptForMiseErlangSdk(elixirSdk)
        assertNull("Should return null when Elixir SDK has no homePath", result)
    }

    fun testFindRegisteredErlangSdk_ignoresNonErlangSdks() {
        // Register an Elixir SDK (not Erlang) -- should not be returned
        val elixirSdk = ProjectJdkImpl("Test Elixir SDK", Type.instance).apply {
            WriteAction.run<Throwable> {
                sdkModificator.apply {
                    homePath = "/fake/elixir/1.15"
                    commitChanges()
                }
            }
        }

        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().addJdk(elixirSdk)
        }
        registeredSdks.add(elixirSdk)

        val result = ErlangSdkResolver.findAnyRegistered()
        assertNull("Should not return non-Erlang SDK", result)
    }

    fun testRegisterErlangSdk_returnsNullForInvalidPath() {
        // Use a path that exists but isn't a valid Erlang home
        val result = Type.registerErlangSdk(System.getProperty("java.io.tmpdir"))
        assertNull("Should return null for invalid Erlang home path", result)
    }

    fun testRegisterErlangSdk_registersInProjectJdkTable() {
        // Find a real valid mise Erlang home on this machine
        val miseHomes = mutableMapOf<SdkHomeKey, String>()
        SdkHomePaths.mergeMise(miseHomes, "erlang")

        val erlangSdkType = ErlangSdkType()
        val validHome = miseHomes.values.firstOrNull { erlangSdkType.isValidSdkHome(it) }
            ?: return // Skip if no valid mise Erlang on this machine

        val sdk = Type.registerErlangSdk(validHome)
        assertNotNull("registerErlangSdk should return non-null for valid home", sdk)
        registeredSdks.add(sdk!!)

        val table = ProjectJdkTable.getInstance()
        val found = table.allJdks.any { it.name == sdk.name }
        assertTrue("Registered SDK should be in ProjectJdkTable", found)
    }
}
