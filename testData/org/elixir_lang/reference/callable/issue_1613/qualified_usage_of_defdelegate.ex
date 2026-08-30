defmodule DelegateTarget do
  def delegated(x), do: x
end

defmodule Delegator do
  defdelegate delegated(x), to: DelegateTarget
end

defmodule Caller do
  def call_it(x), do: Delegator.delegat<caret>ed(x)
end
