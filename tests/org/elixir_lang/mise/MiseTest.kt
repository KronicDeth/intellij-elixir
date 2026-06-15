package org.elixir_lang.mise

import org.elixir_lang.PlatformTestCase
import java.nio.file.Path

/**
 * Tests for [Mise.parseOutput] (internal).
 *
 * Pure with no IDE dependencies, so no sandbox setup is needed.
 */
class MiseTest : PlatformTestCase() {
    private val workDir: Path = Path.of(".")

    // -------------------------------------------------------------------------
    // parseOutput
    // -------------------------------------------------------------------------

    fun testParseOutput_bothTools_returnsElixirAndErlang() {
        val json = """
            {
              "elixir": [{"version":"1.13.4-otp-24","requested_version":"1.13.4","install_path":"/home/user/.local/share/mise/installs/elixir/1.13.4-otp-24","source":{"type":"file","path":"/project/.tool-versions"},"installed":true,"active":true}],
              "erlang": [{"version":"24.3.4.6","requested_version":"24.3","install_path":"/home/user/.local/share/mise/installs/erlang/24.3.4.6","source":{"type":"file","path":"/project/.tool-versions"},"installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNotNull(result!!.elixir)
        assertEquals("1.13.4-otp-24", result.elixir!!.version)
        assertEquals("/home/user/.local/share/mise/installs/elixir/1.13.4-otp-24", result.elixir.installPath)
        assertNotNull(result.erlang)
        assertEquals("24.3.4.6", result.erlang!!.version)
    }

    fun testParseOutput_onlyElixir_erlangIsNull() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNotNull(result!!.elixir)
        assertNull(result.erlang)
    }

    fun testParseOutput_onlyErlang_elixirIsNull() {
        val json = """
            {
              "erlang": [{"version":"26.2.5","requested_version":"26","install_path":"/installs/erlang/26.2.5","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
        assertNotNull(result.erlang)
        assertEquals("26.2.5", result.erlang!!.version)
    }

    fun testParseOutput_emptyObject_returnsBothNull() {
        val result = Mise.parseOutput("{}", workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
        assertNull(result.erlang)
    }

    fun testParseOutput_firstActiveInstalledEntryWins() {
        // First entry: not active. Second entry: active and installed.
        val json = """
            {
              "elixir": [
                {"version":"1.12.0","requested_version":"1.12.0","install_path":"/installs/elixir/1.12.0","installed":true,"active":false},
                {"version":"1.13.4","requested_version":"~>1.13","install_path":"/installs/elixir/1.13.4","installed":true,"active":true}
              ]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertEquals("1.13.4", result!!.elixir!!.version)
    }

    fun testParseOutput_notInstalledEntrySkipped() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":false,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
    }

    fun testParseOutput_notActiveEntrySkipped() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":true,"active":false}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
    }

    fun testParseOutput_extraToolsIgnored() {
        val json = """
            {
              "node": [{"version":"20.0.0","requested_version":"20","install_path":"/installs/node/20.0.0","installed":true,"active":true}],
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNotNull(result!!.elixir)
        assertNull(result.erlang)
    }

    fun testParseOutput_missingVersion_entrySkipped() {
        // version field is null - toMiseToolEntry() returns null, filtered out
        val json = """
            {
              "elixir": [{"requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
    }

    fun testParseOutput_missingInstallPath_entrySkipped() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir)
    }

    fun testParseOutput_emptyString_returnsNull() {
        assertNull(Mise.parseOutput("", workDir))
    }

    fun testParseOutput_malformedJson_returnsNull() {
        assertNull(Mise.parseOutput("not json at all {{{", workDir))
    }

    fun testParseOutput_sourceFieldPresent_populatedCorrectly() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","source":{"type":"file","path":"/project/.tool-versions"},"installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertEquals("file", result!!.elixir!!.sourceType)
        assertEquals("/project/.tool-versions", result.elixir.sourcePath)
    }

    fun testParseOutput_sourceFieldAbsent_nullSourceFields() {
        val json = """
            {
              "elixir": [{"version":"1.15.7","requested_version":"1.15.7","install_path":"/installs/elixir/1.15.7","installed":true,"active":true}]
            }
        """.trimIndent()

        val result = Mise.parseOutput(json, workDir)

        assertNotNull(result)
        assertNull(result!!.elixir!!.sourceType)
        assertNull(result.elixir.sourcePath)
    }

    // -------------------------------------------------------------------------
    // parseTrustError
    // -------------------------------------------------------------------------

    fun testParseTrustError_documentedTwoLineStderr_extractsPath() {
        // Mirrors the exact two-line output produced by mise when a config file is untrusted.
        val stderr = """
            mise ERROR error parsing config file: /home/user/proj/mise.toml
            mise ERROR Config files in /home/user/proj/mise.toml are not trusted.
        """.trimIndent()
        val result = Mise.parseTrustError(stderr)
        assertNotNull(result)
        assertEquals("/home/user/proj/mise.toml", result!!.configFilePath)
    }

    fun testParseTrustError_singleMatchingLine_extractsPath() {
        val stderr = "mise ERROR Config files in /my/project are not trusted."
        val result = Mise.parseTrustError(stderr)
        assertNotNull(result)
        assertEquals("/my/project", result!!.configFilePath)
    }

    fun testParseTrustError_unrelatedStderr_returnsNull() {
        assertNull(Mise.parseTrustError("mise ERROR command not found: elixir"))
    }

    fun testParseTrustError_emptyString_returnsNull() {
        assertNull(Mise.parseTrustError(""))
    }

    fun testParseTrustError_pathWithSpaces_trimmedCorrectly() {
        // Surrounding whitespace inside the captured group is trimmed via .trim().
        val stderr = "mise ERROR Config files in   /home/user/my project/mise.toml   are not trusted."
        val result = Mise.parseTrustError(stderr)
        assertNotNull(result)
        assertEquals("/home/user/my project/mise.toml", result!!.configFilePath)
    }

    fun testParseTrustError_patternEmbeddedInLongerStderr_stillMatched() {
        // .find() scans the whole string; warnings/noise before and after must not prevent matching.
        val stderr = """
            mise WARN  some other warning
            mise ERROR Config files in /proj/.mise.toml are not trusted.
            mise INFO  continuing...
        """.trimIndent()
        val result = Mise.parseTrustError(stderr)
        assertNotNull(result)
        assertEquals("/proj/.mise.toml", result!!.configFilePath)
    }
}
