defmodule RemoteTypeDef do
  @type renamee :: integer
end

defmodule RemoteTypeUse do
  @spec use_it(RemoteTypeDef.renamee) :: RemoteTypeDef.renamee
  def use_it(x), do: x
end
