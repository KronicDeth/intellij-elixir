package org.elixir_lang.new_project_wizard

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.FileUtil
import com.intellij.ui.dsl.builder.panel
import org.elixir_lang.PlatformTestCase
import org.elixir_lang.sdk.SdkDetectionContext
import java.nio.file.Path

/**
 * Tests the SDK-environment wiring of [elixirSdkComboBox]: the New Project wizard has no real
 * Project, so the combo must publish the Location field as the SDK detection context and keep it
 * current as the user edits it (WSL-specific behaviour itself is covered by SdkEnvironment usage
 * in ElixirSdkForModuleStepTest).
 */
class ElixirSdkComboBoxTest : PlatformTestCase() {

    override fun tearDown() {
        try {
            SdkDetectionContext.clear()
        } finally {
            super.tearDown()
        }
    }

    fun testLocationDrivesSdkDetectionContextThroughWizardLifecycle() {
        val wizardDisposable = Disposer.newDisposable(testRootDisposable)
        val wizardContext = WizardContext(null, wizardDisposable)
        val propertyGraph = PropertyGraph()
        val locationProperty = propertyGraph.property("")
        val sdkProperty = propertyGraph.property<Sdk?>(null)

        panel {
            row {
                elixirSdkComboBox(wizardContext, sdkProperty, locationProperty)
            }
        }

        assertNull(
            "A blank location should publish no detection context",
            SdkDetectionContext.resolve()
        )

        val firstDirectory = FileUtil.createTempDirectory("newProject", null).path
        locationProperty.set(firstDirectory)

        assertEquals(
            "Setting the location should publish it for SDK detection",
            Path.of(firstDirectory),
            SdkDetectionContext.resolve()
        )

        val secondDirectory = FileUtil.createTempDirectory("newProject", null).path
        locationProperty.set(secondDirectory)

        assertEquals(
            "Editing the location should republish it",
            Path.of(secondDirectory),
            SdkDetectionContext.resolve()
        )

        Disposer.dispose(wizardDisposable)

        assertNull(
            "Disposing the wizard should clear the published location",
            SdkDetectionContext.resolve()
        )
    }
}
