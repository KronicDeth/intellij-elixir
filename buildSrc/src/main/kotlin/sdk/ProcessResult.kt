package sdk

/**
 * Captured output from a short-lived process invocation.
 */
data class ProcessResult(
    val exitCode: Int,
    val output: String
)
