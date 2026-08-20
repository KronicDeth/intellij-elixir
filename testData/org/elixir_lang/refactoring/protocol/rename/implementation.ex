defprotocol ProtocolRename do
  def convert(data)
end

defimpl ProtocolRename, for: List do
  def con<caret>vert(data), do: data
end

defmodule ProtocolRenameUsage do
    def run, do: ProtocolRename.convert([])
end
