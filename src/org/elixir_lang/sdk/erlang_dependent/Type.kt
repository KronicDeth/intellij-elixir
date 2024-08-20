package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.SdkAdditionalData
import com.intellij.openapi.projectRoots.impl.DependentSdkType
import com.intellij.openapi.roots.JavadocOrderRootType
import com.intellij.openapi.roots.OrderRootType
import org.elixir_lang.sdk.ProcessOutput.isSmallIde
import org.elixir_lang.sdk.elixir.Type.Companion.erlangSdkType
import org.elixir_lang.sdk.erlang.Type
import org.jdom.Element

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
        return "You need to configure an " + dependencyType.name + ".  Click OK to be taken through the " +
                dependencyType.name + " configuration.  Click Cancel to stop configuring this SDK AND the " +
                dependencyType.name + "."
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

    companion object {
        private const val ERLANG_SDK_TYPE_CANONICAL_NAME = "org.intellij.erlang.sdk.ErlangSdkType"
        private val ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME: String = Type::class.java.canonicalName

        fun staticIsValidDependency(sdk: Sdk): Boolean {
            val sdkTypeCanonicalName = sdk.sdkType.javaClass.canonicalName

            val isValidDependency = if (isSmallIde) {
                sdkTypeCanonicalName == ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME
            } else {
                sdkTypeCanonicalName == ERLANG_SDK_TYPE_CANONICAL_NAME ||
                        sdkTypeCanonicalName == ERLANG_SDK_FOR_ELIXIR_SDK_TYPE_CANONICAL_NAME
            }

            return isValidDependency
        }
    }
}
