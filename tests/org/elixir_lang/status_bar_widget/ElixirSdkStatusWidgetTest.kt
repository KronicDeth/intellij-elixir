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

    fun testWidgetPresentation() {
        val widget = ElixirSdkStatusWidget(project)
        val presentation = widget.getPresentation()
        assertNotNull(presentation)
    }

    fun testWidgetPopupCreation() {
        val widget = ElixirSdkStatusWidget(project)
        // getPopup() should return a non-null popup with the refresh action
        val popup = widget.getPopup()
        assertNotNull("Popup should be created", popup)
    }

    fun testWidgetSelectedValueWithNoSdk() {
        val widget = ElixirSdkStatusWidget(project)
        val selectedValue = widget.getSelectedValue()
        // When no SDK is configured, should show "No Elixir SDK"
        assertEquals("No Elixir SDK", selectedValue)
    }

    fun testWidgetTooltipWithNoSdk() {
        val widget = ElixirSdkStatusWidget(project)
        val tooltip = widget.getTooltipText()
        // Tooltip should indicate no SDKs configured
        assertTrue(
            "Tooltip should indicate no SDK configured",
            tooltip?.contains("No Elixir SDK") == true || tooltip?.contains("not configured") == true
        )
    }
}
