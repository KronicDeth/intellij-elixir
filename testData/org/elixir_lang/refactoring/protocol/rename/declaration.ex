defprotocol ProtocolRename do
  def con<caret>vert(data)
end

defimpl ProtocolRename, for: List do
  def convert(data), do: data
end
