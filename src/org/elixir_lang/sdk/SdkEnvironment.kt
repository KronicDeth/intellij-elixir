package org.elixir_lang.sdk

import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.eel.EelDescriptor
import com.intellij.platform.eel.provider.getEelDescriptor
import org.elixir_lang.sdk.wsl.wslCompat
import java.nio.file.FileSystemNotFoundException
import java.nio.file.InvalidPathException
import java.nio.file.Path

/**
 * Execution-environment identity for SDK homes and wizard target directories, shared by the
 * import wizard ([org.elixir_lang.mix.project._import.step.ElixirSdkForModuleStep]) and the New
 * Project wizard ([org.elixir_lang.new_project_wizard.elixirSdkComboBox]).
 *
 * ProjectSdksModel.reset() filters SDKs to the project's environment, but during import and New
 * Project the wizard supplies the default project, which always resolves to the local
 * environment, so WSL/remote SDKs are filtered out. The APIs that would let us pass the real
 * target environment (syncSdks(EelMachine), sdkMatchesEel()) are @ApiStatus.Internal, and
 * implementing Project to fake the path violates @NonExtendable on Project -- see
 * https://youtrack.jetbrains.com/issue/IJPL-243914. Instead, the target environment is resolved
 * from the wizard's directory with the public (experimental) Path.getEelDescriptor(), and
 * matching SDKs are re-added via the public ProjectSdksModel.addSdk() in [syncTargetSdks].
 *
 * Environments are compared by [EelDescriptor] equality rather than by EelMachine, because no
 * machine API spans the supported builds (EelDescriptor.machine exists only in 2025.3,
 * getResolvedEelMachine() only in 2026.1+) while Path.getEelDescriptor() is binary-stable across
 * all of them. WSL descriptor equality includes the UNC root, so the path is normalized with
 * WslCompatService.canonicalizeWslPrefix first to make `\\wsl$\<distro>` and
 * `\\wsl.localhost\<distro>` resolve to the same descriptor.
 */
@Suppress("UnstableApiUsage")
object SdkEnvironment {
    /**
     * The [EelDescriptor] of the environment [path] belongs to (e.g. a WSL distro for a
     * `\\wsl$\...` path), or null when the path cannot be parsed.
     *
     * Only the prefix is normalized (not canonicalizePath): symlinks are irrelevant to
     * environment identity, and resolving them would hit the (possibly stopped) WSL filesystem
     * from the EDT. SdkModificator stores home paths system-independently
     * (`//wsl$/<distro>/...`), so the path is converted to system-dependent form first or the
     * prefix rewrite would never match SDK homes. FileSystemNotFoundException covers SDKs whose
     * WSL distribution no longer exists.
     */
    fun eelDescriptor(path: String): EelDescriptor? =
        try {
            val systemDependentPath = FileUtil.toSystemDependentName(path)
            Path.of(with(wslCompat) { systemDependentPath.canonicalizeWslPrefix() }).getEelDescriptor()
        } catch (_: InvalidPathException) {
            null
        } catch (_: FileSystemNotFoundException) {
            null
        }

    /**
     * The [EelDescriptor] of the environment owning the SDK's home path, or null when the home
     * path is blank (a never-configured ProjectJdkImpl reports "" rather than null) or invalid.
     */
    fun sdkDescriptor(sdk: Sdk): EelDescriptor? =
        sdk.homePath?.takeUnless(String::isBlank)?.let { eelDescriptor(it) }

    /**
     * Whether the SDK should be visible for [targetDescriptor]: hides SDKs from other
     * environments (e.g. local SDKs when the wizard targets WSL), mirroring the environment
     * filtering that ProjectSdksModel.reset() applies for opened projects. Permissive when
     * either environment cannot be determined, so SDK validation stays the deciding factor.
     */
    fun sdkVisibleFor(targetDescriptor: EelDescriptor?, sdk: Sdk): Boolean {
        if (targetDescriptor == null) return true
        val descriptor = sdkDescriptor(sdk) ?: return true

        return descriptor == targetDescriptor
    }

    /**
     * Re-adds registered SDKs that reset() filtered out because the default project resolves to
     * the local environment. Replicates the internal ProjectSdksModel.syncSdks(EelMachine) using
     * public API: addSdk() clones the SDK into the model and fires sdkAdded, as syncSdks() does,
     * and apply() treats SDKs already present in ProjectJdkTable as updates, not additions.
     */
    fun syncTargetSdks(sdksModel: ProjectSdksModel, targetDescriptor: EelDescriptor?) {
        if (targetDescriptor == null) return
        val projectSdks = sdksModel.projectSdks

        for (sdk in ProjectJdkTable.getInstance().allJdks) {
            if (projectSdks.containsKey(sdk) || projectSdks.containsValue(sdk)) continue

            if (sdkDescriptor(sdk) == targetDescriptor) {
                sdksModel.addSdk(sdk)
            }
        }
    }
}
