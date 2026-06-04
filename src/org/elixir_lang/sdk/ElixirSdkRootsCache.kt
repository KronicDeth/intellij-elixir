package org.elixir_lang.sdk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.jps.shared.ElixirSdkTypeId
import org.elixir_lang.jps.shared.ErlangSdkTypeId

/**
 * Application-scoped cache of virtual-file roots belonging to Elixir and Erlang SDKs.
 *
 * Invalidated automatically via [ProjectJdkTable.JDK_TABLE_TOPIC] whenever an SDK is added,
 * removed, or renamed, so callers always get a consistent view without polling.
 */
internal object ElixirSdkRootsCache {

    @Volatile private var classRootsCache: Set<VirtualFile>? = null
    @Volatile private var classAndSourceRootsCache: Set<VirtualFile>? = null

    init {
        ApplicationManager.getApplication().messageBus.connect()
            .subscribe(ProjectJdkTable.JDK_TABLE_TOPIC, object : ProjectJdkTable.Listener {
                override fun jdkAdded(jdk: Sdk) = invalidate()
                override fun jdkRemoved(jdk: Sdk) = invalidate()
                override fun jdkNameChanged(jdk: Sdk, previousName: String) = invalidate()
            })
    }

    private fun invalidate() {
        classRootsCache = null
        classAndSourceRootsCache = null
    }

    private fun elixirSdks(): List<Sdk> =
        ProjectJdkTable.getInstance().allJdks.filter {
            it.sdkType.name == ElixirSdkTypeId.ELIXIR_SDK_TYPE_ID ||
                it.sdkType.name == ErlangSdkTypeId.ERLANG_SDK_TYPE_ID
        }

    /** CLASSES roots of all registered Elixir/Erlang SDKs. */
    fun classRoots(): Set<VirtualFile> =
        classRootsCache ?: buildSet {
            for (sdk in elixirSdks()) addAll(sdk.rootProvider.getFiles(OrderRootType.CLASSES))
        }.also { classRootsCache = it }

    /** CLASSES + SOURCES roots of all registered Elixir/Erlang SDKs. */
    fun classAndSourceRoots(): Set<VirtualFile> =
        classAndSourceRootsCache ?: buildSet {
            for (sdk in elixirSdks()) {
                addAll(sdk.rootProvider.getFiles(OrderRootType.CLASSES))
                addAll(sdk.rootProvider.getFiles(OrderRootType.SOURCES))
            }
        }.also { classAndSourceRootsCache = it }
}
