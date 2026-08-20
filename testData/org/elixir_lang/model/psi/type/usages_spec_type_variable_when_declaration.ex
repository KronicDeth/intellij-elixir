defmodule SpecTypeVariableUsages do
  @spec swap({a, b}) :: {b, a} when <caret>a: term(), b: term()
  def swap({a, b}), do: {b, a}
end
