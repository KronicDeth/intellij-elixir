package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel


/**
 * Interface allows mocking in tests
 */
interface ErlangSdkResolver {
    fun resolveErlangSdk(elixirSdk: Sdk, sdkModel: SdkModel? = null): Sdk?

    companion object {
        fun getInstance(): ErlangSdkResolver =
            ApplicationManager.getApplication().getService(ErlangSdkResolver::class.java)
    }
}

/**
 * Default implementation extracted from SdkAdditionalData
 */
class DefaultErlangSdkResolver : ErlangSdkResolver {
    override fun resolveErlangSdk(elixirSdk: Sdk, sdkModel: SdkModel?): Sdk? {
        val additionalData = elixirSdk.elixirAdditionalData ?: return null
        return resolveFromAdditionalData(elixirSdk, additionalData, sdkModel)
    }

    private fun resolveFromAdditionalData(
        elixirSdk: Sdk,
        additionalData: SdkAdditionalData,
        sdkModel: SdkModel?
    ): Sdk? {
        val elixirName = elixirSdk.name

        additionalData.getCachedErlangSdk()?.let { cached ->
            if (isValidAndExists(cached, sdkModel)) {
                return cached
            }
            logger.debug { "[$elixirName] Cached Erlang SDK '${cached.name}' no longer valid" }
            additionalData.setCachedErlangSdk(null)
        }

        additionalData.getErlangSdkName()?.let { name ->
            logger.debug { "[$elixirName] Looking up Erlang SDK by name: $name" }
            val found = findErlangSdkByName(name, sdkModel)
            if (found != null) {
                logger.debug { "[$elixirName] Found Erlang SDK '$name'" }
                additionalData.setCachedErlangSdk(found)
                return found
            }
            logger.debug { "[$elixirName] Erlang SDK '$name' not found" }
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
        return sdkModel?.sdks?.find { it.name == name && Type.staticIsValidDependency(it) }
            ?: jdkTable.findJdk(name)?.takeIf { Type.staticIsValidDependency(it) }
    }

    companion object {
        private val logger = Logger.getInstance(DefaultErlangSdkResolver::class.java)
    }
}
