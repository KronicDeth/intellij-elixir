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

    constructor(erlangSdk: Sdk?, elixirSdk: Sdk) {
        this.erlangSdk = erlangSdk
        this.elixirSdk = elixirSdk
    }

    // readExternal
    constructor(elixirSdk: Sdk) {
        this.elixirSdk = elixirSdk
    }

    /**
     * Checks if the ERLANG_SDK_NAME properties are configured correctly, and throws an exception
     * if they are not.
     *
     * @param sdkModel the model containing all configured SDKs.
     * @throws ConfigurationException if the ERLANG_SDK_NAME is not configured correctly.
     * @since 5.0.1
     */
    @Throws(ConfigurationException::class)
    override fun checkValid(sdkModel: SdkModel) {
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

            throw ConfigurationException(message)
        }
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
        val elixirSdkName = elixirSdk.name

        if (erlangSdk == null) {
            if (erlangSdkName != null) {
                LOG.debug("[$elixirSdkName] Looking up Erlang SDK by name: $erlangSdkName")

                // First try the sdkModel (includes unsaved SDKs)
                if (sdkModel != null) {
                    erlangSdk = sdkModel.sdks.find { it.name == this.erlangSdkName }
                    if (erlangSdk != null) {
                        LOG.debug("[$elixirSdkName] Found Erlang SDK '${this.erlangSdkName}' in sdkModel")
                        this.erlangSdkName = null
                    }
                }

                // Fall back to ProjectJdkTable
                if (erlangSdk == null) {
                    erlangSdk = jdkTable.findJdk(this.erlangSdkName!!)
                    if (erlangSdk == null) {
                        LOG.debug("[$elixirSdkName] Erlang SDK '${this.erlangSdkName}' not found in table, clearing orphaned reference")
                        this.erlangSdkName = null
                    } else {
                        LOG.debug("[$elixirSdkName] Found Erlang SDK '${this.erlangSdkName}' in ProjectJdkTable")
                        this.erlangSdkName = null
                    }
                }
            } else {
                LOG.debug("[$elixirSdkName] No Erlang SDK name configured, auto-discovering...")

                // Auto-discover: first try sdkModel, then ProjectJdkTable
                if (sdkModel != null) {
                    erlangSdk = sdkModel.sdks.find { Type.staticIsValidDependency(it) }
                    if (erlangSdk != null) {
                        LOG.debug("[$elixirSdkName] Auto-discovered Erlang SDK '${erlangSdk!!.name}' from sdkModel")
                    }
                }

                if (erlangSdk == null) {
                    for (jdk in jdkTable.allJdks) {
                        if (Type.staticIsValidDependency(jdk)) {
                            erlangSdk = jdk
                            LOG.debug("[$elixirSdkName] Auto-discovered Erlang SDK '${jdk.name}' from ProjectJdkTable")
                            break
                        }
                    }
                }

                if (erlangSdk == null) {
                    LOG.debug("[$elixirSdkName] No valid Erlang SDK found for auto-discovery")
                }
            }
        } else {
            // Validate that the cached SDK still exists
            val cachedName = erlangSdk!!.name
            val existsInModel = sdkModel?.sdks?.any { it.name == cachedName } == true
            val existsInTable = jdkTable.findJdk(cachedName) != null

            if (!existsInModel && !existsInTable) {
                LOG.debug("[$elixirSdkName] Cached Erlang SDK '$cachedName' no longer exists, clearing reference")
                erlangSdk = null
            } else {
                LOG.debug("[$elixirSdkName] Using cached Erlang SDK '$cachedName' (inModel=$existsInModel, inTable=$existsInTable)")
            }
        }

        return erlangSdk
    }

    /**
     * Returns the configured Erlang SDK name without triggering a lookup.
     * Useful for debugging and understanding the current state.
     */
    fun getErlangSdkName(): String? = erlangSdkName ?: erlangSdk?.name

    fun setErlangSdk(erlangSdk: Sdk?) {
        this.erlangSdk = erlangSdk
    }

    companion object {
        private const val ERLANG_SDK_NAME = "erlang-sdk-name"
        private val LOG = Logger.getInstance(SdkAdditionalData::class.java)
    }
}
