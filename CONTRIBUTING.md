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

##### Starting

For the tests to find `intellij_elixir`, `intellij_elixir` must be running locally with the short node
name `intellij_elixir`:

```
iex --sname intellij_elixir -S mix
```
