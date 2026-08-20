package org.elixir_lang.sdk.elixir

import com.intellij.openapi.projectRoots.Sdk
import org.elixir_lang.sdk.erlang_dependent.SdkAdditionalData

/**
 * Shared classification of a module's Elixir SDK, so the status text shown by the status-bar
 * widget ([org.elixir_lang.status_bar_widget.ElixirEditorBasedSdkWidget]) and by the
 * Settings → Elixir per-module SDK page ([org.elixir_lang.facet.Configurable]) is defined in one
 * place rather than duplicated.
 */
sealed interface ModuleSdkStatus {
    object NoSdk : ModuleSdkStatus
    data class Invalid(val elixirSdk: Sdk) : ModuleSdkStatus
    data class MissingErlang(val elixirSdk: Sdk) : ModuleSdkStatus
    data class Ready(val elixirSdk: Sdk, val erlangSdk: Sdk) : ModuleSdkStatus

    companion object {
        /** Classifies [elixirSdk] (typically the SDK resolved for a module, or a candidate SDK). */
        fun of(elixirSdk: Sdk?): ModuleSdkStatus {
            if (elixirSdk == null) return NoSdk
            val sdkType = elixirSdk.sdkType as? Type ?: return Invalid(elixirSdk)
            val homePath = elixirSdk.homePath ?: return Invalid(elixirSdk)
            if (!sdkType.isValidSdkHome(homePath)) return Invalid(elixirSdk)
            val erlangSdk = (elixirSdk.sdkAdditionalData as? SdkAdditionalData)?.getErlangSdk()
                ?: return MissingErlang(elixirSdk)
            return Ready(elixirSdk, erlangSdk)
        }
    }
}

/**
 * HTML summary of the SDK status (usable in tooltips and [com.intellij.ui.components.JBLabel]s).
 * When [moduleName] is non-null it is appended, matching the status-bar widget's multi-module tooltip.
 */
fun ModuleSdkStatus.summaryHtml(moduleName: String? = null): String {
    val moduleSuffix = moduleName?.let { " (module '$it')" } ?: ""
    return when (this) {
        is ModuleSdkStatus.NoSdk ->
            "No Elixir SDK configured$moduleSuffix"
        is ModuleSdkStatus.Invalid ->
            "Elixir SDK: ${elixirSdk.name} - Invalid SDK$moduleSuffix"
        is ModuleSdkStatus.MissingErlang ->
            "Elixir SDK: ${elixirSdk.name} - Missing Erlang SDK$moduleSuffix"
        is ModuleSdkStatus.Ready -> buildString {
            append("Elixir: <b>${elixirSdk.name}</b>")
            append("<br>Erlang: <b>${erlangSdk.name}</b>")
            if (moduleName != null) append("<br>Module: <b>$moduleName</b>")
        }
    }
}
