package org.elixir_lang.ui.framework.ultimate

import com.intellij.driver.client.Driver
import com.intellij.driver.sdk.step
import com.intellij.driver.sdk.ui.components.common.dialogs.licenseDialog
import com.intellij.driver.sdk.ui.components.common.welcomeScreen
import org.elixir_lang.ui.framework.core.clickButtonText
import org.elixir_lang.ui.framework.core.selectItemFromList
import org.elixir_lang.ui.framework.core.waitUntilReady
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Extensions for activating IntelliJ IDEA Ultimate licenses.
 *
 * These operations are specific to IntelliJ IDEA Ultimate and will not work
 * in Community Edition or other JetBrains IDEs without license activation.
 */

/**
 * Activates an IntelliJ IDEA Ultimate license via the welcome screen.
 *
 * This method navigates through the welcome screen options menu to access
 * the license activation dialog and completes the activation process.
 *
 * @receiver Driver The UI test driver instance
 * @throws Exception if the license activation UI components cannot be found
 */
fun Driver.activateUltimateLicense() = step("Activate Ultimate license") {
    welcomeScreen {
        clickButtonText("Options Menu")

        selectItemFromList("Manage Subscription")

        licenseDialog {
            activateButton.waitUntilReady()
            activateButton.click()
            closeDialog()
        }

        waitUntilReady(timeout = 5.minutes, minimum = 2.seconds)
    }
}
