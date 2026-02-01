package org.elixir_lang.ui.ultimate

import com.intellij.driver.sdk.step
import com.intellij.driver.sdk.ui.components.common.ideFrame
import com.intellij.driver.sdk.ui.components.common.runToolWindow
import com.intellij.driver.sdk.ui.components.common.toolwindows.projectView
import com.intellij.driver.sdk.ui.components.common.welcomeScreen
import com.intellij.driver.sdk.ui.components.elements.balloon
import com.intellij.driver.sdk.ui.components.elements.popupMenu
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import org.elixir_lang.ui.framework.core.*
import org.elixir_lang.ui.framework.ultimate.activateUltimateLicense
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes

/**
 * Integration test for IntelliJ IDEA Ultimate license activation and Elixir project import.
 *
 * This test verifies the complete Ultimate-specific workflow:
 * 1. License activation (Ultimate only)
 * 2. SDK configuration (Erlang and Elixir)
 * 3. Project import
 * 4. Mix dependencies installation
 * 5. Running tests via the IDE
 */
class UltimateProjectImportTest {

    @Test
    fun `import Elixir project with license activation and dependency installation`() {
        step("Run test: import Elixir project with license activation and dependency installation") {
            val ctx = IdeTestContext.setupTestContext("UltimateProjectImportTest")

            ctx.runIdeWithDriver()
                .useDriverAndCloseIde {
                    ensureIdeInForeground()
                    activateUltimateLicense()

                    welcomeScreen {
                        addSdkFromDisk(
                            "Add Erlang SDK for Elixir SDK from disk",
                            IdeTestContext.erlangSdkPath,
                            "Select Home Directory for Erlang SDK for Elixir SDK"
                        )

                        addSdkFromDisk(
                            "Add Elixir SDK from disk",
                            IdeTestContext.elixirSdkPath,
                            "Select Home Directory for Elixir SDK"
                        )
                    }
                    openProjectAt(IdeTestContext.projectPath)

                    step("Install Mix dependencies") {
                        ideFrame {
                            val notificationBalloon = balloon("Mix dependencies outdated")
                            // Notification actions appear as ActionLink components in the balloon
                            val installButton = notificationBalloon.x { byVisibleText("Install Mix dependencies") }
                            installButton.click()
                            waitUntilReady()
                        }
                    }

                    step("Run ExUnit tests") {
                        ideFrame {
                            projectView {
                                projectViewTree.rightClickPath(".", "test", fullMatch = false)
                            }
                            popupMenu().selectContains("Run 'Mix ExUnit")

                            // Wait for test runner to open and tests to execute
                            waitUntilReady(5.minutes)

                            runToolWindow {
                                waitContainsText("2 tests passed", null, true, 5.minutes)
                            }
                        }
                    }
                }
        }
    }
}
