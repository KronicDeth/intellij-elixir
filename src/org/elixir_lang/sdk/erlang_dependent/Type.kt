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
import org.elixir_lang.sdk.elixir.Type.Companion.erlangSdkType
import org.elixir_lang.sdk.erlang.Type
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
     * Override fixDependency to use the new selectSdkHome API with proper Path handling.
     * This allows WSL paths to be selected when creating the dependency (Erlang) SDK.
     *
     * The parent class uses the deprecated selectSdkHome(sdkType, consumer) which defaults
     * to user.home on Windows, preventing WSL SDK selection.
     *
     * When creating the first Erlang SDK, we ask the user which environment they want.
     */
    override fun fixDependency(sdkModel: SdkModel, sdkCreatedCallback: Consumer<in Sdk?>): Sdk? {
        // Load WSL distributions on background thread to avoid EDT blocking
        val availableDistributions = ApplicationManager.getApplication().executeOnPooledThread<List<com.intellij.execution.wsl.WSLDistribution>> {
            wslCompat.getInstalledDistributions()
        }.get()

        // If no WSL distributions available, just use Windows (no need to ask)
        if (availableDistributions.isEmpty()) {
            return createSdkOfType(sdkModel, dependencyType, null, sdkCreatedCallback)
        }

        // Build list of options: "Windows (Local)" + WSL distributions
        val environmentOptions = mutableListOf("Windows (Local)")
        environmentOptions.addAll(availableDistributions.map { it.presentableName })

        // Ask the user which environment they want to create the Erlang SDK in
        val environmentChoice = Messages.showEditableChooseDialog(
            "Where would you like to create the ${dependencyType.presentableName}?",
            "Select Environment",
            Messages.getQuestionIcon(),
            environmentOptions.toTypedArray(),
            environmentOptions[0],
            null
        )

        if (environmentChoice == null) {
            // User cancelled
            return null
        }

        // Create a mock SDK to represent the environment choice
        // This allows getBasePath() to determine the correct starting path
        val environmentHintSdk = if (environmentChoice == "Windows (Local)") {
            // Use null for Windows (will use user.home)
            null
        } else {
            // Find the selected WSL distribution and create a mock SDK
            val selectedDistribution = availableDistributions.find { it.presentableName == environmentChoice }
            if (selectedDistribution != null) {
                createMockWslSdkForDistribution(selectedDistribution)
            } else {
                null
            }
        }

        return createSdkOfType(sdkModel, dependencyType, environmentHintSdk, sdkCreatedCallback)
    }

    /**
     * Creates a mock SDK for a specific WSL distribution to use as an environment hint.
     * This tells getBasePath() to start the file chooser in the selected WSL distribution.
     */
    @Suppress("NonExtendableApiUsage")
    private fun createMockWslSdkForDistribution(distribution: com.intellij.execution.wsl.WSLDistribution): Sdk {
        // Get the Windows UNC path for the WSL distribution root
        val wslRootPath = distribution.getWindowsPath("/")

        return object : Sdk {
            override fun getHomePath(): String = wslRootPath
            override fun getHomeDirectory(): com.intellij.openapi.vfs.VirtualFile? = null
            override fun getName(): String = "Mock WSL SDK (${distribution.presentableName})"
            override fun getSdkType(): SdkType = dependencyType
            override fun getVersionString(): String? = null
            override fun getSdkModificator(): SdkModificator = throw UnsupportedOperationException()
            override fun getSdkAdditionalData(): SdkAdditionalData? = null
            override fun getRootProvider(): com.intellij.openapi.roots.RootProvider = throw UnsupportedOperationException()
            override fun clone(): Sdk = throw UnsupportedOperationException()
            override fun <T> getUserData(key: com.intellij.openapi.util.Key<T>): T? = null
            override fun <T> putUserData(key: com.intellij.openapi.util.Key<T>, value: T?) {}
        }
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
        val basePath = getBasePath(dependencySdk)
        SdkConfigurationUtil.selectSdkHome(sdkType, null, basePath, com.intellij.util.Consumer { home: String? ->
            val newSdk =
                SdkConfigurationUtil.createSdk(sdkModel.sdks.toList(), home!!, sdkType, null, null)

            // If creating an Elixir SDK and we have a specific Erlang SDK to use,
            // store it for use during configureSdkPaths
            if (erlangSdkToUse != null) {
                newSdk.putUserData(ERLANG_SDK_KEY, erlangSdkToUse)
            }

            sdkCreatedCallback.accept(newSdk)
            result.set(newSdk)
        })
        return result.get()
    }

    /**
     * Determines the appropriate base path for SDK home selection, ensuring it matches the
     * dependency SDK's environment (EelDescriptor).
     *
     * For dependent SDKs (like Elixir depending on Erlang), this ensures that:
     * 1. If the dependency SDK is in WSL, the base path points to that WSL distribution
     * 2. If the dependency SDK is in Windows, the base path points to Windows
     *
     * This guarantees that the EelDescriptor check in SdkConfigurationUtil.selectSdkHome passes,
     * and that the dependent SDK is accessible from the same environment as its dependency.
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
        private val ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME: String = Type::class.java.canonicalName

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
