package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.projectRoots.ProjectJdkTable
import org.elixir_lang.PlatformTestCase

/**
 * Tests for AddDevelopmentSdksAction.
 */
class AddDevelopmentSdksActionTest : PlatformTestCase() {

    private var originalErlangPath: String? = null
    private var originalElixirPath: String? = null

    override fun setUp() {
        super.setUp()
        // Save original system properties
        originalErlangPath = System.getProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY)
        originalElixirPath = System.getProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY)
    }

    override fun tearDown() {
        try {
            // Restore original system properties
            if (originalErlangPath != null) {
                System.setProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY, originalErlangPath!!)
            } else {
                System.clearProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY)
            }
            if (originalElixirPath != null) {
                System.setProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY, originalElixirPath!!)
            } else {
                System.clearProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY)
            }

            // Clean up any SDKs we created
            val jdkTable = ProjectJdkTable.getInstance()
            WriteAction.run<Throwable> {
                jdkTable.allJdks.filter {
                    it.name.contains("Dev") || it.name.contains("Test")
                }.forEach { jdkTable.removeJdk(it) }
            }
        } finally {
            super.tearDown()
        }
    }

    fun testActionIsDisabledWhenNoPropertiesSet() {
        System.clearProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY)
        System.clearProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY)

        val action = AddDevelopmentSdksAction()
        val event = createTestEvent()

        action.update(event)

        assertFalse("Action should be disabled when no SDK paths are configured", event.presentation.isEnabled)
    }

    fun testActionIsEnabledWhenErlangPathSet() {
        System.setProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY, "/fake/erlang/path")
        System.clearProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY)

        val action = AddDevelopmentSdksAction()
        val event = createTestEvent()

        action.update(event)

        assertTrue("Action should be enabled when Erlang path is configured", event.presentation.isEnabled)
        assertTrue(
            "Description should mention Erlang path",
            event.presentation.description?.contains("Erlang") == true
        )
    }

    fun testActionIsEnabledWhenElixirPathSet() {
        System.clearProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY)
        System.setProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY, "/fake/elixir/path")

        val action = AddDevelopmentSdksAction()
        val event = createTestEvent()

        action.update(event)

        assertTrue("Action should be enabled when Elixir path is configured", event.presentation.isEnabled)
        assertTrue(
            "Description should mention Elixir path",
            event.presentation.description?.contains("Elixir") == true
        )
    }

    fun testActionIsEnabledWhenBothPathsSet() {
        System.setProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY, "/fake/erlang/path")
        System.setProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY, "/fake/elixir/path")

        val action = AddDevelopmentSdksAction()
        val event = createTestEvent()

        action.update(event)

        assertTrue("Action should be enabled when both paths are configured", event.presentation.isEnabled)
        val description = event.presentation.description ?: ""
        assertTrue("Description should mention Erlang path", description.contains("Erlang"))
        assertTrue("Description should mention Elixir path", description.contains("Elixir"))
    }

    fun testActionIsDumbAware() {
        val action = AddDevelopmentSdksAction()
        assertTrue("Action should be dumb aware", action.isDumbAware)
    }

    fun testCompanionObjectConstants() {
        assertEquals(
            "Erlang path property should match expected value",
            "runIdeSdkErlangPath",
            AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY
        )
        assertEquals(
            "Elixir path property should match expected value",
            "runIdeSdkElixirPath",
            AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY
        )
    }

    fun testDescriptionShowsNoPathsWhenEmpty() {
        System.clearProperty(AddDevelopmentSdksAction.ERLANG_PATH_PROPERTY)
        System.clearProperty(AddDevelopmentSdksAction.ELIXIR_PATH_PROPERTY)

        val action = AddDevelopmentSdksAction()
        val event = createTestEvent()

        action.update(event)

        assertTrue(
            "Description should indicate no paths configured",
            event.presentation.description?.contains("No SDK paths") == true
        )
    }

    @Suppress("DEPRECATION")
    private fun createTestEvent(): AnActionEvent {
        val presentation = Presentation()
        val dataContext = object : DataContext {
            @Suppress("OVERRIDE_DEPRECATION")
            override fun getData(dataId: String): Any? {
                return when (dataId) {
                    CommonDataKeys.PROJECT.name -> project
                    else -> null
                }
            }
        }
        return AnActionEvent.createFromDataContext("test", presentation, dataContext)
    }
}
