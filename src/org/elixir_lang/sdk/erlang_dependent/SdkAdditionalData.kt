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
        // Also store the SDK name for persistence
        this.erlangSdkName = erlangSdk?.name
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

        val erlangSdk = getErlangSdk(sdkModel)

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
        // Use the stored name directly if available
        // This ensures we can persist newly created SDKs that aren't in the global table yet
        val nameToWrite = erlangSdkName ?: erlangSdk?.name

        if (nameToWrite != null) {
            element.setAttribute(ERLANG_SDK_NAME, nameToWrite)
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

        // 1. Check cached SDK (but don't mutate the cache)
        erlangSdk?.let { selected ->
            val selectedName = selected.name
            val exists = (sdkModel?.sdks?.any { it.name == selectedName } == true) || (jdkTable.findJdk(selectedName) != null)
            if (exists) {
                return selected
            } else {
                LOG.debug("[$elixirName] Cached Erlang SDK '$selectedName' no longer exists, will look up by name")
                // Don't clear cache here - would violate read-only contract after commit
            }
        }

        // 2. Lookup by name if configured
        erlangSdkName?.let { name ->
            LOG.debug("[$elixirName] Looking up Erlang SDK by name: $name")
            val foundSdk = sdkModel?.sdks?.find { it.name == name }
                ?: jdkTable.findJdk(name)

            if (foundSdk != null) {
                LOG.debug("[$elixirName] Found Erlang SDK '$name'")
                return foundSdk
            } else {
                LOG.debug("[$elixirName] Erlang SDK '$name' not found in table")
                // Don't clear erlangSdkName here - would violate read-only contract
            }
        }

        // 3. Auto-discovery fallback
        LOG.debug("[$elixirName] No Erlang SDK name configured, auto-discovering...")
        val discoveredSdk = findSdk(sdkModel) { Type.staticIsValidDependency(it) }
            ?: findSdk(jdkTable) { Type.staticIsValidDependency(it) }

        if (discoveredSdk != null) {
            LOG.debug("[$elixirName] Auto-discovered Erlang SDK '${discoveredSdk.name}'")
        } else {
            LOG.debug("[$elixirName] No valid Erlang SDK found for auto-discovery")
        }

        return discoveredSdk
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
