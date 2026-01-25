package org.elixir_lang.sdk

import com.intellij.testFramework.TestLoggerFactory
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

/**
 * Tests that SDK classes can be loaded without requiring ApplicationManager.
 *
 * This catches bugs where companion object init blocks access services
 * that aren't available during early plugin initialization (before COMPONENTS_LOADED state).
 *
 * Note: This test intentionally does NOT extend PlatformTestCase
 * because we want to test class loading WITHOUT a full IDE context.
 */
class EarlyInitializationTest {
    @Rule
    @JvmField
    val testWatcher = TestLoggerFactory.createTestWatcher()
    /**
     * Verify that loading org.elixir_lang.sdk.elixir.Type does not throw.
     *
     * Previously, this class had a companion object init block that called
     * ApplicationManager.getApplication().messageBus, which failed during
     * early IDE startup before COMPONENTS_LOADED state.
     */
    @Test
    fun `elixir Type class loads without ApplicationManager`() {
        // Force class loading via reflection
        // This will execute any companion object init blocks
        val clazz = Class.forName("org.elixir_lang.sdk.elixir.Type")
        assertNotNull(clazz)
    }

    @Test
    fun `erlang Type class loads without ApplicationManager`() {
        val clazz = Class.forName("org.elixir_lang.sdk.erlang.Type")
        assertNotNull(clazz)
    }

    @Test
    fun `SdkAdditionalData class loads without ApplicationManager`() {
        val clazz = Class.forName("org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData")
        assertNotNull(clazz)
    }
}
