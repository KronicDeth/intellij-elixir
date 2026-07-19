package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Plugin-side replacement for SdkConfigurationUtil.selectSdkHome.
 *
 * The platform method cannot be used:
 * - The deprecated 2-arg form anchors its execution-environment check at `user.home`, so any
 *   SDK home picked on WSL fails with "Environment Mismatch" and can never be created.
 * - The Path-aware forms are binary-incompatible across supported IDEs: 261 has
 *   selectSdkHome(SdkType, Component?, Path, Consumer); 262.4852.50 inserted Project? before
 *   Consumer (https://youtrack.jetbrains.com/issue/IJPL-236990 /
 *   https://github.com/JetBrains/intellij-community/commit/edf92c9185e1a3e2f28c237c91e6fe493f7f80ac),
 *   so no single call compiles and runs against both.
 *
 * Mirrors the platform implementation minus that environment check: the base path anchors the
 * chooser in the right environment, and validity is enforced by SdkType.isValidSdkHome plus the
 * chooser's own descriptor validation instead.
 */
object SdkHomeChooser {
    /**
     * The base path to anchor the chooser to when the caller has nothing better: the wizard's
     * published target directory if any, else the user home directory.
     */
    fun defaultBasePath(): Path =
        SdkDetectionContext.resolve() ?: Path.of(System.getProperty("user.home"))

    fun selectSdkHome(sdkType: SdkType, basePath: Path = defaultBasePath(), onHomeChosen: (String) -> Unit) {
        val suggestedRootFuture = ApplicationManager.getApplication().executeOnPooledThread<VirtualFile?> {
            SdkConfigurationUtil.getSuggestedSdkRoot(sdkType, basePath)
        }
        val suggestedRoot = try {
            suggestedRootFuture.get(200, TimeUnit.MILLISECONDS)
        } catch (_: Exception) {
            null
        } ?: LocalFileSystem.getInstance().refreshAndFindFileByNioFile(basePath)

        FileChooser.chooseFiles(sdkType.homeChooserDescriptor, null, null, suggestedRoot) { chosen ->
            val chosenPath = chosen[0].path
            val adjustedPath = sdkType.adjustSelectedSdkHome(chosenPath)
            val adjustedPathValid = AtomicBoolean(false)
            ProgressManager.getInstance().runProcessWithProgressSynchronously(
                { adjustedPathValid.set(sdkType.isValidSdkHome(adjustedPath)) },
                ProjectBundle.message("progress.title.checking.sdk.home"), true, null
            )
            onHomeChosen(if (adjustedPathValid.get()) adjustedPath else chosenPath)
        }
    }
}
