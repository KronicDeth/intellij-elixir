<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Contributing](#contributing)
  - [Development](#development)
    - [Importing the project](#importing-the-project)
    - [Building and running](#building-and-running)
    - [Color Schemes](#color-schemes)
      - [Customizing Scheme](#customizing-scheme)
      - [Exporting Settings](#exporting-settings)
      - [Unpack Settings](#unpack-settings)
      - [Convert ICLS to Additional Text Attributes format](#convert-icls-to-additional-text-attributes-format)
      - [Add Additional Text Attributes to plugin](#add-additional-text-attributes-to-plugin)
  - [Building](#building)
    - [Documentation](#documentation)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# Contributing

## Development

### Importing the project

1. Import the project via `File > New > Project from Existing Sources...`  OR from the `Import Project` option of the splash menu
2. Select the checkout of `intellij-elixir` directory
3. Select "Import project from external model" from the radio button group
4. Select Gradle from the external models
5. Click Next
6. In "Import Project from Gradle"
  1. Check "Use auto-import"
  2. Check "Create separate module per source set"
  3. Ensure Gradle JVM is **AT LEAST** Java 1.7 / 7.  (Java 1.8 / 8 is recommended.)
  Your import settings should look something like this:<br/>
  ![Gradle settings](/screenshots/contributing/gradle_settings.png?raw=true "Gradle settings")
  4. Click Finish
7. When the "Gradle Project Data to Import" dialog pops up
  1. Leave "Elixir (root module), ":jps-builder", and "jps-shared" checked.<br/>
  ![Select modules](/screenshots/contributing/select_modules.png?raw=true "Select modules")
  2. Click OK
8. When the "Import Gradle Projects" dialog pops up
  1. Leave "intellij-elixir" checked.  You can remove the old main module:<br/>
     ![Remove module](/screenshots/contributing/remove_module.png?raw=true "Remove module")

### Building and running

Gradle will handle all dependency management, including fetching the Intellij IDEA platform specified in `gradle.properties`, so you can use a normal JDK instead of setting up an "Intellij Platform Plugin SDK".

**NOTE:** The tests require some additional dependencies which Gradle will fetch for you. You must have Elixir and Hex installed for Gradle to compile some of the dependencies for running the tests. You can run individual JUnit tests via the JUnit run configuration type, but note that some tests require additional setup which the Gradle `test` task does for you, and these tests won't work when run outside Gradle without some additional work. See `build.gradle` for details.

**NOTE:** If you're having trouble running the plugin against Intellij IDEA 14.1 on Mac, see this [comment](https://github.com/KronicDeth/intellij-elixir/pull/504#issuecomment-284275036).

### From commandline
You can run `./gradlew runIde` or `./gradlew test`.

### From IntelliJ

1. Open the Gradle Tool Window (`View > Tool Windows > Gradle` OR from the Gradle button on the right tool button bar)
2. Expand `Elixir (root) > Task`

#### `runIdea`

1. Expand `intellij`
2. Right-click `runIdea`
3. Select `Create 'intellij-elixir' [runIdea]`

#### `test`

1. Expand `verifcation`
2. Right-click `test`
3. Select `Create 'intellij-elixir' [test]`

### Color Schemes

JetBrains plugins are able to set the text attribute values for `TextAttributeKey`s that are unique to the plugin by using `additionalTextAttributes` entries in `src/META-INF/plugin.xml`.  If you have a Color Scheme for Elixir you like, you can propose it as the default for a named theme by extracting the `additionTextAttributes` `file` from an Exported Settings `.jar`.

#### Customizing Scheme

1. Preferences > Editor > Colors & Fonts > Elixir
2. Customize the colors
3. Click "Save As" to name the Scheme (`My $SCHEME_NAME`)_

#### Exporting Settings

1. File > Export Settings
2. Click "Select None"
3. Check "Editor Colors"
4. Change the "Export settings to:" path to a place you can easily access it in the terminal
5. Click "OK"

#### Unpack Settings

1. `mkdir settings`
2. `cd settings`
3. `jar xf $SAVE_DIRECTORY/settings.jar`

#### Convert ICLS to Additional Text Attributes format

1. `mv colors/colors/My\ $SCHEME_NAME.icls $INTELLIJ_ELIXIR/colorSchemes/ElixirSCHEME_NAME.xml` (`$SCHEME_NAME` will be `Default`, `Darcula` or another shared theme name.)
2. Remove all elements except for `scheme attributes`.
3. Remove the outer `scheme` tag
4. Rename the `attributes` tag to `list`.
5. Add `<?xml version='1.0'?>` to the top of the file

#### Add Additional Text Attributes to plugin

1. In `plugin.xml` inside the `idea-plugin extensions[defaultExtensionNs="com.intellij"]` tag, add a new additionalTextAttribute tag: `<additionalTextAttributes file="colorSchemes/Elixir$SCHEME_NAME.xml" scheme="SCHEME_NAME"/>`

## Building

### Documentation

The documentation files ([`CHANGELOG.md`](CHANGELOG.md), [`CONTRIBUTING.md`](CONTRIBUTING.md), [`README.md`](README.md),
and [`UPGRADING.md`](UPGRADING.md)) all have table of contents generated by
[`doctoc`](https://github.com/thlorenz/doctoc).

Install `doctoc` (globally) using `npm`

```sh
npm install -g doctoc
```

Then regenerate the table of contents using `doctoc`

```sh
doctoc .
```

