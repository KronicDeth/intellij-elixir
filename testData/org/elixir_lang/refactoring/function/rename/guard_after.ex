defmodule RenameMod do
  defguard is_pair(value) when rem(value, 2) == 0

  def do_it(x) when is_pair(x), do: :ok
end
