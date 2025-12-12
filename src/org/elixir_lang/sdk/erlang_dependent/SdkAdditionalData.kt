package org.elixir_lang.sdk.erlang_dependent

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

    fun getErlangSdk(): Sdk? {
        val jdkTable = ProjectJdkTable.getInstance()

        if (erlangSdk == null) {
            if (erlangSdkName != null) {
                erlangSdk = jdkTable.findJdk(erlangSdkName!!)
                if (erlangSdk == null) {
                    // Clear orphaned reference - SDK was deleted
                    erlangSdkName = null
                } else {
                    // Found the SDK, clear the name since we have the reference
                    erlangSdkName = null
                }
            } else {
                // Auto-discover if no specific SDK was configured
                for (jdk in jdkTable.allJdks) {
                    if (Type.staticIsValidDependency(jdk)) {
                        erlangSdk = jdk
                        break
                    }
                }
            }
        } else {
            // Validate that the cached SDK still exists in the table
            if (jdkTable.findJdk(erlangSdk!!.name) == null) {
                // SDK was deleted, clear the reference
                erlangSdk = null
            }
        }

        return erlangSdk
    }

    fun setErlangSdk(erlangSdk: Sdk?) {
        this.erlangSdk = erlangSdk
    }

    companion object {
        private const val ERLANG_SDK_NAME = "erlang-sdk-name"
    }
}
