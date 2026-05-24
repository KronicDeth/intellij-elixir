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
 * Both the Erlang SDK **home path** (`erlang-sdk-home-path`) and **name** (`erlang-sdk-name`)
 * are persisted to disk. Home path is the primary stable identity - it survives SDK renames.
 * Name is retained as a display hint and legacy fallback for configs written before the
 * home-path field was added.
 *
 * ## Resolution Order
 *
 * 1. `erlangSdkHomePath` (stable, survives rename) - looked up by home path in ProjectJdkTable
 * 2. `erlangSdkName` (legacy fallback) - looked up by name; self-heals by writing resolved path back
 * 3. Both absent → NOT_CONFIGURED
 *
 * ## Cache Behavior
 *
 * A transient cache ([cachedErlangSdk]) avoids repeated lookups. The cache is:
 * - Populated on first successful lookup
 * - Invalidated when the referenced SDK no longer exists in ProjectJdkTable
 * - Cleared on [readExternal] to force re-resolution after loading from disk
 *
 * Cache resolution is performed by [ErlangSdkResolver].
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

    // Primary stable identity - survives SDK renames.
    // @Volatile ensures background-thread writes (self-heal in ErlangSdkResolver) are immediately
    // visible to the EDT reading these fields in writeExternal, without requiring a write lock.
    @Volatile
    private var erlangSdkHomePath: String? = null

    // Display hint + legacy fallback for configs written before erlangSdkHomePath was added
    @Volatile
    private var erlangSdkName: String? = null

    // Runtime cache - lazily populated, cleared when invalid
    // Not persisted, not cloned
    @Transient
    private var cachedErlangSdk: Sdk? = null

    companion object {
        private const val ERLANG_SDK_HOME_PATH = "erlang-sdk-home-path"
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
        this.erlangSdkHomePath = erlangSdk?.homePath
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
        erlangSdkHomePath = element.getAttributeValue(ERLANG_SDK_HOME_PATH)
        erlangSdkName = element.getAttributeValue(ERLANG_SDK_NAME)
        cachedErlangSdk = null  // Force re-lookup on next access
    }

    @Throws(WriteExternalException::class)
    fun writeExternal(element: Element) {
        // Snapshot both @Volatile fields into locals before writing so that a concurrent
        // self-heal write in ErlangSdkResolver cannot produce a torn (home, name) pair.
        val homePath = erlangSdkHomePath
        val name = erlangSdkName
        homePath?.let { element.setAttribute(ERLANG_SDK_HOME_PATH, it) }
        name?.let { element.setAttribute(ERLANG_SDK_NAME, it) }
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        val cloned = SdkAdditionalData(elixirSdk)
        cloned.erlangSdkHomePath = this.erlangSdkHomePath
        cloned.erlangSdkName = this.erlangSdkName
        // Don't clone cachedErlangSdk - let it be lazily resolved
        return cloned
    }

    /**
     * Returns the configured Erlang SDK name without triggering a lookup.
     * Use this when you need to check the stored reference (e.g., in SDK removal listeners).
     */
    fun getErlangSdkName(): String? = erlangSdkName

    fun getErlangSdkHomePath(): String? = erlangSdkHomePath

    fun setErlangSdk(sdk: Sdk?) {
        erlangSdkName = sdk?.name
        erlangSdkHomePath = sdk?.homePath
        cachedErlangSdk = sdk
    }

    internal fun getCachedErlangSdk(): Sdk? = cachedErlangSdk

    internal fun setCachedErlangSdk(sdk: Sdk?) {
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
        return ErlangSdkResolver.getInstance().resolveErlangSdk(elixirSdk, sdkModel)
    }
}
