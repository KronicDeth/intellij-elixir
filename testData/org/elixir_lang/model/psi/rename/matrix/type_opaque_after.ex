defmodule OpaqueSites do
  @opaque fresh :: integer

  @spec make(integer) :: fresh
  def make(x), do: x
end
