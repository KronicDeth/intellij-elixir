defmodule DelegateTarget do
  def fresh(x), do: x
end

defmodule Delegator do
  defdelegate fresh(x), to: DelegateTarget
end
