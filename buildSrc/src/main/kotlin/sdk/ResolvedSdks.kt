package sdk

/**
 * Aggregates resolved Erlang and Elixir SDKs for serialization.
 */
data class ResolvedSdks(
    val erlang: ResolvedSdk,
    val elixir: ResolvedSdk
) {
    fun toPropertiesString(): String {
        // Persist a simple key=value format consumed by ElixirErlangSdkArgumentProvider.
        val lines = listOf(
            "erlang.sdk.path=${erlang.homePath}",
            "erlang.version=${erlang.actualVersion.orEmpty()}",
            "erlang.expected.version=${erlang.expectedVersion.orEmpty()}",
            "erlang.source=${erlang.source}",
            "elixir.sdk.path=${elixir.homePath}",
            "elixir.version=${elixir.actualVersion.orEmpty()}",
            "elixir.expected.version=${elixir.expectedVersion.orEmpty()}",
            "elixir.source=${elixir.source}"
        )
        return lines.joinToString(System.lineSeparator())
    }
}
