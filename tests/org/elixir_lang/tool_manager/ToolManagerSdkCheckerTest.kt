package org.elixir_lang.tool_manager

import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.erlang.Release
import org.mockito.Mockito.mock
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Unit tests for the pure (Phase 2/3) methods of [ToolManagerSdkChecker]:
 * [ToolManagerSdkChecker.detectMismatchIssues], [ToolManagerSdkChecker.buildAssignments],
 * and [ToolManagerSdkChecker.collectErrors].
 *
 * These methods perform no platform I/O and require no read lock; all inputs are
 * plain Kotlin data objects.  A [Project] mock is passed for construction only -
 * none of the methods under test call through to it.
 */
class ToolManagerSdkCheckerTest : PlatformTestCase() {

    private lateinit var checker: ToolManagerSdkChecker

    override fun setUp() {
        super.setUp()
        checker = ToolManagerSdkChecker(
            project = mock(Project::class.java),
            toolManagers = emptyList(),
            settings = ToolManagerSettings(),
        )
    }

    // -------------------------------------------------------------------------
    // Test-local helpers
    // -------------------------------------------------------------------------

    /** Minimal [ToolManagerVersions] backed by plain values (no platform needed). */
    private fun versions(
        toolManagerName: String = "mise",
        elixir: ToolEntry? = null,
        erlang: ToolEntry? = null,
    ): ToolManagerVersions = object : ToolManagerVersions {
        override val toolManagerName = toolManagerName
        override val elixir = elixir
        override val erlang = erlang
    }

    private fun elixirEntry(
        version: String,
        installPath: String,
        installed: Boolean = true,
    ) = ToolEntry(version, installPath, installed)

    private fun erlangEntry(
        version: String,
        installPath: String = "/erlang-install",
    ) = ToolEntry(version, installPath, installed = true)

    private fun moduleData(
        moduleName: String,
        sdk: Sdk? = null,
        erlangHome: String? = null,
        contentRoot: Path? = Paths.get("/project"),
    ) = ToolManagerSdkChecker.ModuleCheckData(moduleName, sdk, erlangHome, contentRoot)

    private fun success(versions: ToolManagerVersions): ToolManagerResult.Success =
        ToolManagerResult.Success(versions)

    private fun error(
        description: String = "an error",
        toolManagerName: String = "mise",
    ): ToolManagerResult.Error = ToolManagerResult.Error(toolManagerName, description)

    // -------------------------------------------------------------------------
    // collectErrors
    // -------------------------------------------------------------------------

    fun testCollectErrors_emptyInput_returnsEmpty() {
        assertTrue(checker.collectErrors(emptyMap()).isEmpty())
    }

    fun testCollectErrors_onlySuccessAndNull_returnsEmpty() {
        val roots = mapOf<Path, ToolManagerResult?>(
            Paths.get("/a") to success(versions()),
            Paths.get("/b") to null,
        )
        assertTrue(checker.collectErrors(roots).isEmpty())
    }

    fun testCollectErrors_mixedResults_returnsOnlyErrors() {
        val roots = mapOf<Path, ToolManagerResult?>(
            Paths.get("/a") to error("err1"),
            Paths.get("/b") to success(versions()),
            Paths.get("/c") to null,
            Paths.get("/d") to error("err2"),
        )
        val errors = checker.collectErrors(roots)
        assertEquals(2, errors.size)
        assertTrue(errors.any { it.description == "err1" })
        assertTrue(errors.any { it.description == "err2" })
    }

    fun testCollectErrors_deduplicatesByDescription() {
        val roots = mapOf<Path, ToolManagerResult?>(
            Paths.get("/a") to error("same error"),
            Paths.get("/b") to error("same error"),  // duplicate
            Paths.get("/c") to error("different error"),
        )
        val errors = checker.collectErrors(roots)
        assertEquals(2, errors.size)
        assertEquals(1, errors.count { it.description == "same error" })
        assertEquals(1, errors.count { it.description == "different error" })
    }

