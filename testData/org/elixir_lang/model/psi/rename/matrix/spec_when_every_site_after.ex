defmodule SpecWhenSites do
  @spec swap({fresh, b}) :: {b, fresh} when fresh: term(), b: term()
  def swap({x, y}), do: {y, x}
end
