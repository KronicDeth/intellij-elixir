package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.elixir.Type as ElixirSdkType
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import org.elixir_lang.sdk.erlang_dependent.Type as ErlangDependentType
import org.elixir_lang.sdk.wsl.wslCompat

object SdkRegistrar {
    fun registerOrUpdateErlangSdk(
        homePath: String,
        resolvedVersion: String? = null,
    ): Sdk? {
        val canonicalHomePath = canonicalHomePath(homePath) ?: return null
        val sdkType = ErlangSdkType.instance
        val versionString = ErlangSdkType.versionStringForHome(canonicalHomePath, resolvedVersion) ?: return null
        val existing = findSdkByHomePath(sdkType, canonicalHomePath)
        val sdkName = ErlangSdkType.suggestSdkNameForHome(canonicalHomePath, resolvedVersion)

        val updatedSdk = ProjectJdkImpl(sdkName, sdkType, canonicalHomePath, versionString)
        val registeredSdk = registerOrUpdate(existing, updatedSdk)

        sdkType.setupSdkPaths(registeredSdk)
        return registeredSdk
    }

    fun registerOrUpdateElixirSdk(
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

        if (erlangSdk == null && project != null) {
            Notifier.elixirSdkMissingErlangDependency(project, registeredSdk.name)
        }

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

    private fun registerOrUpdate(existing: Sdk?, updatedSdk: Sdk): Sdk {
        return writeAction {
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

    private fun <T> writeAction(block: () -> T): T {
        val app = ApplicationManager.getApplication()
        if (app.isWriteAccessAllowed) {
            return block()
        }

        var result: T? = null
        var resultSet = false
        val runnable = Runnable {
            result = WriteAction.compute<T, Throwable> { block() }
            resultSet = true
        }

        if (app.isDispatchThread) {
            runnable.run()
        } else {
            app.invokeAndWait(runnable)
        }

        if (!resultSet) {
            throw IllegalStateException("Write action did not execute")
        }
        @Suppress("UNCHECKED_CAST")
        return result as T
    }
}
