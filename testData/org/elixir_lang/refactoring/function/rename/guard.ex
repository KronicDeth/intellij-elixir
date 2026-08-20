defmodule RenameMod do
  defguard is_even<caret>(value) when rem(value, 2) == 0

  def do_it(x) when is_even(x), do: :ok
end
