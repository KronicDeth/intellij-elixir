defmodule DelegateTarget do
  def plain(x), do: x
end

defmodule Caller do
  def call_it(x), do: DelegateTarget.pla<caret>in(x)
end
