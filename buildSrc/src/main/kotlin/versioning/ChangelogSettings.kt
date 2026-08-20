package versioning

/**
 * How much of `CHANGELOG.md` reaches users, declared in `gradle.properties` and read by two consumers:
 * this build, and the per-pull-request check in `.github/workflows/changelog.yml`. The declaration
 * lives in a properties file precisely so the workflow can read it without parsing Kotlin.
 *
 * @property groups the full section vocabulary, in the order `patchChangelog` scaffolds them into a
 *   new Unreleased block. Replaces the Keep a Changelog default of
 *   Added/Changed/Deprecated/Removed/Fixed/Security.
 * @property publishedGroups the subset rendered into the plugin's `changeNotes` - the "What's New" on
 *   the Marketplace page and in the Plugins settings dialog. Everything else is recorded for
 *   contributors but says nothing a user can observe.
 * @property publishedVersions how many versions that panel lists: the one being built plus the ones
 *   before it.
 */
data class ChangelogSettings(
    val groups: List<String>,
    val publishedGroups: Set<String>,
    val publishedVersions: Int,
) {
    companion object {
        /**
         * Parses the three `changelog*` properties, failing on a published group that is not in the
         * vocabulary.
         *
         * That check is not pedantry. Publication is an **include-list**, so a name the build does not
         * recognise is excluded silently - which is the safe direction for a typo in an entry, but a
         * disaster in this declaration: the group would publish nothing at all, with no error. It has
         * already cost a version. Before `Breaking changes` was published, v18.0.0 - whose only group
         * is breaking changes - rendered empty and was dropped from the panel entirely.
         */
        fun from(groups: String, publishedGroups: String, publishedVersions: String): ChangelogSettings {
            val vocabulary = split(groups)
            val published = split(publishedGroups)

            val unknown = published - vocabulary.toSet()
            require(unknown.isEmpty()) {
                "changelogPublishedGroups contains $unknown, which is not in changelogGroups " +
                    "($vocabulary). Fix gradle.properties - publication is an include-list, so an " +
                    "unrecognised name publishes nothing rather than failing."
            }

            val count = publishedVersions.trim().toIntOrNull()
            require(count != null && count > 0) {
                "changelogPublishedVersions must be a positive integer, not \"$publishedVersions\"."
            }

            return ChangelogSettings(vocabulary, published.toSet(), count)
        }

        /**
         * The version `CHANGELOG.md` is keyed by, given the version being built.
         *
         * Section headings carry marketing versions (`## [24.0.1] - 2026-08-05`), so any build suffix
         * has to go: a canary is `24.0.1-dev+<timestamp>.<commit>` and still has to find the `24.0.1`
         * entry, or fall back to Unreleased.
         */
        fun marketingVersion(version: String): String = version.substringBefore('-')

        /** Comma-separated, so a group name may not contain a comma. Blanks are dropped. */
        private fun split(value: String): List<String> =
            value.split(",").map(String::trim).filter(String::isNotEmpty)
    }
}
