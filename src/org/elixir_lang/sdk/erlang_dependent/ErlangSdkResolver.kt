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
    fun resolveErlangSdkResult(elixirSdk: Sdk, sdkModel: SdkModel? = null): ErlangSdkResult

    /**
     * Lightweight resolver that returns only the SDK or null, discarding failure details.
     * Use [resolveErlangSdkResult] when you need the reason for a missing dependency.
     */
    fun resolveErlangSdk(elixirSdk: Sdk, sdkModel: SdkModel? = null): Sdk? =
        when (val result = resolveErlangSdkResult(elixirSdk, sdkModel)) {
            is ErlangSdkResult.Success -> result.sdk
            is ErlangSdkResult.Missing -> null
        }

    companion object {
        fun getInstance(): ErlangSdkResolver =
            ApplicationManager.getApplication().getService(ErlangSdkResolver::class.java)
    }
}

/**
 * Default implementation extracted from SdkAdditionalData
 */
class DefaultErlangSdkResolver : ErlangSdkResolver {
    override fun resolveErlangSdkResult(elixirSdk: Sdk, sdkModel: SdkModel?): ErlangSdkResult {
        val elixirName = elixirSdk.name
        val additionalData = elixirSdk.elixirAdditionalData
        val configuredName = additionalData?.getErlangSdkName()?.takeIf { it.isNotBlank() }
            ?: return ErlangSdkResult.Missing(
                elixirSdk,
                MissingErlangSdkReason.NOT_CONFIGURED,
            )

        additionalData.getCachedErlangSdk()?.let { cached ->
            if (isValidAndExists(cached, sdkModel)) {
                if (cached.homePath.isNullOrBlank()) {
                    return ErlangSdkResult.Missing(
                        elixirSdk,
                        MissingErlangSdkReason.MISSING_HOME_PATH,
                        cached.name,
                    )
                }
                return ErlangSdkResult.Success(cached)
            }
            logger.debug { "[$elixirName] Cached Erlang SDK '${cached.name}' no longer valid" }
            additionalData.setCachedErlangSdk(null)
        }

        logger.debug { "[$elixirName] Looking up Erlang SDK by name: $configuredName" }
        val found = findErlangSdkByName(configuredName, sdkModel)
        if (found != null) {
            logger.debug { "[$elixirName] Found Erlang SDK '$configuredName'" }
            additionalData.setCachedErlangSdk(found)
            if (found.homePath.isNullOrBlank()) {
                return ErlangSdkResult.Missing(
                    elixirSdk,
                    MissingErlangSdkReason.MISSING_HOME_PATH,
                    found.name,
                )
            }
            return ErlangSdkResult.Success(found)
        }

        logger.debug { "[$elixirName] Erlang SDK '$configuredName' not found" }
        val candidate =
            sdkModel?.sdks?.find { it.name == configuredName }
                ?: ProjectJdkTable.getInstance().findJdk(configuredName)
        val reason = when {
            candidate == null -> MissingErlangSdkReason.NOT_FOUND
            !Type.staticIsValidDependency(candidate) -> MissingErlangSdkReason.INVALID_TYPE
            else -> MissingErlangSdkReason.NOT_FOUND
        }

        return ErlangSdkResult.Missing(
            elixirSdk,
            reason,
            configuredName,
        )
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

enum class MissingErlangSdkReason {
    NOT_CONFIGURED,
    NOT_FOUND,
    INVALID_TYPE,
    MISSING_HOME_PATH
}

sealed class ErlangSdkResult {
    data class Success(val sdk: Sdk) : ErlangSdkResult()
    data class Missing(
        val elixirSdkName: String,
        val reason: MissingErlangSdkReason,
        val erlangSdkName: String? = null,
    ) : ErlangSdkResult() {
        constructor(
            elixirSdk: Sdk,
            reason: MissingErlangSdkReason,
            erlangSdkName: String? = null,
        ) : this(elixirSdk.name, reason, erlangSdkName)
    }
}
