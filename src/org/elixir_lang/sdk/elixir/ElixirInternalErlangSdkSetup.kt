package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import com.intellij.openapi.ui.Messages
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.SdkHomeKey
import org.elixir_lang.sdk.SdkHomePaths
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.erlang_dependent.Type.Companion.ERLANG_SDK_KEY
import org.elixir_lang.sdk.wsl.wslCompat
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType

/**
 * Handles the internal Erlang SDK setup path for Elixir SDKs.
 *
 * This covers the wizard-driven flow: auto-linking an already-registered Erlang SDK,
 * prompting the user to pick a mise-installed Erlang SDK when none is registered, and
 * registering the selected one. Separated from Type.kt so that the OTP compatibility
 * validation added later can be focused here.
 */
object ElixirInternalErlangSdkSetup {

    private val LOG = Logger.getInstance(ElixirInternalErlangSdkSetup::class.java)

    /**
     * Finds or prompts for an Erlang SDK and links it to the given Elixir SDK.
     *
     * Resolution order:
     * 1. Explicit SDK stored in UserData (set by the wizard when creating Erlang + Elixir together)
     * 2. Already-linked SDK from existing SdkAdditionalData
     * 3. Any registered Erlang SDK in ProjectJdkTable
     * 4. Prompt the user to pick a mise-installed Erlang SDK (mise Elixir SDKs only)
     */
    internal fun configureInternalErlangSdk(
        elixirSdk: Sdk,
        elixirSdkModificator: SdkModificator,
    ): Sdk? {
        val existingAdditionalData = elixirSdk.sdkAdditionalData as? SdkAdditionalData
        val existingErlangSdk = existingAdditionalData?.getErlangSdk()
        val explicitErlangSdk = elixirSdk.getUserData(ERLANG_SDK_KEY)

        LOG.trace { "[${elixirSdk.name}] configureInternalErlangSdk: explicitErlangSdk=${explicitErlangSdk?.name}, existingErlangSdk=${existingErlangSdk?.name}" }

        val erlangSdk = explicitErlangSdk
            ?: existingErlangSdk
            ?: ErlangSdkResolver.findAnyRegistered()?.also { LOG.trace { "[${elixirSdk.name}] Resolution: found via findAnyRegistered: '${it.name}'" } }
            ?: promptForMiseErlangSdk(elixirSdk)?.also { LOG.trace { "[${elixirSdk.name}] Resolution: found via promptForMiseErlangSdk: '${it.name}'" } }

        if (erlangSdk != null) {
            LOG.trace { "[${elixirSdk.name}] Using Erlang SDK '${erlangSdk.name}' (identity: ${System.identityHashCode(erlangSdk)})" }
            if (explicitErlangSdk == null && existingErlangSdk == null) {
                LOG.info("Auto-linked Erlang SDK '${erlangSdk.name}' to Elixir SDK '${elixirSdk.name}'")
            }

            if (existingAdditionalData == null || existingErlangSdk == null) {
                val sdkAdditionData: com.intellij.openapi.projectRoots.SdkAdditionalData =
                    SdkAdditionalData(erlangSdk, elixirSdk)
                elixirSdkModificator.sdkAdditionalData = sdkAdditionData
            }

            ElixirErlangClasspath.addNewCodePathsFromInternErlangSdk(elixirSdk, erlangSdk, elixirSdkModificator)

            elixirSdk.putUserData(ERLANG_SDK_KEY, null)
        } else {
            LOG.warn("No Erlang SDK found, Elixir SDK will be incomplete")
        }
        return erlangSdk
    }

    /**
     * For mise Elixir SDKs only: finds mise-installed Erlang SDK homes,
     * presents a chooser dialog, and registers the selected one.
     */
    internal fun promptForMiseErlangSdk(elixirSdk: Sdk): Sdk? {
        val homePath = elixirSdk.homePath ?: return null
        if (SdkPaths.detectSource(homePath) != SdkPaths.SOURCE_NAME_MISE) return null

        val erlangSdkType = ErlangSdkType.instance
        val miseHomes = java.util.TreeMap<SdkHomeKey, String>()
        SdkHomePaths.mergeMise(miseHomes, "erlang")

        val validHomes = miseHomes.entries.filter { (_, path) ->
            erlangSdkType.isValidSdkHome(path)
        }
        if (validHomes.isEmpty()) return null

        val displayNames = validHomes.map { (_, path) ->
            ErlangSdkType.suggestSdkNameForHome(path, null)
        }.toTypedArray()

        // Build a reverse map from display name → home path for lookup after dialog
        val nameToHome = validHomes.associate { (_, path) ->
            ErlangSdkType.suggestSdkNameForHome(path, null) to path
        }

        val validator = object : com.intellij.openapi.ui.InputValidator {
            override fun checkInput(inputString: String) = inputString in nameToHome
            override fun canClose(inputString: String) = inputString in nameToHome
        }

        var selectedName: String? = null
        val app = ApplicationManager.getApplication()
        val showDialog = Runnable {
            selectedName = Messages.showEditableChooseDialog(
                "No Erlang SDK is configured. Select one to use with this Elixir SDK:",
                "Select Erlang SDK",
                Messages.getQuestionIcon(),
                displayNames,
                displayNames[0],
                validator
            )
        }

        if (app.isDispatchThread) {
            showDialog.run()
        } else {
            app.invokeAndWait(showDialog)
        }

        val selectedHome = selectedName?.let { nameToHome[it] } ?: return null

        val erlangSdk = registerErlangSdk(selectedHome) ?: return null

        return erlangSdk
    }

    internal fun registerErlangSdk(homePath: String): Sdk? {
        val erlangSdkType = ErlangSdkType.instance
        val canonicalHome = wslCompat.canonicalizePath(homePath)
        val versionString = ErlangSdkType.versionStringForHome(canonicalHome, null)
            ?: return null
        val sdkName = ErlangSdkType.suggestSdkNameForHome(canonicalHome, null)

        val newSdk = ProjectJdkImpl(sdkName, erlangSdkType, canonicalHome, versionString)
        LOG.trace { "registerErlangSdk: created SDK '$sdkName' (identity: ${System.identityHashCode(newSdk)})" }

        val app = ApplicationManager.getApplication()
        val addSdk = Runnable {
            app.runWriteAction {
                ProjectJdkTable.getInstance().addJdk(newSdk)
                val tableEntry = ProjectJdkTable.getInstance().findJdk(sdkName)
                LOG.trace { "registerErlangSdk: after addJdk, findJdk('$sdkName') = ${tableEntry?.let { "'${it.name}' (identity: ${System.identityHashCode(it)})" } ?: "NOT FOUND"}" }
            }
        }

        if (app.isDispatchThread) {
            addSdk.run()
        } else {
            app.invokeAndWait(addSdk)
        }

        LOG.trace { "registerErlangSdk: before setupSdkPaths, findJdk('$sdkName') = ${ProjectJdkTable.getInstance().findJdk(sdkName)?.let { "found (identity: ${System.identityHashCode(it)})" } ?: "NOT FOUND"}" }
        erlangSdkType.setupSdkPaths(newSdk)
        LOG.trace { "registerErlangSdk: after setupSdkPaths, findJdk('$sdkName') = ${ProjectJdkTable.getInstance().findJdk(sdkName)?.let { "found (identity: ${System.identityHashCode(it)})" } ?: "NOT FOUND"}" }

        LOG.info("Registered Erlang SDK '${newSdk.name}' from $canonicalHome")
        return newSdk
    }
}
