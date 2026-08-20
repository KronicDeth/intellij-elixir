defmodule TypeRename do
  @opaque my_<caret>type :: integer

  @spec value() :: my_type
  def value, do: 0
end
