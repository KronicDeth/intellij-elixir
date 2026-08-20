package sdk

/**
 * Does a resolved SDK's [actual] version satisfy the [expected] one?
 *
 * Compares dot-separated components, after stripping the `-otp-N` build tag that mise and asdf append
 * to Elixir versions (`1.13.4-otp-24` is Elixir `1.13.4`). [expected] may be less precise than
 * [actual] - a pin of `1.17` is satisfied by `1.17.3`, which is what `mise use elixir@1.17` records
 * unless `pin` is set - but never the other way round: asking for `1.17.3` is not satisfied by `1.17`,
 * because that could be any patch.
 *
 * Component-wise rather than textual: a `startsWith` comparison accepts `1.1` for `1.19.5`.
 *
 * Strings that are not dotted-numeric at all - a prerelease like `1.18.0-rc.0`, an OTP built from a
 * patched tree whose `OTP_VERSION` reads `26.2.5.21**`, or a JBR-style build tag - cannot be compared
 * component-wise, so they fall back to text equality. That comparison is still made on the
 * build-tag-stripped form, because the two sides come from different places: the expected version is
 * mise's (`1.18.0-rc.0-otp-27`) while the actual one is the SDK's own report (`1.18.0-rc.0`), and
 * comparing the raw strings would call an exactly matching pair a mismatch and send the resolver into
 * a needless mise install or from-source build.
 */
fun isCompatibleVersion(expected: String, actual: String): Boolean {
    val expectedComponents = versionComponents(expected)
    val actualComponents = versionComponents(actual)

    // Not version-shaped (a prerelease, a patched-OTP marker, or a name like a JBR build tag) - fall
    // back to text equality on the build-tag-stripped form.
    if (expectedComponents.isEmpty() || actualComponents.isEmpty()) {
        return versionWithoutBuildTag(expected) == versionWithoutBuildTag(actual)
    }

    if (expectedComponents.size > actualComponents.size) {
        return false
    }

    return expectedComponents.indices.all { expectedComponents[it] == actualComponents[it] }
}

/**
 * Strips the `-otp-N` build tag mise and asdf append to Elixir versions: `1.13.4-otp-24` -> `1.13.4`.
 *
 * Use for anything that must be stable across equivalent pins - cache directory names in particular,
 * where `1.13.4` and `1.13.4-otp-24` are the same Elixir and must not produce two build trees. Do NOT
 * use it when handing a version back to mise, which needs the full string to identify the install.
 */
fun versionWithoutBuildTag(version: String): String = version.trim().substringBefore("-otp-").trim()

/** `1.13.4-otp-24` -> [1, 13, 4]. Empty when the string is not a dotted numeric version. */
private fun versionComponents(version: String): List<Int> {
    val core = version.trim().substringBefore("-otp-").trim()
    if (core.isEmpty()) return emptyList()
    val parts = core.split('.')
    return parts.map { it.toIntOrNull() ?: return emptyList() }
}