    // -------------------------------------------------------------------------
    // buildAssignments
    // -------------------------------------------------------------------------

    fun testBuildAssignments_elixirInstalled_included() {
        val path = Paths.get("/project")
        val v = versions(elixir = elixirEntry("1.17.3", "/elixir-1.17", installed = true))
        val assignments = checker.buildAssignments(
            listOf(moduleData("mod", contentRoot = path)),
            mapOf(path to success(v)),
        )
        assertEquals(1, assignments.size)
        assertNotNull(assignments["mod"])
    }

    fun testBuildAssignments_elixirNotInstalled_excluded() {
        val path = Paths.get("/project")
        val v = versions(elixir = elixirEntry("1.17.3", "/elixir-1.17", installed = false))
        assertTrue(
            checker.buildAssignments(
                listOf(moduleData("mod", contentRoot = path)),
                mapOf(path to success(v)),
            ).isEmpty()
        )
    }

    fun testBuildAssignments_noElixirEntry_excluded() {
        val path = Paths.get("/project")
        val v = versions(elixir = null, erlang = erlangEntry("27.3"))
        assertTrue(
            checker.buildAssignments(
                listOf(moduleData("mod", contentRoot = path)),
                mapOf(path to success(v)),
            ).isEmpty()
        )
    }

    fun testBuildAssignments_errorResult_excluded() {
        val path = Paths.get("/project")
        assertTrue(
            checker.buildAssignments(
                listOf(moduleData("mod", contentRoot = path)),
                mapOf(path to error()),
            ).isEmpty()
        )
    }

    fun testBuildAssignments_nullResult_excluded() {
        val path = Paths.get("/project")
        assertTrue(
            checker.buildAssignments(
                listOf(moduleData("mod", contentRoot = path)),
                mapOf(path to null),
            ).isEmpty()
        )
    }

    fun testBuildAssignments_nullContentRoot_excluded() {
        assertTrue(
            checker.buildAssignments(
                listOf(moduleData("mod", contentRoot = null)),
                emptyMap(),
            ).isEmpty()
        )
    }

    fun testBuildAssignments_preservesInsertionOrder() {
        val pathA = Paths.get("/a")
        val pathB = Paths.get("/b")
        val pathC = Paths.get("/c")
        val installed = { versions(elixir = elixirEntry("1.17", "/elixir", installed = true)) }
        val assignments = checker.buildAssignments(
            listOf(
                moduleData("alpha", contentRoot = pathA),
                moduleData("beta",  contentRoot = pathB),
                moduleData("gamma", contentRoot = pathC),
            ),
            mapOf(
                pathA to success(installed()),
                pathB to success(installed()),
                pathC to success(installed()),
            ),
        )
        assertEquals(listOf("alpha", "beta", "gamma"), assignments.keys.toList())
    }

    // -------------------------------------------------------------------------
    // detectMismatchIssues - Elixir cases
    // -------------------------------------------------------------------------

