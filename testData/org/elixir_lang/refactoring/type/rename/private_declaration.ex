defmodule TypeRename do
  @typep my_<caret>type :: integer

  @spec value() :: my_type
  defp value, do: 0
end
