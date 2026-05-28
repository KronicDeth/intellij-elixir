package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.impl.DependentSdkType
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.roots.JavadocOrderRootType
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Ref
import org.elixir_lang.sdk.ProcessOutput.isSmallIde
import org.elixir_lang.sdk.elixir.ElixirSdkMutation
import org.elixir_lang.sdk.elixir.Type.Companion.erlangSdkType
import org.elixir_lang.sdk.wsl.wslCompat
import org.jdom.Element
import java.nio.file.Path
import java.util.function.Consumer
import javax.swing.JComponent

/**
 * An SDK that depends on an Erlang SDK, either
 * * org.intellij.erlang.sdk.ErlangSdkType when intellij-erlang IS installed
 * * org.elixir_lang.sdk.erlang.Type when intellij-erlang IS NOT installed
 */
abstract class Type protected constructor(name: String) : DependentSdkType(name) {
    override fun isValidDependency(sdk: Sdk): Boolean {
        return staticIsValidDependency(sdk)
    }

    override fun getUnsatisfiedDependencyMessage(): String {
        return "Before you can create your first $presentableName you need to select an ${dependencyType.presentableName}."
    }

    override fun getDependencyType(): SdkType {
        return erlangSdkType()
    }

    override fun createAdditionalDataConfigurable(
        sdkModel: SdkModel,
        sdkModificator: SdkModificator
    ): AdditionalDataConfigurable? {
        return null
    }

    override fun isRootTypeApplicable(type: OrderRootType): Boolean {
        return type === OrderRootType.CLASSES || type === OrderRootType.SOURCES || type === JavadocOrderRootType.getInstance()
    }

    override fun saveAdditionalData(additionalData: SdkAdditionalData, additional: Element) {
        // intentionally left blank
    }

    /**
     * Override fixDependency to use the Path-aware selectSdkHome API.
     *
     * The starting directory is inferred by [getBasePath] from project context when there is no
     * dependency SDK yet (first Erlang SDK), so no environment picker is needed.
     */
    override fun fixDependency(sdkModel: SdkModel, sdkCreatedCallback: Consumer<in Sdk?>): Sdk? {
        return createSdkOfType(sdkModel, dependencyType, null, sdkCreatedCallback)
    }

    override fun showCustomCreateUI(
        sdkModel: SdkModel, parentComponent: JComponent, selectedSdk: Sdk?,
        sdkCreatedCallback: Consumer<in Sdk?>
    ) {
        // First, ensure we have at least one Erlang SDK
        var createdOrSelectedErlangSdk: Sdk? = null
        if (!checkDependency(sdkModel)) {
            val result = Messages.showOkCancelDialog(
                null,
                unsatisfiedDependencyMessage,
                ProjectBundle.message("dialog.title.cannot.create.sdk"),
                "Create ${dependencyType.presentableName}",
                "Cancel",
                Messages.getWarningIcon()
            )
            if (result != Messages.OK) {
                return
            }
            createdOrSelectedErlangSdk = fixDependency(sdkModel, sdkCreatedCallback)
            if (createdOrSelectedErlangSdk == null) {
                return
            }
        }

        // Let the user choose which Erlang SDK this Elixir SDK should depend on
        // This determines the environment (Windows vs WSL distribution)
        val erlangSdks = sdkModel.sdks.filter { isValidDependency(it) }

        val selectedErlangSdk = if (createdOrSelectedErlangSdk != null) {
            // Use the Erlang SDK we just created
            createdOrSelectedErlangSdk
        } else if (erlangSdks.size == 1) {
            // Only one Erlang SDK available, use it
            erlangSdks[0]
        } else {
            // Multiple Erlang SDKs - let user choose
            val sdkNames = erlangSdks.map { it.name }.toTypedArray()
            val choice = Messages.showEditableChooseDialog(
                "Multiple Erlang SDKs are available. Select which one this $presentableName SDK should use:",
                "Select Erlang SDK",
                Messages.getQuestionIcon(),
                sdkNames,
                sdkNames[0],
                null
            )
            if (choice == null) {
                // User cancelled
                return
            }
            erlangSdks.find { it.name == choice }
        }

        // Now create the dependent SDK using the selected Erlang SDK's environment
        createSdkOfType(sdkModel, this, selectedErlangSdk, sdkCreatedCallback, selectedErlangSdk)
    }

