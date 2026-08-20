package sdk

/**
 * A resolved SDK location with version metadata for logging and diagnostics.
 */
data class ResolvedSdk(
    val name: String,
    val homePath: String,
    val actualVersion: String?,
    val expectedVersion: String?,
    val source: String
)
