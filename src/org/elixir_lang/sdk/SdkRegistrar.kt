package org.elixir_lang.sdk

import com.intellij.openapi.application.edtWriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import org.elixir_lang.sdk.erlang_dependent.resolveErlangSdkOrNullAndNotify
import org.elixir_lang.sdk.wsl.wslCompat
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import org.elixir_lang.sdk.erlang_dependent.Type as ErlangDependentType

/** Holds the pre-computed parts for registering or updating an Erlang SDK. */
internal data class ErlangSdkPreparation(
    val existing: Sdk?,
    val template: ProjectJdkImpl,
)

object SdkRegistrar {
    /**
     * Canonicalises [homePath], resolves the version string and SDK name, and looks up any
     * already-registered SDK at that home. Returns null if the path or version cannot be resolved.
     * Shared by both the suspend ([registerOrUpdateErlangSdk]) and synchronous
     * ([org.elixir_lang.sdk.elixir.ElixirInternalErlangSdkSetup.registerErlangSdk]) registration paths.
     */
    internal fun prepareErlangSdk(homePath: String, resolvedVersion: String? = null): ErlangSdkPreparation? {
        val canonicalHomePath = canonicalHomePath(homePath) ?: return null
        val sdkType = ErlangSdkType.instance
        val versionString = ErlangSdkType.versionStringForHome(canonicalHomePath, resolvedVersion) ?: return null
        val sdkName = ErlangSdkType.suggestSdkNameForHome(canonicalHomePath, resolvedVersion)
        val existing = findSdkByHomePath(sdkType, canonicalHomePath)
        return ErlangSdkPreparation(existing, ProjectJdkImpl(sdkName, sdkType, canonicalHomePath, versionString))
    }

    suspend fun registerOrUpdateErlangSdk(
        homePath: String,
        resolvedVersion: String? = null,
    ): Sdk? {
        val prep = prepareErlangSdk(homePath, resolvedVersion) ?: return null
        val registeredSdk = registerOrUpdate(prep.existing, prep.template)
        ErlangSdkType.instance.setupSdkPaths(registeredSdk)
        return registeredSdk
    }

    suspend fun registerOrUpdateElixirSdk(
        homePath: String,
        erlangSdk: Sdk?,
        resolvedVersion: String? = null,
        project: Project? = null,
    ): Sdk? {
        val canonicalHomePath = canonicalHomePath(homePath) ?: return null
        val sdkType = ElixirSdkType.instance
        val versionString = ElixirSdkType.versionStringForHome(canonicalHomePath, resolvedVersion) ?: return null
        val existing = findSdkByHomePath(sdkType, canonicalHomePath)
        val sdkName = ElixirSdkType.suggestSdkNameForHome(canonicalHomePath, resolvedVersion)

        val updatedSdk = ProjectJdkImpl(sdkName, sdkType, canonicalHomePath, versionString)
        val registeredSdk = registerOrUpdate(existing, updatedSdk)

        ErlangDependentType.attachErlangDependency(registeredSdk, erlangSdk)

        registeredSdk.resolveErlangSdkOrNullAndNotify(sdkModel = null, project = project)

        sdkType.setupSdkPaths(registeredSdk)
        return registeredSdk
    }

    private fun canonicalHomePath(homePath: String?): String? =
        wslCompat.canonicalizePathNullable(homePath)

    private fun findSdkByHomePath(sdkType: SdkType, homePath: String): Sdk? {
        val projectJdkTable = ProjectJdkTable.getInstance()
        return projectJdkTable.allJdks.firstOrNull { sdk ->
            sdk.sdkType == sdkType && wslCompat.pathsEqualWslAware(sdk.homePath, homePath)
        }
    }

    private suspend fun registerOrUpdate(existing: Sdk?, updatedSdk: Sdk): Sdk {
        return edtWriteAction {
            val table = ProjectJdkTable.getInstance()
            if (existing == null) {
                table.addJdk(updatedSdk)
                updatedSdk
            } else {
                table.updateJdk(existing, updatedSdk)
                existing
            }
        }
    }
}
