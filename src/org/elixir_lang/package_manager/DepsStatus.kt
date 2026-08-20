package org.elixir_lang.package_manager

data class DepStatus(
    val name: String,
    val state: DepState,
    val message: String? = null,
)

enum class DepState {
    OK,
    OUTDATED,
    UNKNOWN,
}

data class DepsStatus(val dependencies: List<DepStatus>) {
    val hasNonOk: Boolean
        get() = dependencies.any { it.state != DepState.OK }

    val nonOkDependencies: List<DepStatus>
        get() = dependencies.filter { it.state != DepState.OK }
}

sealed class DepsStatusResult {
    data class Available(val status: DepsStatus) : DepsStatusResult()
    data class Error(val message: String) : DepsStatusResult()
    object Unsupported : DepsStatusResult()
}
