package org.elixir_lang.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.elixir_lang.notification.setup_sdk.Notifier
import org.elixir_lang.sdk.SdkRegistrar
import org.elixir_lang.sdk.erlang.Type as ErlangSdkType
import java.io.File

/**
 * Development-only action that adds Elixir and Erlang SDKs from system properties.
 *
 * Usage with Gradle:
 *   ./gradlew runIde -PrunIdeSdkErlangPath='/path/to/erlang' -PrunIdeSdkElixirPath='/path/to/elixir'
 *
 * This action is only visible in internal/development mode (idea.is.internal=true).
 */
class AddDevelopmentSdksAction : AnAction() {

    companion object {
        const val ERLANG_PATH_PROPERTY = "runIdeSdkErlangPath"
        const val ELIXIR_PATH_PROPERTY = "runIdeSdkElixirPath"
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val erlangPath = System.getProperty(ERLANG_PATH_PROPERTY)
        val elixirPath = System.getProperty(ELIXIR_PATH_PROPERTY)

        if (erlangPath.isNullOrBlank() && elixirPath.isNullOrBlank()) {
            Notifier.sdkRefreshWarning(
                project,
                "No SDK paths configured. Use -P$ERLANG_PATH_PROPERTY and/or -P$ELIXIR_PATH_PROPERTY with Gradle."
            )
            return
        }

        try {
            var erlangSdk: com.intellij.openapi.projectRoots.Sdk? = null
            var elixirSdkCreated = false

            // Create Erlang SDK first (Elixir depends on it)
            if (!erlangPath.isNullOrBlank()) {
                erlangSdk = createErlangSdk(erlangPath)
                if (erlangSdk != null) {
                    Notifier.sdkRefreshSuccess(project, 0, 0, 1, 1)
                }
            }

            // Create Elixir SDK
            if (!elixirPath.isNullOrBlank()) {
                val createdElixirSdk = createElixirSdk(elixirPath, erlangSdk, project)
                elixirSdkCreated = createdElixirSdk != null
                if (elixirSdkCreated) {
                    Notifier.sdkRefreshSuccess(project, 1, 1, 0, 0)
                }
            }

            if (erlangSdk == null && !elixirSdkCreated) {
                Notifier.sdkRefreshWarning(project, "Failed to create SDKs. Check paths are valid.")
            }
        } catch (ex: Exception) {
            Notifier.sdkRefreshError(project, ex.message ?: "Unknown error creating SDKs")
        }
    }

    private fun createErlangSdk(homePath: String): com.intellij.openapi.projectRoots.Sdk? {
        if (!File(homePath).exists()) {
            return null
        }

        return SdkRegistrar.registerOrUpdateErlangSdk(homePath)
    }

    private fun createElixirSdk(
        homePath: String,
        erlangSdk: com.intellij.openapi.projectRoots.Sdk?,
        project: com.intellij.openapi.project.Project
    ): com.intellij.openapi.projectRoots.Sdk? {
        if (!File(homePath).exists()) {
            return null
        }

        val actualErlangSdk = erlangSdk ?: findExistingErlangSdk()
        return SdkRegistrar.registerOrUpdateElixirSdk(
            homePath = homePath,
            erlangSdk = actualErlangSdk,
            project = project,
        )
    }

    private fun findExistingErlangSdk(): com.intellij.openapi.projectRoots.Sdk? =
        com.intellij.openapi.projectRoots.ProjectJdkTable.getInstance()
            .getSdksOfType(ErlangSdkType.instance)
            .firstOrNull()

    override fun update(e: AnActionEvent) {
        // Only enable if we have SDK paths configured
        val hasErlangPath = !System.getProperty(ERLANG_PATH_PROPERTY).isNullOrBlank()
        val hasElixirPath = !System.getProperty(ELIXIR_PATH_PROPERTY).isNullOrBlank()
        e.presentation.isEnabled = hasErlangPath || hasElixirPath

        // Update description to show configured paths
        val paths = buildList {
            System.getProperty(ERLANG_PATH_PROPERTY)?.takeIf { it.isNotBlank() }?.let {
                add("Erlang: $it")
            }
            System.getProperty(ELIXIR_PATH_PROPERTY)?.takeIf { it.isNotBlank() }?.let {
                add("Elixir: $it")
            }
        }
        e.presentation.description = if (paths.isNotEmpty()) {
            "Add SDKs from: ${paths.joinToString(", ")}"
        } else {
            "No SDK paths configured via Gradle properties"
        }
    }

    override fun isDumbAware(): Boolean = true
}
