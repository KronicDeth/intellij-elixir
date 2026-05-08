package org.elixir_lang.status_bar_widget

import com.intellij.facet.FacetManager
import com.intellij.facet.FacetType
import com.intellij.facet.impl.FacetUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.SimpleJavaSdkType
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.ModuleRootModificationUtil
import com.intellij.openapi.roots.ProjectRootManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.elixir_lang.Facet
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.facet.Type
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

/**
 * Tests for [ElixirEditorBasedSdkWidget] detection logic (notification scan methods).
 *
 * These tests exercise the project-wide background notification helpers directly:
 * [ElixirEditorBasedSdkWidget.detectModuleSdkIssues] and
 * [ElixirEditorBasedSdkWidget.findModuleLevelElixirSdk].
 *
 * Tests scenarios from the project-sdk-fixes plan (adapted for the new widget class):
 * 1a. Project SDK = Java, module SDK = Elixir → no NotConfigured, no mismatch
 * 1b. Project SDK = Java, module SDK = Elixir → no mismatch issues
 * 2.  Project SDK = null, module SDK = Elixir → no NotConfigured
 * 3.  Project SDK = Elixir A, module SDK = Elixir B → mismatch reported
 * 5.  No Elixir modules → widget factory isAvailable() = false
 * 6.  Small IDE: stale Facet SDK reference, Elixir SDKs exist → dangling issue
 * 7.  Small IDE: Facet SDK resolves correctly → no issue
 * 8.  Small IDE: stale Facet SDK reference, no Elixir SDKs in table → no dangling (NotConfigured)
 */
class ElixirSdkStatusWidgetTest : PlatformTestCase() {

    private val addedSdks = mutableListOf<Sdk>()
    private lateinit var testScope: CoroutineScope

    override fun setUp() {
        super.setUp()
        testScope = CoroutineScope(SupervisorJob())
        ensureElixirFacet()
    }

