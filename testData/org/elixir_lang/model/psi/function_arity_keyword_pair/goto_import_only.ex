defmodule KeywordPairExporter do
  def perform, do: :ok
end

defmodule KeywordPairImporter do
  import KeywordPairExporter, only: [per<caret>form: 0]

  def run, do: perform()
end
