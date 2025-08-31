package org.elixir_lang

import com.intellij.openapi.util.registry.Registry
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.elixir_lang.injection.markdown.Injector

@org.junit.Ignore("abstract")
open class PlatformTestCase : BasePlatformTestCase() {

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()

        // Enable literal sigil injection for tests to maintain backward compatibility
        Registry.get(Injector.REG_KEY_ENABLE_LITERAL_SIGIL_INJECTION).setValue(true)
    }

    @Throws(Exception::class)
    override fun tearDown() {
        try {
            // Reset registry to default
            Registry.get(Injector.REG_KEY_ENABLE_LITERAL_SIGIL_INJECTION).resetToDefault()
        } catch (e: Exception) {

        }
    }

}