    override fun tearDown() {
        try {
            testScope.cancel("test tearDown")
            ModuleRootModificationUtil.setModuleSdk(module, null)
            WriteAction.run<Throwable> {
                ProjectRootManager.getInstance(project).projectSdk = null
            }
            WriteAction.run<Throwable> {
                val jdkTable = ProjectJdkTable.getInstance()
                for (sdk in addedSdks) {
                    if (jdkTable.allJdks.contains(sdk)) {
                        jdkTable.removeJdk(sdk)
                    }
                }
                addedSdks.clear()
            }
            removeElixirFacetLibraries()
        } finally {
            super.tearDown()
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun ensureElixirFacet() {
        val facetManager = FacetManager.getInstance(module)
        if (facetManager.getFacetByType(Facet.ID) == null) {
            FacetUtil.addFacet(module, FacetType.findInstance(Type::class.java))
        }
    }

    private fun removeElixirFacet() {
        val facetManager = FacetManager.getInstance(module)
        val facet = facetManager.getFacetByType(Facet.ID) ?: return
        ApplicationManager.getApplication().runWriteAction {
            val model = facetManager.createModifiableModel()
            model.removeFacet(facet)
            model.commit()
        }
    }

    private fun removeElixirFacetLibraries() {
        val facetManager = FacetManager.getInstance(module)
        val facet = facetManager.getFacetByType(Facet.ID) ?: return
        ApplicationManager.getApplication().runWriteAction {
            facet.sdk = null
        }
    }

    private fun createAndRegisterElixirSdk(name: String): Sdk {
        val sdk = ProjectJdkImpl(name, ElixirSdkType.instance)
        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().addJdk(sdk)
        }
        addedSdks.add(sdk)
        return sdk
    }

    private fun createJavaSdk(): Sdk {
        val javaHome = System.getProperty("java.home") ?: "/usr/lib/jvm/java"
        return SimpleJavaSdkType().createJdk("Java Mock", javaHome)
    }

    private fun setModuleSdk(sdk: Sdk) {
        ModuleRootModificationUtil.setModuleSdk(module, sdk)
    }

    private fun setProjectSdk(sdk: Sdk?) {
        WriteAction.run<Throwable> {
            ProjectRootManager.getInstance(project).projectSdk = sdk
        }
    }

    private fun setFacetSdk(sdk: Sdk) {
        val facetManager = FacetManager.getInstance(module)
        val facet = facetManager.getFacetByType(Facet.ID) ?: error("No Elixir Facet on module")
        ApplicationManager.getApplication().runWriteAction {
            facet.sdk = sdk
        }
    }

    private fun createWidget(): ElixirEditorBasedSdkWidget = ElixirEditorBasedSdkWidget(project, testScope)

    /**
     * Calls [ElixirEditorBasedSdkWidget.detectModuleSdkIssues] from the EDT-bound test body.
     *
     * [detectModuleSdkIssues] asserts it runs off the EDT.  Test methods run on the EDT, so we
     * dispatch to a pooled background thread via [com.intellij.openapi.application.Application.executeOnPooledThread] and
     * use the synchronous (blocking, non-suspending) [com.intellij.openapi.application.Application.runReadAction]
     * to acquire the read lock.  [java.util.concurrent.Future.get] blocks the EDT while waiting;
     * this is safe because there are no pending write actions at the call site, so the background
     * thread acquires the read lock immediately without needing the EDT.
     *
     * Prefer this over `runBlocking { readAction { } }` on the EDT: the suspend form of
     * `readAction` internally needs the EDT to pump events for write-action coordination, which
     * deadlocks when the EDT is already blocked by `runBlocking`.
     */
    private fun ElixirEditorBasedSdkWidget.detectModuleSdkIssuesInTest(): List<ModuleSdkIssue> =
        ApplicationManager.getApplication().executeOnPooledThread<List<ModuleSdkIssue>> {
            ApplicationManager.getApplication().runReadAction<List<ModuleSdkIssue>> {
                detectModuleSdkIssues()
            }
        }.get()

    // -------------------------------------------------------------------------
    // Scenario 1: Project SDK = Java, module SDK = Elixir
    // -------------------------------------------------------------------------

    fun testModuleElixirSdkFoundWhenProjectSdkIsJava() {
        val elixirSdk = createAndRegisterElixirSdk("Elixir Mock")
        val javaSdk = createJavaSdk()

        setProjectSdk(javaSdk)
        setModuleSdk(elixirSdk)

        val widget = createWidget()
        val foundSdk = widget.findModuleLevelElixirSdk()

        assertNotNull("Should find Elixir SDK from module even when project SDK is Java", foundSdk)
        assertEquals("Should return the module's Elixir SDK", elixirSdk, foundSdk)
    }

    fun testNoMismatchWhenProjectSdkIsJavaAndModuleSdkIsElixir() {
        val elixirSdk = createAndRegisterElixirSdk("Elixir Mock")
        val javaSdk = createJavaSdk()

        setProjectSdk(javaSdk)
        setModuleSdk(elixirSdk)

        val widget = createWidget()
        val issues = widget.detectModuleSdkIssuesInTest()

        assertTrue(
            "No module SDK issues should be reported when project SDK is non-Elixir; got: $issues",
            issues.isEmpty()
        )
    }

    // -------------------------------------------------------------------------
    // Scenario 2: Project SDK = null, module SDK = Elixir
    // -------------------------------------------------------------------------

    fun testModuleElixirSdkFoundWhenProjectSdkIsNull() {
        val elixirSdk = createAndRegisterElixirSdk("Elixir Mock")

        setProjectSdk(null)
        setModuleSdk(elixirSdk)

        val widget = createWidget()
        val foundSdk = widget.findModuleLevelElixirSdk()

        assertNotNull("Should find Elixir SDK from module when project SDK is null", foundSdk)
        assertEquals(elixirSdk, foundSdk)
    }

    // -------------------------------------------------------------------------
    // Scenario 3: Project SDK = Elixir A, module SDK = Elixir B → mismatch reported
    // -------------------------------------------------------------------------

    fun testMismatchReportedWhenModuleSdkDiffersFromElixirProjectSdk() {
        val elixirSdkA = createAndRegisterElixirSdk("Elixir 1.17")
        val elixirSdkB = createAndRegisterElixirSdk("Elixir 1.16")

        setProjectSdk(elixirSdkA)
        setModuleSdk(elixirSdkB)

        val widget = createWidget()
        val issues = widget.detectModuleSdkIssuesInTest()

        assertTrue(
            "Expected a mismatch issue (project=Elixir 1.17, module=Elixir 1.16); got: $issues",
            issues.any { !it.isDangling && it.moduleName == module.name }
        )
    }

    // -------------------------------------------------------------------------
    // Scenario 5: No Elixir modules → widget factory isAvailable = false
    // -------------------------------------------------------------------------

    fun testWidgetFactoryNotAvailableWhenNoElixirModules() {
        removeElixirFacet()

        val factory = ElixirSdkStatusWidgetFactory()
        assertFalse(
            "Widget should not be available when the project has no Elixir modules",
            factory.isAvailable(project)
        )
    }

    // -------------------------------------------------------------------------
    // Scenario 6: Small IDE - stale Facet SDK, SDKs exist → dangling issue
    // -------------------------------------------------------------------------

    fun testDanglingFacetSdkReportedWhenElixirSdkExistsButFacetReferenceIsStale() {
        val staleSdk = createAndRegisterElixirSdk("Elixir 1.17")
        setFacetSdk(staleSdk)

        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().removeJdk(staleSdk)
        }
        addedSdks.remove(staleSdk)

        // Register a different SDK so Facet.sdks().isNotEmpty() → stale (not NotConfigured)
        createAndRegisterElixirSdk("Elixir 1.18")

        // Do NOT call setModuleSdk() - no JdkOrderEntry, simulating Small IDE path

        val widget = createWidget()
        val issues = widget.detectModuleSdkIssuesInTest()

        assertTrue(
            "Expected a dangling issue for stale Facet SDK reference; got: $issues",
            issues.any { it.isDangling && it.moduleName == module.name }
        )
    }

