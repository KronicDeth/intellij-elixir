defprotocol ProtocolRename do
  def convert(data)
end

defimpl ProtocolRename, for: List do
  def convert(data), do: data
end

defmodule ProtocolRenameUsage do
  def run, do: ProtocolRename.con<caret>vert([])
end
