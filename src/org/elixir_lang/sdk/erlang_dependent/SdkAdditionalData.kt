package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.jdom.Element

class SdkAdditionalData :
    ValidatableSdkAdditionalData,
    Cloneable {
    private val elixirSdk: Sdk
    private var erlangSdk: Sdk? = null
    private var erlangSdkName: String? = null

    companion object {
        private const val ERLANG_SDK_NAME = "erlang-sdk-name"
        private val LOG = Logger.getInstance(SdkAdditionalData::class.java)
    }

    constructor(erlangSdk: Sdk?, elixirSdk: Sdk) {
        this.erlangSdk = erlangSdk
        this.elixirSdk = elixirSdk
    }

    // readExternal
    constructor(elixirSdk: Sdk) {
        this.elixirSdk = elixirSdk
    }

    /**
     * Checks if the ERLANG_SDK_NAME properties are configured correctly and throws an exception
     * if they are not.
     *
     * @param sdkModel the model containing all configured SDKs.
     * @throws ConfigurationException if the ERLANG_SDK_NAME is not configured correctly.
     * @since 5.0.1
     */
    @Throws(ConfigurationException::class)
    override fun checkValid(sdkModel: SdkModel) {
        LOG.debug("checkValid called for Elixir SDK: ${elixirSdk.name} (homePath: ${elixirSdk.homePath})")

        val erlangSdk = getErlangSdk()

        if (erlangSdk == null) {
            val availableErlangSdks = ProjectJdkTable.getInstance().allJdks.filter {
                Type.staticIsValidDependency(it)
            }

            val message = if (availableErlangSdks.isEmpty()) {
                "No Erlang SDK found. Please configure an Erlang SDK first, then configure this Elixir SDK."
            } else {
                val availableNames = availableErlangSdks.joinToString(", ") { it.name }
                "No valid Erlang SDK configured for this Elixir SDK. Available Erlang SDKs: $availableNames"
            }

            LOG.debug("Validation failed for ${elixirSdk.name}: $message")
            throw ConfigurationException(message)
        }

        LOG.debug("checkValid completed successfully for ${elixirSdk.name}")
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any = SdkAdditionalData(erlangSdk, elixirSdk)

    @Throws(InvalidDataException::class)
    fun readExternal(element: Element) {
        erlangSdkName = element.getAttributeValue(ERLANG_SDK_NAME)
    }

    @Throws(WriteExternalException::class)
    fun writeExternal(element: Element) {
        val sdk = getErlangSdk()

        if (sdk != null) {
            // Double-check that the SDK still exists in the table before writing
            val jdkTable = ProjectJdkTable.getInstance()
            if (jdkTable.findJdk(sdk.name) != null) {
                element.setAttribute(ERLANG_SDK_NAME, sdk.name)
            }
            // If SDK doesn't exist in table, don't write the attribute
            // This prevents persisting references to deleted SDKs
        }
    }

    fun getErlangSdk(): Sdk? = getErlangSdk(sdkModel = null)

    /**
     * Gets the Erlang SDK, optionally searching in the provided SdkModel.
     * When sdkModel is provided, it searches there first (includes unsaved SDKs in Project Structure dialog).
     * Falls back to ProjectJdkTable for saved SDKs.
     *
     * @param sdkModel optional SdkModel to search in (for unsaved SDKs)
     * @return the Erlang SDK, or null if none found
     */
    fun getErlangSdk(sdkModel: SdkModel?): Sdk? {
        val jdkTable = ProjectJdkTable.getInstance()
        val elixirName = elixirSdk.name

        // 1. Validate cached SDK
        erlangSdk?.let { selected ->
            val selectedName = selected.name
            val exists = (sdkModel?.sdks?.any { it.name == selectedName } == true) || (jdkTable.findJdk(selectedName) != null)
            if (!exists) {
                LOG.debug("[$elixirName] selected Erlang SDK '$selectedName' no longer exists, clearing reference")
                erlangSdk = null
            } else {
                return erlangSdk
            }
        }

        // 2. Lookup by name if configured
        erlangSdkName?.let { name ->
            LOG.debug("[$elixirName] Looking up Erlang SDK by name: $name")
            erlangSdk = sdkModel?.sdks?.find { it.name == name }
                ?: jdkTable.findJdk(name)

            if (erlangSdk != null) {
                LOG.debug("[$elixirName] Found Erlang SDK '$name'")
                erlangSdkName = null
            } else {
                LOG.debug("[$elixirName] Erlang SDK '$name' not found, clearing orphaned reference")
                erlangSdkName = null
            }
            return erlangSdk
        }

        // 3. Auto-discovery fallback
        LOG.debug("[$elixirName] No Erlang SDK name configured, auto-discovering...")
        erlangSdk = findSdk(sdkModel) { Type.staticIsValidDependency(it) }
            ?: findSdk(jdkTable) { Type.staticIsValidDependency(it) }

        if (erlangSdk != null) {
            LOG.debug("[$elixirName] Auto-discovered Erlang SDK '${erlangSdk?.name}'")
        } else {
            LOG.debug("[$elixirName] No valid Erlang SDK found for auto-discovery")
        }

        return erlangSdk
    }

    private fun findSdk(source: Any?, predicate: (Sdk) -> Boolean): Sdk? {
        return when (source) {
            is SdkModel -> source.sdks.find(predicate)
            is ProjectJdkTable -> source.allJdks.find(predicate)
            else -> null
        }
    }

    /**
     * Returns the configured Erlang SDK name without triggering a lookup.
     * Useful for debugging and understanding the current state.
     */
    fun getErlangSdkName(): String? = erlangSdkName ?: erlangSdk?.name

    fun setErlangSdk(erlangSdk: Sdk?) {
        this.erlangSdk = erlangSdk
    }
}
