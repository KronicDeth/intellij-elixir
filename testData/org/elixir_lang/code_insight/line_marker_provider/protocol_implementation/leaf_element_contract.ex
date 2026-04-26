# Exercises defprotocol/defimpl macro calls and callback definitions for line marker providers.
defprotocol ProtocolImplementationLeafContract do
  @spec run(term()) :: term()
  def run(value)
end

defimpl ProtocolImplementationLeafContract, for: List do
  def run(value), do: value
end

defimpl ProtocolImplementationLeafContract, for: Map do
  def run(value), do: value
end
