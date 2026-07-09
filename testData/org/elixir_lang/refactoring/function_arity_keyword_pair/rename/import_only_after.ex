defmodule KeywordPairExporter do
  def execute, do: :ok
end

defmodule KeywordPairImporter do
  import KeywordPairExporter, only: [execute: 0]

  def run, do: execute()
end
