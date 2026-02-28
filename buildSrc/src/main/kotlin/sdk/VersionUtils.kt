package sdk

/**
 * Lenient version comparison that allows prefix matches (e.g., 1.13.4 vs 1.13.4-otp-24).
 */
fun isCompatibleVersion(expected: String, actual: String): Boolean {
    val expectedTrimmed = expected.trim()
    val actualTrimmed = actual.trim()
    return actualTrimmed == expectedTrimmed ||
        actualTrimmed.startsWith(expectedTrimmed) ||
        expectedTrimmed.startsWith(actualTrimmed)
}
