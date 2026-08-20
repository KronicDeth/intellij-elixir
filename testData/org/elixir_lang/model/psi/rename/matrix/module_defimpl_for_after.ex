defprotocol ForProto do
  def show(data)
end

defmodule Fresh do
  defstruct [:x]
end

defimpl ForProto, for: Fresh do
  def show(%Fresh{x: x}), do: x
end
