# Changelog

## v17.0.1

### Bug Fixes
* [#3491](https://github.com/KronicDeth/intellij-elixir/pull/3491) - [@neominik](https://github.com/neominik)
  * Render code snippets that are not links.
* [#3562](https://github.com/KronicDeth/intellij-elixir/pull/3562) - [@ashleysommer](https://github.com/ashleysommer)
  * Add the correct ERL and elixir arguments for starting IEx depending on the version of Elixir SDK.
* [#3563](https://github.com/KronicDeth/intellij-elixir/pull/3563) - [@ashleysommer](https://github.com/ashleysommer)
  * Bundle latest OtpErlang.jar from JInterface v1.14 for OTP v26.

## v17.0.0

### Breaking changes
* [#3500](https://github.com/KronicDeth/intellij-elixir/pull/3500) - [@KronicDeth](https://github.com/KronicDeth)
  * Drop support for <= 2023.2 IDEs.

### Enhancements
* [#2402](https://github.com/KronicDeth/intellij-elixir/pull/3402) - [@joshuataylor](https://github.com/joshuataylor)
  * Support 2023.3 IDEs.

### Bug Fixes
* [#3431](https://github.com/KronicDeth/intellij-elixir/pull/3431) - [@KronicDeth](https://github.com/KronicDeth)
  * Add `displayName` in `plugin.xml` for configurables for faster menu loading.
    * `org.elixir_lang.facet.configurable.Project` - "Elixir"
    * `org.elixir_lang.facets.sdks.erlang.Configurable` - "Internal Erlang SDKs"
    * `org.elixir_lang.facets.sdks.elixir.Configurable` - "SDKs"

## v16

The [CHANGELOG for v15](https://github.com/KronicDeth/intellij-elixir/blob/v15.1.0/CHANGELOG.md) can be found in [the v16.0.0 tag](https://github.com/KronicDeth/intellij-elixir/tree/v16.0.0).
