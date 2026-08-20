package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

/**
 * Tests for [ModuleSdkStatus] and [summaryHtml] - the shared per-module SDK status text used by
 * both the status-bar widget and the Settings → Elixir SDK page.
 */
class ModuleSdkStatusTest : PlatformTestCase() {

    private fun elixirSdk(name: String): Sdk = ProjectJdkImpl(name, ElixirSdkType.instance)

    fun testOfNull() {
        assertEquals(ModuleSdkStatus.NoSdk, ModuleSdkStatus.of(null))
        assertEquals("No Elixir SDK configured", ModuleSdkStatus.of(null).summaryHtml())
    }

    fun testOfSdkWithoutHomeIsInvalid() {
        // A registered SDK with no (valid) home path classifies as Invalid.
        val status = ModuleSdkStatus.of(elixirSdk("elixir-no-home"))
        assertTrue("expected Invalid, got $status", status is ModuleSdkStatus.Invalid)
        assertTrue(status.summaryHtml().contains("Invalid SDK"))
    }

    fun testSummaryReady() {
        val status = ModuleSdkStatus.Ready(elixirSdk("Elixir 1.17"), elixirSdk("Erlang 27"))
        assertEquals("Elixir: <b>Elixir 1.17</b><br>Erlang: <b>Erlang 27</b>", status.summaryHtml())
    }

    fun testSummaryReadyWithModuleName() {
        val status = ModuleSdkStatus.Ready(elixirSdk("Elixir 1.17"), elixirSdk("Erlang 27"))
        assertEquals(
            "Elixir: <b>Elixir 1.17</b><br>Erlang: <b>Erlang 27</b><br>Module: <b>my_app</b>",
            status.summaryHtml("my_app"),
        )
    }

    fun testSummaryMissingErlang() {
        val status = ModuleSdkStatus.MissingErlang(elixirSdk("Elixir 1.17"))
        assertEquals("Elixir SDK: Elixir 1.17 - Missing Erlang SDK", status.summaryHtml())
    }

    fun testSummaryInvalid() {
        val status = ModuleSdkStatus.Invalid(elixirSdk("Elixir 1.17"))
        assertEquals("Elixir SDK: Elixir 1.17 - Invalid SDK", status.summaryHtml())
    }

    fun testSummaryNoSdkWithModuleName() {
        assertEquals("No Elixir SDK configured (module 'my_app')", ModuleSdkStatus.NoSdk.summaryHtml("my_app"))
    }
}
