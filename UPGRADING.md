# Upgrading

## v0.3.0

The prior version of New > Elixir File validated that the input name was a valid path and would only allow lowercase
names, so you'd be forced to set the name to `foo`, which would produce a `foo.ex` file, but `foo` would also be used
in the file contents:

```elixir
defmodule foo do

end
```

In v0.3.0, the validator was corrected so that it only allows Alias (with and without `.`), so instead of entering the
name as `foo`, enter it as `Foo`.  The file will still be named `foo.ex`, but the module name will correctly be `Foo`
in the file contents:

```elixir
defmodule Foo do
end
```
