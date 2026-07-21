defmodule TypeSites do
  @type fresh :: integer

  @spec use_it(fresh) :: fresh
  def use_it(x), do: x
end
