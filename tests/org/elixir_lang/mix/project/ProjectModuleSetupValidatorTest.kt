package org.elixir_lang.mix.project

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import org.elixir_lang.Facet
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.facet.Type
import org.elixir_lang.mix.project.ProjectModuleSetupValidator.detectFolderMarkIssues

class ProjectModuleSetupValidatorTest : PlatformTestCase() {

    /** URLs of content entries added during a test, cleaned up in [tearDown]. */
    private val addedContentRootUrls = mutableListOf<String>()

    override fun setUp() {
        super.setUp()

        // The validator uses mixContentRoots() which requires isElixirModule() == true.
        // Light test fixtures default to JAVA_MODULE, so we add the Elixir Facet explicitly.
        val facetManager = FacetManager.getInstance(module)
        if (facetManager.getFacetByType(Facet.ID) == null) {
            FacetUtil.addFacet(module, FacetType.findInstance(Type::class.java))
        }
    }

    override fun tearDown() {
        try {
            if (addedContentRootUrls.isNotEmpty()) {
                ModuleRootModificationUtil.updateModel(module) { model ->
                    for (entry in model.contentEntries.toList()) {
                        if (entry.url in addedContentRootUrls) {
                            model.removeContentEntry(entry)
                        }
                    }
                }
                addedContentRootUrls.clear()
            }
        } finally {
            super.tearDown()
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a Mix project directory under the fixture temp dir with a `mix.exs` marker, then
     * adds a content entry for it to the test module and calls [block] to configure source/exclude
     * folders before committing.  Returns the root [com.intellij.openapi.vfs.VirtualFile].
     */
    private fun addMixContentEntry(
        dirName: String,
        block: (
            content: com.intellij.openapi.roots.ContentEntry,
            rootUrl: String
        ) -> Unit = { _, _ -> }
    ): com.intellij.openapi.vfs.VirtualFile {
        val appRoot = myFixture.tempDirFixture.findOrCreateDir(dirName)
        myFixture.tempDirFixture.createFile("$dirName/mix.exs", "")

        ModuleRootModificationUtil.updateModel(module) { model ->
            val content = model.addContentEntry(appRoot)
            block(content, appRoot.url)
        }

        addedContentRootUrls.add(appRoot.url)
        return appRoot
    }

    private fun detectFolderMarkIssuesOnBackgroundThread(): List<ProjectModuleSetupValidator.FolderMarkIssue> {
        return ApplicationManager.getApplication()
            .executeOnPooledThread<List<ProjectModuleSetupValidator.FolderMarkIssue>> {
                detectFolderMarkIssues(project)
            }
            .get()
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    /**
     * A module whose content root contains no `mix.exs` must be silently skipped - it is not an
     * Elixir Mix app and the validator must not report any issue for it.
     */
    fun testNonMixModuleIsSkipped() {
        // The light test module's default content entry has no mix.exs child - skip it.
        val nonMixRoot = myFixture.tempDirFixture.findOrCreateDir("non_mix_app")
        // Deliberately do NOT create mix.exs here.

        ModuleRootModificationUtil.updateModel(module) { model ->
            model.addContentEntry(nonMixRoot)
        }
        addedContentRootUrls.add(nonMixRoot.url)

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        // Only non-Mix content entries have been set up, so no issues should be reported.
        val nonMixIssues = issues.filter { it.moduleName == module.name }
        assertTrue(
            "Non-Mix module (no mix.exs) must produce no folder mark issues",
            nonMixIssues.isEmpty()
        )
    }

    /**
     * A fully-configured Mix module - one where all canonical folders have the correct marks - must
     * produce an empty issue list.
     */
    fun testFullyConfiguredModuleProducesNoIssues() {
        addMixContentEntry("fully_configured_app") { content, rootUrl ->
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/web", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/deps")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        // Create all canonical directories on disk so the validator actually checks each mark
        // (not just skips them as absent).  This exercises the positive-match path where the
        // directory exists and the mark is correct.
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/test")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/.elixir_ls")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/assets/node_modules/phoenix")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/assets/node_modules/phoenix_html")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/cover")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/deps")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/doc")
        myFixture.tempDirFixture.findOrCreateDir("fully_configured_app/logs")

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val appIssues = issues.filter { it.moduleName == module.name }
        assertTrue(
            "Fully configured module must have no folder mark issues, but got: $appIssues",
            appIssues.isEmpty()
        )
    }

