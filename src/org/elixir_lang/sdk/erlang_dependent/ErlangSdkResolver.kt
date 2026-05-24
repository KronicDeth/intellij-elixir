package org.elixir_lang.sdk.erlang_dependent

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.debug
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkModel
import org.elixir_lang.sdk.wsl.wslCompat


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

        /**
         * Returns the first registered Erlang SDK that has a non-null home path, or null if none
         * are registered. Used as a fallback when no Erlang SDK has been explicitly paired with an
         * Elixir SDK.
         */
        fun findAnyRegistered(): Sdk? =
            ProjectJdkTable.getInstance()
                .getSdksOfType(org.elixir_lang.sdk.erlang.Type.instance)
                .firstOrNull { it.homePath != null }
    }
}

/**
 * Default implementation.
 *
 * Resolution priority:
 * 1. `erlangSdkHomePath` (stable; survives SDK renames) - WSL-aware path match in ProjectJdkTable
 * 2. `erlangSdkName` (legacy fallback for configs written before home-path was added) - name match;
 *    self-heals by writing the resolved path back to `erlangSdkHomePath` so future lookups use path
 * 3. Both absent → NOT_CONFIGURED
 */
class DefaultErlangSdkResolver : ErlangSdkResolver {
    override fun resolveErlangSdkResult(elixirSdk: Sdk, sdkModel: SdkModel?): ErlangSdkResult {
        val elixirName = elixirSdk.name
        val additionalData = elixirSdk.elixirAdditionalData
            ?: return ErlangSdkResult.Missing(elixirSdk, MissingErlangSdkReason.NOT_CONFIGURED)

        // Check cache first
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

        // 1. Path-first lookup (stable; survives renames)
        val configuredHomePath = additionalData.getErlangSdkHomePath()?.takeIf { it.isNotBlank() }
        if (configuredHomePath != null) {
            logger.debug { "[$elixirName] Looking up Erlang SDK by home path: $configuredHomePath" }
            val found = findErlangSdkByHomePath(configuredHomePath, sdkModel)
            if (found != null) {
                logger.debug { "[$elixirName] Found Erlang SDK by home path: ${found.name}" }
                additionalData.setCachedErlangSdk(found)
                // Self-heal: keep name in sync with current SDK name after a rename.
                // Not wrapped in a write action - see name-fallback path below for rationale.
                if (additionalData.getErlangSdkName() != found.name) {
                    additionalData.setErlangSdk(found)
                }
                if (found.homePath.isNullOrBlank()) {
                    return ErlangSdkResult.Missing(elixirSdk, MissingErlangSdkReason.MISSING_HOME_PATH, found.name)
                }
                return ErlangSdkResult.Success(found)
            }
            logger.debug { "[$elixirName] No Erlang SDK found at home path: $configuredHomePath" }
        }

        // 2. Name-based fallback (legacy configs without erlangSdkHomePath)
        val configuredName = additionalData.getErlangSdkName()?.takeIf { it.isNotBlank() }
            ?: return ErlangSdkResult.Missing(elixirSdk, MissingErlangSdkReason.NOT_CONFIGURED)

        logger.debug { "[$elixirName] Falling back to name lookup: $configuredName" }
        val found = findErlangSdkByName(configuredName, sdkModel)
        if (found != null) {
            logger.debug { "[$elixirName] Found Erlang SDK by name: $configuredName - self-healing home path and name" }
            additionalData.setCachedErlangSdk(found)
            // Self-heal: atomically write both home path and name so that future lookups use
            // path-first resolution and the name stays in sync with the current SDK name.
            // Not wrapped in a write action intentionally - this resolver can be called while
            // already holding a read lock (e.g. from EditorNotificationProvider.collectNotificationData
            // which is @RequiresReadLock), and acquiring a write action from under a read lock
            // deadlocks. String field assignment is an atomic JVM reference write; a stale value
            // is only visible until the next project save, at which point commitChanges() in
            // SdkRegistrar / attachErlangDependency writes the authoritative value.
            additionalData.setErlangSdk(found)
            if (found.homePath.isNullOrBlank()) {
                return ErlangSdkResult.Missing(elixirSdk, MissingErlangSdkReason.MISSING_HOME_PATH, found.name)
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

        return ErlangSdkResult.Missing(elixirSdk, reason, configuredName)
    }

    private fun isValidAndExists(sdk: Sdk, sdkModel: SdkModel?): Boolean {
        if (!Type.staticIsValidDependency(sdk)) return false
        val name = sdk.name
        val jdkTable = ProjectJdkTable.getInstance()
        return (sdkModel?.sdks?.any { it.name == name } == true)
            || (jdkTable.findJdk(name) != null)
    }

    private fun findErlangSdkByHomePath(homePath: String, sdkModel: SdkModel?): Sdk? {
        val fromModel = sdkModel?.sdks?.find { sdk ->
            Type.staticIsValidDependency(sdk) && wslCompat.pathsEqualWslAware(sdk.homePath, homePath)
        }
        if (fromModel != null) return fromModel
        return ProjectJdkTable.getInstance().allJdks.firstOrNull { sdk ->
            Type.staticIsValidDependency(sdk) && wslCompat.pathsEqualWslAware(sdk.homePath, homePath)
        }
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
