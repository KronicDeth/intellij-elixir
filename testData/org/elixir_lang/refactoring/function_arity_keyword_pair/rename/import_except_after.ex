defmodule KeywordPairExporter do
  def execute, do: :ok
  def other, do: :ok
end

defmodule KeywordPairImporter do
  import KeywordPairExporter, except: [execute: 0]

  def run, do: other()
end
