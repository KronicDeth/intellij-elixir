defprotocol ProtoDef do
  def fresh(data)
end

defimpl ProtoDef, for: List do
  def fresh(data), do: data
end

defmodule ProtoClient do
  def call(data) do
    ProtoDef.fresh(data)
  end
end
