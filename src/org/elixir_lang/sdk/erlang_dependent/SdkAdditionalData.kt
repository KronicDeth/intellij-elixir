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
        if (getErlangSdk() == null) {
            throw ConfigurationException("Please configure the Erlang ERLANG_SDK_NAME")
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
            element.setAttribute(ERLANG_SDK_NAME, sdk.name)
        }
    }

    fun getErlangSdk(): Sdk? {
        val jdkTable = ProjectJdkTable.getInstance()

        if (erlangSdk == null) {
            if (erlangSdkName != null) {
                erlangSdk = jdkTable.findJdk(erlangSdkName!!)
                erlangSdkName = null
            } else {
                for (jdk in jdkTable.allJdks) {
                    if (Type.staticIsValidDependency(jdk)) {
                        erlangSdk = jdk
                        break
                    }
                }
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
