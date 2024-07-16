v2 Template: https://github.com/JetBrains/intellij-platform-plugin-template/tree/2.0.0

Docs: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html

https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-migration.html

https://github.com/JetBrains/intellij-platform-gradle-plugin


https://github.com/JetBrains/intellij-platform-gradle-plugin/releases

Steps:

1. Rename `build.gradle` to `build.gradle.old` (for dev reference)
2. Rename `settings.gradle` to `settings.gradle.old`
3. Create `build.gradle.kts` and `settings.gradle.kts`
4. Port `settings.gradle.kts` to Kotlin
```kt
rootProject.name = 'Elixir'
include 'jps-shared'
include 'jps-builder'e
```

5. Update `gradle.properties`

Before:
```
baseVersion=18.0.0
ideaVersion=2024.1.4
javaVersion=17
javaTargetVersion=17
sources=true
elixirVersion=1.13.4
publishChannels=canary
org.gradle.jvmargs=-Xmx4096m
```

Now:
```shell
# IntelliJ Platform Artifacts Repositories -> https://plugins.jetbrains.com/docs/intellij/intellij-artifacts.html
pluginGroup=org.elixir_lang
pluginName=Elixir
pluginRepositoryUrl=https://github.com/KronicDeth/intellij-elixir/
pluginVersion=18.0.1
pluginSinceBuild=241.14494.240
pluginUntilBuild=241.*
# IntelliJ Platform Properties -> https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html#configuration-intellij-extension
platformType=IC
platformVersion=2024.1.4
# Plugin Dependencies -> https://plugins.jetbrains.com/docs/intellij/plugin-dependencies.html
# Example: platfor mPlugins = com.jetbrains.php:203.4449.22, org.intellij.scala:2023.3.27@EAP
#platformPlugins =
platformPlugins=
# Example: platformBundledPlugins = com.intellij.java
platformBundledPlugins=com.intellij.java
# Gradle Releases -> https://github.com/gradle/gradle/releases
gradleVersion=8.9
# Opt-out flag for bundling Kotlin standard library -> https://jb.gg/intellij-platform-kotlin-stdlib
kotlin.stdlib.default.dependency=false
# Enable Gradle Configuration Cache -> https://docs.gradle.org/current/userguide/configuration_cache.html
org.gradle.configuration-cache=true
# Enable Gradle Build Cache -> https://docs.gradle.org/current/userguide/build_cache.html
org.gradle.caching=true
```
