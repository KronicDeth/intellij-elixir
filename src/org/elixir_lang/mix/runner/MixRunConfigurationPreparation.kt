package org.elixir_lang.mix.runner

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.isElixirModule
import org.elixir_lang.mixContentRoots
import org.elixir_lang.mix.sync.MixEventClassifier
import org.elixir_lang.sdk.elixir.findElixirSdkForRoot

internal enum class MixRunConfigurationPreparationStatus {
    NO_ROOT,
    NO_SDK,
    NO_MODULE,
    READY,
}

internal data class MixRunConfigurationPreparation(
    val status: MixRunConfigurationPreparationStatus,
    val rootName: String? = null,
    val rootPath: String? = null,
    val settings: RunnerAndConfigurationSettings? = null,
)

@RequiresBackgroundThread
@RequiresReadLock
internal fun prepareMixRunConfiguration(
    project: Project,
    fileHint: VirtualFile?,
    createSettings: (Project, VirtualFile) -> RunnerAndConfigurationSettings?,
): MixRunConfigurationPreparation {
    ThreadingAssertions.assertBackgroundThread()
    ThreadingAssertions.assertReadAccess()

    val projectRoot = findMixRootForHint(project, fileHint)
        ?: return MixRunConfigurationPreparation(MixRunConfigurationPreparationStatus.NO_ROOT)

    val rootName = projectRoot.name
    val rootPath = projectRoot.path

    if (findElixirSdkForRoot(project, projectRoot) == null) {
        return MixRunConfigurationPreparation(
            status = MixRunConfigurationPreparationStatus.NO_SDK,
            rootName = rootName,
            rootPath = rootPath,
        )
    }

    val settings = createSettings(project, projectRoot)
        ?: return MixRunConfigurationPreparation(
            status = MixRunConfigurationPreparationStatus.NO_MODULE,
            rootName = rootName,
            rootPath = rootPath,
        )

    return MixRunConfigurationPreparation(
        status = MixRunConfigurationPreparationStatus.READY,
        rootName = rootName,
        rootPath = rootPath,
        settings = settings,
    )
}

/**
 * Resolves the Mix project root to use for the mix run configuration.
 *
 * When [fileHint] is available (e.g. the currently open editor file), the owning Elixir
 * module is looked up and its Mix content root is returned - this is unambiguous even when
 * multiple independent Mix projects are open in the same IDE window.
 *
 * Falls back to the first top-level Elixir Mix root when no file context is available
 * (e.g. when invoked from a notification action that carries no editor context).
 */
@RequiresBackgroundThread
@RequiresReadLock
private fun findMixRootForHint(project: Project, fileHint: VirtualFile?): VirtualFile? {
    ThreadingAssertions.assertBackgroundThread()
    ThreadingAssertions.assertReadAccess()

    if (fileHint != null) {
        val module = ModuleUtilCore.findModuleForFile(fileHint, project)
        if (module != null && module.isElixirModule()) {
            val mixRoot = module.mixContentRoots().firstOrNull()?.root
            if (mixRoot != null) return mixRoot
        }
    }

    // Fallback: notification-triggered path or no editor open - use first top-level Mix root
    return MixEventClassifier.elixirMixContentRoots(project).firstOrNull()
}
