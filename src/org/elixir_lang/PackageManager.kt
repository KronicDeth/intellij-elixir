package org.elixir_lang

import com.intellij.openapi.extensions.ExtensionPointName
import org.elixir_lang.package_manager.DepGatherer

interface PackageManager {
    val fileName: String
    fun depGatherer(): DepGatherer
}

fun packageManagers(): Array<out PackageManager> {
    return EP_NAME.extensions
}

private val EP_NAME: ExtensionPointName<PackageManager> = ExtensionPointName.create("org.elixir_lang.packageManager")