    // -------------------------------------------------------------------------
    // Scenario 7: Small IDE - Facet SDK resolves correctly → no issue
    // -------------------------------------------------------------------------

    fun testNoDanglingWhenFacetSdkResolvesCorrectly() {
        val registeredSdk = createAndRegisterElixirSdk("Elixir 1.17")
        setFacetSdk(registeredSdk)

        val widget = createWidget()
        val issues = widget.detectModuleSdkIssuesInTest()

        assertTrue(
            "Expected no issues when Facet SDK resolves correctly; got: $issues",
            issues.isEmpty()
        )
    }

    // -------------------------------------------------------------------------
    // Scenario 8: Small IDE - stale Facet SDK, NO Elixir SDKs in table → no dangling
    // -------------------------------------------------------------------------

    fun testNoDanglingWhenNoElixirSdksExistInTable() {
        val sdk = createAndRegisterElixirSdk("Elixir 1.17")
        setFacetSdk(sdk)

        WriteAction.run<Throwable> {
            ProjectJdkTable.getInstance().removeJdk(sdk)
        }
        addedSdks.remove(sdk)

        assertTrue("Precondition: no Elixir SDKs in table", Facet.sdks().isEmpty())

        val widget = createWidget()
        val issues = widget.detectModuleSdkIssuesInTest()

        assertTrue(
            "Expected no dangling issue when no Elixir SDKs exist (this is NotConfigured); got: $issues",
            issues.isEmpty()
        )
    }

    // -------------------------------------------------------------------------
    // Smoke tests (regression)
    // -------------------------------------------------------------------------

    fun testWidgetIdConstant() {
        assertEquals("ElixirSdkStatus", ElixirEditorBasedSdkWidget.ID)
    }
}
