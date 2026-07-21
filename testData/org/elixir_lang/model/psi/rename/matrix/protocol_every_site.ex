defprotocol ProtoDef do
  def renamee(data)
end

defimpl ProtoDef, for: List do
  def renamee(data), do: data
end

defmodule ProtoClient do
  def call(data) do
    ProtoDef.renamee(data)
  end
end