    fun testDetectMismatch_elixirVersionsMatch_noIssueNoTable() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val installPath = "/elixir-1.17"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(elixir = elixirEntry("1.17.3", installPath)))),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = mapOf(installPath to "1.17.3"),
        )
        assertTrue("No issues when Elixir versions match", issues.isEmpty())
        assertTrue("No table when Elixir versions match", tables.isEmpty())
    }

    fun testDetectMismatch_elixirVersionMismatch_producesIssueAndTable() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val installPath = "/elixir-1.18"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("myModule", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(elixir = elixirEntry("1.18.0", installPath)))),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = mapOf(installPath to "1.18.0"),
        )
        assertEquals(1, issues.size)
        assertEquals("myModule", issues[0].moduleName)
        assertFalse("Elixir mismatch is not a dangling reference", issues[0].isDangling)

        val table = tables["myModule"]
        assertNotNull("Table produced for mismatching module", table)
        val row = table!!.rows.single()
        assertEquals("Elixir", row.label)
        assertTrue("Row marked as mismatch", row.isMismatch)
        assertEquals("1.17.3", row.configuredVersion)
        assertEquals("1.18.0", row.toolManagerVersion)
    }

    // -------------------------------------------------------------------------
    // detectMismatchIssues - Erlang cases
    // -------------------------------------------------------------------------

    fun testDetectMismatch_erlangOtpMajorsMatch_noIssueNoTable() {
        val path = Paths.get("/project")
        val erlangHome = "/erlang-27"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", erlangHome = erlangHome, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(erlang = erlangEntry("27.3.4")))),
            elixirVersionBySdk = emptyMap(),
            erlangReleaseByHomePath = mapOf(erlangHome to Release("27", "27.3.4")),
            elixirVersionByInstallPath = emptyMap(),
        )
        assertTrue("No issues when Erlang OTP majors match", issues.isEmpty())
        assertTrue("No table when Erlang OTP majors match", tables.isEmpty())
    }

    fun testDetectMismatch_erlangOtpMajorMismatch_producesIssueAndTable() {
        val path = Paths.get("/project")
        val erlangHome = "/erlang-26"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("myModule", erlangHome = erlangHome, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(erlang = erlangEntry("27.3.4")))),
            elixirVersionBySdk = emptyMap(),
            erlangReleaseByHomePath = mapOf(erlangHome to Release("26", "26.2.5")),
            elixirVersionByInstallPath = emptyMap(),
        )
        assertEquals(1, issues.size)
        assertEquals("myModule", issues[0].moduleName)
        assertFalse("OTP mismatch is not a dangling reference", issues[0].isDangling)

        val table = tables["myModule"]
        assertNotNull("Table produced for mismatching module", table)
        val row = table!!.rows.single()
        assertEquals("Erlang", row.label)
        assertTrue("Row marked as mismatch", row.isMismatch)
        assertEquals("26.2.5", row.configuredVersion)   // Release.otpVersion → table's configuredVersion
        assertEquals("27.3.4", row.toolManagerVersion)
    }

    // -------------------------------------------------------------------------
    // detectMismatchIssues - combined and edge cases
    // -------------------------------------------------------------------------

    fun testDetectMismatch_bothElixirAndErlangMismatch_twoIssuesTwoRowTable() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val elixirInstall = "/elixir-1.18"
        val erlangHome = "/erlang-26"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("myModule", sdk = sdk, erlangHome = erlangHome, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(
                path to success(versions(
                    elixir = elixirEntry("1.18.0", elixirInstall),
                    erlang = erlangEntry("27.3.4"),
                ))
            ),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = mapOf(erlangHome to Release("26", "26.2.5")),
            elixirVersionByInstallPath = mapOf(elixirInstall to "1.18.0"),
        )
        assertEquals("Two issues for two mismatches", 2, issues.size)
        assertEquals("One table entry for the module", 1, tables.size)
        val rows = tables["myModule"]!!.rows
        assertEquals("Two rows in table (one per tool)", 2, rows.size)
        assertTrue("All rows are mismatches", rows.all { it.isMismatch })
        assertTrue(rows.any { it.label == "Elixir" })
        assertTrue(rows.any { it.label == "Erlang" })
    }

    fun testDetectMismatch_allVersionsMatch_noIssueNoTable() {
        // Both Elixir and Erlang present, both match → rows exist but none mismatch → no table.
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val elixirInstall = "/elixir-1.17"
        val erlangHome = "/erlang-27"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, erlangHome = erlangHome, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(
                path to success(versions(
                    elixir = elixirEntry("1.17.3", elixirInstall),
                    erlang = erlangEntry("27.3.4"),
                ))
            ),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = mapOf(erlangHome to Release("27", "27.3.4")),
            elixirVersionByInstallPath = mapOf(elixirInstall to "1.17.3"),
        )
        assertTrue("No issues when all versions match", issues.isEmpty())
        assertTrue("No table when no row mismatches", tables.isEmpty())
    }

    fun testDetectMismatch_noConfiguredSdk_noIssue() {
        // sdk = null → configuredVersion = null → isMismatch guard (both non-null) fails → no issue.
        val path = Paths.get("/project")
        val installPath = "/elixir-1.17"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = null, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(elixir = elixirEntry("1.17.3", installPath)))),
            elixirVersionBySdk = emptyMap(),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = mapOf(installPath to "1.17.3"),
        )
        assertTrue("No issue when no SDK is configured", issues.isEmpty())
        assertTrue("No table when no issue", tables.isEmpty())
    }

    fun testDetectMismatch_tmVersionNullInstallPathAbsent_noIssue_rowFallsBackToEntryVersion() {
        // installPath not in elixirVersionByInstallPath → tmVersion = null → isMismatch = false.
        // The row's toolManagerVersion falls back to tmElixir.version (the raw entry version string).
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val installPath = "/elixir-1.17"
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to success(versions(elixir = elixirEntry("1.17.3", installPath)))),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = emptyMap(),   // install path absent
        )
        assertTrue("No issue when tmVersion is null", issues.isEmpty())
        assertTrue("No table when tmVersion is null", tables.isEmpty())
    }

    fun testDetectMismatch_nullContentRoot_moduleSkipped() {
        val sdk = mock(Sdk::class.java)
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = null)),
            toolManagerResultsByRoot = emptyMap(),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = emptyMap(),
        )
        assertTrue("Module skipped when contentRoot is null", issues.isEmpty())
        assertTrue(tables.isEmpty())
    }

    fun testDetectMismatch_errorResult_moduleSkipped() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to error("config not trusted")),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = emptyMap(),
        )
        assertTrue("Module skipped when result is Error", issues.isEmpty())
        assertTrue(tables.isEmpty())
    }

    fun testDetectMismatch_nullResult_moduleSkipped() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(path to null),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = emptyMap(),
        )
        assertTrue("Module skipped when result is null", issues.isEmpty())
        assertTrue(tables.isEmpty())
    }

    fun testDetectMismatch_tableNameMatchesToolManagerName() {
        val sdk = mock(Sdk::class.java)
        val path = Paths.get("/project")
        val installPath = "/elixir-1.18"
        val (_, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(moduleData("mod", sdk = sdk, contentRoot = path)),
            toolManagerResultsByRoot = mapOf(
                path to success(versions(toolManagerName = "asdf", elixir = elixirEntry("1.18.0", installPath)))
            ),
            elixirVersionBySdk = mapOf(sdk to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = mapOf(installPath to "1.18.0"),
        )
        assertEquals("asdf", tables["mod"]?.toolManagerName)
    }

    fun testDetectMismatch_multipleModules_independentlyEvaluated() {
        val sdkA = mock(Sdk::class.java)
        val sdkB = mock(Sdk::class.java)
        val pathA = Paths.get("/a")
        val pathB = Paths.get("/b")
        val installA = "/elixir-1.18"
        val installB = "/elixir-1.17"
        // Module A mismatches; module B matches.
        val (issues, tables) = checker.detectMismatchIssues(
            moduleCheckData = listOf(
                moduleData("modA", sdk = sdkA, contentRoot = pathA),
                moduleData("modB", sdk = sdkB, contentRoot = pathB),
            ),
            toolManagerResultsByRoot = mapOf(
                pathA to success(versions(elixir = elixirEntry("1.18.0", installA))),
                pathB to success(versions(elixir = elixirEntry("1.17.3", installB))),
            ),
            elixirVersionBySdk = mapOf(sdkA to "1.17.3", sdkB to "1.17.3"),
            erlangReleaseByHomePath = emptyMap(),
            elixirVersionByInstallPath = mapOf(installA to "1.18.0", installB to "1.17.3"),
        )
        assertEquals("Only module A has a mismatch issue", 1, issues.size)
        assertEquals("modA", issues[0].moduleName)
        assertNotNull("Table for mismatching module A", tables["modA"])
        assertTrue("No table for matching module B", !tables.containsKey("modB"))
    }
}
