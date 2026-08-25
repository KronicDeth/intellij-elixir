package org.elixir_lang.run

import com.intellij.openapi.util.JDOMUtil
import com.intellij.openapi.util.io.FileUtil
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.debugger.settings.stepping.ModuleFilter
import org.jdom.Element

/**
 * Pins the run-configuration XML round trip for the two configuration types users report as
 * "Cannot read scheme <TYPE_ID>-<factory>.xml": whatever [Configuration.writeExternal] emits,
 * [Configuration.readExternal] must read back into an equivalent configuration.
 */
class ConfigurationRoundTripTest : PlatformTestCase() {
    fun testMixConfigurationRoundTrip() {
        val written = mixConfiguration().apply {
            erlArguments = "-sname round_trip"
            elixirArguments = "--no-halt"
            programParameters = "run --no-start"
            workingDirectory = myFixture.tempDirPath
            envs = mapOf("MIX_ENV" to "dev")
            isPassParentEnvs = true
            inheritApplicationModuleFilters = false
            moduleFilterList = mutableListOf(ModuleFilter(true, "Elixir.Foo.*"))
            configurationModule.module = myFixture.module
        }

        val read = mixConfiguration()
        read.readExternal(writeToElement(written))

        assertEquals("-sname round_trip", read.erlArguments)
        assertEquals("--no-halt", read.elixirArguments)
        assertEquals("run --no-start", read.programParameters)
        assertWorkingDirectoryEquals(myFixture.tempDirPath, read.workingDirectory)
        assertEquals(mapOf("MIX_ENV" to "dev"), read.envs)
        assertTrue("Expected passParentEnvs to survive the round trip", read.isPassParentEnvs)
        assertFalse(read.inheritApplicationModuleFilters)
        assertEquals(listOf(ModuleFilter(true, "Elixir.Foo.*")), read.moduleFilterList)
        assertEquals(myFixture.module, read.configurationModule.module)
    }

    fun testExUnitConfigurationRoundTrip() {
        val written = exUnitConfiguration().apply {
            erlArgumentList = mutableListOf("-sname", "round_trip")
            elixirArgumentList = mutableListOf("--no-halt")
            mixTestArgumentList = mutableListOf("--trace", "test/foo_test.exs")
            workingDirectory = myFixture.tempDirPath
            envs = mapOf("MIX_ENV" to "test")
            isPassParentEnvs = true
            inheritApplicationModuleFilters = false
            moduleFilterList = mutableListOf(ModuleFilter(false, "Elixir.Bar.*"))
            configurationModule.module = myFixture.module
        }

        val read = exUnitConfiguration()
        read.readExternal(writeToElement(written))

        assertEquals(listOf("-sname", "round_trip"), read.erlArgumentList)
        assertEquals(listOf("--no-halt"), read.elixirArgumentList)
        assertEquals(listOf("--trace", "test/foo_test.exs"), read.mixTestArgumentList)
        assertWorkingDirectoryEquals(myFixture.tempDirPath, read.workingDirectory)
        assertEquals(mapOf("MIX_ENV" to "test"), read.envs)
        assertTrue("Expected passParentEnvs to survive the round trip", read.isPassParentEnvs)
        assertFalse(read.inheritApplicationModuleFilters)
        assertEquals(listOf(ModuleFilter(false, "Elixir.Bar.*")), read.moduleFilterList)
        assertEquals(myFixture.module, read.configurationModule.module)
    }

    /**
     * The module name is written by both [com.intellij.execution.configurations.ModuleBasedConfiguration]
     * (through its serialized options) and by this plugin's own `writeExternalModule`, so the saved
     * scheme must not end up with conflicting `module` elements.
     */
    fun testMixConfigurationWritesOneModuleElement() {
        val configuration = mixConfiguration().apply { configurationModule.module = myFixture.module }

        val element = writeToElement(configuration)

        assertEquals(
            "module written more than once:\n${JDOMUtil.write(element)}",
            1,
            element.getChildren("module").size
        )
    }

    /**
     * A saved scheme whose `module` element carries children but no `name` attribute is what used to raise
     * `java.lang.AssertionError` out of the platform's option-tag deserialization, surfacing as
     * "Cannot read scheme <TYPE_ID>-<factory>.xml" and losing the whole run configuration.
     */
    fun testReadsSchemeWithMalformedModuleElement() {
        val element = JDOMUtil.load("<configuration><module><child /></module></configuration>")

        mixConfiguration().readExternal(element)
    }

    private fun mixConfiguration() =
        org.elixir_lang.mix.Configuration(NAME, project, org.elixir_lang.mix.configuration.Factory)

    private fun exUnitConfiguration() = org.elixir_lang.exunit.Configuration(NAME, project)

    private fun writeToElement(configuration: Configuration): Element =
        Element("configuration").also { configuration.writeExternal(it) }

    private fun assertWorkingDirectoryEquals(expected: String, actual: String?) {
        assertNotNull(actual)
        assertEquals(
            FileUtil.toSystemIndependentName(expected),
            FileUtil.toSystemIndependentName(actual!!)
        )
    }

    companion object {
        private const val NAME = "round trip"
    }
}
