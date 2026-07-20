defmodule KeywordPairExporter do
  def per<caret>form, do: :ok
end

defmodule KeywordPairImporter do
  import KeywordPairExporter, only: [perform: 0]

  def run, do: perform()
end
