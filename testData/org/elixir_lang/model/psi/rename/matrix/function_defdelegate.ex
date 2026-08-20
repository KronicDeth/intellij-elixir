defmodule DelegateTarget do
  def renamee(x), do: x
end

defmodule Delegator do
  defdelegate renamee(x), to: DelegateTarget
end
