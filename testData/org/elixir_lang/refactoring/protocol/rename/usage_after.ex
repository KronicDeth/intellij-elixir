defprotocol ProtocolRename do
  def transform(data)
end

defimpl ProtocolRename, for: List do
  def transform(data), do: data
end

defmodule ProtocolRenameUsage do
  def run, do: ProtocolRename.transform([])
end
