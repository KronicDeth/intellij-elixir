defmodule RemoteTypeDef do
  @type fresh :: integer
end

defmodule RemoteTypeUse do
  @spec use_it(RemoteTypeDef.fresh) :: RemoteTypeDef.fresh
  def use_it(x), do: x
end
