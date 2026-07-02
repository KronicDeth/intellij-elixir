package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.concurrency.ThreadingAssertions
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread
import com.intellij.util.concurrency.annotations.RequiresReadLock
import org.elixir_lang.sdk.erlang.ErlangVersionDetector
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData
import org.elixir_lang.sdk.wsl.wslCompat

/**
 * Validation and health-check queries for Elixir SDK pairings.
 *
 * Houses logic that answers "is this SDK pairing healthy?" - distinct from path population
 * ([ElixirSdkPathConfigurator], [ElixirErlangClasspath]) and version detection
 * ([ElixirVersionDetector], [ElixirBuildInfo]).
 */
object ElixirSdkValidation {

    // -------------------------------------------------------------------------
    // OTP mismatch detection
    // -------------------------------------------------------------------------

    /**
     * Detects whether the OTP major the Elixir SDK was compiled against differs from the OTP
     * major of its currently paired Erlang IDE SDK.
     *
     * Returns `(elixirOtpMajor, erlangOtpMajor)` when they differ, `null` when they match,
     * when either side cannot be determined (e.g. missing BEAM file, missing Erlang SDK),
     * or when the user has suppressed this warning for the SDK.
     *
     * Must be called on a background thread and under a read lock.
     *
     * - Background thread: reads `Elixir.System.beam` and `OTP_VERSION` from disk.
     * - Read lock: resolves the paired Erlang SDK via [SdkAdditionalData.getErlangSdk],
     *   which may access the SDK table through [org.elixir_lang.sdk.erlang_dependent.ErlangSdkResolver].
     *
     * Use [org.elixir_lang.util.runWithEdtGuard] when calling from EDT-context code such as
     * [org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable].
     */
    @RequiresBackgroundThread
    @RequiresReadLock
    fun detectOtpMismatch(sdk: Sdk): Pair<String, String>? {
        ThreadingAssertions.assertBackgroundThread()
        ThreadingAssertions.assertReadAccess()
        val additionalData = sdk.sdkAdditionalData as? SdkAdditionalData ?: return null
        if (additionalData.isSuppressOtpMismatchWarning()) return null
        val erlangSdk = additionalData.getErlangSdk() ?: return null
        return detectOtpMismatch(sdk, erlangSdk)
    }

    /**
     * Detects OTP major mismatch between a given Elixir SDK and a given Erlang SDK.
     *
     * Use this overload when the Erlang SDK pairing may differ from what is persisted in
     * [SdkAdditionalData] - for example in
     * [org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable] where the user has
     * selected but not yet applied a new Erlang SDK.
     *
     * Returns `(elixirOtpMajor, erlangOtpMajor)` when they differ, `null` when they match or
     * when either side cannot be determined.
     *
     * Must NOT be called on the EDT - reads `Elixir.System.beam` and `OTP_VERSION` from disk.
     */
    @RequiresBackgroundThread
    fun detectOtpMismatch(elixirSdk: Sdk, erlangSdk: Sdk): Pair<String, String>? {
        ThreadingAssertions.assertBackgroundThread()
        val elixirHome = elixirSdk.homePath ?: return null
        val elixirOtpMajor = elixirSdk.getUserData(ElixirBuildInfo.ELIXIR_OTP_MAJOR_KEY)
            ?: ElixirBuildInfo.elixirOtpRelease(wslCompat.canonicalizePath(elixirHome))
            ?: return null
        val erlangHome = erlangSdk.homePath ?: return null
        val erlangOtpMajor = ErlangVersionDetector.detectRelease(erlangHome)?.otpMajor ?: return null
        return if (elixirOtpMajor != erlangOtpMajor) elixirOtpMajor to erlangOtpMajor else null
    }

    // -------------------------------------------------------------------------
    // Erlang classpath presence checks
    // -------------------------------------------------------------------------

    /**
     * Returns `true` when the Elixir SDK's class roots contain at least one entry from the
     * Erlang SDK's home directory.
     *
     * Can be `false` when the JetBrains settings persistence does not save the SDK configuration
     * correctly, or when the Erlang SDK was changed after the classpath was last populated.
     */
    fun hasErlangClasspathInElixirSdk(elixirSdk: Sdk, erlangSdk: Sdk): Boolean {
        val classRoots = elixirSdk.rootProvider.getFiles(OrderRootType.CLASSES)
        return hasErlangClasspathInRoots(classRoots, erlangSdk)
    }

    /**
     * Returns `true` when [classRoots] contains at least one file rooted under the Erlang
     * SDK's home directory.
     *
     * Used by [hasErlangClasspathInElixirSdk] and by
     * [org.elixir_lang.sdk.erlang_dependent.AdditionalDataConfigurable] where the class roots
     * come from the UI-side [com.intellij.openapi.projectRoots.SdkModificator] rather than
     * the persisted SDK.
     */
    fun hasErlangClasspathInRoots(classRoots: Array<VirtualFile>, erlangSdk: Sdk): Boolean {
        val erlangHomePath = erlangSdk.homePath ?: return false
        val erlangHomePathVf = LocalFileSystem.getInstance()
            .refreshAndFindFileByPath(FileUtil.toSystemIndependentName(erlangHomePath))
            ?: return false
        return classRoots.any { root -> VfsUtilCore.isAncestor(erlangHomePathVf, root, true) }
    }
}
