# Releasing

## Update README

Any changes to the README are delayed until the last PR before release because in the past new users have read the
README and assumed that any features in the README MUST exist in the version they can install from the JetBrains
repository, so documenting `main` features in the README leads to just more support work.

## Promote the changelog

`CHANGELOG.md` is in [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format and is the single
source of the plugin's change notes: the Gradle Changelog Plugin renders it into `changeNotes`, which is
the "What's New" on the [Marketplace page](https://plugins.jetbrains.com/plugin/7522-elixir) and in the
IDE's Plugins settings dialog. Contributors add entries under `## [Unreleased]` as they go (see the
Changelog section of [`CONTRIBUTING.md`](CONTRIBUTING.md)), so there is nothing to write at release -
only to promote.

The panel shows the version being built plus the five before it, and publishes only the
`### Breaking changes`, `### Enhancements` and `### Bug Fixes` groups; `### Threading / Platform
Hygiene` and `### Build / CI` are recorded for contributors and filtered out. All three settings live in
`gradle.properties`: `changelogGroups`, `changelogPublishedGroups` and `changelogPublishedVersions`.

**Do this before `buildPlugin`** - the built zip embeds the rendered notes.

1. Set `pluginVersion` in `gradle.properties` to the release version.
2. Promote `## [Unreleased]` into a dated section for it:

   ```sh
   ./gradlew patchChangelog -PpublishChannels=default
   ```

   `-PpublishChannels=default` is **required**. Without it the build bumps the patch for canary
   versioning, and `patchChangelog` would stamp that bumped number instead of the release: a `24.1.0`
   release would be recorded as `## [24.1.1]`.

3. Review and commit the result. The task adds the dated release heading and opens a fresh
   `## [Unreleased]` scaffolded with the five group headings.

If `## [Unreleased]` is empty the task skips with `missing release note in the 'Unreleased'` and
changes nothing - which means the release has no notes, so check why before continuing.

## Build Release

1. `pluginVersion` in `gradle.properties` was already set in the previous step. Do **not** edit
   `resources/META-INF/plugin.xml` - Gradle patches its `<version>` and `<change-notes>` during
   `patchPluginXml`, so any value committed there is overwritten.
2. Run the `buildPlugin` gradle task.

The **Tag Release** workflow checks that the tag's version matches `pluginVersion`, so a forgotten
bump fails before anything is built rather than shipping a mislabelled artifact.

## Smoke Test Built Release

1. Install the build plugin from disk
  1. Preferences > Plugins
  2. Click "Install plugin from disk..."
  3. Select the `build/distributions/intellij-elixir-VERSION.zip`
  4. Click Open
  5. Click Apply
  6. Click Restart
3. Ensure no errors are raised during re-indexing and reparsing of any previously open files.
4. Try out new features for this release

## Tag release

Prefer the **Tag Release** workflow (`.github/workflows/tag.yml`). Its `validate-tag` job runs before
anything is built, so a mistake costs seconds instead of the whole test matrix, and it checks:

| Check | Applies to |
|---|---|
| `v<major>.<minor>.<patch>` for a release, `…-pre-<n>` for a pre-release | both, per the `prerelease` input |
| Dispatched from `main` | releases only |
| Tag does not already exist | both |
| Version higher than every existing tag | both |
| Tag version matches `pluginVersion` in `gradle.properties` | both |

There is deliberately no change-notes check here. The notes are rendered from `CHANGELOG.md`, so no
separate file can go stale against the tag, and that every change is recorded at all is enforced per
pull request by `changelog.yml`.

One input is worth understanding rather than just satisfying: **the `prerelease` checkbox defaults to
ticked.** Leaving it ticked for a release would build the release version on the canary channel, so the
tag shape and the flag must agree.

The leading `v` is not decoration: `refs/tags/v*` globs and `git describe` both rely on it, and the
repository carries tags that got this wrong four different ways (a missing `v`, a doubled `vv`, one
that is just `v`, and four-component versions).

Tagging by hand **bypasses every check above** - get it right yourself, or use the workflow.

1. `git tag -a vVERSION -m "Version VERSION"`
2. `git push`
3. `git push --tags`

## Release Notes

To create release notes for the new tag

1. Open [https://github.com/KronicDeth/intellij-elixir](https://github.com/KronicDeth/intellij-elixir)
2. Click [releases](https://github.com/KronicDeth/intellij-elixir/releases)
3. Click [Tags](https://github.com/KronicDeth/intellij-elixir/tags)
4. Click "Add release notes" for tag you pushed
5. Set the "Release title" to `vVERSION`
6. Add the release notes
1. Thanks for bug reporters for the release (use the Milestone filter to find issues fixed for the release version)
2. The changes from the for the release (copy directly from `CHANGELOG.md`)
3. README updates (copy directly from `README.md`)
7. Attach the `build/distributions/intellij-elixir-VERSION.zip` binary.
8. Click Publish Release

## Publish to JetBrains Repository

1. Go to https://plugins.jetbrains.com/plugin/7522
2. Click Update Plugin
3. Click "Choose File" and select `build/distributions/intellij-elixir-VERSION.zip`
4. Add a brief summary of important enhancements or bug fixes for the RSS feed
5. Click "Upload New Build"

## Announce on Elixir Forums

1.
Open [IntelliJ Elixir - Elixir plugin for JetBrain's IntelliJ Platform](https://elixirforum.com/t/intellij-elixir-elixir-plugin-for-jetbrains-intellij-platform/1697)
2. Click "+ Reply" at the bottom of the thread
3. Put version as title of post
   ```
   # Version VERSION
   ```
4. Add Sponsor link
   ```
   [❤️ Sponsor](https://github.com/sponsors/KronicDeth)
   
   Historical One-time/Monthly Donations:
   
   | Stat    | Amount  |
   |:--------|--------:|
   | Minimum |   $1.00 |
   | Median  |   $6.25 |
   | Mean    |  $12.52 |
   | Maximum | $200.00 |
   ```
5. Paste Release Notes from GitHub in message body
6. Add Installation Instructions link
   ```
   [Installation Instructions](https://github.com/KronicDeth/intellij-elixir/blob/VERSION/README.md#installation)
   ```
7. Click "Reply" to post reply

## Announce on Twitter

1. Tweet

  ```
  IntelliJ Elixir vVERSION
  SUMMARY
  https://plugins.jetbrains.com/plugin/7522
  https://github.com/KronicDeth/intellij-elixir/releases/tag/vVERSION
  #myelixirstatus
  ```

2. Pin Tweet

## Announce on ElixirStatus.com

1. Open [http://elixirstatus.com/](http://elixirstatus.com/)
2. Click Sign in and Post
3. Put "IntelliJ Elixir VERSION" for the title
4. Put in brief bullet-points of enhancements and bug fixes
5. Add Installation Instructions link
   ```
   [Installation Instructions](https://github.com/KronicDeth/intellij-elixir/blob/VERSION/README.md#installation)
   ```
6. Click "Post this"
7. Click "Retweet this!"
