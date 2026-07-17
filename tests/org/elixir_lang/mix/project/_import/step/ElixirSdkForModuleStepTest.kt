package org.elixir_lang.mix.project._import.step

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.eel.provider.getEelDescriptor
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.elixir.Type
import java.nio.file.Path

/**
 * Tests for the EEL-aware SDK filtering in [ElixirSdkForModuleStep].
 *
 * WSL/remote environments cannot be simulated in platform tests, so these tests pin down the
 * behaviour that is observable locally: a local import target resolves to the local machine,
 * visibility is permissive when an SDK's environment cannot be determined, and the post-reset
 * sync loop ([ElixirSdkForModuleStep.addImportTargetSdks] via updateStep()) never adds spurious
 * SDKs or marks the model modified for a local-only import.
 */
class ElixirSdkForModuleStepTest : PlatformTestCase() {

    private fun step(importDirectory: String): ElixirSdkForModuleStep {
        val wizardContext = WizardContext(null, testRootDisposable)
        wizardContext.setProjectFileDirectory(importDirectory)

        return ElixirSdkForModuleStep(wizardContext)
    }

    private fun localImportDirectory(): String = FileUtil.createTempDirectory("mixImport", null).path

    private fun registerElixirSdk(name: String, homePath: String? = null): Sdk {
        val sdk = ProjectJdkImpl(name, Type.instance)

        WriteAction.run<Throwable> {
            if (homePath != null) {
                val modificator = sdk.sdkModificator
                modificator.homePath = homePath
                modificator.commitChanges()
            }
            ProjectJdkTable.getInstance().addJdk(sdk, testRootDisposable)
        }

        return sdk
    }

    fun testImportTargetMachineForLocalDirectoryIsLocalMachine() {
        val targetMachine = step(localImportDirectory()).importTargetMachine()

        assertNotNull("A local import directory should resolve to a machine", targetMachine)
        assertEquals(
            "A local import directory should resolve to the same machine as any other local path",
            Path.of(FileUtil.getTempDirectory()).getEelDescriptor().machine,
            targetMachine
        )
    }

    fun testSdkMachineIsNullWithoutHomePath() {
        val sdk = registerElixirSdk("Homeless Elixir")

        assertNull(
            "An SDK without a home path has no determinable machine",
            step(localImportDirectory()).sdkMachine(sdk)
        )
    }

    fun testSdkMachineIsNullForUnparseableHomePath() {
        // A NUL character is rejected by Path.of() on every platform
        val sdk = registerElixirSdk("Unparseable Elixir", homePath = "\u0000invalid")

        assertNull(
            "An SDK with an unparseable home path has no determinable machine",
            step(localImportDirectory()).sdkMachine(sdk)
        )
    }

    fun testSdkMachineForLocalHomeMatchesLocalImportTarget() {
        val sdk = registerElixirSdk("Local Elixir", homePath = localImportDirectory())
        val moduleStep = step(localImportDirectory())

        assertEquals(
            "An SDK with a local home should be on the same machine as a local import target",
            moduleStep.importTargetMachine(),
            moduleStep.sdkMachine(sdk)
        )
    }

    fun testSdkWithUnknownMachineIsVisible() {
        val sdk = registerElixirSdk("Homeless Elixir")

        assertTrue(
            "Visibility should be permissive when the SDK's environment cannot be determined",
            step(localImportDirectory()).sdkVisibleForImportTarget(sdk)
        )
    }

    fun testLocalSdkIsVisibleForLocalImportTarget() {
        val sdk = registerElixirSdk("Local Elixir", homePath = localImportDirectory())

        assertTrue(
            "A local SDK should be visible for a local import target",
            step(localImportDirectory()).sdkVisibleForImportTarget(sdk)
        )
    }

    fun testUpdateStepPopulatesLocalSdkWithoutModifyingModel() {
        val sdk = registerElixirSdk("Local Elixir", homePath = localImportDirectory())
        val moduleStep = step(localImportDirectory())

        moduleStep.updateStep()

        val projectSdksModel = moduleStep.projectSdksModel
        assertTrue(
            "reset() should pick up a local SDK for a local import target",
            projectSdksModel.projectSdks.containsKey(sdk)
        )
        assertFalse(
            "The import-target sync loop must not mark the model modified for a local-only import",
            projectSdksModel.isModified
        )

        val sdkCountAfterFirstUpdate = projectSdksModel.projectSdks.size
        moduleStep.updateStep()

        assertEquals(
            "Repeated updateStep() must not duplicate SDKs in the model",
            sdkCountAfterFirstUpdate,
            projectSdksModel.projectSdks.size
        )
    }

    fun testHomelessSdkIsNotReAddedAfterReset() {
        val sdk = registerElixirSdk("Homeless Elixir")
        val moduleStep = step(localImportDirectory())

        moduleStep.updateStep()

        val projectSdksModel = moduleStep.projectSdksModel
        assertFalse(
            "The sync loop must not re-add an SDK whose environment cannot be determined",
            projectSdksModel.projectSdks.containsKey(sdk)
        )
        assertFalse(
            "The sync loop must not mark the model modified when nothing was added",
            projectSdksModel.isModified
        )
    }
}