    protected fun createSdkOfType(
        sdkModel: SdkModel,
        sdkType: SdkType,
        dependencySdk: Sdk?,
        sdkCreatedCallback: Consumer<in Sdk?>,
        erlangSdkToUse: Sdk? = null
    ): Sdk? {
        val result = Ref<Sdk?>(null)
        // The 4-arg selectSdkHome(SdkType, Component?, Path, Consumer) was changed to a 5-arg form
        // in 262.4852.50 by inserting Project? before Consumer (https://youtrack.jetbrains.com/issue/IJPL-236990/Docker-project.-Project-Settings.-Add-JDK-from-disk-File-Chooser-container-is-absent-in-the-tree#focus=Comments-27-13746689.0-0 /
        // https://github.com/JetBrains/intellij-community/commit/edf92c9185e1a3e2f28c237c91e6fe493f7f80ac).
        // The 5-arg form does not exist in 261, so there is no single call that compiles against both.
        // Fall back to the 2-arg deprecated form until 261 support is dropped.
        @Suppress("DEPRECATION")
        SdkConfigurationUtil.selectSdkHome(sdkType, com.intellij.util.Consumer { home: String? ->
            val newSdk =
                SdkConfigurationUtil.createSdk(sdkModel.sdks.toList(), home!!, sdkType, null, null)

            // If creating an Elixir SDK and we have a specific Erlang SDK to use,
            // store it for use during configureSdkPaths and persist additional data immediately
            // to avoid workspace model mismatch.
            if (erlangSdkToUse != null && sdkType is org.elixir_lang.sdk.elixir.Type) {
                ApplicationManager.getApplication().runWriteAction {
                    ElixirSdkMutation.applyDependencySelection(newSdk, erlangSdkToUse)
                }
            }

            sdkCreatedCallback.accept(newSdk)
            result.set(newSdk)
        })
        return result.get()
    }

    /**
     * Determines a preferred base path hint for SDK home selection.
     *
     * For dependent SDKs (like Elixir depending on Erlang), this prefers:
     * 1. WSL paths when the dependency SDK is in WSL.
     * 2. Windows paths when the dependency SDK is local Windows.
     * 3. `user.home` when no dependency SDK is available.
     *
     * This value is passed to `SdkConfigurationUtil.selectSdkHome` as the initial directory hint.
     * The chooser may still apply platform-specific behavior (for example, native dialogs on
     * Windows can override the suggested start directory).
     *
     * @param dependencySdk the SDK that this SDK depends on (e.g. Erlang SDK for Elixir SDK)
     * @return the base path to use for SDK home selection
     */
    private fun getBasePath(dependencySdk: Sdk?): Path {
        // If we have a dependency SDK, use its home path to determine the environment
        dependencySdk?.homePath?.let { dependencyHomePath ->
            if (wslCompat.isWslUncPath(dependencyHomePath)) {
                // Dependency is in WSL - get its distribution and use that as base
                val distribution = wslCompat.getDistributionByWindowsUncPath(dependencyHomePath)
                distribution?.getWindowsPath("/")?.let { wslRootPath ->
                    LOG.debug("Using WSL base path from dependency SDK: $wslRootPath")
                    return Path.of(wslRootPath)
                }

                // Keep WSL context even if distro lookup fails.
                LOG.debug("Using WSL dependency SDK path directly as base: $dependencyHomePath")
                return Path.of(dependencyHomePath)
            } else {
                // Dependency is in Windows - use its home path as base
                LOG.debug("Using Windows base path from dependency SDK: $dependencyHomePath")
                return Path.of(dependencyHomePath).parent ?: Path.of(dependencyHomePath)
            }
        }
        // Fallback: default to user home directory
        LOG.debug("Using default base path: user.home")
        return Path.of(System.getProperty("user.home"))
    }


    companion object {
        private val LOG = Logger.getInstance(Type::class.java)
        private const val ERLANG_SDK_TYPE_CANONICAL_NAME = "org.intellij.erlang.sdk.ErlangSdkType"
        private val ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME: String =
            org.elixir_lang.sdk.erlang.Type::class.java.canonicalName

        /**
         * UserData key to pass the Erlang SDK from showCustomCreateUI to configureSdkPaths.
         * This avoids race conditions where the newly created Erlang SDK might not be
         * immediately available in ProjectJdkTable.
         */
        @JvmStatic
        val ERLANG_SDK_KEY = com.intellij.openapi.util.Key.create<Sdk>("ERLANG_SDK_FOR_ELIXIR")

        fun staticIsValidDependency(sdk: Sdk): Boolean {
            val sdkTypeCanonicalName = sdk.sdkType.javaClass.canonicalName

            return if (isSmallIde) {
                sdkTypeCanonicalName == ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME
            } else {
                sdkTypeCanonicalName == ERLANG_SDK_TYPE_CANONICAL_NAME ||
                        sdkTypeCanonicalName == ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME
            }
        }
    }
}
