defprotocol ForProto do
  def show(data)
end

defmodule Renamee do
  defstruct [:x]
end

defimpl ForProto, for: Renamee do
  def show(%Renamee{x: x}), do: x
end
