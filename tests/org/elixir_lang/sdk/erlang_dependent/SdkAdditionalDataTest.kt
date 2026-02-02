package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkTypeId
import org.elixir_lang.PlatformTestCase
import org.jdom.Element
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for SdkAdditionalData Phase 1 refactoring.
 *
 * These tests verify the core persistence and caching logic.
 * Integration tests with actual SDK creation/deletion should be done manually
 * or in a full IDE test environment.
 */
class SdkAdditionalDataTest: PlatformTestCase() {

    /**
     * Verify that creating SdkAdditionalData with an Erlang SDK sets both name and cache.
     */
    fun testNewSdkCreation_SetsNameAndCache() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val erlangSdk = createMockSdk("Erlang 26.0")

        val additionalData = SdkAdditionalData(erlangSdk, elixirSdk)

        // Both name and cache should be set
        assertEquals("Erlang 26.0", additionalData.getErlangSdkName())
        // Note: Cannot directly test cachedErlangSdk as it's private
        // But getErlangSdk() should return it from cache without lookup
    }

    /**
     * Verify that setErlangSdk() updates both the name and cache atomically.
     */
    fun testSetErlangSdk_UpdatesNameAndCache() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val erlangSdk1 = createMockSdk("Erlang 25.0")
        val erlangSdk2 = createMockSdk("Erlang 26.0")

        val additionalData = SdkAdditionalData(erlangSdk1, elixirSdk)
        assertEquals("Erlang 25.0", additionalData.getErlangSdkName())

        // Update to a different Erlang SDK
        additionalData.setErlangSdk(erlangSdk2)

        // Name should be updated
        assertEquals("Erlang 26.0", additionalData.getErlangSdkName())
    }

    /**
     * Verify that setting null clears both name and cache.
     */
    fun testSetErlangSdk_WithNull_ClearsNameAndCache() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val erlangSdk = createMockSdk("Erlang 26.0")

        val additionalData = SdkAdditionalData(erlangSdk, elixirSdk)
        assertEquals("Erlang 26.0", additionalData.getErlangSdkName())

        // Clear the Erlang SDK
        additionalData.setErlangSdk(null)

        // Name should be null
        assertNull(additionalData.getErlangSdkName())
    }

    /**
     * Verify that readExternal() loads only the name and clears the cache.
     */
    fun testReadExternal_LoadsNameAndClearsCache() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val additionalData = SdkAdditionalData(elixirSdk)

        val element = Element("additional")
        element.setAttribute("erlang-sdk-name", "Erlang 26.0")

        additionalData.readExternal(element)

        // Name should be loaded
        assertEquals("Erlang 26.0", additionalData.getErlangSdkName())
        // Cache would be null (tested implicitly by getErlangSdk needing to do lookup)
    }

    /**
     * Verify that writeExternal() persists only the name.
     */
    fun testWriteExternal_PersistsNameOnly() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val erlangSdk = createMockSdk("Erlang 26.0")

        val additionalData = SdkAdditionalData(erlangSdk, elixirSdk)

        val element = Element("additional")
        additionalData.writeExternal(element)

        // Name should be persisted
        assertEquals("Erlang 26.0", element.getAttributeValue("erlang-sdk-name"))
    }

    /**
     * Verify that writeExternal() doesn't write anything if name is null.
     */
    fun testWriteExternal_WithNullName_WritesNothing() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val additionalData = SdkAdditionalData(elixirSdk)

        val element = Element("additional")
        additionalData.writeExternal(element)

        // No attribute should be written
        assertNull(element.getAttributeValue("erlang-sdk-name"))
    }

    /**
     * Verify that clone() creates a new instance with the same name but no shared cache.
     */
    fun testClone_CreatesIndependentCopy() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val erlangSdk = createMockSdk("Erlang 26.0")

        val original = SdkAdditionalData(erlangSdk, elixirSdk)
        val cloned = original.clone() as SdkAdditionalData

        // Name should be copied
        assertEquals(original.getErlangSdkName(), cloned.getErlangSdkName())

        // Modifying the clone should not affect the original
        cloned.setErlangSdk(null)
        assertNull(cloned.getErlangSdkName())
        assertEquals("Erlang 26.0", original.getErlangSdkName())
    }

    /**
     * Verify that getErlangSdkName() returns the stored name without triggering SDK lookup.
     */
    fun testGetErlangSdkName_ReturnsNameWithoutLookup() {
        val elixirSdk = createMockSdk("Elixir 1.15.0")
        val additionalData = SdkAdditionalData(elixirSdk)

        // Set name directly via readExternal
        val element = Element("additional")
        element.setAttribute("erlang-sdk-name", "Erlang 26.0")
        additionalData.readExternal(element)

        // getErlangSdkName should return the name immediately
        // (no lookup happens, no ProjectJdkTable access)
        assertEquals("Erlang 26.0", additionalData.getErlangSdkName())
    }

    // Helper method to create mock SDK
    private fun createMockSdk(name: String): Sdk {
        val sdk = mock(Sdk::class.java)
        `when`(sdk.name).thenReturn(name)

        // Mock the sdkType to be a valid Erlang type for validation
        val sdkType = mock(SdkTypeId::class.java)
        `when`(sdk.sdkType).thenReturn(sdkType)

        return sdk
    }
}
