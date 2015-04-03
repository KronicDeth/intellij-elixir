# Contributing

## Testing

### Dependencies

#### `intellij_elixir`
[`org.elixir_lang.parsing_definition` tests](tests/org/elixir_lang.parsing_definition) uses
[`intellij_elixir`](https://github.com/KronicDeth/intellij_elixir)'s `IntellijElixir.Quoter` process to verify that
intellij-elixir's parsed and quoted format matches that of native Elixir's `Code.string_to_quoted`.

##### Installation

1. `mkdir ~/git/KronicDeth`
2. `cd ~/git/KronicDeth`
3. `git clone git@github.com:KronicDeth/intellij_elixir.git`
4. `cd intellij_elixir`
5. `mix local.hex --force`
6. `mix deps.get`
7. `mix release`

##### Starting

For the tests to find `intellij_elixir`, `intellij_elixir` must be running locally:

1. `cd ~/git/KronicDeth/intellij_elixir`
2. `rel/intellij_elixir/bin/intellij_elixir start`

##### Stopping

After the tests are completed you can stop `intellij_elixir`:

1. `cd ~/git/KronicDeth/intellij_elixir`
2. `rel/intellij_elixir/bin/intellij_elixir stop`
