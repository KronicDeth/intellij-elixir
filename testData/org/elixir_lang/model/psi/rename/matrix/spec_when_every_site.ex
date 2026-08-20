defmodule SpecWhenSites do
  @spec swap({renamee, b}) :: {b, renamee} when renamee: term(), b: term()
  def swap({x, y}), do: {y, x}
end
