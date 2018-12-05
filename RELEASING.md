# Releasing

## Update README

Any changes to the README are delayed until the last PR before release because in the past new users have read the README and assumed that any features in the README MUST exist in the version they can install from the JetBrains repository, so documenting `master` features in the README leads to just more support work.

## Build Release

1. Update `version` in `gradle.properties` to the release version used in `resources/META-INF/plugin.xml`.
2. Update `ideaVersion` in `gradle.properties` to the earliest version being supported (`2018.3`).
3. Run the `buildPlugin` gradle task.

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
6. Add the the release notes
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

1. Open [IntelliJ Elixir - Elixir plugin for JetBrain's IntelliJ Platform](https://elixirforum.com/t/intellij-elixir-elixir-plugin-for-jetbrains-intellij-platform/1697)
2. Click "+ Reply" at the bottom of the thread
3. Put version as title of post
   ```
   # Version VERSION
   ```
4. Add Donate link
   ```
   [Donate](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=Kronic%2eDeth%40gmail%2ecom&lc=US&item_name=Elixir%20plugin%20for%20IntelliJ%20IDEA&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted)
   ```
5. Paste Release Notes from Github in message body
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
