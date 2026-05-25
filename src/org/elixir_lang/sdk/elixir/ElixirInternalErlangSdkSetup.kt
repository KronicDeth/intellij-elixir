package org.elixir_lang.sdk.elixir

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.asContextElement
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.trace
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModificator
import com.intellij.openapi.ui.Messages
import kotlinx.coroutines.runBlocking
import org.elixir_lang.jps.shared.sdk.SdkPaths
import org.elixir_lang.sdk.SdkHomeKey
import org.elixir_lang.sdk.SdkHomePaths
import org.elixir_lang.sdk.SdkRegistrar
import org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.erlang_dependent.Type.Companion.ERLANG_SDK_KEY
import org.elixir_lang.util.WriteActions
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
     * For mise Elixir SDKs only: finds mise-installed Erlang SDK homes, presents a chooser dialog, and registers the
     * selected one.
     *
     * This is normally called from within `setupSdkPaths`, which IntelliJ runs inside its own "Configuring SDK…" modal
     * (see `ProjectSdksModel.doAdd`). In that context the EDT loop is pumping at the modal modality level.
     *
     * When on a background thread we capture [ModalityState.defaultModalityState] - which, from a `ProgressManager`
     * background thread, returns the modality of the enclosing progress dialog - and pass it as a coroutine context
     * element so that [com.intellij.openapi.application.edtWriteAction] inside [SdkRegistrar] dispatches at the correct
     * modality level and is processed by the modal loop.
     *
     * When already on the EDT we cannot use [runBlocking] (it would deadlock), so we fall back to the classical
     * [com.intellij.openapi.application.Application.invokeAndWait] + [com.intellij.openapi.application.runWriteAction]
     * path via [org.elixir_lang.sdk.elixir.ElixirInternalErlangSdkSetup.registerErlangSdk].
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
        if (app.isDispatchThread) showDialog.run() else app.invokeAndWait(showDialog)

        val selectedHome = selectedName?.let { nameToHome[it] } ?: return null

        return if (app.isDispatchThread) {
            // Can't use runBlocking on EDT - fall back to modal-aware invokeAndWait + runWriteAction.
            registerErlangSdk(selectedHome)
        } else {
            // Capture the current ProgressManager modality (the "Configuring SDK…" modal) so that
            // edtWriteAction inside SdkRegistrar dispatches at the correct modality level.
            //
            // Safety invariant: ErlangSdkType.setupSdkPaths (called inside registerOrUpdateErlangSdk)
            // must not block waiting for this background thread. Any invokeAndWait it issues dispatches
            // to the EDT, which is safe because we are not holding any lock the EDT needs. If
            // setupSdkPaths ever changed to block on the calling thread, this runBlocking would deadlock.
            val modality = ModalityState.defaultModalityState()
            runBlocking(modality.asContextElement()) {
                SdkRegistrar.registerOrUpdateErlangSdk(selectedHome)
            }
        }
    }

    /**
     * Registers an Erlang SDK using [com.intellij.openapi.application.Application.invokeAndWait] +
     * [com.intellij.openapi.application.runWriteAction], which is modal-aware and works whether called from the EDT or
     * from a background thread inside a modal context.
     *
     * Reuses an already-registered SDK at the same home path rather than creating a duplicate.
     */
    internal fun registerErlangSdk(homePath: String): Sdk? {
        val prep = SdkRegistrar.prepareErlangSdk(homePath) ?: return null
        val sdk = if (prep.existing != null) {
            prep.existing
        } else {
            WriteActions.runWriteAction { ProjectJdkTable.getInstance().addJdk(prep.template) }
            prep.template
        }
        ErlangSdkType.instance.setupSdkPaths(sdk)
        LOG.info("${if (prep.existing != null) "Reused" else "Registered"} Erlang SDK '${sdk.name}' from ${sdk.homePath}")
        return sdk
    }
}
