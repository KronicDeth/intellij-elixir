package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData
import com.intellij.openapi.projectRoots.impl.SdkAdditionalDataBase
import com.intellij.openapi.util.InvalidDataException
import com.intellij.openapi.util.WriteExternalException
import org.elixir_lang.sdk.HomePath
import org.elixir_lang.sdk.wsl.wslCompat
import org.jdom.Element

/**
 * Stores the reference from an Elixir SDK to its dependent Erlang SDK.
 *
 * ## Persistence Model
 *
 * The Erlang SDK name is persisted in the `erlang-sdk-name` XML attribute. Additional JPS-facing
 * attributes (mix home and WSL flags) are also written to disk for the compiler process.
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
class SdkAdditionalData(private val elixirSdk: Sdk) :
    SdkAdditionalDataBase(),
    ValidatableSdkAdditionalData,
    Cloneable {
    // Persistence layer - the ONLY thing saved to disk
    private var dataVersion: Int = CURRENT_DATA_VERSION
    private var erlangSdkName: String? = null
    private var mixHome: String? = null
    private var mixHomeReplacePrefix: String? = null
    private var wslUncPath: Boolean = false

    // Runtime cache - lazily populated, cleared when invalid
    // Not persisted, not cloned
    @Transient
    private var cachedErlangSdk: Sdk? = null

    companion object {
        private const val DATA_VERSION = "data-version"
        private const val CURRENT_DATA_VERSION = 1
        private const val ERLANG_SDK_NAME = "erlang-sdk-name"
        private const val MIX_HOME = "mix-home"
        private const val MIX_HOME_REPLACE_PREFIX = "mix-home-replace-prefix"
        private const val WSL_UNC_PATH = "wsl-unc-path"
        private val LOG = Logger.getInstance(SdkAdditionalData::class.java)
    }

    // Secondary constructor for creating new SDKs with a known Erlang SDK
    constructor(erlangSdk: Sdk?, elixirSdk: Sdk) : this(elixirSdk) {
        this.erlangSdkName = erlangSdk?.name
        this.cachedErlangSdk = erlangSdk
        refreshDerivedValues()
    }

    override fun markInternalsAsCommited(commitStackTrace: Throwable) {
        // No internal mutable structures to freeze.
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
        dataVersion = element.getAttributeValue(DATA_VERSION)?.toIntOrNull() ?: 0
        erlangSdkName = element.getAttributeValue(ERLANG_SDK_NAME)
        mixHome = element.getAttributeValue(MIX_HOME)
        mixHomeReplacePrefix = element.getAttributeValue(MIX_HOME_REPLACE_PREFIX)
        wslUncPath = element.getAttributeValue(WSL_UNC_PATH)?.toBoolean() == true
        cachedErlangSdk = null  // Force re-lookup on next access
    }

    @Throws(WriteExternalException::class)
    fun writeExternal(element: Element) {
        if (dataVersion > 0) {
            element.setAttribute(DATA_VERSION, dataVersion.toString())
        }
        erlangSdkName?.let {
            element.setAttribute(ERLANG_SDK_NAME, it)
        }
        mixHome?.let { element.setAttribute(MIX_HOME, it) }
        mixHomeReplacePrefix?.let { element.setAttribute(MIX_HOME_REPLACE_PREFIX, it) }
        if (wslUncPath) {
            element.setAttribute(WSL_UNC_PATH, "true")
        }
    }

    @Throws(CloneNotSupportedException::class)
    public override fun clone(): Any {
        val cloned = SdkAdditionalData(elixirSdk)
        cloned.dataVersion = this.dataVersion
        cloned.erlangSdkName = this.erlangSdkName
        cloned.mixHome = this.mixHome
        cloned.mixHomeReplacePrefix = this.mixHomeReplacePrefix
        cloned.wslUncPath = this.wslUncPath
        // Don't clone cachedErlangSdk - let it be lazily resolved
        return cloned
    }

    /**
     * Returns the configured Erlang SDK name without triggering a lookup.
     * Use this when you need to check the stored reference (e.g., in SDK removal listeners).
     */
    fun getErlangSdkName(): String? = erlangSdkName

    fun ensureDataVersion(): Boolean {
        assertWritable()
        if (dataVersion == CURRENT_DATA_VERSION) {
            return false
        }
        dataVersion = CURRENT_DATA_VERSION
        return true
    }

    fun setErlangSdk(sdk: Sdk?) {
        assertWritable()
        erlangSdkName = sdk?.name
        cachedErlangSdk = sdk
    }

    fun setErlangSdkName(name: String?) {
        assertWritable()
        erlangSdkName = name
        cachedErlangSdk = null
    }

    fun getMixHome(): String? = mixHome

    fun getMixHomeReplacePrefix(): String? = mixHomeReplacePrefix

    fun isWslUncPath(): Boolean = wslUncPath

    fun refreshDerivedValues(): Boolean {
        assertWritable()
        val homePath = elixirSdk.homePath
        val source = homePath?.let { HomePath.detectSource(it) }

        val newMixHome = HomePath.mixHome(homePath)
        val newMixHomeReplacePrefix = HomePath.mixHomeReplacePrefix(source)
        val newWslUncPath = wslCompat.isWslUncPath(homePath)

        val changed =
            newMixHome != mixHome ||
                newMixHomeReplacePrefix != mixHomeReplacePrefix ||
                newWslUncPath != wslUncPath

        if (changed) {
            mixHome = newMixHome
            mixHomeReplacePrefix = newMixHomeReplacePrefix
            wslUncPath = newWslUncPath
        }

        return changed
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
        return null
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
}
