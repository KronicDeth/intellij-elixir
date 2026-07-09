defmodule TypeRename do
  @typep renamed_type :: integer

  @spec value() :: renamed_type
  defp value, do: 0
end
