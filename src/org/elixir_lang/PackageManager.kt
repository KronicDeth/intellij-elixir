package org.elixir_lang

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.package_manager.DepGatherer
import org.elixir_lang.package_manager.DepsStatusResult

interface PackageManager {
    val fileName: String
    fun depGatherer(): DepGatherer
    fun depsStatus(project: Project, packageVirtualFile: VirtualFile, sdk: Sdk?): DepsStatusResult =
        DepsStatusResult.Unsupported
}

fun packageManagers(): Array<out PackageManager> {
    return EP_NAME.extensions
}

private val EP_NAME: ExtensionPointName<PackageManager> = ExtensionPointName.create("org.elixir_lang.packageManager")
