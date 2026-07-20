defmodule SpecTypeVariableRename do
  @spec swap({<caret>a, b}) :: {b, a} when a: term(), b: term()
  def swap({a, b}), do: {b, a}
end
