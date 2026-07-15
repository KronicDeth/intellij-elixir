package org.elixir_lang.facet.sdks

import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import com.intellij.testFramework.common.runAll
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.facet.SdksService
import org.elixir_lang.facet.sdk.Model
import org.elixir_lang.facet.sdks.elixir.Configurable as ElixirSdksConfigurable
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType

/**
 * Tests for the Settings → Elixir → SDKs page ([org.elixir_lang.facet.sdks.Configurable]) and the
 * SDK chooser [Model], covering the regressions:
 *  - the list stays empty even though SDKs exist (model not populated),
 *  - removing an SDK throws an NPE, and
 *  - a removed SDK "ghosts" in the chooser.
 */
class ElixirSdksConfigurableTest : PlatformTestCase() {

    private val added = mutableListOf<Sdk>()

    override fun setUp() {
        super.setUp()
        service().resetForTests()
    }

    override fun tearDown() {
        runAll(
            {
                WriteAction.run<Throwable> {
                    val table = ProjectJdkTable.getInstance()
                    added.filter { table.allJdks.contains(it) }.forEach { table.removeJdk(it) }
                }
                added.clear()
            },
            { service().resetForTests() },
            { super.tearDown() },
        )
    }

    private fun service() = SdksService.getInstance()!!

    private fun addElixirSdkToTable(name: String): Sdk {
        val sdk = ProjectJdkImpl(name, ElixirSdkType.instance)
        WriteAction.run<Throwable> { ProjectJdkTable.getInstance().addJdk(sdk) }
        added.add(sdk)
        return sdk
    }

    private fun registerElixirSdk(name: String): Sdk {
        val sdk = addElixirSdkToTable(name)
        service().resetForTests()   // rebuild the model so it reflects the new table entry
        return sdk
    }

    private fun listedNames(): kotlin.collections.List<String> =
        service().projectJdkImplList(ElixirSdkType::class.java).map { it.name }

    private fun modelClone(name: String): ProjectJdkImpl =
        service().getModel().findSdk(name) as ProjectJdkImpl

    private fun comboNames(model: Model): kotlin.collections.List<String> =
        model.items.mapNotNull { it?.name }

    fun testListReflectsRegisteredSdk() {
        registerElixirSdk("Elixir Test A")
        assertTrue("SDK should be listed; got ${listedNames()}", "Elixir Test A" in listedNames())
    }

    fun testRemoveDoesNotThrowAndRemovesSdk() {
        registerElixirSdk("Elixir Test B")

        val configurable = ElixirSdksConfigurable()
        configurable.createComponent()
        configurable.reset()

        // Regression: this used to NPE (findSdk(clone)!! returned null after model re-cloning).
        configurable.removeSelectedSdk(modelClone("Elixir Test B"))

        assertFalse("model should no longer list it; got ${listedNames()}", "Elixir Test B" in listedNames())

        configurable.apply()
        assertFalse(
            "SDK should be gone from the JDK table",
            ProjectJdkTable.getInstance().allJdks.any { it.name == "Elixir Test B" },
        )
    }

    fun testExternalSdkAdditionRefreshesCachedModel() {
        // Prime the cached model without the SDK.
        assertFalse("Elixir Test D" in listedNames())

        // Register directly in the JDK table (as the tool-manager "Configure from mise" action does)
        // WITHOUT resetForTests - the JDK-table listener in SdksService must invalidate the cached
        // model so it no longer takes an IDE restart for the SDK to appear in Settings.
        addElixirSdkToTable("Elixir Test D")

        assertTrue("cached model should refresh after external add; got ${listedNames()}", "Elixir Test D" in listedNames())
    }

    fun testDisposeUIResourcesRemovesSdkModelListener() {
        val configurable = ElixirSdksConfigurable()
        configurable.createComponent()
        configurable.reset()

        val libraryTable = LibraryTablesRegistrar.getInstance().libraryTable
        try {
            // While the page is open, its SdkModel listener mirrors model additions into the
            // application library table (that is its observable side effect).
            service().getModel().addSdk(ProjectJdkImpl("Elixir Listener A", ElixirSdkType.instance))
            assertNotNull(
                "sanity: listener should mirror the SDK into the library table while the page is open",
                libraryTable.getLibraryByName("Elixir Listener A"),
            )

            configurable.disposeUIResources()

            // After disposal the listener must be removed from the shared, app-lifetime model -
            // otherwise one listener accumulates per Settings open.
            service().getModel().addSdk(ProjectJdkImpl("Elixir Listener B", ElixirSdkType.instance))
            assertNull(
                "listener must be removed on disposeUIResources",
                libraryTable.getLibraryByName("Elixir Listener B"),
            )
        } finally {
            WriteAction.run<Throwable> {
                listOf("Elixir Listener A", "Elixir Listener B").forEach { name ->
                    libraryTable.getLibraryByName(name)?.let { libraryTable.removeLibrary(it) }
                }
            }
        }
    }

    fun testRemovedSdkDoesNotGhostInChooser() {
        registerElixirSdk("Elixir Test C")

        val model = Model()
        assertTrue("chooser should list it initially; got ${comboNames(model)}", "Elixir Test C" in comboNames(model))

        // Removing via the model fires beforeSdkRemove(original); the chooser must drop it by name.
        service().getModel().removeSdk(modelClone("Elixir Test C"))

        assertFalse("removed SDK must not ghost in the chooser; got ${comboNames(model)}", "Elixir Test C" in comboNames(model))
    }
}
