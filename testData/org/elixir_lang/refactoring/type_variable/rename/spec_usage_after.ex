defmodule SpecTypeVariableRename do
  @spec swap({renamed, b}) :: {b, renamed} when renamed: term(), b: term()
  def swap({a, b}), do: {b, a}
end
