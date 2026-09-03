package org.elixir_lang.debugger.configuration

import org.elixir_lang.PlatformTestCase

/**
 * Pins what [Debuggable.debuggedConfiguration] carries across from the configuration the user
 * edited to the clone that is actually launched under the debugger.
 *
 * Debug does not run the user's configuration. `org.elixir_lang.debugger.Process` mints a node name
 * and cookie and asks the configuration for a *copy* of itself carrying them, and it is that copy
 * whose command line gets built. Everything the user set has to survive that copy, and for the
 * environment it did not: `Configuration.debuggedConfiguration` populated the clone's environment
 * with `envs.putAll(envs)` - the local variable copying itself rather than the property - so a
 * configuration's environment variables were dropped on the way to Debug while working correctly
 * under Run.
 *
 * That is what issue #2187 reported. It was fixed by adding the `this.` qualifier, in v13.1.0, and
 * nothing has pinned it since.
 */
class DebuggedConfigurationTest : PlatformTestCase() {
    fun testExUnitDebuggedConfigurationCarriesEnvs() {
        val configuration = exUnitConfiguration().apply {
            envs = mapOf("DATABASE_URL" to "ecto://localhost/test", "SECRET" to "shh")
        }

        val debugged = configuration.debuggedConfiguration(NODE_NAME, COOKIE)

        assertEquals(
            "environment variables were dropped on the way to the debugged configuration",
            "ecto://localhost/test",
            debugged.envs["DATABASE_URL"]
        )
        assertEquals("shh", debugged.envs["SECRET"])
    }

    /**
     * The clone also forces `MIX_ENV=test` so `intellij_elixir.debug` uses the test code paths - but
     * with `putIfAbsent`, so a user who set `MIX_ENV` themselves keeps their value.
     */
    fun testExUnitDebuggedConfigurationDefaultsMixEnvWithoutOverridingIt() {
        val defaulted = exUnitConfiguration().debuggedConfiguration(NODE_NAME, COOKIE)

        assertEquals("test", defaulted.envs["MIX_ENV"])

        val overridden = exUnitConfiguration()
            .apply { envs = mapOf("MIX_ENV" to "dev") }
            .debuggedConfiguration(NODE_NAME, COOKIE)

        assertEquals(
            "an explicit MIX_ENV must not be overwritten by the debugged configuration's default",
            "dev",
            overridden.envs["MIX_ENV"]
        )
    }

    fun testExUnitDebuggedConfigurationCarriesPassParentEnvs() {
        val configuration = exUnitConfiguration().apply { isPassParentEnvs = false }

        assertFalse(configuration.debuggedConfiguration(NODE_NAME, COOKIE).isPassParentEnvs)
    }

    /**
     * The node name and cookie `Process` mints are the whole point of the clone: without both, the
     * debugged node and the IDE cannot find or authorize each other.
     */
    fun testExUnitDebuggedConfigurationCarriesNodeNameAndCookie() {
        val debugged = exUnitConfiguration().debuggedConfiguration(NODE_NAME, COOKIE)

        assertEquals(listOf("-name", NODE_NAME), argumentPair(debugged.erlArgumentList, "-name"))
        assertEquals(listOf("-setcookie", COOKIE), argumentPair(debugged.erlArgumentList, "-setcookie"))
    }

    private fun argumentPair(argumentList: List<String>, flag: String): List<String>? =
        argumentList.indexOf(flag).takeIf { it >= 0 && it + 1 < argumentList.size }?.let {
            argumentList.subList(it, it + 2)
        }

    private fun exUnitConfiguration() = org.elixir_lang.exunit.Configuration(NAME, project)

    companion object {
        private const val NAME = "debugged configuration"
        private const val NODE_NAME = "debugged00000000-0000-0000-0000-000000000000@127.0.0.1"
        private const val COOKIE = "00000000-0000-0000-0000-000000000000"
    }
}
