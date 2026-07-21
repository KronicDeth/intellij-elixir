defmodule TypeSites do
  @type renamee :: integer

  @spec use_it(renamee) :: renamee
  def use_it(x), do: x
end
