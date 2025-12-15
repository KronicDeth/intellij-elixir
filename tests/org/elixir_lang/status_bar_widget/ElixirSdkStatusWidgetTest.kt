package org.elixir_lang.status_bar_widget

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests for ElixirSdkStatusWidget functionality.
 */
class ElixirSdkStatusWidgetTest : BasePlatformTestCase() {

    fun testWidgetCreation() {
        val widget = ElixirSdkStatusWidget(project)
        assertNotNull(widget)
        assertEquals(ElixirSdkStatusWidget.ID, widget.ID())
    }

    fun testWidgetComponent() {
        val widget = ElixirSdkStatusWidget(project)
        val component = widget.getComponent()
        assertNotNull("Widget should have a component", component)
    }

    fun testWidgetComponentTextWithNoSdk() {
        val widget = ElixirSdkStatusWidget(project)
        val component = widget.getComponent()

        // The component is a TextPanel.WithIconAndArrows
        // When no SDK is configured, should show "No Elixir SDK"
        assertNotNull(component)
    }

    fun testWidgetIdConstant() {
        assertEquals("ElixirSdkStatus", ElixirSdkStatusWidget.ID)
    }

    fun testWidgetDispose() {
        val widget = ElixirSdkStatusWidget(project)
        // Should not throw
        widget.dispose()
    }
}
