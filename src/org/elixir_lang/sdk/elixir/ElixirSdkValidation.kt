package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile

/**
 * Validation and health-check queries for Elixir SDK pairings.
 *
 * Houses logic that answers "is this SDK pairing healthy?".
 */
object ElixirSdkValidation {
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
