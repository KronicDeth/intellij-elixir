defmodule OpaqueSites do
  @opaque renamee :: integer

  @spec make(integer) :: renamee
  def make(x), do: x
end
