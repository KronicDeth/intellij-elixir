defmodule Stdlib do
  @moduledoc """
  Stands in for a module of Elixir's standard library, which ships as `.ex` sources under the SDK's
  `lib/<app>/lib` sourcepath root rather than as `.beam` files under `ebin`.

  Two functions share the `stdlib_` prefix so a completion after that prefix opens a popup instead of
  auto-inserting a lone candidate.
  """

  def stdlib_puts(term), do: term

  def stdlib_write(term), do: term
end
