defmodule TypeRename do
  @type my_type :: integer

  @spec value() :: my_<caret>type
  def value, do: 0
end