    /**
     * When `test/` exists on disk but is not marked as Test Sources, the validator must report a
     * single issue for that directory with [FolderMark.TEST_SOURCES].
     */
    fun testMissingTestSourceMarkReportsIssue() {
        myFixture.tempDirFixture.findOrCreateDir("missing_test_mark_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("missing_test_mark_app/test")

        addMixContentEntry("missing_test_mark_app") { content, rootUrl ->
            // lib/ is correctly marked as Sources.
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/web", false)
            content.addSourceFolder("$rootUrl/spec", true)
            // test/ is intentionally NOT added - simulates an unconfigured module.
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/deps")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val testIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "test"
        }
        assertEquals("Expected exactly one issue for test/", 1, testIssues.size)

        val issue = testIssues.single()
        assertEquals(FolderMark.TEST_SOURCES, issue.folderMark)
        assertEquals("test", issue.folderRelativePath)
        assertEquals("unmarked", issue.currentState)
    }

    /**
     * When `lib/` exists on disk but is marked as Test Sources instead of Sources, the validator
     * must report an issue with [FolderMark.SOURCES] and a `currentState` of `"test sources"`.
     */
    fun testLibMarkedAsTestSourcesReportsIssue() {
        myFixture.tempDirFixture.findOrCreateDir("wrong_lib_mark_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("wrong_lib_mark_app/test")

        addMixContentEntry("wrong_lib_mark_app") { content, rootUrl ->
            // lib/ incorrectly marked as Test Sources.
            content.addSourceFolder("$rootUrl/lib", true)
            content.addSourceFolder("$rootUrl/web", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            content.addExcludeFolder("$rootUrl/deps")
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val libIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "lib"
        }
        assertEquals("Expected exactly one issue for lib/", 1, libIssues.size)

        val issue = libIssues.single()
        assertEquals(FolderMark.SOURCES, issue.folderMark)
        assertEquals("test sources", issue.currentState)
    }

    /**
     * Canonical exclusions are NOT reported when the directory does not exist on disk.
     * The reconfigure action still applies marks by URL (so they're ready when the dir appears),
     * but the validator should not warn about non-existent folders.
     */
    fun testMissingDepsExclusionNotReportedWithoutDirOnDisk() {
        myFixture.tempDirFixture.findOrCreateDir("missing_deps_app/lib")

        addMixContentEntry("missing_deps_app") { content, rootUrl ->
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/web", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            // deps/ exclusion intentionally omitted.
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val depsIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "deps"
        }
        assertEquals("deps/ exclusion must NOT be reported when absent on disk", 0, depsIssues.size)
    }

    /**
     * When `deps/` exists on disk but is not marked as Excluded, the validator must report
     * an issue with [FolderMark.EXCLUDED].
     */
    fun testMissingDepsExclusionReportedWhenDirExistsOnDisk() {
        myFixture.tempDirFixture.findOrCreateDir("deps_exists_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("deps_exists_app/deps")

        addMixContentEntry("deps_exists_app") { content, rootUrl ->
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/web", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            // deps/ exclusion intentionally omitted, but deps/ dir DOES exist on disk.
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val depsIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "deps"
        }
        assertEquals(
            "deps/ exclusion must be reported when the directory exists on disk",
            1,
            depsIssues.size
        )

        val issue = depsIssues.single()
        assertEquals(FolderMark.EXCLUDED, issue.folderMark)
        assertEquals("unmarked", issue.currentState)
    }

    /**
     * Canonical source folders are NOT reported when the corresponding directory is not present
     * on disk.  The reconfigure action applies marks by URL regardless, but the validator only
     * warns about folders that actually exist.
     */
    fun testWebFolderNotReportedWhenAbsent() {
        myFixture.tempDirFixture.findOrCreateDir("no_web_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("no_web_app/test")

        addMixContentEntry("no_web_app") { content, rootUrl ->
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            // All canonical exclusions present; web/ is intentionally missing.
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/deps")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val webIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "web"
        }
        assertEquals("web/ must NOT be reported when absent on disk", 0, webIssues.size)
    }

    /**
     * When `web/` exists on disk (pre-Phoenix 1.3 project) but is not marked as Sources, the
     * validator must report an issue with [FolderMark.SOURCES].
     */
    fun testConditionalWebFolderReportedWhenPresentButUnmarked() {
        myFixture.tempDirFixture.findOrCreateDir("legacy_web_app/lib")
        myFixture.tempDirFixture.findOrCreateDir("legacy_web_app/web")

        addMixContentEntry("legacy_web_app") { content, rootUrl ->
            content.addSourceFolder("$rootUrl/lib", false)
            content.addSourceFolder("$rootUrl/spec", true)
            content.addSourceFolder("$rootUrl/test", true)
            // web/ exists on disk but is intentionally NOT marked.
            content.addExcludeFolder("$rootUrl/deps")
            content.addExcludeFolder("$rootUrl/.elixir_ls")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix")
            content.addExcludeFolder("$rootUrl/assets/node_modules/phoenix_html")
            content.addExcludeFolder("$rootUrl/cover")
            content.addExcludeFolder("$rootUrl/doc")
            content.addExcludeFolder("$rootUrl/logs")
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()

        val webIssues = issues.filter {
            it.moduleName == module.name && it.folderRelativePath == "web"
        }
        assertEquals("Expected exactly one issue for web/", 1, webIssues.size)
        assertEquals(FolderMark.SOURCES, webIssues.single().folderMark)
    }

    /**
     * Umbrella projects have multiple content entries in the same module (or one module per
     * sub-app).  The validator must independently validate each content entry that contains a
     * `mix.exs`.  A correctly-configured sub-app must not affect the reporting for a
     * misconfigured sibling.
     */
    fun testUmbrellaMultipleContentEntriesValidatedIndependently() {
        // Sub-app A: fully configured - should produce no issues.
        myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_a/lib")
        myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_a/test")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_a/mix.exs", "")

        // Sub-app B: misconfigured (missing test/ source mark and deps/ exclusion).
        myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_b/lib")
        myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_b/test")
        myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_b/deps")
        myFixture.tempDirFixture.createFile("umbrella/apps/app_b/mix.exs", "")

        val appARoot = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_a")
        val appBRoot = myFixture.tempDirFixture.findOrCreateDir("umbrella/apps/app_b")

        ModuleRootModificationUtil.updateModel(module) { model ->
            // Sub-app A - correctly configured.
            val contentA = model.addContentEntry(appARoot)
            contentA.addSourceFolder("${appARoot.url}/lib", false)
            contentA.addSourceFolder("${appARoot.url}/web", false)
            contentA.addSourceFolder("${appARoot.url}/spec", true)
            contentA.addSourceFolder("${appARoot.url}/test", true)
            contentA.addExcludeFolder("${appARoot.url}/.elixir_ls")
            contentA.addExcludeFolder("${appARoot.url}/assets/node_modules/phoenix")
            contentA.addExcludeFolder("${appARoot.url}/assets/node_modules/phoenix_html")
            contentA.addExcludeFolder("${appARoot.url}/cover")
            contentA.addExcludeFolder("${appARoot.url}/deps")
            contentA.addExcludeFolder("${appARoot.url}/doc")
            contentA.addExcludeFolder("${appARoot.url}/logs")

            // Sub-app B - missing test/ mark and deps/ exclusion.
            val contentB = model.addContentEntry(appBRoot)
            contentB.addSourceFolder("${appBRoot.url}/lib", false)
            contentB.addSourceFolder("${appBRoot.url}/web", false)
            contentB.addSourceFolder("${appBRoot.url}/spec", true)
            // test/ intentionally NOT marked.
            contentB.addExcludeFolder("${appBRoot.url}/.elixir_ls")
            contentB.addExcludeFolder("${appBRoot.url}/assets/node_modules/phoenix")
            contentB.addExcludeFolder("${appBRoot.url}/assets/node_modules/phoenix_html")
            contentB.addExcludeFolder("${appBRoot.url}/cover")
            // deps/ exclusion intentionally omitted.
            contentB.addExcludeFolder("${appBRoot.url}/doc")
            contentB.addExcludeFolder("${appBRoot.url}/logs")
        }
        addedContentRootUrls.add(appARoot.url)
        addedContentRootUrls.add(appBRoot.url)

        val issues = detectFolderMarkIssuesOnBackgroundThread()
        val moduleIssues = issues.filter { it.moduleName == module.name }

        // Sub-app A is fully configured - it must contribute zero issues.
        // Sub-app B is missing test/ mark and deps/ exclusion.
        val testIssues = moduleIssues.filter { it.folderRelativePath == "test" }
        val depsIssues = moduleIssues.filter { it.folderRelativePath == "deps" }

        // Only app_b is missing test/ mark → exactly 1 test issue
        assertEquals(
            "Only one sub-app (app_b) should report a test/ issue",
            1,
            testIssues.size
        )
        assertEquals(FolderMark.TEST_SOURCES, testIssues.single().folderMark)

        // Only app_b is missing deps/ exclusion → exactly 1 deps issue
        assertEquals(
            "Only one sub-app (app_b) should report a deps/ issue",
            1,
            depsIssues.size
        )
        assertEquals(FolderMark.EXCLUDED, depsIssues.single().folderMark)
    }

    /**
     * When an umbrella project is imported as a single module (one content entry at the umbrella
     * root), sub-apps under `apps/` that have no folder marks on the content entry must produce
     * [ProjectModuleSetupValidator.FolderMarkIssue]s for each missing mark.
     */
    fun testUmbrellaSubAppsNotConfiguredReportsFolderMarkIssues() {
        // Set up umbrella root with mix.exs and a single sub-app (app_a) with lib/ and test/.
        myFixture.tempDirFixture.findOrCreateDir("umbrella_single/apps/app_a/lib")
        myFixture.tempDirFixture.findOrCreateDir("umbrella_single/apps/app_a/test")
        myFixture.tempDirFixture.createFile("umbrella_single/apps/app_a/mix.exs", "")

        addMixContentEntry("umbrella_single") { _, _ ->
            // No sub-app folder marks added - simulates an unconfigured single-module umbrella.
        }

        val issues = detectFolderMarkIssuesOnBackgroundThread()
        val moduleIssues = issues.filter { it.moduleName == module.name }

        val libIssues = moduleIssues.filter { it.folderRelativePath == "apps/app_a/lib" }
        val testIssues = moduleIssues.filter { it.folderRelativePath == "apps/app_a/test" }

        assertEquals(
            "Expected exactly one issue for apps/app_a/lib (unmarked Sources)",
            1,
            libIssues.size,
        )
        assertEquals(FolderMark.SOURCES, libIssues.single().folderMark)
        assertEquals("unmarked", libIssues.single().currentState)

        assertEquals(
            "Expected exactly one issue for apps/app_a/test (unmarked Test Sources)",
            1,
            testIssues.size,
        )
        assertEquals(FolderMark.TEST_SOURCES, testIssues.single().folderMark)
        assertEquals("unmarked", testIssues.single().currentState)
    }

    /**
     * When one sub-app (`app_a`) already has its own content entry (e.g., from the import wizard),
     * the umbrella scan must skip it and only report issues for sub-apps (`app_b`) that do NOT
     * have their own content entry.
     */
    fun testUmbrellaSubAppAlreadyCoveredByContentEntryIsSkipped() {
        // Umbrella root with mix.exs.
        // app_a: has its own content entry → umbrella scan must skip it.
        // app_b: only a plain directory under apps/ → umbrella scan must report it.
        myFixture.tempDirFixture.findOrCreateDir("umbrella_mixed/apps/app_a/lib")
        myFixture.tempDirFixture.findOrCreateDir("umbrella_mixed/apps/app_a/test")
        myFixture.tempDirFixture.createFile("umbrella_mixed/apps/app_a/mix.exs", "")

        myFixture.tempDirFixture.findOrCreateDir("umbrella_mixed/apps/app_b/lib")
        myFixture.tempDirFixture.findOrCreateDir("umbrella_mixed/apps/app_b/test")
        myFixture.tempDirFixture.createFile("umbrella_mixed/apps/app_b/mix.exs", "")

        // Umbrella root content entry (no sub-app marks).
        addMixContentEntry("umbrella_mixed") { _, _ -> }

        // app_a gets its own content entry, fully configured - the per-entry loop handles it.
        val appARoot = myFixture.tempDirFixture.findOrCreateDir("umbrella_mixed/apps/app_a")
        ModuleRootModificationUtil.updateModel(module) { model ->
            val contentA = model.addContentEntry(appARoot)
            contentA.addSourceFolder("${appARoot.url}/lib", false)
            contentA.addSourceFolder("${appARoot.url}/test", true)
        }
        addedContentRootUrls.add(appARoot.url)

        val issues = detectFolderMarkIssuesOnBackgroundThread()
        val moduleIssues = issues.filter { it.moduleName == module.name }

        // app_a is covered by its own content entry and fully configured → no umbrella issues.
        val appAIssues = moduleIssues.filter { it.folderRelativePath.startsWith("apps/app_a/") }
        assertTrue(
            "app_a is covered by its own content entry and must produce no umbrella issues, got: $appAIssues",
            appAIssues.isEmpty(),
        )

        // app_b has no content entry → umbrella scan must report lib/ and test/.
        val appBLibIssues = moduleIssues.filter { it.folderRelativePath == "apps/app_b/lib" }
        val appBTestIssues = moduleIssues.filter { it.folderRelativePath == "apps/app_b/test" }

        assertEquals("Expected one issue for apps/app_b/lib", 1, appBLibIssues.size)
        assertEquals(FolderMark.SOURCES, appBLibIssues.single().folderMark)

        assertEquals("Expected one issue for apps/app_b/test", 1, appBTestIssues.size)
        assertEquals(FolderMark.TEST_SOURCES, appBTestIssues.single().folderMark)
    }

    override fun getTestDataPath(): String = "testData/org/elixir_lang/mix/project"
}
