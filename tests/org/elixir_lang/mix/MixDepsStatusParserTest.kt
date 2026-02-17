package org.elixir_lang.mix

import org.elixir_lang.PlatformTestCase
import org.elixir_lang.package_manager.DepState

class MixDepsStatusParserTest : PlatformTestCase() {
    fun testParsesOkAndOutdatedStatuses() {
        val output = """
            * bandit 1.7.0 (Hex package) (mix)
              locked at 1.7.0 (bandit) 3e2f7a98
              ok
            * cc_precompiler (Hex package) (mix)
              locked at 0.1.11 (cc_precompiler) 3427232c
              the dependency build is outdated, please run "mix deps.compile"
            * thousand_island (Hex package) (mix)
              locked at 1.3.14 (thousand_island) d0d24a92
              the dependency is not available, run "mix deps.get"
        """.trimIndent()

        val status = MixDepsStatusParser.parse(output)

        assertEquals(3, status.dependencies.size)

        val bandit = status.dependencies.first { it.name == "bandit" }
        assertEquals(DepState.OK, bandit.state)

        val ccPrecompiler = status.dependencies.first { it.name == "cc_precompiler" }
        assertEquals(DepState.OUTDATED, ccPrecompiler.state)
        assertEquals(
            "the dependency build is outdated, please run \"mix deps.compile\"",
            ccPrecompiler.message
        )

        val thousandIsland = status.dependencies.first { it.name == "thousand_island" }
        assertEquals(DepState.OUTDATED, thousandIsland.state)
    }

    fun testParsesUnknownWhenStatusLineMissing() {
        val output = """
            * heroicons (https://github.com/tailwindlabs/heroicons.git - v2.1.1)
              locked at 88ab3a0 (tag: v2.1.1)
        """.trimIndent()

        val status = MixDepsStatusParser.parse(output)

        assertEquals(1, status.dependencies.size)
        assertEquals(DepState.UNKNOWN, status.dependencies.single().state)
        assertTrue(status.hasNonOk)
    }

    fun testStripsAnsiCodesFromStatusLines() {
        val output =
            "* bandit 1.7.0 (Hex package) (mix)\n" +
                "  locked at 1.7.0 (bandit) 3e2f7a98\n" +
                "  \u001B[32mok\u001B[0m\n"

        val status = MixDepsStatusParser.parse(output)

        assertEquals(1, status.dependencies.size)
        assertEquals(DepState.OK, status.dependencies.single().state)
    }
}
