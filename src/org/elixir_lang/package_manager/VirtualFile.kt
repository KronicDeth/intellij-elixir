package org.elixir_lang.package_manager

import com.intellij.openapi.vfs.VirtualFile
import org.elixir_lang.PackageManager
import org.elixir_lang.packageManagers

data class VirtualFile(val packageManager: PackageManager, val virtualFile: VirtualFile)

fun virtualFile(root: VirtualFile): org.elixir_lang.package_manager.VirtualFile? {
    var virtualFile: org.elixir_lang.package_manager.VirtualFile? = null

    for (packageManager in packageManagers()) {
        virtualFile = root.findChild(packageManager.fileName)?.let {
            org.elixir_lang.package_manager.VirtualFile(packageManager, it)
        }

        if (virtualFile != null) {
            break
        }
    }

    return virtualFile
}
