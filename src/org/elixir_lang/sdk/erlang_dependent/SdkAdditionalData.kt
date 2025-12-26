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

/**
 * Stores the reference from an Elixir SDK to its dependent Erlang SDK.
 *
 * ## Persistence Model
 *
 * Only the Erlang SDK **name** is persisted to disk (in the `erlang-sdk-name` XML attribute).
 * The actual SDK reference is resolved lazily when [getErlangSdk] is called.
 *
 * ## Cache Behavior
 *
 * A transient cache ([cachedErlangSdk]) avoids repeated lookups. The cache is:
 * - Populated on first successful lookup
 * - Invalidated when the referenced SDK no longer exists in ProjectJdkTable
 * - Cleared on [readExternal] to force re-resolution after loading from disk
 *
 * ## SDK Removal Handling
 *
 * When an Erlang SDK is removed from ProjectJdkTable, the [org.elixir_lang.sdk.elixir.Type]
 * table listener calls [getErlangSdkName] (not [getErlangSdk]) to find affected Elixir SDKs,
 * avoiding race conditions where the SDK is already removed from the table.
 *
 * @see org.elixir_lang.sdk.elixir.Type.setupSdkTableListener
 */
class SdkAdditionalData :
    ValidatableSdkAdditionalData,
    Cloneable {
    private val elixirSdk: Sdk

    // Persistence layer - the ONLY thing saved to disk
    private var erlangSdkName: String? = null

    // Runtime cache - lazily populated, cleared when invalid
    // Not persisted, not cloned
    @Transient
    private var cachedErlangSdk: Sdk? = null

    companion object {
        private const val ERLANG_SDK_NAME = "erlang-sdk-name"
        private val LOG = Logger.getInstance(SdkAdditionalData::class.java)
    }

    // Primary constructor for readExternal
    constructor(elixirSdk: Sdk) {
        this.elixirSdk = elixirSdk
    }

    // Secondary constructor for creating new SDKs with a known Erlang SDK
    constructor(erlangSdk: Sdk?, elixirSdk: Sdk) : this(elixirSdk) {
        this.erlangSdkName = erlangSdk?.name
        this.cachedErlangSdk = erlangSdk
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

        // Allow auto-discovery during validation - if the configured SDK is missing,
        // we'll find another one automatically
        val erlangSdk = getErlangSdk(sdkModel)

        if (erlangSdk == null) {
            val available = ProjectJdkTable.getInstance().allJdks
                .filter { Type.staticIsValidDependency(it) }
                .joinToString(", ") { it.name }

            val message = if (available.isEmpty()) {
                "No Erlang SDK found. Please configure an Erlang SDK first."
            } else {
                "No valid Erlang SDK configured. Available: $available"
            }

            LOG.debug("Validation failed for ${elixirSdk.name}: $message")
            throw ConfigurationException(message)
        }

        LOG.debug("checkValid completed successfully for ${elixirSdk.name}")
    }

    @Throws(InvalidDataException::class)
    fun readExternal(element: Element) {
        erlangSdkName = element.getAttributeValue(ERLANG_SDK_NAME)
        cachedErlangSdk = null  // Force re-lookup on next access
    }

    @Throws(WriteExternalException::class)
    fun writeExternal(element: Element) {
        erlangSdkName?.let {
            element.setAttribute(ERLANG_SDK_NAME, it)
        }
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        val cloned = SdkAdditionalData(elixirSdk)
        cloned.erlangSdkName = this.erlangSdkName
        // Don't clone cachedErlangSdk - let it be lazily resolved
        return cloned
    }

    /**
     * Returns the configured Erlang SDK name without triggering a lookup.
     * Use this when you need to check the stored reference (e.g., in SDK removal listeners).
     */
    fun getErlangSdkName(): String? = erlangSdkName

    fun setErlangSdk(sdk: Sdk?) {
        erlangSdkName = sdk?.name
        cachedErlangSdk = sdk
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
        val elixirName = elixirSdk.name

        // 1. Check cached SDK - verify it still exists AND is valid type
        cachedErlangSdk?.let { cached ->
            if (isValidAndExists(cached, sdkModel)) {
                return cached
            }
            LOG.debug("[$elixirName] Cached Erlang SDK '${cached.name}' no longer valid")
            cachedErlangSdk = null
        }

        // 2. Lookup by configured name
        erlangSdkName?.let { name ->
            LOG.debug("[$elixirName] Looking up Erlang SDK by name: $name")
            val found = findErlangSdkByName(name, sdkModel)
            if (found != null) {
                LOG.debug("[$elixirName] Found Erlang SDK '$name'")
                cachedErlangSdk = found
                return found
            }
            LOG.debug("[$elixirName] Erlang SDK '$name' not found")
        }

        // 3. Auto-discovery fallback
        LOG.debug("[$elixirName] Auto-discovering Erlang SDK...")
        val discovered = autoDiscoverErlangSdk(sdkModel)
        if (discovered != null) {
            LOG.debug("[$elixirName] Auto-discovered Erlang SDK '${discovered.name}'")
            cachedErlangSdk = discovered
            // Note: Don't update erlangSdkName here - that would silently change
            // the user's configuration. Let validation prompt them to fix it.
        } else {
            LOG.debug("[$elixirName] No valid Erlang SDK found")
        }
        return discovered
    }

    private fun isValidAndExists(sdk: Sdk, sdkModel: SdkModel?): Boolean {
        if (!Type.staticIsValidDependency(sdk)) return false
        val name = sdk.name
        val jdkTable = ProjectJdkTable.getInstance()
        return (sdkModel?.sdks?.any { it.name == name } == true)
            || (jdkTable.findJdk(name) != null)
    }

    private fun findErlangSdkByName(name: String, sdkModel: SdkModel?): Sdk? {
        val jdkTable = ProjectJdkTable.getInstance()
        // Check SdkModel first (for unsaved SDKs in dialogs)
        return sdkModel?.sdks?.find { it.name == name && Type.staticIsValidDependency(it) }
            ?: jdkTable.findJdk(name)?.takeIf { Type.staticIsValidDependency(it) }
    }

    private fun autoDiscoverErlangSdk(sdkModel: SdkModel?): Sdk? {
        val jdkTable = ProjectJdkTable.getInstance()
        return sdkModel?.sdks?.find { Type.staticIsValidDependency(it) }
            ?: jdkTable.allJdks.find { Type.staticIsValidDependency(it) }
    }
}
